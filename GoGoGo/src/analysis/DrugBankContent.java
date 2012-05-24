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

	analysis.GroupsDistribution();

	//	analysis.TypesDistributionForNonExperimentalDrugs();


    }

    private void TypesDistributionForNonExperimentalDrugs() {
	HashMap<String, Integer> typesDistribution = new HashMap<String, Integer>();
	for (Drug drug : this.getDrugBank().getDrugs()) {

	    if(drug.isExperimental() == false){
		if(typesDistribution.get(drug.getType()) == null){
		    typesDistribution.put(drug.getType(), 1);
		}else{
		    typesDistribution.put(drug.getType(), typesDistribution.get(drug.getType()) + 1);
		}
	    } 
	}
	for (String type : typesDistribution.keySet()) {
	    System.out.println(type + ": " + typesDistribution.get(type));
	}
    }

    private void GroupsDistribution() {
	Distribution<String> distribution = new Distribution<String>();
	for (Drug drug : this.getDrugBank().getDrugs()) {


	    distribution.add(drug.getGroups().toString());

	}
	distribution.printReport();
    }

}
