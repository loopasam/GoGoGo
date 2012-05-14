/**
 * 
 */
package analysis;

import java.io.IOException;


import drugbank.Drug;
import drugbank.Partner;
import drugbank.TargetRelation;

import network.Attribute;
import network.Edge;
import network.Network;
import network.Node;
import network.Relation;
import network.StringAttributeFactory;
import goa.GoAnnotation;
import gogogo.GoGoGoDataset;


/**
 * @author Samuel Croset
 *
 */
public class DrugGrapher {

    private GoGoGoDataset data;
    private Network network;
    private StringAttributeFactory label;
    private StringAttributeFactory edgeType;
    private StringAttributeFactory nodeType;
    private StringAttributeFactory nodeName;

    public GoGoGoDataset getData() {
	return data;
    }
    public void setData(GoGoGoDataset data) {
	this.data = data;
    }
    public Network getNetwork() {
	return network;
    }
    public void setNetwork(Network network) {
	this.network = network;
    }
    public StringAttributeFactory getLabel() {
	return label;
    }
    public void setLabel(StringAttributeFactory label) {
	this.label = label;
    }
    public StringAttributeFactory getEdgeType() {
	return edgeType;
    }
    public void setEdgeType(StringAttributeFactory edgeType) {
	this.edgeType = edgeType;
    }
    public StringAttributeFactory getNodeType() {
	return nodeType;
    }
    public void setNodeType(StringAttributeFactory nodeType) {
	this.nodeType = nodeType;
    }
    public StringAttributeFactory getNodeName() {
	return nodeName;
    }
    public void setNodeName(StringAttributeFactory nodeName) {
	this.nodeName = nodeName;
    }

    public DrugGrapher() throws IOException, ClassNotFoundException {

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

	DrugGrapher grapher = new DrugGrapher();

	Attribute drugNodeType = grapher.getNodeType().getNewAttribute("drug");
	Attribute protNodeType = grapher.getNodeType().getNewAttribute("protein");

	int counter = 0;
	int max = 3;


	for (Drug drug : grapher.getData().getDrugbank().getDrugs()) {

	    if(counter < max){

		System.out.println("Drug: " + drug.getName());

		counter++;
		Node drugNode = new Node();
		Attribute nodeName = grapher.getNodeName().getNewAttribute(drug.getId());
		Attribute nodeAccession = grapher.getLabel().getNewAttribute(drug.getName());

		drugNode.addAttribute(nodeName);
		drugNode.addAttribute(drugNodeType);
		drugNode.addAttribute(nodeAccession);


		System.out.println("Numer of relations: " + drug.getTargetRelations().size());


		for (TargetRelation targetRelation : drug.getTargetRelations()) {
		    
		    int partnerId = targetRelation.getPartnerId();
		    Partner currentPartner = grapher.getData().getDrugbank().getPartner(partnerId);

		    Node targetNode = new Node();
		    Attribute targetNodeName = grapher.getNodeName().getNewAttribute(Integer.toString(partnerId));
		    Attribute targetNodeLabel = grapher.getLabel().getNewAttribute(currentPartner.getName());
		    targetNode.addAttribute(targetNodeName);
		    targetNode.addAttribute(protNodeType);
		    targetNode.addAttribute(targetNodeLabel);



		    for (GoAnnotation annotation : currentPartner.getAnnotations()) {

			if(!annotation.getEvidence().equals("IEA")){
			    Node annotNode = new Node();
			    Attribute termNodeName = grapher.getNodeName().getNewAttribute(annotation.getGoId());
			    Attribute termNodeLabel = grapher.getLabel().getNewAttribute(grapher.getData().getGo().getTerm(annotation.getGoId()).getName());
			    Attribute termTypeNode = grapher.getNodeType().getNewAttribute(grapher.getData().getGo().getTerm(annotation.getGoId()).getNamespace());

			    annotNode.addAttribute(termNodeName);
			    annotNode.addAttribute(termTypeNode);
			    annotNode.addAttribute(termNodeLabel);

			    Edge edge = new Edge();
			    Attribute edgeActionType = grapher.getEdgeType().getNewAttribute("involved");
			    edge.addAttribute(edgeActionType);

			    Relation relation = new Relation(targetNode, edge, annotNode);
			    grapher.getNetwork().addRelation(relation);
			}

		    }


		    for (String action : targetRelation.getActions()) {

			System.out.println("relation type: " + action);

			Edge edge = new Edge();
			Attribute edgeActionType = grapher.getEdgeType().getNewAttribute(action);
			edge.addAttribute(edgeActionType);

			Relation relation = new Relation(drugNode, edge, targetNode);
			grapher.getNetwork().addRelation(relation);

		    }


		}

	    }

	}



	System.out.println("saving...");

	grapher.getNetwork().saveAll("data/graph", "drug_annot");
	System.out.println("done...");


    }

}
