/**
 * 
 */
package analysis;

import java.util.HashMap;

/**
 * @author Samuel Croset
 *
 */
public class Distribution<T> {
    
    private HashMap<T, Integer> distributionMap;
    
    
    public void setDistributionMap(HashMap<T, Integer> distributionMap) {
	this.distributionMap = distributionMap;
    }

    public HashMap<T, Integer> getDistributionMap() {
	return distributionMap;
    }

    public Distribution() {
	this.setDistributionMap(new HashMap<T, Integer>());
    }

    public void add(T category) {
	if(this.getDistributionMap().containsKey(category)){
	    Integer oldValue = this.getDistributionMap().get(category);
	    this.getDistributionMap().put(category, oldValue + 1);
	}else{
	    this.getDistributionMap().put(category, 1);
	}
    }


    public void printReport() {
	for (T category : this.getDistributionMap().keySet()) {
	    System.out.println(category.toString() + ": " + this.getDistributionMap().get(category));
	}
    }

}
