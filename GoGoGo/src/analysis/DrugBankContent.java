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
    private GoGoGoDataset data;

    public void setData(GoGoGoDataset data) {
	this.data = data;
    }

    public GoGoGoDataset getData() {
	return data;
    }

    public void setDrugBank(DrugBank drugBank) {
	this.drugBank = drugBank;
    }

    public DrugBank getDrugBank() {
	return drugBank;
    }

    public DrugBankContent() throws FileNotFoundException, IOException, ClassNotFoundException {
	this.setData(new GoGoGoDataset("data/dataset-filtered.ser"));
	this.setDrugBank(this.getData().getDrugbank());
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {

	DrugBankContent analysis = new DrugBankContent();

	//	analysis.GroupsDistribution();
	//	analysis.TypesDistributionForNonExperimentalDrugs();
	//	analysis.targetTypesDistributionFor("biotech");
	//	analysis.targetTypesDistributionFor("small molecule");
	//	analysis.targetTypesNonProteinDistribution("biotech");
	//	analysis.targetTypesNonProteinDistribution("small molecule");
	//	analysis.NonExperimentalDrugsWithAnnotatedPartners();
	//	analysis.goldPartners();
	//	analysis.distributionOrganisms();
	analysis.distributionOrganismClasses();
    }


    private void distributionOrganismClasses() {
	Distribution<String> distribution = new Distribution<String>();
	for (Drug drug : this.getData().getClassifiableDrugs()) {
	    for (Partner partner : this.getDrugBank().getPartners(drug.getId())) {
		if(partner.getSpecies().getCategory() != null){
		    distribution.add(partner.getSpecies().getCategory());
		}else{
		    distribution.add("unknown");
		}
	    }
	}
	distribution.printReport();
    }

    private void distributionOrganisms() {
	Distribution<String> distribution = new Distribution<String>();
	for (Drug drug : this.getData().getClassifiableDrugs()) {
	    for (Partner partner : this.getDrugBank().getPartners(drug.getId())) {
		if(partner.getSpecies().getName() != null){
		    distribution.add(partner.getSpecies().getName());
		}else{
		    distribution.add("unknown");
		}
	    }
	}
	distribution.printReport();
    }

    private void goldPartners() {
	ArrayList<Drug> classifiableDrugs = this.getData().getClassifiableDrugs();
	System.out.println(classifiableDrugs.size());
    }

    private void NonExperimentalDrugsWithAnnotatedPartners() {

	ArrayList<Drug> relevantDrugs = new ArrayList<Drug>();
	ArrayList<Drug> nonAnnotatedDrugs = new ArrayList<Drug>();

	for (Drug drug : this.getDrugBank().getNonExperimentalDrugs()) {
	    boolean hasAnAnnotationAtLeast = false;
	    for (TargetRelation relation : drug.getTargetRelations()) {
		int partnerId = relation.getPartnerId();
		Partner partner = this.getDrugBank().getPartner(partnerId);
		if(partner.getNonIEAAnnotations() != null && partner.getNonIEAAnnotations().size() > 0){
		    hasAnAnnotationAtLeast = true;
		    if(!relevantDrugs.contains(drug)){
			relevantDrugs.add(drug);
		    }
		}
	    }

	    if(hasAnAnnotationAtLeast == false){
		nonAnnotatedDrugs.add(drug);
	    }

	}

	System.out.println("Number of relevant: " + relevantDrugs.size() 
		+ " Total: " + this.getDrugBank().getNonExperimentalDrugs().size()
		+ " Non-Annotated: " + nonAnnotatedDrugs.size()	
	);

	for (Drug drug : nonAnnotatedDrugs) {
	    System.out.println(drug.getId());
	}
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
