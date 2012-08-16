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

    public EvaluationFTC(String pathFtc, String pathAtc, String pathMapping) throws IOException, OWLOntologyCreationException, ClassNotFoundException {

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
	EvaluationFTC evaluation = new EvaluationFTC("data/ftc/ftc.min.out.owl", "data/atc/atc.owl", "data/mapping/ATC_FTC/mapping-enhanced-atc-ftc.txt");

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
		System.out.println("ATC subclasses: " + atcSubclasses);
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
