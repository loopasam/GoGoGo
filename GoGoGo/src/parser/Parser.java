/**
 * 
 */
package parser;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Samuel Croset
 *
 */
public abstract class Parser {
    
    private String pathToFile;
    private String pathOut;
    
    public Parser(String pathIn, String pathOut) {
	this.setPathToFile(pathIn);
	this.setPathOut(pathOut);
    }

    public void setPathOut(String pathOut) {
	this.pathOut = pathOut;
    }

    public String getPathOut() {
	return pathOut;
    }

    public void setPathToFile(String pathToFile) {
	this.pathToFile = pathToFile;
    }

    public String getPathToFile() {
	return pathToFile;
    }
    
    public abstract void parse() throws FileNotFoundException, IOException;
    
    public abstract void save() throws FileNotFoundException, IOException;
    
}
