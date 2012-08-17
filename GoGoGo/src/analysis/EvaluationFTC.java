/**
 * 
 */
package analysis;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import classification.ATC;

import play.modules.elephant.errors.OWLEntityQueryException;

/**
 * Standard and final version of the evaluation for TAC meeting
 * @author Samuel Croset
 *
 */
public class EvaluationFTC {

    Mappings mappings;
    BrainNonStatic ftcBrain;
    BrainNonStatic atcBrain;
    Reports reports;
    ATC atc;
    ArrayList<String> atcStructuralClasses;
    boolean removeStructuralClasses;

    public EvaluationFTC(String pathFtc, String pathAtc, String pathMapping, boolean removeStructuralClasses, String pathToStructuralExceptions) throws IOException, OWLOntologyCreationException, ClassNotFoundException {

	this.removeStructuralClasses = removeStructuralClasses;

	//Load the structural classes information
	if(removeStructuralClasses){
	    atcStructuralClasses = new ArrayList<String>();
	    FileInputStream fstream = new FileInputStream(pathToStructuralExceptions);
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String line;
	    while ((line = br.readLine()) != null)   {
		atcStructuralClasses.add(line);
	    }	
	}

	//Load an initiate the mapping hash
	mappings = new Mappings();
	atc = new ATC("data/atc/atc.ser");
	FileInputStream fstream = new FileInputStream(pathMapping);
	DataInputStream in = new DataInputStream(fstream);
	BufferedReader br = new BufferedReader(new InputStreamReader(in));
	String line;
	while ((line = br.readLine()) != null)   {
	    Pattern pattern = Pattern.compile("(.*):\\s\\((.*)\\) = \\((.*)\\)");
	    Matcher matcher = pattern.matcher(line);
	    if (matcher.find()){
		Mapping mapping = new Mapping();
		mapping.atcGround = matcher.group(1);
		mapping.atcList = matcher.group(2).split("or");
		mapping.ftcList = matcher.group(3).split("or");
		mappings.addMapping(mapping);
	    }else{
		System.err.println("Error while parsing the mapping file: " + line);
	    }
	}

	//create a new brain for the FTC
	ftcBrain = new BrainNonStatic(pathFtc);
	//create a new brain for the ATC
	atcBrain = new BrainNonStatic(pathAtc);

	reports = new Reports();

    }

    public static void main(String[] args) throws IOException, OWLOntologyCreationException, OWLEntityQueryException, ParserException, ClassNotFoundException {

	//Initiate the evaluation
	EvaluationFTC evaluation = new EvaluationFTC("data/ftc/ftc.min.out.owl", "data/atc/atc.owl", "data/mapping/ATC_FTC/mapping-enhanced-atc-ftc.txt", false, "data/mapping/ATC_FTC/structural-classes.txt");

	//Get the full list of drugs within the ATC.
	List<OWLClass> allAtcOwlClassDrugs = evaluation.atcBrain.getSubClasses("DrugBankCompound", false);
	ArrayList<String> allAtcDrugs = shortFormifiedClasses(allAtcOwlClassDrugs, evaluation.atcBrain);

	//Get the full list of drugs within the FTC
	List<OWLClass> allFtcOwlClassDrugs = evaluation.ftcBrain.getSubClasses("FTC:03", false);
	ArrayList<String> allFtcDrugs = shortFormifiedClasses(allFtcOwlClassDrugs, evaluation.ftcBrain);


	//Iterates over the equivalences defined in the file
	for (Mapping mapping : evaluation.mappings.mappings) {

	    //hold the subclasses of the expression inside the mapping
	    ArrayList<OWLClass> sumFtcClasses = new ArrayList<OWLClass>();
	    //Simulation of "or"
	    for (String ftcClass : mapping.getFtcClasses()) {
		//Gets all the subclasses (descendant) and DrungBankCompounds for the current FTC category
		List<OWLClass> ftcSubclasses = null;
		System.out.println("getting FTC subclasses for expression: " + ftcClass + " and FTC:03...");
		ftcSubclasses = evaluation.ftcBrain.getSubClasses(ftcClass + " and FTC:03", false);
		for (OWLClass ftcSubclass : ftcSubclasses) {
		    if(!sumFtcClasses.contains(ftcSubclass)){
			sumFtcClasses.add(ftcSubclass);
		    }
		}
	    }

	    //hold the subclasses of the expression inside the mapping
	    ArrayList<OWLClass> sumAtcClasses = new ArrayList<OWLClass>();
	    //Simulation of "or"
	    for (String atcClass : mapping.getAtcClasses()) {
		//Gets all the subclasses (descendant) and DrungBankCompounds for the current FTC category
		List<OWLClass> atcSubclasses = null;
		System.out.println("getting ATC subclasses for expression: " + atcClass + " and DrugBankCompound");
		atcSubclasses = evaluation.atcBrain.getSubClasses(atcClass + " and DrugBankCompound", false);
		for (OWLClass atcSubclass : atcSubclasses) {
		    if(!sumAtcClasses.contains(atcSubclass)){			
			sumAtcClasses.add(atcSubclass);
		    }
		}
	    }


	    ArrayList<String> atcCompoundsShortFormified = shortFormifiedClasses(sumAtcClasses, evaluation.atcBrain);
	    ArrayList<String> ftcCompoundsShortFormified = shortFormifiedClasses(sumFtcClasses, evaluation.ftcBrain);

	    //Get true positives: is in the ATC and was found by the FTC.
	    //Impl: overlapping classes except Nothing
	    int TP = 0;
	    ArrayList<String> tpList = new ArrayList<String>();
	    for (String atcCompound : atcCompoundsShortFormified) {
		if(ftcCompoundsShortFormified.contains(atcCompound) && !atcCompound.equals("Nothing")){
		    tpList.add(atcCompound);
		    TP++;
		}
	    }

	    //Get false positives: FTC found something but there is nothing in the ATC but the drug is present in the ATC ==> predictions.
	    //Impl: classes that are present in in the FTC set but not in the ATC set except Nothing
	    int FP = 0;
	    ArrayList<String> fpList = new ArrayList<String>();
	    for (String ftcCompound : ftcCompoundsShortFormified) {
		if(!atcCompoundsShortFormified.contains(ftcCompound) && !ftcCompound.equals("Nothing")){
		    //Chceck whether the compound exists in the ATC in the first place
		    if(allAtcDrugs.contains(ftcCompound)){

			//If the flag is true, then remove all the child classes to the structural class
			if(evaluation.removeStructuralClasses){
			    //Get the direct therapeutical categories for the ATC drug compound
			    ArrayList<String> parentCodes = evaluation.atc.getParentCodesForTherapeutic(ftcCompound);
			    //For each of them, check whether ALL the therapeutic categories are structures.
			    //If yes, then discard
			    boolean allCategoriesAreStructurals = false;
			    int counterOfStructuralCatgories = 0;
			    int numberOfParents = parentCodes.size();

			    for (String atcStructuralClass : evaluation.atcStructuralClasses) {
				Pattern pattern = Pattern.compile(atcStructuralClass);
				for (String parentCode : parentCodes) {
				    Matcher matcher = pattern.matcher(parentCode);
				    if (matcher.find()){
					counterOfStructuralCatgories++;
				    }
				}
			    }

			    if(numberOfParents == counterOfStructuralCatgories){
				allCategoriesAreStructurals = true;
			    }

			    if(!allCategoriesAreStructurals){
				FP++;
				fpList.add(ftcCompound);
			    }
			}else{
			    FP++;
			    fpList.add(ftcCompound);
			}
		    }
		}
	    }

	    //Get false negative: the FTC was not able to determine some of the compounds in the ATC and the drug is prsent in the FTC
	    //Impl: classes that are in the ATC set but not in the FTC except Nothing
	    int FN = 0;
	    ArrayList<String> fnList = new ArrayList<String>();
	    for (String atcCompound : atcCompoundsShortFormified) {
		if(!ftcCompoundsShortFormified.contains(atcCompound) && !atcCompound.equals("Nothing")){
		    if(allFtcDrugs.contains(atcCompound)){
			FN++;
			fnList.add(atcCompound);
		    }
		}
	    }

	    Report report = new Report(TP, FP, FN);
	    report.fnList = fnList;

	    String fpString = "";	    
	    for (String fp : fpList) {
		String indications = evaluation.atc.getTherapeuticIndications(fp);
		if(indications.equals("")){
		    fpString += fp + ": Not in the ATC\n";
		}else{
		    fpString += indications;
		}
	    }
	    fpString += "\n";

	    report.fpString = fpString;
	    report.fpList = fpList;
	    report.tpList = tpList;
	    report.indication = mapping.atcGround;
	    report.compoundsAtc = atcCompoundsShortFormified;
	    report.compoundsFtc = ftcCompoundsShortFormified;
	    report.classAtc = mapping.getAtcString();
	    report.classFtc = mapping.getFtcString();
	    evaluation.reports.addReport(report);
	}

	System.out.println("Done!");

	evaluation.reports.getAnalysis("data/report.txt");

	evaluation.atcBrain.getReasoner().dispose();
	evaluation.ftcBrain.getReasoner().dispose();

    }

    private static ArrayList<String> shortFormifiedClasses(List<OWLClass> classes, BrainNonStatic brain) {

	ArrayList<String> shortFormified = new ArrayList<String>();
	for (OWLClass owlClass : classes) {
	    shortFormified.add(brain.getShortForm(owlClass));
	}
	return shortFormified;
    }

}
