/**
 * 
 */
package parser;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import pharmgkb.PharmGKB;

/**
 * @author Samuel Croset
 *
 */
public class PharmGKBParser extends Parser {

    private PharmGKB pharmgkb;

    public void setPharmgkb(PharmGKB pharmgkb) {
	this.pharmgkb = pharmgkb;
    }

    public PharmGKB getPharmgkb() {
	return pharmgkb;
    }


    public PharmGKBParser(String pathIn, String pathOut) {
	super(pathIn, pathOut);
	this.setPharmgkb(new PharmGKB());
    }

    /* (non-Javadoc)
     * @see parser.Parser#parse()
     */
    @Override
    public void parse() throws FileNotFoundException, IOException {
	FileInputStream fstream = null;
	fstream = new FileInputStream(this.getPathToFile());
	DataInputStream in = new DataInputStream(fstream);
	BufferedReader br = new BufferedReader(new InputStreamReader(in));
	String line =null;
	while ((line = br.readLine()) != null)   {
	    
	    String[] splittedLine = line.split("\t");

	    //TODO implemeter le parsage
	    System.out.println(splittedLine[8]);
	    

	}
    }

    /* (non-Javadoc)
     * @see parser.Parser#save()
     */
    @Override
    public void save() throws FileNotFoundException, IOException {	
	ObjectOutput out = null;
	out = new ObjectOutputStream(new FileOutputStream(this.getPathOut()));
	out.writeObject(this.getPharmgkb());
	out.close();
    }

}
