/**
 * 
 */
package analysis;

import java.io.IOException;

import drugbank.DrugBank;
import drugbank.Partner;

import goa.GoAnnotation;
import gogogo.GoGoGoDataset;

/**
 * @author Samuel Croset
 *
 */
public class DrugBankContent {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

	GoGoGoDataset data = new GoGoGoDataset("data/dataset-filtered.ser");
	DrugBank drugBank = data.getDrugbank();


    }

}
