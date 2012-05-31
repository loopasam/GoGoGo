/**
 * 
 */
package classification;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Samuel Croset
 *
 */
public class ATC implements Serializable {
    
    private static final long serialVersionUID = -7881747449067909230L;
    private ArrayList<ATCTerm> terms;
    
    public void setTerms(ArrayList<ATCTerm> terms) {
	this.terms = terms;
    }
    public ArrayList<ATCTerm> getTerms() {
	return terms;
    }
    
    public ATC() {
	this.setTerms(new ArrayList<ATCTerm>());
    }

    public void addTerm(ATCTerm term) {
	this.getTerms().add(term);
    }

    public ATCTerm getTerm(String termCode) {
	for (ATCTerm atcTerm : this.getTerms()) {
	    if(atcTerm.getCode().equals(termCode)){
		return atcTerm;
	    }
	}
	return null;
    }
    

}
