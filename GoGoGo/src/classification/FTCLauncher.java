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
public class FTCLauncher {
    
    public static void main(String[] args) throws OWLOntologyCreationException, IOException, ClassNotFoundException, OWLOntologyStorageException, MappingException {
	System.out.println("Loading data...");
	FTC classification =new FTC("data/dataset-filtered.ser");

	System.out.println("Checking profile...");
	boolean el = classification.checkIfELProfile();
	System.out.println("is EL: " + el);
	System.out.println("Converting in owl...");
	classification.generateOwlOntology();
	
	System.out.println("Generating prots and drugs axioms...");
//	classification.generateProteinandDrugAxioms();
	
	classification.generateProteinandDrugAxioms();
	
	System.out.println("Checking for consistency...");
	boolean isConsistent = classification.isConsistent();
	System.out.println("consistent: " + isConsistent);

	System.out.println("Ading inferred axioms to the ontology...");
	classification.classify();

	System.out.println("Saving...");
	classification.save("data/ftc-2.owl");
	
	

    }

}
