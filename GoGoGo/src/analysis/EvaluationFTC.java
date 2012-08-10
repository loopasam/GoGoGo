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
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * Standard and final version of the evaluation for TAC meeting
 * @author Samuel Croset
 *
 */
public class EvaluationFTC {

    HashMap<String, ArrayList<String>> classesMapping;
    BrainNonStatic ftcBrain;
    BrainNonStatic atcBrain;
    Reports reports;

    public EvaluationFTC(String pathFtc, String pathAtc, String pathMapping) throws IOException, OWLOntologyCreationException {

	//Load an initiate the mapping hash
	FileInputStream fstream = new FileInputStream(pathMapping);
	DataInputStream in = new DataInputStream(fstream);
	BufferedReader br = new BufferedReader(new InputStreamReader(in));
	String line;
	HashMap<String, ArrayList<String>> classesMapping = new HashMap<String, ArrayList<String>>();
	while ((line = br.readLine()) != null)   {
	    Pattern pattern = Pattern.compile("(.*) = (.*)");
	    Matcher matcher = pattern.matcher(line);
	    if (matcher.find()){
		String ftcClass = matcher.group(2);
		String atcClass = matcher.group(1);
		if(classesMapping.containsKey(ftcClass)){
		    ArrayList<String> knownAtcClasses = classesMapping.get(ftcClass);
		    knownAtcClasses.add(atcClass);
		    classesMapping.put(ftcClass, knownAtcClasses);
		}else{
		    ArrayList<String> atcClasses = new ArrayList<String>();
		    atcClasses.add(atcClass);
		    classesMapping.put(ftcClass, atcClasses);
		}
	    }else{
		System.err.println("Error while parsing the mapping file");
	    }
	}
	//set the value from the parsing.
	this.classesMapping = classesMapping;

	//create a new brain for the FTC
	ftcBrain = new BrainNonStatic(pathFtc);
	//create a new brain for the ATC
	atcBrain = new BrainNonStatic(pathAtc);

	reports = new Reports();

    }

    public static void main(String[] args) throws IOException, OWLOntologyCreationException {

	//Initiate the evaluation
	EvaluationFTC evaluation = new EvaluationFTC("data/ftc/ftc.min.out.owl", "data/atc/atc.owl", "data/mapping/ATC_FTC/mapping-atc-ftc.txt");


	//Iterates over the equivalences defined in the file
	for (String ftcMappedCategory : evaluation.classesMapping.keySet()) {
	    //Gets all the subclasses (descendant) and DrungBankCompounds for the current FTC category
	    List<OWLClass> ftcSubclasses = null;
	    try {
		ftcSubclasses = evaluation.ftcBrain.getSubClasses(ftcMappedCategory + " and FTC:03", false);
	    } catch (ParserException e) {
		System.err.println("not found: " + ftcMappedCategory);
		ftcSubclasses = new ArrayList<OWLClass>();
	    }

	    ArrayList<String> atcEquivalentCategories = evaluation.classesMapping.get(ftcMappedCategory);
	    List<OWLClass> atcSubclasses = new ArrayList<OWLClass>();

	    List<OWLClass> atcSubclassesCategory = null;
	    for (String atcEquivalentCategory : atcEquivalentCategories) {
		try {
		    atcSubclassesCategory = evaluation.atcBrain.getSubClasses(atcEquivalentCategory + " and DrugBankCompound", false);
		} catch (ParserException e) {
		    System.err.println("not found: " + atcEquivalentCategory);
		    atcSubclassesCategory = new ArrayList<OWLClass>();
		} finally {

		    for (OWLClass atcSubclass : atcSubclassesCategory) {
			if(!atcSubclasses.contains(atcSubclass)){
			    atcSubclasses.add(atcSubclass);
			}
		    }

		}
	    }

	    ArrayList<String> atcCompoundsShortFormified = shortFormifiedClasses(atcSubclasses, evaluation.atcBrain);
	    ArrayList<String> ftcCompoundsShortFormified = shortFormifiedClasses(ftcSubclasses, evaluation.ftcBrain);

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

	    //Get false positives: FTC found something but there is nothing in the ATC == predictions.
	    //Impl: classes that are present in in the FTC set but not in the ATC set except Nothing
	    int FP = 0;
	    ArrayList<String> fpList = new ArrayList<String>();
	    for (String ftcCompound : ftcCompoundsShortFormified) {
		if(!atcCompoundsShortFormified.contains(ftcCompound) && !ftcCompound.equals("Nothing")){
		    FP++;
		    fpList.add(ftcCompound);
		}
	    }

	    //Get false negative: the FTC was not able to determine some of the compounds in the ATC
	    //Impl: classes that are in the ATC set but not in the FTC except Nothing
	    int FN = 0;
	    ArrayList<String> fnList = new ArrayList<String>();
	    for (String atcCompound : atcCompoundsShortFormified) {
		if(!ftcCompoundsShortFormified.contains(atcCompound) && !atcCompound.equals("Nothing")){
		    FN++;
		    fnList.add(atcCompound);
		}
	    }

	    Report report = new Report(TP, FP, FN);
	    report.classFtc = ftcMappedCategory;
	    report.classAtc = atcEquivalentCategories.toString();
	    report.fnList = fnList;
	    report.fpList = fpList;
	    report.tpList = tpList;
	    report.compoundsAtc = atcCompoundsShortFormified;
	    report.compoundsFtc = ftcCompoundsShortFormified;
	    evaluation.reports.addReport(report);

	}
	System.out.println("Done!");

	evaluation.reports.getAnalysis("data/report.txt");

    }

    /**
     * @param atcBrain2 
     * @param atcSubclasses
     * @return
     */
    private static ArrayList<String> shortFormifiedClasses(List<OWLClass> classes, BrainNonStatic brain) {

	ArrayList<String> shortFormified = new ArrayList<String>();
	for (OWLClass owlClass : classes) {
	    shortFormified.add(brain.getShortForm(owlClass));
	}
	return shortFormified;
    }

}
