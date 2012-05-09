/**
 * 
 */
package test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import drugbank.Drug;
import drugbank.DrugBank;
import drugbank.Partner;

import parser.DrugBankParser;

/**
 * @author Samuel Croset
 *
 */
public class DrugBankParserTest {

    private DrugBankParser parser;
    private DrugBank drugbank;

    @Before
    public void initDrugBankParser() {
	parser = new DrugBankParser("data/drugbank.xml", "data/drugbankTest.ser");
	parser.parse();
	this.drugbank = parser.getDrugbank();
    }

    @Test
    public void testDrug() {

	Drug drug = this.drugbank.getDrug("DB00001");	
	assertEquals("Lepirudin", drug.getName());
	assertEquals(1, drug.getGroups().size());
	assertEquals("approved", drug.getGroups().get(0));
	assertEquals(1, drug.getTargetRelations().size());
	assertEquals(54, drug.getTargetRelations().get(0).getPartnerId());
	assertEquals("yes", drug.getTargetRelations().get(0).getKnowAction());
	assertEquals("inhibitor", drug.getTargetRelations().get(0).getActions().get(0));

	Drug drug1 = this.drugbank.getDrug("DB00203");	
	assertEquals("Sildenafil", drug1.getName());
	assertEquals(1, drug1.getGroups().size());
	assertEquals("approved", drug1.getGroups().get(0));
	assertEquals(3, drug1.getTargetRelations().size());
	assertEquals(6038, drug1.getTargetRelations().get(2).getPartnerId());
	assertEquals("no", drug1.getTargetRelations().get(2).getKnowAction());
	assertEquals("inhibitor", drug1.getTargetRelations().get(0).getActions().get(0));

    }

    @Test
    public void testPartner() {
	Partner partner = this.drugbank.getPartner(3188);
	assertEquals("IspD/ispF bifunctional enzyme [Includes: 2-C-methyl-D-erythritol 4- phosphate cytidylyltransferase", partner.getName());
	assertEquals("Q9PM68", partner.getUniprotIdentifer());
    }

    @Test
    public void testGetPartners(){
	ArrayList<Partner> partners = this.drugbank.getPartners("DB00224");
	assertEquals(1, partners.size());
	assertEquals("O90777", partners.get(0).getUniprotIdentifer());
    }
    
    @Test
    public void saveTest() throws FileNotFoundException, IOException{
	this.parser.save();
    }
    
    @Test
    public void serializationLoading() throws FileNotFoundException, IOException, ClassNotFoundException{
	DrugBank drugBank = new DrugBank("data/drugbankTest.ser");
	Drug drug = drugBank.getDrug("DB01211");
	assertEquals("Clarithromycin", drug.getName());
    }
}
