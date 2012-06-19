/**
 * 
 */
package parser;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunking;
import com.aliasi.dict.ExactDictionaryChunker;
import com.aliasi.dict.MapDictionary;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;

import classification.ATC;
import classification.ATCTerm;
import dictionaries.DrugBankDictionary;
import drugbank.Drug;
import drugbank.DrugBank;

/**
 * @author Samuel Croset
 *
 */
public class ATCParser extends Parser {


    private ATC atc;

    public void setAtc(ATC atc) {
	this.atc = atc;
    }

    public ATC getAtc() {
	return atc;
    }

    public ATCParser(String pathIn, String pathOut) {
	super(pathIn, pathOut);
	this.setAtc(new ATC());
    }

    /* (non-Javadoc)
     * @see parser.Parser#parse()
     */
    @Override
    public void parse() throws FileNotFoundException, IOException {
	FileInputStream fstream = null;
	fstream = new FileInputStream(this.getPathToFile());
	DataInputStream in = new DataInputStream(fstream);
	BufferedReader br = new BufferedReader(new InputStreamReader(in));
	String line =null;
	while ((line = br.readLine()) != null)   {

	    Pattern patternCategory = Pattern.compile("^(\\w.*)\\s\\s+(\\w.*)");
	    Pattern patternTherapeutic = Pattern.compile("^\\s+(\\w\\d\\d.*)\\s\\s+(\\w.*)");

	    Matcher matcherCategory = patternCategory.matcher(line);
	    Matcher matcherTherapeutic = patternTherapeutic.matcher(line);

	    if (matcherCategory.find()){
		ATCTerm term = new ATCTerm();
		term.setCode(matcherCategory.group(1).replaceAll(" ", ""));
		term.setLabel(matcherCategory.group(2));
		term.setATherapeutic(false);
		this.getAtc().addTerm(term);
	    }else if(matcherTherapeutic.find()){
		ATCTerm term = new ATCTerm();
		term.setCode(matcherTherapeutic.group(1).replaceAll(" ", ""));
		term.setLabel(matcherTherapeutic.group(2));
		term.setATherapeutic(true);
		this.getAtc().addTerm(term);
	    }

	}

	for (ATCTerm term : this.getAtc().getTerms()) {
	    Pattern patternParent = getRegexParent(term);
	    if(patternParent != null){
		for (ATCTerm termToCheck : this.getAtc().getTerms()) {
		    Matcher matcherParent = patternParent.matcher(termToCheck.getCode());
		    if (matcherParent.find()){
			term.setParentCode(termToCheck.getCode());
		    }
		}
	    }
	}
    }

    private Pattern getRegexParent(ATCTerm term) {
	ArrayList<String> patterns = new ArrayList<String>();
	patterns.add("^(\\w\\d\\d\\w\\w)\\d\\d$");
	patterns.add("^(\\w\\d\\d\\w)\\w$");
	patterns.add("^(\\w\\d\\d)\\w$");
	patterns.add("^(\\w)\\d\\d$");

	for (String pattern : patterns) {
	    Pattern patternTermStructure = Pattern.compile(pattern);
	    Matcher matcherTerm = patternTermStructure.matcher(term.getCode());
	    if (matcherTerm.find()){
		return Pattern.compile("^" + matcherTerm.group(1) + "$");
	    }

	}

	return null;
    }

    /* (non-Javadoc)
     * @see parser.Parser#save()
     */
    @Override
    public void save() throws FileNotFoundException, IOException {	
	ObjectOutput out = null;
	out = new ObjectOutputStream(new FileOutputStream(this.getPathOut()));
	out.writeObject(this.getAtc());
	out.close();
    }

    public void convertInOwl(String path) throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException, IOException, ClassNotFoundException {
	DrugBank drugbank = new DrugBank("data/drugbank/drugbank.ser");
	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	IRI ontologyIRI = IRI.create("http://www.atc.org/atc.owl");
	IRI documentIRI = IRI.create(path);
	SimpleIRIMapper mapper = new SimpleIRIMapper(ontologyIRI, documentIRI);
	manager.addIRIMapper(mapper);
	OWLOntology ontology = manager.createOntology(ontologyIRI);
	PrefixManager atcprefixManager = new DefaultPrefixManager("http://www.ebi.ac.uk/atc/");
	PrefixManager drugbankprefixManager = new DefaultPrefixManager("http://www.drugbank.ca/drugs/");
	OWLDataFactory factory = manager.getOWLDataFactory();

	for (ATCTerm term : this.getAtc().getTerms()) {

	    OWLClass owlTerm = factory.getOWLClass(":" + term.getCode(), atcprefixManager);
	    
	    OWLAnnotationProperty labelproperty = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
	    OWLLiteral literal = factory.getOWLLiteral(term.getLabel());
	    OWLAnnotation labelAnnot = factory.getOWLAnnotation(labelproperty, literal);
	    OWLAxiom labelAxiom = factory.getOWLAnnotationAssertionAxiom(owlTerm.getIRI(), labelAnnot);
	    manager.applyChange(new AddAxiom(ontology, labelAxiom));

	    if(term.getAllDrugBankReferences().size() > 0){

		for (String dbid : term.getAllDrugBankReferences()) {

		    OWLClass dbdrug = factory.getOWLClass(":" + dbid, drugbankprefixManager);
		    OWLAxiom dbaxiom = factory.getOWLSubClassOfAxiom(dbdrug, owlTerm);
		    AddAxiom adddbAxiom = new AddAxiom(ontology, dbaxiom);
		    manager.applyChange(adddbAxiom);
		    
		    OWLLiteral drugliteral = factory.getOWLLiteral(drugbank.getDrug(dbid).getName());
		    OWLAnnotation druglabelAnnot = factory.getOWLAnnotation(labelproperty, drugliteral);
		    OWLAxiom druglabelAxiom = factory.getOWLAnnotationAssertionAxiom(dbdrug.getIRI(), druglabelAnnot);
		    manager.applyChange(new AddAxiom(ontology, druglabelAxiom));

		    OWLClass drug = factory.getOWLClass(":Drug", atcprefixManager);
		    OWLAxiom axiom = factory.getOWLSubClassOfAxiom(dbdrug, drug);
		    AddAxiom addAxiom = new AddAxiom(ontology, axiom);
		    manager.applyChange(addAxiom);

		}

	    }

	    if(term.getParentCode() != null){
		OWLClass owlTermParent = factory.getOWLClass(":" + term.getParentCode(), atcprefixManager);
		OWLAxiom axiom = factory.getOWLSubClassOfAxiom(owlTerm, owlTermParent);
		AddAxiom addAxiom = new AddAxiom(ontology, axiom);
		manager.applyChange(addAxiom);
	    }
	}
	manager.saveOntology(ontology);

    }


    public void addDrugBankInfo(String path) throws FileNotFoundException, IOException, ClassNotFoundException {
	DrugBank drugBank = new DrugBank(path);
	//Check DB and add the term to the curated categories.
	for (Drug drug : drugBank.getDrugs()) {
	    if(drug.getAtcCodes().size() > 0){
		for (String code : drug.getAtcCodes()) {
		    ATCTerm term = this.getAtc().getTerm(code);
		    if(term == null){
			System.err.println("Category doesn't exists in the ATC: " + code);
		    }else{
			term.getDrugBankReferences().add(drug.getId());
		    }
		}
	    }
	}

	//Some therapeutics present within drugbank are not mapped to an ATC eventhough they should be.
	//This text-mining part corrects that
	
	DrugBankDictionary dico = new DrugBankDictionary();
	dico.load("/home/samuel/git/BioDicoManager/BioDicoManager/data/drugbank-dico.xml");
	MapDictionary<String> lingpipedico = dico.getLingPipeDico();
	ExactDictionaryChunker chunker = new ExactDictionaryChunker(lingpipedico, IndoEuropeanTokenizerFactory.INSTANCE, true, false);

	for (ATCTerm term : this.getAtc().getTerms()) {
	    if(term.isATherapeutic()){
		Chunking chunking = chunker.chunk(term.getLabel());
		for (Chunk chunk : chunking.chunkSet()) {
		    int start = chunk.start();
		    int end = chunk.end();
		    String type = chunk.type();
		    String uri = dico.getURIForTerm(type).substring(29, 36);

		    boolean coverTheAllTerm = false;
		    if(term.getLabel().length() == end - start){
			coverTheAllTerm = true;
		    }

		    if(coverTheAllTerm){
			term.getTextMinedDrugBankReferences().add(uri);
		    }

		}
	    }
	}


    }

}
