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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import play.modules.elephant.errors.OWLEntityQueryException;

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

    public static void main(String[] args) throws IOException, OWLOntologyCreationException, OWLEntityQueryException {

	//Initiate the evaluation
	EvaluationFTC evaluation = new EvaluationFTC("data/ftc/ftc.min.out.owl", "data/atc/atc.owl", "data/mapping/ATC_FTC/mapping-atc-ftc-without-full-classes.txt");

	//Holds info about the number of drugs that are considered by the evaluation for the FTC.
	Set<OWLClass> uniqueDrugsInFtcFromMapping = new HashSet<OWLClass>();
	//Holds info about the number of drugs that are considered by the evaluation for the ATC.
	Set<OWLClass> uniqueDrugsInAtcFromMapping = new HashSet<OWLClass>();

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

	    //There can be multiple equivalent categories.
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

	    //Add the info to the set
	    for (OWLClass atcDrug : atcSubclasses) {
		uniqueDrugsInAtcFromMapping.add(atcDrug);
	    }
	    for (OWLClass ftcDrug : ftcSubclasses) {
		uniqueDrugsInFtcFromMapping.add(ftcDrug);
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
	    OWLClass ftcClass;
	    try {
		ftcClass = evaluation.ftcBrain.getOWLClass(ftcMappedCategory);
		report.classFtcLabel = evaluation.ftcBrain.getLabel(ftcClass);
	    } catch (OWLEntityQueryException e) {
		report.classFtcLabel = "not in the min version";
	    }

	    report.classAtc = atcEquivalentCategories.toString();
	    ArrayList<String> atcEquivalentLabel = null;
	    try {
		atcEquivalentLabel = getLabels(atcEquivalentCategories, evaluation.atcBrain);
	    } catch (OWLEntityQueryException e) {
		e.printStackTrace();
	    }
	    report.classAtcLabel = atcEquivalentLabel.toString();

	    report.fnList = fnList;
	    report.fpList = fpList;
	    report.tpList = tpList;
	    report.compoundsAtc = atcCompoundsShortFormified;
	    report.compoundsFtc = ftcCompoundsShortFormified;

	    ArrayList<String> predictions = new ArrayList<String>();
	    for (String fp : fpList) {
		String prediction = fp + ": ";
		try {
		    List<OWLClass> atcCategory = evaluation.atcBrain.getSuperClasses(fp, true);

		    for (OWLClass owlClass : atcCategory) {
			String atcLabel = evaluation.atcBrain.getShortForm(owlClass);
			if(!atcLabel.equals("DrugBankCompound")){
			    //Super dirty hack that removes the last part of the ATC code to get higher class
			    String trimmedName = atcLabel.substring(0, atcLabel.length() -2);
			    OWLClass atcGroup = evaluation.atcBrain.getOWLClass(trimmedName);
			    String label = evaluation.atcBrain.getLabel(atcGroup);
			    prediction += " " + atcLabel + " - " + label;
			}
		    }

		} catch (ParserException e) {
		    prediction += " Not in the ATC";
		}
		predictions.add(prediction);
	    }
	    report.predictionList = predictions;

	    evaluation.reports.addReport(report);

	}

	evaluation.reports.numberOfDrugsFromAtc = uniqueDrugsInAtcFromMapping.size();
	evaluation.reports.numberOfDrugsFromFtc = uniqueDrugsInFtcFromMapping.size();

	System.out.println("Done!");

	evaluation.reports.getAnalysis("data/report.txt");

	evaluation.atcBrain.getReasoner().dispose();
	evaluation.ftcBrain.getReasoner().dispose();

    }

    /**
     * @param atcEquivalentCategories
     * @param brain 
     * @return
     * @throws OWLEntityQueryException 
     */
    private static ArrayList<String> getLabels(ArrayList<String> atcEquivalentCategories, BrainNonStatic brain) throws OWLEntityQueryException {
	ArrayList<String> labels = new ArrayList<String>();
	for (String category : atcEquivalentCategories) {
	    OWLClass owlClass  = brain.getOWLClass(category);
	    labels.add(brain.getLabel(owlClass));
	}
	return labels;
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
