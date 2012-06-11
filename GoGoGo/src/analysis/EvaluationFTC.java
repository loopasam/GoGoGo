/**
 * 
 */
package analysis;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import querier.DLQueryPrinter;

/**
 * @author Samuel Croset
 *
 */
public class EvaluationFTC {

    private DLQueryPrinter ftcQuerier;
    private DLQueryPrinter atcQuerier;
    private HashMap<String, String> classesMapping;

    public void setClassesMapping(HashMap<String, String> classesMapping) {
	this.classesMapping = classesMapping;
    }

    public HashMap<String, String> getClassesMapping() {
	return classesMapping;
    }

    public DLQueryPrinter getFtcQuerier() {
	return ftcQuerier;
    }

    public void setFtcQuerier(DLQueryPrinter ftcQuerier) {
	this.ftcQuerier = ftcQuerier;
    }

    public DLQueryPrinter getAtcQuerier() {
	return atcQuerier;
    }

    public void setAtcQuerier(DLQueryPrinter atcQuerier) {
	this.atcQuerier = atcQuerier;
    }

    public EvaluationFTC() throws OWLOntologyCreationException, IOException {
	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	File atcFile = new File("data/atc.owl");
	OWLOntology atc = manager.loadOntologyFromOntologyDocument(atcFile);
	System.out.println("Loaded ontology: " + atc.getOntologyID());
	OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
	OWLReasoner atcReasoner = reasonerFactory.createReasoner(atc);
	ShortFormProvider atcShortFormProvider = new SimpleShortFormProvider();
	this.setAtcQuerier(new DLQueryPrinter(atcReasoner, atcShortFormProvider));

	File ftcFile = new File("data/ftc-2.owl");
	OWLOntology ftc = manager.loadOntologyFromOntologyDocument(ftcFile);
	System.out.println("Loaded ontology: " + ftc.getOntologyID());
	OWLReasonerFactory ftcReasonerFactory = new Reasoner.ReasonerFactory();
	OWLReasoner ftcReasoner = ftcReasonerFactory.createReasoner(ftc);
	ShortFormProvider ftcShortFormProvider = new SimpleShortFormProvider();
	this.setFtcQuerier(new DLQueryPrinter(ftcReasoner, ftcShortFormProvider));

	FileInputStream fstream = new FileInputStream("data/mapping/mapping-atc-ftc.txt");
	DataInputStream in = new DataInputStream(fstream);
	BufferedReader br = new BufferedReader(new InputStreamReader(in));
	String line;
	HashMap<String, String> classesMapping = new HashMap<String, String>();
	while ((line = br.readLine()) != null)   {
	    Pattern pattern = Pattern.compile("(.*) = (.*)");
	    Matcher matcher = pattern.matcher(line);
	    if (matcher.find()){
		String ftcClass = matcher.group(2);
		String atcClass = matcher.group(1);
		if(classesMapping.containsKey(ftcClass)){
		    String newExpression = classesMapping.get(ftcClass) + " or " + atcClass;
		    classesMapping.put(ftcClass, newExpression);
		}else{
		    classesMapping.put(ftcClass, atcClass);
		}

	    }else{
		System.err.println("Error while parsing the mapping file");
	    }
	}

	this.setClassesMapping(classesMapping);

    }

    public static void main(String[] args) throws OWLOntologyCreationException, IOException {
	EvaluationFTC evaluation = new EvaluationFTC();
	
	int overlappingDrugsOverall = 0;
	int numberOfDrugsInAtc = 0;

	for (String ftcClass : evaluation.getClassesMapping().keySet()) {
	    ArrayList<String> atcResults = evaluation.getSubClasses("Drug and " + evaluation.getClassesMapping().get(ftcClass), evaluation.getAtcQuerier());
	    ArrayList<String> ftcResults = evaluation.getSubClasses("Drug and " + ftcClass, evaluation.getFtcQuerier());

	    if(ftcResults.size() > 0){
		System.out.println("Drugs in the class: " + ftcClass + " = " + evaluation.getClassesMapping().get(ftcClass));

		int overlappingDrugs = 0;

		for (String atcDrug : atcResults) {
		    if(ftcResults.contains(atcDrug)){
			overlappingDrugs++;
			overlappingDrugsOverall++;
		    }else{
			System.err.println("Drug not identified within FTC: " + atcDrug);
		    }
		    numberOfDrugsInAtc++;
		}

		for (String ftcDrug : ftcResults) {
		    if(!atcResults.contains(ftcDrug)){
//			System.out.println("Repuposing opportunity: " + ftcDrug);
		    }
		}

		System.out.println("Number of drugs identified: " + overlappingDrugs + "/" + atcResults.size() + "(" + overlappingDrugs*100/atcResults.size() + "%)");
	    }else{
		System.err.println("the FTC class + " + ftcClass + " is not in the FTC yet");
	    }

	}
	
	System.out.println("Overall coverage: " + overlappingDrugsOverall + "/" + numberOfDrugsInAtc);
	System.out.println(overlappingDrugsOverall*100/numberOfDrugsInAtc + "%");
 
    }

    private ArrayList<String> getSubClasses(String expression, DLQueryPrinter querier) {
	Set<OWLClass> results = querier.returnSubClasses(expression);
	ArrayList<String> shortFormified = new ArrayList<String>();
	if(results != null){
	    for (OWLClass owlClass : results) {
		shortFormified.add(querier.getShortFormProvider().getShortForm(owlClass));
	    }
	}
	return shortFormified;
    }

}
