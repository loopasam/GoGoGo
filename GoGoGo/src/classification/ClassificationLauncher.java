/**
 * 
 */
package classification;

import java.io.IOException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import exceptions.MappingException;


/**
 * @author Samuel Croset
 *
 */
public class ClassificationLauncher {

    public static void main(String[] args) throws IOException, ClassNotFoundException, OWLOntologyCreationException, OWLOntologyStorageException, MappingException {

	System.out.println("Loading data...");
	FunctionalClassification classification =new FunctionalClassification("data/dataset-filtered.ser");

	System.out.println("Converting in owl...");
	classification.generateOwlOntology();

	boolean isConsistent = classification.isConsistent();
	System.out.println("consistent: " + isConsistent);

	System.out.println("Generating patterns...");
	classification.generateAgentPatterns();
	boolean isConsistentAfterPatterns = classification.isConsistent();
	System.out.println("consistent after patterns: " + isConsistentAfterPatterns);


	System.out.println("Generating prots and drugs axioms...");
	classification.generateProteinandDrugAxioms();
	System.out.println("Start reasoning...");
	boolean isConsistentAfterProteins = classification.isConsistent();
	System.out.println("consistent after adding proteins: " + isConsistentAfterProteins);

	System.out.println("Saving...");
	classification.save("data/ftc.owl");

    }

}
