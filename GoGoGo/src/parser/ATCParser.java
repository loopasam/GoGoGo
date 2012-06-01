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

import classification.ATC;
import classification.ATCTerm;
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
		this.getAtc().addTerm(term);
	    }else if(matcherTherapeutic.find()){
		ATCTerm term = new ATCTerm();
		term.setCode(matcherTherapeutic.group(1).replaceAll(" ", ""));
		term.setLabel(matcherTherapeutic.group(2));
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

    public void convertInOwl(String path) throws OWLOntologyCreationException, OWLOntologyStorageException {
	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	IRI ontologyIRI = IRI.create("http://www.atc.org/atc.owl");
	IRI documentIRI = IRI.create(path);
	SimpleIRIMapper mapper = new SimpleIRIMapper(ontologyIRI, documentIRI);
	manager.addIRIMapper(mapper);
	OWLOntology ontology = manager.createOntology(ontologyIRI);
	PrefixManager prefixManager = new DefaultPrefixManager("http://www.atc.org/atc.owl#");
	OWLDataFactory factory = manager.getOWLDataFactory();

	for (ATCTerm term : this.getAtc().getTerms()) {
	    OWLClass owlTerm = factory.getOWLClass(":" + term.getCode(), prefixManager);

	    OWLAnnotationProperty labelproperty = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
	    OWLLiteral literal = factory.getOWLLiteral(term.getLabel());
	    OWLAnnotation labelAnnot = factory.getOWLAnnotation(labelproperty, literal);
	    OWLAxiom labelAxiom = factory.getOWLAnnotationAssertionAxiom(owlTerm.getIRI(), labelAnnot);
	    manager.applyChange(new AddAxiom(ontology, labelAxiom));

	    //TODO revise that block
	    if(term.getDrugBankReferences().size() > 0){

		for (String dbid : term.getDrugBankReferences()) {

		    OWLAnnotationProperty seeAlsoproperty = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_SEE_ALSO.getIRI());
		    OWLLiteral seeAlsoliteral = factory.getOWLLiteral(dbid);
		    OWLAnnotation seeAlsoAnnot = factory.getOWLAnnotation(seeAlsoproperty, seeAlsoliteral);
		    OWLAxiom seeAlsoAxiom = factory.getOWLAnnotationAssertionAxiom(owlTerm.getIRI(), seeAlsoAnnot);
		    manager.applyChange(new AddAxiom(ontology, seeAlsoAxiom));

		    OWLClass drug = factory.getOWLClass(":Drug", prefixManager);
		    OWLAxiom axiom = factory.getOWLSubClassOfAxiom(owlTerm, drug);
		    AddAxiom addAxiom = new AddAxiom(ontology, axiom);
		    manager.applyChange(addAxiom);

		}


	    }

	    if(term.getParentCode() != null){
		OWLClass owlTermParent = factory.getOWLClass(":" + term.getParentCode(), prefixManager);
		OWLAxiom axiom = factory.getOWLSubClassOfAxiom(owlTerm, owlTermParent);
		AddAxiom addAxiom = new AddAxiom(ontology, axiom);
		manager.applyChange(addAxiom);
	    }
	}
	manager.saveOntology(ontology);

    }

    //TODO to improve
    public void addDrugBankInfo(String path) throws FileNotFoundException, IOException, ClassNotFoundException {
	DrugBank drugBank = new DrugBank(path);
	for (Drug drug : drugBank.getDrugs()) {
	    if(drug.getAtcCodes().size() > 0){		
		for (String code : drug.getAtcCodes()) {
		    ATCTerm term = this.getAtc().getTerm(code);
		    if(term == null){
			System.err.println(code);
		    }else{
			term.getDrugBankReferences().add(drug.getId());
		    }
		}
	    }
	}

    }

}
