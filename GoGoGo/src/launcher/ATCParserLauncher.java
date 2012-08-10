/**
 * 
 */
package launcher;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import parser.ATCParser;

/**
 * @author Samuel Croset
 *
 */
public class ATCParserLauncher {

    public static void main(String[] args) throws FileNotFoundException, IOException, OWLOntologyCreationException, OWLOntologyStorageException, ClassNotFoundException {
	ATCParser atcParser = new ATCParser("data/atc/ASCII_Index_med_DDD.asc", "data/atc/atc.ser");
	System.out.println("Parsing...");
	atcParser.parse();
	System.out.println("Adding DB info...");
	atcParser.addDrugBankInfo("data/drugbank/drugbank.ser", false);
	atcParser.save();
	System.out.println("Converting in OWL and saving...");
	atcParser.convertInOwl("file:/home/samuel/git/GoGoGo/GoGoGo/data/atc/atc.owl");
	System.out.println("Done!");
    }
}
