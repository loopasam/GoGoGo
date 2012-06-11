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
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gene_ontology.GeneOntology;
import gene_ontology.GoRelation;
import gene_ontology.GoTerm;

/**
 * @author Samuel Croset
 *
 */
public class GeneOntologyParser extends Parser {

    private GeneOntology go;

    public void setGo(GeneOntology go) {
	this.go = go;
    }

    public GeneOntology getGo() {
	return go;
    }

    /**
     * @param pathIn
     * @param pathOut
     */
    public GeneOntologyParser(String pathIn, String pathOut) {
	super(pathIn, pathOut);
	this.setGo(new GeneOntology());
    }

    /* (non-Javadoc)
     * @see parser.Parser#parse()
     */
    @Override
    public void parse() throws IOException {
	FileInputStream fstream = null;
	fstream = new FileInputStream(this.getPathToFile());
	DataInputStream in = new DataInputStream(fstream);
	BufferedReader br = new BufferedReader(new InputStreamReader(in));
	String line =null;
	while ((line = br.readLine()) != null)   {
	    if(line.equals("[Term]")){
		String id = null;
		String name= null;
		String namespace = null;
		String def = null;
		ArrayList<GoRelation> relations = new ArrayList<GoRelation>();
		String isObsolete = "false";


		while(line != null && line.length() > 0){
		    if(line.startsWith("id:")){
			id = getStringFromPattern("id: (.*)", line);
		    }

		    if(line.startsWith("name:")){
			name = getStringFromPattern("name: (.*)", line);
		    }

		    if(line.startsWith("namespace:")){
			namespace = getStringFromPattern("namespace: (.*)", line);
		    }
		    
		    //TODO ici
		    
		    if(line.startsWith("def:")){
			def = getStringFromPattern("def: (.*)", line);
		    }

		    if(line.startsWith("is_a:")){
			String target = getStringFromPattern("is_a: (.*)\\s!\\s.*", line);
			GoRelation goRel = new GoRelation("is_a", target);
			relations.add(goRel);
		    }

		    if(line.startsWith("relationship:")){
			ArrayList<String> relation = getArrayFromPattern("relationship: (.*) (.*) ! .*", line);
			GoRelation newRel = new GoRelation(relation.get(0), relation.get(1));
			relations.add(newRel);
		    }

		    if(line.startsWith("is_obsolete")){
			isObsolete = getStringFromPattern("is_obsolete: (.*)", line);
		    }
		    line = br.readLine();
		}

		if(!isObsolete.equals("true")){
		    GoTerm newTerm = new GoTerm();
		    newTerm.setId(id);
		    newTerm.setName(name);
		    newTerm.setNamespace(namespace);
		    newTerm.setRelations(relations);
		    newTerm.setDefinition(def);
		    this.getGo().getTerms().add(newTerm);
		}
	    }
	}
    }

    /**
     * @param string
     * @param line
     * @return
     */
    private ArrayList<String> getArrayFromPattern(String patternString, String line) {

	ArrayList<String> array = new ArrayList<String>();
	Pattern pattern = Pattern.compile(patternString);
	Matcher matcher = pattern.matcher(line);
	if (matcher.find()){
	    array.add(matcher.group(1));
	    array.add(matcher.group(2));
	}

	return array;
    }

    /**
     * @param string
     * @param line
     * @return
     */
    private String getStringFromPattern(String patternString, String line) {
	Pattern pattern = Pattern.compile(patternString);
	Matcher matcher = pattern.matcher(line);
	if (matcher.find()){
	    return matcher.group(1);
	}
	return null;
    }

    /* (non-Javadoc)
     * @see parser.Parser#save()
     */
    @Override
    public void save() throws FileNotFoundException, IOException {
	ObjectOutput out = null;
	out = new ObjectOutputStream(new FileOutputStream(this.getPathOut()));
	out.writeObject(this.getGo());
	out.close();
    }

}
