/**
 * 
 */
package drugbank;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Samuel Croset
 *
 */
public class Drug implements Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1837611788051168494L;
    private String id;
    private String name;
    private ArrayList<String> groups;
    private ArrayList<TargetRelation> targetRelations;
    
    /**
     * 
     */
    public Drug() {
	this.setGroups(new ArrayList<String>());
	this.setTargetRelations(new ArrayList<TargetRelation>());
    }
    
    public void setTargetRelations(ArrayList<TargetRelation> targetRelations) {
	this.targetRelations = targetRelations;
    }
    public ArrayList<TargetRelation> getTargetRelations() {
	return targetRelations;
    }
    public void setId(String id) {
	this.id = id;
    }
    public String getId() {
	return id;
    }
    public void setName(String name) {
	this.name = name;
    }
    public String getName() {
	return name;
    }
    public void setGroups(ArrayList<String> groups) {
	this.groups = groups;
    }
    public ArrayList<String> getGroups() {
	return groups;
    }
    

}
