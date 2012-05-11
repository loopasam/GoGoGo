/**
 * 
 */
package analysis;

import java.io.File;
import java.io.IOException;

import gene_ontology.GeneOntology;
import gene_ontology.GoRelation;
import gene_ontology.GoTerm;
import gogogo.GoGoGoDataset;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

/**
 * @author Samuel Croset
 *
 */
public class GoQuery {

    public static void main(String[] args) throws OWLOntologyCreationException, IOException, ClassNotFoundException {

	GoGoGoDataset data = new GoGoGoDataset("data/dataset.ser");
	GeneOntology go = data.getGo();

	OWLOntologyManager man = OWLManager.createOWLOntologyManager();
	String base = "http://org.semanticweb.restrictionexample";

	File file = new File("data/flamingo.owl");

	OWLOntology ont = man.loadOntologyFromOntologyDocument(file);

	OWLDataFactory factory = man.getOWLDataFactory();

	IRI iriPositivelyRegulates = IRI.create(base + "#positively_regulates");
	OWLObjectProperty property = factory.getOWLObjectProperty(iriPositivelyRegulates);


    }

}
