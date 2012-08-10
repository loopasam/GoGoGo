/**
 * 
 */
package analysis;

import java.io.IOException;
import java.util.ArrayList;

import network.Attribute;
import network.Edge;
import network.Network;
import network.Node;
import network.Relation;
import network.StringAttributeFactory;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import play.modules.elephant.Brain;
import play.modules.elephant.entity_views.EntityView;
import play.modules.elephant.entity_views.Matryoshkas;
import play.modules.elephant.entity_views.ParentMatryoshkas;
import play.modules.elephant.errors.OWLEntityQueryException;

/**
 * @author Samuel Croset
 *
 */
public class GraphFullClassification {






    public static void main(String[] args) throws OWLOntologyCreationException, OWLEntityQueryException {

	Brain.learn("data/ftc/ftc-2.owl");
	OWLClass top = Brain.getOWLClass("Agent");
	GraphView view = new GraphView(top);
	Matryoshkas matryoshkas = new ParentMatryoshkas((OWLClass) top, 3);

	//Create a new network (which will be saved later on).
	Network network = new Network();
	//Create an attribute factory, producing attributes called "name" of type String.
	//At the moment there is only String and Integer factories.
	StringAttributeFactory nameFactory = network.getNewStringAttributeFactory("name");

	//Create a second factory for the edges attributes.
	StringAttributeFactory relationStrengthFactory = network.getNewStringAttributeFactory("relationStrength");

	//Important! Specifies which attribute should be used as identifier.
	//The attributes should be unique for each node.
	network.setIdentifierNodes("name");
	network.setIdentifierEdges("relationStrength");

	hierarchyToGraph(top, matryoshkas, 0, network, nameFactory, relationStrengthFactory);


	//Save the network. Nodes with same identifier will be merged together automatically.
	//The main network has a ".sif" suffix. The node attributes files have ".na" and the edges ".ea"
	//You can import them in Cytoscape and have fun exploring your graph!
	try {
	    network.saveAll("data/graph", "ftc");
	} catch (IOException e) {
	    e.printStackTrace();
	}

	System.out.println("DONE");

    }


    protected static void hierarchyToGraph(OWLClass owlClass, Matryoshkas matryoshkas, int level, Network network, StringAttributeFactory nameFactory, StringAttributeFactory relationStrengthFactory){

	//Create a node
	Node parent = new Node();
	//Call the "name" factory for a new attribute with the value "sildenafil"
	Attribute parentName = nameFactory.getNewAttribute(Brain.getShortForm(owlClass));
	//The attribute is added to the node.
	parent.addAttribute(parentName);

	ArrayList<OWLClass> directSubClasses = matryoshkas.getChildrenJustUnder(owlClass);
	for (OWLClass directSubClass : directSubClasses) {

	    if(!Brain.getShortForm(directSubClass).equals("Nothing")){
		Node child = new Node();
		Attribute childName = nameFactory.getNewAttribute(Brain.getShortForm(directSubClass));
		child.addAttribute(childName);

		Edge is_a = new Edge();
		Attribute is_aLabel = relationStrengthFactory.getNewAttribute("is_a");
		is_a.addAttribute(is_aLabel);
		Relation relation = new Relation(child, is_a, parent);
		network.addRelation(relation);

		hierarchyToGraph(directSubClass, matryoshkas, level +1, network, nameFactory, relationStrengthFactory);
	    }

	}
    }


}
