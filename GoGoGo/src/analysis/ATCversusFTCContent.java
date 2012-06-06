/**
 * 
 */
package analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

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
public class ATCversusFTCContent {

    public static void main(String[] args) throws OWLOntologyCreationException {

	ArrayList<String> drugsInAtc = getSubClasses("data/atc.owl", "Drug");
	System.out.println("Number of drugs within the ATC: " + drugsInAtc.size());
	
	ArrayList<String> drugsInFtc = getSubClasses("data/ftc-2.owl", "Drug");
	System.out.println("Number of drugs within the FTC: " + drugsInFtc.size());

	int overlappingDrugs = 0;
	for (String ftcDrug : drugsInFtc) {
	    if(drugsInAtc.contains(ftcDrug)){
		overlappingDrugs++;
	    }
	}
	System.out.println("Number of overlapping drugs: " + overlappingDrugs);

    }


    private static ArrayList<String> getSubClasses(String path, String classExpression) throws OWLOntologyCreationException {
	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	File file = new File(path);
	OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
	System.out.println("Loaded ontology: " + ontology.getOntologyID());
	OWLReasoner reasoner = createReasoner(ontology);
	ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
	DLQueryPrinter dlQueryPrinter = new DLQueryPrinter(reasoner, shortFormProvider);
	Set<OWLClass> results = dlQueryPrinter.returnSubClasses(classExpression);
	ArrayList<String> shortFormified = new ArrayList<String>();
	for (OWLClass owlClass : results) {
	    shortFormified.add(shortFormProvider.getShortForm(owlClass));
	}
	return shortFormified;
    }


    private static OWLReasoner createReasoner(OWLOntology rootOntology) {
	OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
	return reasonerFactory.createReasoner(rootOntology);
    }

}
