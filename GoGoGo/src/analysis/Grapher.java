/**
 * 
 */
package analysis;

import java.io.IOException;

import drugbank.Partner;

import network.Attribute;
import network.Edge;
import network.Network;
import network.Node;
import network.Relation;
import network.StringAttributeFactory;

import gene_ontology.GoRelation;
import gene_ontology.GoTerm;
import goa.GoAnnotation;
import gogogo.GoGoGoDataset;

/**
 * @author Samuel Croset
 *
 */
public class Grapher {

    private Network network;
    private StringAttributeFactory label;
    private StringAttributeFactory edgeType;
    private GoGoGoDataset data;
    private StringAttributeFactory nodeType;
    private StringAttributeFactory nodeName;


    public void setLabel(StringAttributeFactory label) {
	this.label = label;
    }

    public StringAttributeFactory getLabel() {
	return label;
    }

    public void setNodeName(StringAttributeFactory nodeName) {
	this.nodeName = nodeName;
    }

    public StringAttributeFactory getNodeName() {
	return nodeName;
    }

    public void setNodeType(StringAttributeFactory nodeType) {
	this.nodeType = nodeType;
    }

    public StringAttributeFactory getNodeType() {
	return nodeType;
    }

    public Network getNetwork() {
	return network;
    }

    public void setNetwork(Network network) {
	this.network = network;
    }

    public StringAttributeFactory getEdgeType() {
	return edgeType;
    }

    public void setEdgeType(StringAttributeFactory edgeType) {
	this.edgeType = edgeType;
    }

    public GoGoGoDataset getData() {
	return data;
    }

    public void setData(GoGoGoDataset data) {
	this.data = data;
    }



    public Grapher() throws IOException, ClassNotFoundException {

	this.setData(new GoGoGoDataset("data/dataset.ser"));
	this.setNetwork(new Network());
	this.setLabel(this.getNetwork().getNewStringAttributeFactory("label"));
	this.setNodeName(this.getNetwork().getNewStringAttributeFactory("nodeName"));
	this.setNodeType(this.getNetwork().getNewStringAttributeFactory("nodeType"));
	this.setEdgeType(this.getNetwork().getNewStringAttributeFactory("relation"));
	this.getNetwork().setIdentifierEdges("relation");
	this.getNetwork().setIdentifierNodes("nodeName");

    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {

	System.out.println("Start...");

	Grapher grapher = new Grapher();
	Attribute protNodeType = grapher.getNodeType().getNewAttribute("partner");
	Attribute annotNodeType = grapher.getNodeType().getNewAttribute("term");
	Attribute edgeType = grapher.getEdgeType().getNewAttribute("annotated_with");


	int counter = 0;
	int max = 10;

	for (Partner partner : grapher.getData().getDrugbank().getPartners()) {

	    if(partner.getUniprotIdentifer() != null && counter < max){
		counter++;

		Node protNode = new Node();
		Attribute nodeName = grapher.getNodeName().getNewAttribute(partner.getUniprotIdentifer());

		Attribute nodeAccession = grapher.getLabel().getNewAttribute(partner.getUniprotIdentifer());

		protNode.addAttribute(nodeName);
		protNode.addAttribute(protNodeType);
		protNode.addAttribute(nodeAccession);


		for (GoAnnotation annot : partner.getAnnotations()) {

		    //TODO plotter que les bioporcess
		    Node annotNode = new Node();
		    Attribute annotNodeName = grapher.getNodeName().getNewAttribute(annot.getGoId());


		    GoTerm term =  grapher.getData().getGo().getTerm(annot.getGoId());

		    Attribute annotName = grapher.getLabel().getNewAttribute(term.getName());
		    Attribute annotNodeNameSpace = grapher.getNodeType().getNewAttribute(term.getNamespace());
		    
		    annotNode.addAttribute(annotName);
		    annotNode.addAttribute(annotNodeName);
		    annotNode.addAttribute(annotNodeNameSpace);

		    Edge edge = new Edge();
		    edge.addAttribute(edgeType);
		    Relation relation = new Relation(protNode, edge, annotNode);
		    grapher.getNetwork().addRelation(relation);
		}

	    }
	}



	System.out.println("saving...");

	grapher.getNetwork().saveAll("data/graph", "prot_annot");
	System.out.println("done...");
    }


}
