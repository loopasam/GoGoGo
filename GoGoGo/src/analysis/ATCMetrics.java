/**
 * 
 */
package analysis;

import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * 
 * Class printing out the number of classes in the ATC.
 * Official class for TAC meeting
 * @author Samuel Croset
 *
 */
public class ATCMetrics {
    
    public static void main(String[] args) throws OWLOntologyCreationException, ParserException {
	
	BrainNonStatic atcBrain = new BrainNonStatic("data/atc/atc.owl");
	
	System.out.println("Number of Thing: " + atcBrain.getSubClasses("Thing", false).size());
	System.out.println("Number of drugbank compounds: " + atcBrain.getSubClasses("DrugBankCompound", false).size());
	
	atcBrain.getReasoner().dispose();	
	
    }
}
