/**
 * 
 */
package test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import classification.ATC;
import classification.ATCTerm;

import parser.ATCParser;


/**
 * @author Samuel Croset
 *
 */
public class ATCTest {
    private ATCParser parser;
    private ATC atc;

    @Before
    public void initATCParserParser() throws IOException {
	parser = new ATCParser("data/ASCII_Index_med_DDD.asc", "data/atc.ser");
	parser.parse();
	this.atc = parser.getAtc();
    }
    
    @Test
    public void testGetParent(){
	ATCTerm term = this.atc.getTerm("A01A");
	assertEquals("A01A", term.getCode());
	assertEquals("STOMATOLOGICAL PREPARATIONS", term.getLabel());
	assertEquals("A01", term.getParentCode());
	
	ATCTerm term1 = this.atc.getTerm("N01AB01");
	assertEquals("N01AB", term1.getParentCode());
	
	ATCTerm term2 = this.atc.getTerm("M09");
	assertEquals("M09", term2.getCode());
	assertEquals("OTHER DRUGS FOR DISORDERS OF THE MUSCULO-SKELETAL SYSTEM", term2.getLabel());
	assertEquals("M", term2.getParentCode());
    }


}
