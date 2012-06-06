/**
 * 
 */
package classification;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import querier.DLQueryPrinter;

/**
 * @author Samuel Croset
 *
 */
public class MappingFTC_ATC {

    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, OWLOntologyCreationException {

	ATC atc = new ATC("data/atc.ser");

	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	OWLDataFactory factory = manager.getOWLDataFactory();

	File atcFile = new File("data/atc.owl");
	OWLOntology owlAtc = manager.loadOntologyFromOntologyDocument(atcFile);
	System.out.println("Loaded ontology: " + owlAtc.getOntologyID());
	OWLReasoner atcReasoner = createReasoner(owlAtc);
	ShortFormProvider atcSF = new SimpleShortFormProvider();
	DLQueryPrinter atcQuerier = new DLQueryPrinter(atcReasoner, atcSF);

	File ftcFile = new File("data/ftc-2.owl");
	OWLOntology owlFtc = manager.loadOntologyFromOntologyDocument(ftcFile);
	System.out.println("Loaded ontology: " + owlFtc.getOntologyID());
	OWLReasoner ftcReasoner = createReasoner(owlFtc);
	ShortFormProvider ftcSF = new SimpleShortFormProvider();
	DLQueryPrinter ftcQuerier = new DLQueryPrinter(ftcReasoner, ftcSF);


	for (ATCTerm term : atc.getFourLettersTerms()) {
	    System.out.println(term.getCode());
	    String classExpression = term.getCode() + " and Drug";
	    Set<OWLClass> resultsAtc = atcQuerier.returnSubClasses(classExpression);
	    for (OWLClass owlClass : resultsAtc) {
		System.out.println("\t" + atcSF.getShortForm(owlClass));
		String classExpressionFtc = atcSF.getShortForm(owlClass);

		if(!classExpressionFtc.equals("Nothing")){
		    Set<OWLClass> resultsFtc = ftcQuerier.returnSuperClasses(classExpressionFtc);

		    if(resultsFtc != null){
			for (OWLClass owlClassFtc : resultsFtc) {
			    System.out.println("\t\t" + ftcSF.getShortForm(owlClassFtc));

			    OWLClass clsAgent = factory.getOWLClass(IRI.create("http://www.gogogo.org/fuctional-skeleton.owl#" + ftcSF.getShortForm(owlClassFtc)));

			    OWLAnnotationProperty label = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());

			    for (OWLAnnotation annotation : clsAgent.getAnnotations(owlFtc, label)) {
				if (annotation.getValue() instanceof OWLLiteral) {
				    OWLLiteral val = (OWLLiteral) annotation.getValue();
				    System.out.println("\t\t" + val.getLiteral());
				}
			    }


			}
		    }

		}

	    }


	}

    }

    private static OWLReasoner createReasoner(OWLOntology rootOntology) {
	OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
	return reasonerFactory.createReasoner(rootOntology);
    }

}
