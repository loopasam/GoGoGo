/**
 * 
 */
package drugbank;

import java.io.Serializable;

/**
 * @author Samuel Croset
 *
 */
public class Partner implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -5913113416467404250L;
    private String uniprotIdentifer;
    private int id;
    private String name;


    public String getUniprotIdentifer() {
	return uniprotIdentifer;
    }
    public void setUniprotIdentifer(String uniprotIdentifer) {
	this.uniprotIdentifer = uniprotIdentifer;
    }
    public int getId() {
	return id;
    }
    public void setId(int id) {
	this.id = id;
    }
    public String getName() {
	return name;
    }
    public void setName(String name) {
	this.name = name;
    }    

}
