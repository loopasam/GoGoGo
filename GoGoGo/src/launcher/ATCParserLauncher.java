/**
 * 
 */
package launcher;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import parser.ATCParser;
import parser.Parser;

/**
 * @author Samuel Croset
 *
 */
public class ATCParserLauncher {

    public static void main(String[] args) throws FileNotFoundException, IOException, OWLOntologyCreationException, OWLOntologyStorageException {
	Parser atcParser = new ATCParser("data/ASCII_Index_med_DDD.asc", "data/atc.ser");
	atcParser.parse();
	atcParser.save();
	((ATCParser) atcParser).convertInOwl("file:/home/samuel/git/GoGoGo/GoGoGo/data/atc.owl");

    }
}
