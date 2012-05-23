/**
 * 
 */
package drugbank;

/**
 * @author Samuel Croset
 *
 */
public class Species {
    
    private String category;
    private String name;
    private int taxonId;
    
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getTaxonId() {
        return taxonId;
    }
    public void setTaxonId(int taxonId) {
        this.taxonId = taxonId;
    }

}
