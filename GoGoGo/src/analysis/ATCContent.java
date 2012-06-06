/**
 * 
 */
package analysis;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class ATCContent {

    private DrugBank drugBank;
    private ATC atc;
    private BufferedWriter report;

    public void setReport(BufferedWriter report) {
	this.report = report;
    }
    public BufferedWriter getReport() {
	return report;
    }
    public void setDrugBank(DrugBank drugBank) {
	this.drugBank = drugBank;
    }
    public DrugBank getDrugBank() {
	return drugBank;
    }
    public void setAtc(ATC atc) {
	this.atc = atc;
    }
    public ATC getAtc() {
	return atc;
    }

    public ATCContent() throws FileNotFoundException, IOException, ClassNotFoundException {
	this.setDrugBank(new DrugBank("data/drugbank.ser"));
	this.setAtc(new ATC("data/atc.ser"));
	FileWriter fstream = null;
	fstream = new FileWriter("data/drugbank-atc-mapping.txt");
	this.setReport(new BufferedWriter(fstream));
    }

    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {

	ATCContent data = new ATCContent();

	//(1) Check if all the drugs are located at the 5th level.
	data.checkWhereDrugsAre();

	//(2) check how many ATCs categories (5th level) have a reference to a drugbank compound.
	data.numberOfCrossReferencedCategories();

	//(3) Some therapeutics present within drugbank are not mapped to an ATC eventhough they should be.
	//This text-mining part corrects that.
	data.textMineIt();

	//(4) Some therapeutics within drugbank are annotated with categories that don't exists, this method fish'em out.
	data.fishNonExistingCategories();

	data.numberOfForthLevelClasses();

	data.getReport().close();

    }

    private void numberOfForthLevelClasses() {

	ArrayList<String> fourLettersCodes = new ArrayList<String>();
	for (ATCTerm term : this.getAtc().getTerms()) {
	    Pattern patternCompound = Pattern.compile("^(\\w\\d\\d\\w\\w)$");
	    Matcher matcherTerm = patternCompound.matcher(term.getCode());
	    if (matcherTerm.find()){
		fourLettersCodes.add(matcherTerm.group(1));
	    }

	}
	System.out.println("Number of four letters categories: " + fourLettersCodes.size());

    }

    private void fishNonExistingCategories() throws IOException {
	this.getReport().write("\nDrugBank compounds annotated with a non-existing ATC code:\n");
	int numberOfNonExistingCategories = 0;

	for (Drug drug : drugBank.getDrugs()) {
	    if(drug.getAtcCodes().size() > 0){
		for (String code : drug.getAtcCodes()) {
		    ATCTerm term = this.getAtc().getTerm(code);
		    if(term == null){
			this.getReport().write(drug.getId() + " : " + code + "\n");
			numberOfNonExistingCategories++;
		    }else{
			term.getDrugBankReferences().add(drug.getId());
		    }
		}
	    }
	}
	System.out.println("Number of inexsisting categories: " + numberOfNonExistingCategories);
    }


    private void textMineIt() throws IOException {
	DrugBankDictionary dico = new DrugBankDictionary();
	dico.load("/home/samuel/git/BioDicoManager/BioDicoManager/data/drugbank-dico.xml");
	MapDictionary<String> lingpipedico = dico.getLingPipeDico();
	ExactDictionaryChunker chunker = new ExactDictionaryChunker(lingpipedico, IndoEuropeanTokenizerFactory.INSTANCE, true, false);

	int numberOfNewMappings = 0;
	this.getReport().write("DrugBank compounds to annotate with the ATC code:\n");

	for (ATCTerm term : this.getAtc().getTerms()) {

	    if(term.isATherapeutic()){
		Chunking chunking = chunker.chunk(term.getLabel());
		for (Chunk chunk : chunking.chunkSet()) {
		    int start = chunk.start();
		    int end = chunk.end();
		    String type = chunk.type();

		    String uri = dico.getURIForTerm(type).substring(29, 36);

		    String phrase = term.getLabel().substring(start,end);
		    boolean coverTheAllTerm = false;
		    if(term.getLabel().length() == end - start){
			coverTheAllTerm = true;
		    }

		    boolean isKnown = false;
		    if(term.getDrugBankReferences().contains(uri)){
			isKnown = true;
		    }

		    if(coverTheAllTerm){
			if(isKnown){
			    //			    System.err.println("term: " + term.getLabel() + " (" + term.getCode() + ")"+ " - Found: " + phrase + " - " + type + " (" + uri + ")");
			}else{
			    numberOfNewMappings++;
			    this.getReport().write(uri + " (" +type + ")"+ " : " + term.getCode() + " (" + term.getLabel() + ")\n");
			    //			    System.out.println(uri + " (" +type + ")"+ " : " + term.getCode() + " (" + term.getLabel() + ")");
			    //			    System.out.println("term: " + term.getLabel() + " (" + term.getCode() + ")"+ " - Found: " + phrase + " - " + type + " (" + uri + ")");
			}
		    }

		}
	    }
	}

	System.out.println("Number of new mappings: " + numberOfNewMappings);

    }


    private void numberOfCrossReferencedCategories() {	int termsWittDBReference = 0;
    int compoundTerm = 0;

    for (ATCTerm term : this.getAtc().getTerms()) {
	Pattern patternCompound = Pattern.compile("^(\\w\\d\\d\\w\\w)\\d\\d$");
	Matcher matcherTerm = patternCompound.matcher(term.getCode());
	if (matcherTerm.find()){
	    compoundTerm++;
	    if(term.getDrugBankReferences().size() > 0){
		termsWittDBReference++;
	    }
	}
    }
    System.out.println("Number of terms with a DB reference: " + termsWittDBReference + "/" + compoundTerm);
    }

    private void checkWhereDrugsAre() {
	for (Drug drug : this.getDrugBank().getDrugs()) {
	    if(drug.getAtcCodes().size() > 0){
		for (String code : drug.getAtcCodes()) {
		    Pattern patternCompound = Pattern.compile("^(\\w\\d\\d\\w\\w)\\d\\d$");
		    Matcher matcherTerm = patternCompound.matcher(code);
		    if (matcherTerm.find()){
			//			System.out.println(code);
		    }else{
			//			System.err.println(code);
		    }
		}
	    }
	}
    }
}
