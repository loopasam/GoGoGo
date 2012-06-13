/**
 * 
 */
package analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import gene_ontology.GeneOntology;
import gene_ontology.GoRelation;
import gene_ontology.GoTerm;
import gogogo.GoGoGoDataset;

/**
 * @author Samuel Croset
 *
 */
public class CorruptedGORegulations {

    private GeneOntology go;

    public void setGo(GeneOntology go) {
	this.go = go;
    }
    public GeneOntology getGo() {
	return go;
    }

    public CorruptedGORegulations() throws IOException, ClassNotFoundException {
	GoGoGoDataset data = new GoGoGoDataset("data/integration/dataset-filtered.ser");
	this.setGo(data.getGo());
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {

	CorruptedGORegulations analysis = new CorruptedGORegulations();
	analysis.getCorruptedRelations();

    }

    private void getCorruptedRelations() throws IOException {

	FileWriter fstream = new FileWriter("data/corrupted-relations.txt");
	BufferedWriter writer = new BufferedWriter(fstream);

	int counter = 0;

	for (GoTerm term : this.getGo().getTerms()) {
	    counter++;
	    System.out.println(counter + "/" + this.getGo().getTerms().size());
	    for (GoRelation relation : term.getRelations()) {
		if(relation.getType().equals("positively_regulates")){
		    boolean hasRelevantParent = false;
		    for (GoRelation relationToCheck : term.getRelations()) {
			if(relationToCheck.getType().equals("is_a") && !hasRelevantParent){
			    ArrayList<GoTerm> parents = this.getGo().getParentsOfTerm(this.getGo().getTerm(relationToCheck.getTarget()));
			    for (GoTerm parent : parents) {
				if(parent.getId().equals("GO:0048518") || parent.getId().equals("GO:0065009")){
				    hasRelevantParent = true;
				}
			    }
			}
		    }
		    if(!hasRelevantParent){
			System.err.println("No good parent: " + term.getName());
			writer.append(term.getName() + "\n");
		    }
		}
		
		if(relation.getType().equals("negatively_regulates")){
		    boolean hasRelevantParent = false;
		    for (GoRelation relationToCheck : term.getRelations()) {
			if(relationToCheck.getType().equals("is_a") && !hasRelevantParent){
			    ArrayList<GoTerm> parents = this.getGo().getParentsOfTerm(this.getGo().getTerm(relationToCheck.getTarget()));
			    for (GoTerm parent : parents) {
				if(parent.getId().equals("GO:0048519") || parent.getId().equals("GO:0044092")){
				    hasRelevantParent = true;
				}
			    }
			}
		    }
		    if(!hasRelevantParent){
			System.err.println("No good parent: " + term.getName());
			writer.append(term.getName() + "\n");
		    }
		}
		
	    }
	}
	writer.close();
    }

}
