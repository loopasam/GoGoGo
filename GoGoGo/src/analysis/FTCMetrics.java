/**
 * 
 */
package analysis;

import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;


/**
 *
 * Print out the metrics about the FTC - Official class for TAC meeting
 * @author Samuel Croset
 *
 */
public class FTCMetrics {
    
    public static void main(String[] args) throws OWLOntologyCreationException, ParserException {
	
	BrainNonStatic ftcFullBrain = new BrainNonStatic("data/ftc/ftc.full.out.owl");
	BrainNonStatic ftcMinBrain = new BrainNonStatic("data/ftc/ftc.min.out.owl");
	
	System.out.println("Number of FTC:02 - Agent in full version: " + ftcFullBrain.getSubClasses("FTC:02", false).size());
	System.out.println("Number of FTC:03 - Drug in full version: " + ftcFullBrain.getSubClasses("FTC:03", false).size());
	
	System.out.println("Number of FTC:02 - Agent in min version: " + ftcMinBrain.getSubClasses("FTC:02", false).size());
	System.out.println("Number of FTC:03 - Drug in min version: " + ftcMinBrain.getSubClasses("FTC:03", false).size());

	
	ftcFullBrain.getReasoner().dispose();
	ftcMinBrain.getReasoner().dispose();
	
	
    }
    

}
