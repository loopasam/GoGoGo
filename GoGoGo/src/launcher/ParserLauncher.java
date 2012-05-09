/**
 * 
 */
package launcher;

import gogogo.GoGoGoDataset;

import java.io.FileNotFoundException;
import java.io.IOException;

import parser.DrugBankParser;
import parser.GeneOntologyParser;
import parser.GoaConnector;
import parser.Parser;

/**
 * @author Samuel Croset
 *
 */
public class ParserLauncher {

    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
	System.out.println("start drug bank parsing...");
	Parser drugBankParser = new DrugBankParser("data/drugbank.xml", "data/drugbank.ser");
	drugBankParser.parse();
	drugBankParser.save();

	System.out.println("start go parsing...");
	Parser goParser = new GeneOntologyParser("data/gene_ontology_ext.obo", "data/go.ser");
	goParser.parse();
	goParser.save();
	
	System.out.println("Start connector...");
	GoaConnector connector = new GoaConnector("data/drugbank.ser", "data/go.ser");
	connector.fillPartnersWithGoTerms();
	connector.save("data/dataset.ser");
	
	GoGoGoDataset dataset = new GoGoGoDataset("data/dataset.ser");
	
	//TODO continue here and test if the stuff is correctly loaded
	
	
	//TODO GOA connector --> webservice completing the drugbank objects with GO object
	//Then saving everything from that as GOggogodataset object
	//end of parser launcher
	// then deal with gogog dataset object


    }



}
