/**
 * 
 */
package gogogo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.semanticweb.owlapi.model.OWLObjectProperty;

import drugbank.Drug;
import drugbank.DrugBank;
import drugbank.Partner;
import drugbank.TargetRelation;
import gene_ontology.GeneOntology;

/**
 * @author Samuel Croset
 *
 */
public class GoGoGoDataset implements Serializable {

    private static final long serialVersionUID = -818679016385547446L;
    private GeneOntology go;
    private DrugBank drugbank;


    /**
     * @param string
     * @throws IOException 
     * @throws ClassNotFoundException 
     */
    public GoGoGoDataset(String pathToSer) throws IOException, ClassNotFoundException {
	File file = new File(pathToSer);
	ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
	GoGoGoDataset dataset = (GoGoGoDataset) in.readObject();
	this.setDrugbank(dataset.getDrugbank());
	this.setGo(dataset.getGo());
	in.close();
    }

    public GoGoGoDataset() {
    }

    public GeneOntology getGo() {
	return go;
    }
    public void setGo(GeneOntology go) {
	this.go = go;
    }
    public DrugBank getDrugbank() {
	return drugbank;
    }
    public void setDrugbank(DrugBank drugbank) {
	this.drugbank = drugbank;
    }

    /**
     * @param relationMapping 
     * @return The non-experimental drugs that have at least partner annotated with at least one (non-IEA) annotation.
     * @throws FileNotFoundException 
     */
    public ArrayList<Drug> getClassifiableDrugs(HashMap<String, OWLObjectProperty> relationMapping) throws FileNotFoundException {

	ArrayList<Drug> classifiableDrugs = new ArrayList<Drug>();
	//Retrieve the non-experimental drugs
	for (Drug drug : this.getDrugbank().getNonExperimentalDrugs()) {
	    //Iterates over the partners of the drug
	    for (TargetRelation relation : drug.getTargetRelations()) {
		Partner partner = this.getDrugbank().getPartner(relation.getPartnerId());
		//Check if the partner as some non-IEA annotations and not CC
		if(partner.getNonIEAAnnotationsNonCC() != null && partner.getNonIEAAnnotationsNonCC().size() > 0){
		    //Iterates over the actions linking the drug to the partner to see if the action is mapped to an OWL property (meaningfull)
		    for (String action : relation.getActions()) {
			if(relationMapping.get(action) != null){
			    //Check if the drug is already in the listy of classifable list, otherwise add it as it fullfills all the criterias
			    if(!classifiableDrugs.contains(drug)){
				classifiableDrugs.add(drug);
			    }
			}
		    }
		}
	    }
	}
	return classifiableDrugs;
    }


}
