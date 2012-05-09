/**
 * 
 */
package test;

import static org.junit.Assert.*;

import goa.GoAnnotation;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import parser.GoaConnector;

import drugbank.Partner;


/**
 * @author Samuel Croset
 *
 */
public class ConnectorTest {
    
    private GoaConnector connector;

    @Before
    public void initConnector() throws FileNotFoundException, IOException, ClassNotFoundException {
	this.connector = new GoaConnector("data/drugbank.ser", "data/go.ser");
	this.connector.fillPartnersWithGoTerms();
    }

    @Test
    public void testAnnotations() {
	
	Partner partner = this.connector.getDrugbank().getPartner(54);
	assertNotNull(partner.getAnnotations());
	assertEquals(109, partner.getAnnotations().size());
	GoAnnotation annot = partner.getAnnotations().get(0);
	assertEquals("GO:0001934", annot.getGoId());
	assertEquals("UniProtKB", annot.getDatabase());
	assertEquals("20090305", annot.getDate());
	assertEquals("IDA", annot.getEvidence());
	assertEquals(":", annot.getEvidenceProvider());
	assertEquals("-", annot.getQualifier());
	assertEquals("PMID:7559487", annot.getReference());
	assertEquals("BHF-UCL", annot.getSource());
	assertEquals("9606", annot.getTaxon());
    }
    
    @Test
    public void testSave() throws FileNotFoundException, IOException{
	this.connector.save("data/dataset-test.ser");
    }


}
