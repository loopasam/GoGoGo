/**
 * 
 */
package analysis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import classification.ATC;
import classification.ATCTerm;

/**
 * @author Samuel Croset
 *
 */
public class ATC_DB_Mapping {

    private ATC atc;

    public void setAtc(ATC atc) {
	this.atc = atc;
    }

    public ATC getAtc() {
	return atc;
    }

    public ATC_DB_Mapping() throws FileNotFoundException, IOException, ClassNotFoundException {
	this.setAtc(new ATC("data/atc/atc.ser"));
    }

    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {

	ATC_DB_Mapping data = new ATC_DB_Mapping();
	data.analyzeMappings();

    }


    private void analyzeMappings() {

	ArrayList<Mapping> nativeMappings = new ArrayList<ATC_DB_Mapping.Mapping>();
	for (ATCTerm term : this.getAtc().getTherapeutics()) {
	    for (String dbRef : term.getDrugBankReferences()) {
		Mapping mapping = new Mapping(term.getCode(), dbRef);
		nativeMappings.add(mapping);
	    }
	}
	System.out.println("Number of native mappings: " + nativeMappings.size());
	ArrayList<Mapping> tmMappings = new ArrayList<ATC_DB_Mapping.Mapping>();
	for (ATCTerm term : this.getAtc().getTherapeutics()) {
	    for (String dbRef : term.getTextMinedDrugBankReferences()) {
		Mapping mapping = new Mapping(term.getCode(), dbRef);
		tmMappings.add(mapping);
	    }
	}
	System.out.println("Number of TM mappings: " + tmMappings.size());
	ArrayList<Mapping> overlappingMappings = new ArrayList<ATC_DB_Mapping.Mapping>();
	for (Mapping tmMapping : tmMappings) {
	    String dbid = tmMapping.getDrugbankId();
	    String atcCategory = tmMapping.getAtcCategory();
	    for (Mapping nativeMapping : nativeMappings) {
		String nativeDb = nativeMapping.getDrugbankId();
		String nativeAtc = nativeMapping.getAtcCategory();
		if(nativeDb.equals(dbid) && nativeAtc.equals(atcCategory)){
		    overlappingMappings.add(tmMapping);
		}
	    }
	}

	System.out.println("Overlapping mappings: " + overlappingMappings.size());

	int totalMapping = 0;
	for (ATCTerm term : this.getAtc().getTherapeutics()) {
	    totalMapping += term.getAllDrugBankReferences().size();
	}
	System.out.println("Total mapping: " + totalMapping);

	int numberOfAtcClassMapped = 0;
	int numberOfAtcClassNotMapped = 0;
	
	for (ATCTerm term : this.getAtc().getTherapeutics()) {
	    if(term.getAllDrugBankReferences().size() > 0){
		numberOfAtcClassMapped++;
	    }else{
		numberOfAtcClassNotMapped++;
	    }
	}
	int totalTherapeutics = numberOfAtcClassNotMapped + numberOfAtcClassMapped;
	System.out.println("Number of ATC therapeutic classes mapped: " + numberOfAtcClassMapped + "/" + totalTherapeutics);
	
	

    }

    public class Mapping{

	private String atcCategory;
	private String drugbankId;

	public Mapping(String atcCategory, String drugbankId) {
	    this.setAtcCategory(atcCategory);
	    this.setDrugbankId(drugbankId);
	}

	public void setDrugbankId(String drugbankId) {
	    this.drugbankId = drugbankId;
	}
	public String getDrugbankId() {
	    return drugbankId;
	}
	public void setAtcCategory(String atcCategory) {
	    this.atcCategory = atcCategory;
	}
	public String getAtcCategory() {
	    return atcCategory;
	}

    }

}
