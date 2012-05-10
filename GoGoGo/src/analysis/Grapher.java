/**
 * 
 */
package analysis;

import java.io.IOException;

import network.Attribute;
import network.Network;
import network.Node;
import network.StringAttributeFactory;

import gene_ontology.GoTerm;
import gogogo.GoGoGoDataset;

/**
 * @author Samuel Croset
 *
 */
public class Grapher {


    public static void main(String[] args) throws IOException, ClassNotFoundException {

	GoGoGoDataset dataset = new GoGoGoDataset("data/dataset.ser");
	Network network = new Network();
	
	StringAttributeFactory nameFactory = network.getNewStringAttributeFactory("name");
	StringAttributeFactory nodeTypeFactory = network.getNewStringAttributeFactory("nodeType");
	StringAttributeFactory relationTypeFactory = network.getNewStringAttributeFactory("relationType");

	network.setIdentifierNodes("name");
	network.setIdentifierEdges("relationType");

	for (GoTerm term : dataset.getGo().getTerms()) {
	    Node childTerm = new Node();
	    Attribute childTermName = nameFactory.getNewAttribute(term.getName());
	    Attribute childTermType = nodeTypeFactory.getNewAttribute(term.getNamespace());
	    childTerm.addAttribute(childTermName);
	    childTerm.addAttribute(childTermType);
	}
	
	

	Edge edgeA = new Edge();
	Attribute edgeAStrength = relationStrengthFactory.getNewAttribute("weak action");
	edgeA.addAttribute(edgeAWeight);

	Relation relationA = new Relation(drugA, edgeA, diseaseA);
	network.addRelation(relationA);

	try {
	    //Save the network. Nodes with same identifier will be merged together automatically.
	    //The main network has a ".sif" suffix. The node attributes files have ".na" and the edges ".ea"
	    //You can import them in Cytoscape and have fun exploring your graph!
	    network.saveAll("dev_data", "demo");
	} catch (IOException e) {
	    e.printStackTrace();
	}



    }


}
