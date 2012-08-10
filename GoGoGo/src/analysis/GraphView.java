/**
 * 
 */
package analysis;

import java.util.ArrayList;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

import play.modules.elephant.entity_views.EntityView;
import play.modules.elephant.entity_views.Matryoshkas;

/**
 * @author Samuel Croset
 *
 */
public class GraphView extends EntityView {

    /**
     * @param owlEntity
     */
    public GraphView(OWLEntity owlEntity) {
	super(owlEntity);
    }

    /* (non-Javadoc)
     * @see play.modules.elephant.entity_views.EntityView#renderHierarchyInHtml(org.semanticweb.owlapi.model.OWLClass, play.modules.elephant.entity_views.Matryoshkas, int)
     */
    @Override
    protected void renderHierarchyInHtml(OWLClass owlClass, Matryoshkas matryoshkas, int level) {
	EntityView view = new EntityView(owlClass);
	ArrayList<OWLClass> directSubClasses = matryoshkas.getChildrenJustUnder(owlClass);
	for (OWLClass directSubClass : directSubClasses) {
	    matryoshkas.addToHtml("");
	    renderHierarchyInHtml(directSubClass, matryoshkas, level +1);
	}
	matryoshkas.addToHtml("</div>\n");
    }

}
