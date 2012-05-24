/**
 * 
 */
package analysis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPopupMenu.Separator;

import drugbank.Drug;
import drugbank.DrugBank;
import drugbank.Partner;
import drugbank.Species;
import drugbank.TargetRelation;

import goa.GoAnnotation;
import gogogo.GoGoGoDataset;

/**
 * @author Samuel Croset
 *
 */
public class DrugBankContent {

    private DrugBank drugBank;

    public void setDrugBank(DrugBank drugBank) {
	this.drugBank = drugBank;
    }

    public DrugBank getDrugBank() {
	return drugBank;
    }

    public DrugBankContent() throws FileNotFoundException, IOException, ClassNotFoundException {
	this.setDrugBank(new DrugBank("data/drugbank.ser"));
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {

	DrugBankContent analysis = new DrugBankContent();

//	analysis.GroupsDistribution();
//	analysis.TypesDistributionForNonExperimentalDrugs();
//	analysis.targetTypesDistributionFor("biotech");
//	analysis.targetTypesDistributionFor("small molecule");
//	analysis.targetTypesNonProteinDistribution("biotech");
//	analysis.targetTypesNonProteinDistribution("small molecule");

    }
    

    private void targetTypesNonProteinDistribution(String type) {
	Distribution<String> distribution = new Distribution<String>();
	for (Drug drug : this.getDrugBank().getDrugs()) {
	    if(drug.getType().equals(type) && drug.isExperimental() == false){
		for (Partner partner : this.getDrugBank().getPartners(drug.getId())) {
		    if(partner.getUniprotIdentifer() == null){
			
			distribution.add(partner.getName());
			
		    }
		}
	    }
	}
	distribution.printReport();
    }

    private void targetTypesDistributionFor(String type) {
	Distribution<String> distribution = new Distribution<String>();
	for (Drug drug : this.getDrugBank().getDrugs()) {
	    if(drug.getType().equals(type) && drug.isExperimental() == false){
		for (Partner partner : this.getDrugBank().getPartners(drug.getId())) {
		    if(partner.getUniprotIdentifer() != null){
			distribution.add("gene-product");
		    }else{
			distribution.add("other");
		    }
		}
	    }
	}
	distribution.printReport();
    }


    private void TypesDistributionForNonExperimentalDrugs() {
	Distribution<String> distribution = new Distribution<String>();
	for (Drug drug : this.getDrugBank().getDrugs()) {
	    if(drug.isExperimental() == false){
			distribution.add(drug.getType());
	    } 
	}
	distribution.printReport();
    }

    private void GroupsDistribution() {
	Distribution<String> distribution = new Distribution<String>();
	for (Drug drug : this.getDrugBank().getDrugs()) {
	    distribution.add(drug.getGroups().toString());
	}
	distribution.printReport();
    }

}
