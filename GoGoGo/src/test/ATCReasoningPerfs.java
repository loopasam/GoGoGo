/**
 * 
 */
package test;

import static org.junit.Assert.assertEquals;


import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

/**
 * @author Samuel Croset
 *
 */
public class ATCReasoningPerfs {
    
    private OWLOntologyManager manager;
    private OWLOntology ontology;

    @Before
    public void initATCParserParser() throws IOException, OWLOntologyCreationException {
	manager = OWLManager.createOWLOntologyManager();
	File file = new File("data/atc/atc.owl");
	ontology = manager.loadOntologyFromOntologyDocument(file);
    }
    
    @Test
    public void elkPerf(){
	OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
	OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
	reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
	assertEquals(true, reasoner.isConsistent());
    }

    @Test
    public void hermitPerf(){
	OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
	OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
	reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
	assertEquals(true, reasoner.isConsistent());
    }


}
