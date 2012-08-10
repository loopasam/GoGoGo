/**
 * 
 */
package analysis;

import java.io.IOException;
import java.util.List;

import network.Attribute;
import network.Edge;
import network.Network;
import network.Node;
import network.Relation;
import network.StringAttributeFactory;

import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import play.modules.elephant.errors.OWLEntityQueryException;

/**
 * Official class for the export from of the FTC to view inside Cytoscape
 * @author Samuel Croset
 *
 */
public class FTCforCytoscape {

    BrainNonStatic brain;
    String pathFtc;
    String pathExport;
    String fileNameTemplate;
    Network network;
    StringAttributeFactory nameFactory;
    StringAttributeFactory labelFactory;
    StringAttributeFactory nodeTypeFactory;

    StringAttributeFactory relationTypeFactory;
    int progress;
    int totalClasses;
    int maxLevel;

    public FTCforCytoscape(String pathFtc, String pathExport, String fileNameTemplate, int maxLevel) throws OWLOntologyCreationException {
	brain = new BrainNonStatic(pathFtc);
	this.pathExport = pathExport;
	this.fileNameTemplate = fileNameTemplate;
	network = new Network();
	nameFactory = network.getNewStringAttributeFactory("name");
	relationTypeFactory = network.getNewStringAttributeFactory("is_a");
	labelFactory = network.getNewStringAttributeFactory("label");
	nodeTypeFactory = network.getNewStringAttributeFactory("type");
	network.setIdentifierNodes("name");
	network.setIdentifierEdges("is_a");
	progress = 0;
	totalClasses = brain.getOntology().getClassesInSignature().size();
	this.maxLevel = maxLevel;
    }


    public static void main(String[] args) throws OWLOntologyCreationException, ParserException, OWLEntityQueryException, IOException {
	FTCforCytoscape converter = new FTCforCytoscape("data/ftc/ftc.min.out.owl", "data/graph", "ftc_debug", 2);
	OWLClass thing = converter.brain.getOWLClass("A0044267");
	converter.addRelations(thing, 0, converter.maxLevel);
	converter.network.saveAll(converter.pathExport, converter.fileNameTemplate);

    }


    /**
     * @param level 
     * @param maxLevel 
     * @param subClass
     * @throws ParserException 
     * @throws OWLEntityQueryException 
     */
    private void addRelations(OWLClass owlClass, int level, int maxLevel) throws ParserException, OWLEntityQueryException {

	if(level <= maxLevel){
	    progress++;
	    System.out.println(progress + "/" + totalClasses + " - " + level);
	    List<OWLClass> subClasses = brain.getSubClasses(owlClass, true);
	    String parentNodeType;
	    if(brain.isSubClass(owlClass, brain.getOWLClass("FTC:03"), false)){
		parentNodeType = "drug";
	    }else{
		parentNodeType = "category";
	    }

	    Node nodeParent = new Node();
	    Attribute typeParent = nodeTypeFactory.getNewAttribute(parentNodeType);
	    nodeParent.addAttribute(typeParent);

	    Attribute nameParent = nameFactory.getNewAttribute(brain.getShortForm(owlClass));
	    nodeParent.addAttribute(nameParent);
	    Attribute labelParent = labelFactory.getNewAttribute(brain.getLabel(owlClass));
	    nodeParent.addAttribute(labelParent);

	    for (OWLClass subClass : subClasses) {
		if(!brain.getShortForm(subClass).equals("Nothing")){
		    Node nodeChild = new Node();
		    Attribute nameChild = nameFactory.getNewAttribute(brain.getShortForm(subClass));
		    nodeChild.addAttribute(nameChild);
		    Attribute labelChild = labelFactory.getNewAttribute(brain.getLabel(subClass));
		    nodeChild.addAttribute(labelChild);
		    String childNodeType;
		    if(brain.isSubClass(subClass, brain.getOWLClass("FTC:03"), false)){
			childNodeType = "drug";
		    }else{
			childNodeType = "category";
		    }
		    Attribute typeChild = nodeTypeFactory.getNewAttribute(childNodeType);
		    nodeChild.addAttribute(typeChild);

		    Edge is_a = new Edge();
		    Attribute is_aLabel = relationTypeFactory.getNewAttribute("is_a");
		    is_a.addAttribute(is_aLabel);
		    Relation relation = new Relation(nodeChild, is_a, nodeParent);
		    network.addRelation(relation);
		    this.addRelations(subClass, level+1, maxLevel);
		}	
	    }	    
	}

    }

}
