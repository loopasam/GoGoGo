/**
 * 
 */
package analysis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import classification.ATC;
import drugbank.Drug;
import drugbank.DrugBank;

/**
 * @author Samuel Croset
 *
 */
public class ATCContent {

    private DrugBank drugBank;
    private ATC atc;

    public void setDrugBank(DrugBank drugBank) {
	this.drugBank = drugBank;
    }
    public DrugBank getDrugBank() {
	return drugBank;
    }
    public void setAtc(ATC atc) {
	this.atc = atc;
    }
    public ATC getAtc() {
	return atc;
    }

    public ATCContent() throws FileNotFoundException, IOException, ClassNotFoundException {
	this.setDrugBank(new DrugBank("data/drugbank.ser"));
	this.setAtc(new ATC("data/atc.ser"));
    }

    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {

	ATCContent data = new ATCContent();
	
	ArrayList<Drug> actDrugs = new ArrayList<Drug>();
	
	for (Drug drug : data.getDrugBank().getDrugs()) {
	    if(drug.getAtcCodes().size() > 0){
		actDrugs.add(drug);
	    }
	}
	System.out.println(actDrugs.size());
    }

}
