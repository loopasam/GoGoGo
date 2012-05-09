/**
 * 
 */
package test;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

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
	parser = new GeneOntologyParser("data/gene_ontology_ext.obo", "data/go-test.ser");
	parser.parse();
	this.go = parser.getGo();
    }

    @Test
    public void testTerms() {
	GoTerm term = this.go.getTerm("GO:0043255");
	assertEquals("regulation of carbohydrate biosynthetic process", term.getName());
	assertEquals("biological_process", term.getNamespace());
    }

    @Test
    public void testRelationsTerms() {
	GoTerm term = this.go.getTerm("GO:0043267");
	assertEquals("negative regulation of potassium ion transport", term.getName());
	assertEquals(3, term.getRelations().size());
	assertEquals("negatively_regulates", term.getRelations().get(2).getType());
	assertEquals("GO:0006813", term.getRelations().get(2).getTarget());
    }
    
    @Test
    public void saveTest() throws FileNotFoundException, IOException{
	this.parser.save();
    }
    
    @Test
    public void serializationLoading() throws FileNotFoundException, IOException, ClassNotFoundException{
	GeneOntology go = new GeneOntology("data/go-test.ser");
	GoTerm term = go.getTerm("GO:0043300");
	assertEquals("regulation of leukocyte degranulation", term.getName());
    }


}
