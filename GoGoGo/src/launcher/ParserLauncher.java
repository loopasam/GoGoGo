/**
 * 
 */
package launcher;


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
	Parser drugBankParser = new DrugBankParser("data/drugbank/drugbank.xml", "data/drugbank/drugbank.ser");
	drugBankParser.parse();
	drugBankParser.save();

	System.out.println("start go parsing...");
	GeneOntologyParser goParser = new GeneOntologyParser("data/go/gene_ontology_ext.obo", "data/go/go.ser");
	goParser.parse();
	//add a few relations that are missing within GO, manual curation ftw.
	goParser.normalize();
	goParser.save();
	
	System.out.println("Start connector...");
	GoaConnector connector = new GoaConnector("data/drugbank/drugbank.ser", "data/go/go.ser");
	connector.fillPartnersWithGoTerms();
	connector.save("data/integration/dataset-filtered.ser");
	
    }



}
