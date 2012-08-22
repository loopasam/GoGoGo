/**
 * 
 */
package launcher;

import java.io.IOException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import exceptions.MappingException;
import ftc.FunctionalTherapeuticClassification;

/**
 * @author Samuel Croset
 *
 */
public class ClassificationLauncher {
    
    public static void main(String[] args) throws OWLOntologyCreationException, IOException, ClassNotFoundException, MappingException, OWLOntologyStorageException {
	
	//TODO adding a updater here to get the latest results.
	System.out.println("Loading data...");
	FunctionalTherapeuticClassification classification =new FunctionalTherapeuticClassification("data/integration/dataset-filtered.ser");
	
	System.out.println("Converting in owl...");
	classification.generateOwlOntology();
	
	System.out.println("Generating prots and drugs axioms...");	
	classification.generateMininumProteinandDrugAxioms();
	
//	System.out.println("Generate full classification...");
//	classification.generateFullProteinAndDrugAxioms();
	
	System.out.println("Checking for consistency...");
	boolean isConsistent = classification.isConsistent();
	System.out.println("consistent: " + isConsistent);

	System.out.println("Adding inferred axioms to the ontology...");
//	classification.classify("data/ftc/ftc-inferred.owl");
	classification.classify();

	System.out.println("Saving...");
	classification.save("data/ftc.min.out.owl");
//	classification.save("data/ftc.full.out.owl");
	System.out.println("Check if saved and stop manually the prgm --> bug to fix");
    }

}
