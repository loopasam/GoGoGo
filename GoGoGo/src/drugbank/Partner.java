/**
 * 
 */
package drugbank;

import goa.GoAnnotation;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Samuel Croset
 *
 */
public class Partner implements Serializable {

    private static final long serialVersionUID = -5913113416467404250L;
    private String uniprotIdentifer;
    private int id;
    private String name;
    private ArrayList<GoAnnotation> annotations;
    private Species species;

    public void setSpecies(Species species) {
	this.species = species;
    }
    public Species getSpecies() {
	return species;
    }
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
    public void setAnnotations(ArrayList<GoAnnotation> annotations) {
	this.annotations = annotations;
    }
    public ArrayList<GoAnnotation> getAnnotations() {
	return annotations;
    }    

}
