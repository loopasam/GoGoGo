/**
 * 
 */
package gene_ontology;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Samuel Croset
 *
 */
public class GoTerm implements Serializable {

    private static final long serialVersionUID = 3936922396836647265L;
    private String name;
    private String id;
    private String namespace;
    private ArrayList<GoRelation> relations;


    public String getName() {
	return name;
    }
    public void setName(String name) {
	this.name = name;
    }
    public String getId() {
	return id;
    }
    public void setId(String id) {
	this.id = id;
    }
    public String getNamespace() {
	return namespace;
    }
    public void setNamespace(String namespace) {
	this.namespace = namespace;
    }
    public ArrayList<GoRelation> getRelations() {
	return relations;
    }
    public void setRelations(ArrayList<GoRelation> relations) {
	this.relations = relations;
    }    

}
