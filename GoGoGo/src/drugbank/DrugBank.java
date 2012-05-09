/**
 * 
 */
package drugbank;

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
public class DrugBank implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 26695180702137819L;
    private ArrayList<Drug> drugs;
    private ArrayList<Partner> partners;

    /**
     * @param pathToSer
     * @throws IOException 
     * @throws FileNotFoundException 
     * @throws ClassNotFoundException 
     */
    public DrugBank(String pathToSer) throws FileNotFoundException, IOException, ClassNotFoundException {
	File file = new File(pathToSer);
	ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
	DrugBank drugank = (DrugBank) in.readObject();
	this.setDrugs(drugank.getDrugs());
	this.setPartners(drugank.getPartners());
	in.close();
    }

    /**
     * 
     */
    public DrugBank() {

    }

    public void setPartners(ArrayList<Partner> partners) {
	this.partners = partners;
    }

    public ArrayList<Partner> getPartners() {
	return partners;
    }

    public void setDrugs(ArrayList<Drug> drugs) {
	this.drugs = drugs;
    }

    public ArrayList<Drug> getDrugs() {
	return drugs;
    }

    /**
     * @param drugBankId
     * @return 
     */
    public Drug getDrug(String drugBankId) {
	for (Drug drug : this.getDrugs()) {
	    if(drug.getId().equals(drugBankId)){
		return drug;
	    }
	}
	return null;
    }

    /**
     * @param partnerId
     * @return
     */
    public Partner getPartner(int partnerId) {
	for (Partner partner : this.getPartners()) {
	    if(partner.getId() == partnerId){
		return partner;
	    }
	}
	return null;
    }

    /**
     * @param drugId
     * @return
     */
    public ArrayList<Partner> getPartners(String drugId) {

	ArrayList<Partner> partners = new ArrayList<Partner>();
	Drug drug = this.getDrug(drugId);
	for (TargetRelation targetRelation : drug.getTargetRelations()) {
	    int partnerId = targetRelation.getPartnerId();
	    partners.add(this.getPartner(partnerId));
	}
	return partners;
    }

}
