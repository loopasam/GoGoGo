/**
 * 
 */
package pharmgkb;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Samuel Croset
 *
 */
public class PharmGKB implements Serializable {
    
    private static final long serialVersionUID = -3676265570597270740L;
    private ArrayList<Drug> drugs;

    public void setDrugs(ArrayList<Drug> drugs) {
	this.drugs = drugs;
    }

    public ArrayList<Drug> getDrugs() {
	return drugs;
    }

}
