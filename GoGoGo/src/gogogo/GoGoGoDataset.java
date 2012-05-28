/**
 * 
 */
package gogogo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

import drugbank.Drug;
import drugbank.DrugBank;
import drugbank.Partner;
import drugbank.TargetRelation;
import gene_ontology.GeneOntology;
import gene_ontology.GoTerm;
import goa.GoAnnotation;

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
     * @return The non-experimental drugs that have a partner annotated with at least one (non-IEA) annotation.
     */
    public ArrayList<Drug> getClassifiableDrugs() {
	ArrayList<Drug> classifiableDrugs = new ArrayList<Drug>();
	for (Drug drug : this.getDrugbank().getNonExperimentalDrugs()) {
	    for (TargetRelation relation : drug.getTargetRelations()) {
		Partner partner = this.getDrugbank().getPartner(relation.getPartnerId());
		if(partner.getNonIEAAnnotationsNonCC() != null && partner.getNonIEAAnnotationsNonCC().size() > 0){
		    if(!classifiableDrugs.contains(drug)){
			classifiableDrugs.add(drug);
		    }
		}
	    }
	}
	return classifiableDrugs;
    }


}
