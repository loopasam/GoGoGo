/**
 * 
 */
package analysis;

import java.util.Comparator;

import org.semanticweb.owlapi.model.OWLEntity;

/**
 * @author Samuel Croset
 *
 */
public class OWLCompare implements Comparator<OWLEntity> {
    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(OWLEntity e1, OWLEntity e2) {
	return e1.getIRI().compareTo(e2.getIRI());
    }
}