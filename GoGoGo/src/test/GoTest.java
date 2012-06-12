/**
 * 
 */
package test;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import gene_ontology.GeneOntology;
import gene_ontology.GoTerm;

import org.junit.Before;
import org.junit.Test;

import parser.GeneOntologyParser;

/**
 * @author Samuel Croset
 *
 */
public class GoTest {

    private GeneOntologyParser parser;
    private GeneOntology go;

    @Before
    public void initGeneOntologyParser() throws IOException {
	parser = new GeneOntologyParser("data/gene_ontology.1_2.obo", "data/go-test.ser");
	parser.parse();
	this.go = parser.getGo();
    }
    
    @Test
    public void testGetParents(){
	ArrayList<GoTerm> parents = this.go.getParentsOfTerm(this.go.getTerm("GO:0050817"));
	assertEquals(3, parents.size());
    }

    @Test
    public void testTerms() {
	GoTerm term = this.go.getTerm("GO:0050435");
	assertEquals("beta-amyloid metabolic process", term.getName());
	assertEquals("biological_process", term.getNamespace());
	assertEquals("\"The chemical reactions and pathways involving beta-amyloid, a glycoprotein associated with Alzheimer's disease, and its precursor, amyloid precursor protein (APP).\" [GOC:ai]", term.getDefinition());
    }

    @Test
    public void testRelationsTerms() {
	GoTerm term = this.go.getTerm("GO:0043267");
	assertEquals("negative regulation of potassium ion transport", term.getName());
	assertEquals(3, term.getRelations().size());
	assertEquals("negatively_regulates", term.getRelations().get(2).getType());
	assertEquals("GO:0006813", term.getRelations().get(2).getTarget());
    }
    
//    @Test
//    public void saveTest() throws FileNotFoundException, IOException{
//	this.parser.save();
//    }
    
//    @Test
//    public void serializationLoading() throws FileNotFoundException, IOException, ClassNotFoundException{
//	GeneOntology go = new GeneOntology("data/go-test.ser");
//	GoTerm term = go.getTerm("GO:0043300");
//	assertEquals("regulation of leukocyte degranulation", term.getName());
//    }


}
