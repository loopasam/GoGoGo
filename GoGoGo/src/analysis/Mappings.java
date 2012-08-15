/**
 * 
 */
package analysis;

import java.util.ArrayList;

/**
 * @author Samuel Croset
 *
 */
public class Mappings {

    ArrayList<Mapping> mappings;

    public Mappings() {
	mappings = new ArrayList<Mapping>();
    }

    /**
     * @param mapping
     */
    public void addMapping(Mapping mapping) {
	mappings.add(mapping);
    }

}
