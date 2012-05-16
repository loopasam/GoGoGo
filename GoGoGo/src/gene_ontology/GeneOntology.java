/**
 * 
 */
package gene_ontology;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;


/**
 * @author Samuel Croset
 *
 */
public class GeneOntology implements Serializable {

    private static final long serialVersionUID = 3306806425055121795L;
    private ArrayList<GoTerm> terms;

    public GeneOntology() {
	this.setTerms(new ArrayList<GoTerm>());
    }

    /**
     * @param string
     * @throws IOException 
     * @throws FileNotFoundException 
     * @throws ClassNotFoundException 
     */
    public GeneOntology(String pathToSer) throws FileNotFoundException, IOException, ClassNotFoundException {
	File file = new File(pathToSer);
	ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
	GeneOntology go = (GeneOntology) in.readObject();
	this.setTerms(go.getTerms());
	in.close();
    }

    public void setTerms(ArrayList<GoTerm> terms) {
	this.terms = terms;
    }

    public ArrayList<GoTerm> getTerms() {
	return terms;
    }

    /**
     * @param termId
     * @return
     */
    public GoTerm getTerm(String termId) {
	for (GoTerm term : this.getTerms()) {
	    if(term.getId().equals(termId)){
		return term;
	    }
	}
	return null;
    }

    public ArrayList<GoTerm> getParentsOfTerm(GoTerm termOfInterest) {

	ArrayList<GoTerm> parents = new ArrayList<GoTerm>();
	ArrayList<GoTerm> toVisit = new ArrayList<GoTerm>();

	parents.add(termOfInterest);
	toVisit.add(termOfInterest);

	while(toVisit.size() > 0){
	    GoTerm currentTerm = toVisit.remove(0);
	    for (GoRelation relation : currentTerm.getRelations()) {
		String target = relation.getTarget();
		parents.add(this.getTerm(target));
		toVisit.add(this.getTerm(target));
	    }
	}

	return parents;
    }

    /**
     * @return
     */
    public ArrayList<GoTerm> getBioProcesses() {

	ArrayList<GoTerm> goTerms = new ArrayList<GoTerm>();
	for (GoTerm term : this.getTerms()) {
	    if(term.getNamespace().equals("biological_process")){
		goTerms.add(term);
	    }
	}
	return goTerms;
    }

}
