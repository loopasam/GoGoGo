/**
 * 
 */
package analysis;

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
public class ComparisonRelations {

    private ArrayList<String> childs;
    private GeneOntology go;
    private GoGoGoDataset data;


    public ArrayList<String> getChilds() {
	return childs;
    }



    public void setChilds(ArrayList<String> childs) {
	this.childs = childs;
    }



    public GeneOntology getGo() {
	return go;
    }



    public void setGo(GeneOntology go) {
	this.go = go;
    }



    public GoGoGoDataset getData() {
	return data;
    }



    public void setData(GoGoGoDataset data) {
	this.data = data;
    }


    public ComparisonRelations() throws IOException, ClassNotFoundException {

	this.setChilds(new ArrayList<String>());
	this.setData(new GoGoGoDataset("data/dataset.ser"));
	this.setGo(this.getData().getGo());
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {

	ComparisonRelations comparer = new ComparisonRelations();

	String childsOfTerm = "GO:0006915";

	ArrayList<String> idToCheck = new ArrayList<String>();

	idToCheck.add(childsOfTerm);

	while(idToCheck.size() > 0){
	    
	    String currentId = idToCheck.remove(0);
	    
	    for (GoTerm term : comparer.getGo().getTerms()) {
		ArrayList<GoRelation> relations = term.getRelations();
		for (GoRelation relation : relations) {
		    if(relation.getTarget().equals(currentId)){
			comparer.getChilds().add(term.getId());
//			System.out.println("new child spotted: " + relation.getType() + " " + term.getName());
			idToCheck.add(term.getId());
		    }
		}
	    }
	}
	
	System.out.println(comparer.getChilds());
	System.out.println("Number of child: " + comparer.getChilds().size());
    }



}
