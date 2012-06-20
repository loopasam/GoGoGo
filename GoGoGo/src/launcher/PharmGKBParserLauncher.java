/**
 * 
 */
package launcher;

import java.io.FileNotFoundException;
import java.io.IOException;

import parser.PharmGKBParser;

/**
 * @author Samuel Croset
 *
 */
public class PharmGKBParserLauncher {

    public static void main(String[] args) throws FileNotFoundException, IOException {
	PharmGKBParser pharmgkbParser = new PharmGKBParser("data/pharmgkb/drugs.tsv", "data/pharmgkb/pharmgkb.ser");
	System.out.println("Parsing...");
	pharmgkbParser.parse();
	System.out.println("saving...");
	pharmgkbParser.save();
    }
}
