/**
 * 
 */
package launcher;

import java.io.FileNotFoundException;
import java.io.IOException;

import parser.DrugBankParser;
import parser.GeneOntologyParser;
import parser.Parser;

/**
 * @author Samuel Croset
 *
 */
public class ParserLauncher {

    public static void main(String[] args) throws FileNotFoundException, IOException {
	Parser drugBankParser = new DrugBankParser("data/drugbank.xml", "data/drugbank.ser");
	drugBankParser.parse();
	drugBankParser.save();

	Parser goParser = new GeneOntologyParser("data/gene_ontology_ext.obo", "data/go.ser");
	goParser.parse();
	goParser.save();

	//TODO GOA connector --> webservice completing the drugbank objects with GO object
	//Then saving everything from that as GOggogodataset object
	//end of parser launcher
	// then deal with gogog dataset object


    }



}
