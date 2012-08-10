/**
 * 
 */
package analysis;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import gene_ontology.GeneOntology;
import gene_ontology.GoRelation;
import gene_ontology.GoTerm;
import gogogo.GoGoGoDataset;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

/**
 * @author Samuel Croset
 *
 */
public class GoQuery {


    OWLObjectProperty positivelyRegulates;
    OWLObjectProperty negativelyRegulates;
    OWLObjectProperty regulates;
    OWLObjectProperty partOf;
    OWLObjectProperty hasPart;
    OWLOntologyManager manager;
    OWLOntology ontology;
    OWLDataFactory factory;
    GeneOntology go;
    String prefix;
    HashMap<String, OWLObjectProperty> mapper;


    public HashMap<String, OWLObjectProperty> getMapper() {
	return mapper;
    }
    public void setMapper(HashMap<String, OWLObjectProperty> mapper) {
	this.mapper = mapper;
    }
    public String getPrefix() {
	return prefix;
    }
    public void setPrefix(String prefix) {
	this.prefix = prefix;
    }
    public GeneOntology getGo() {
	return go;
    }
    public void setGo(GeneOntology go) {
	this.go = go;
    }
    public OWLOntologyManager getManager() {
	return manager;
    }
    public void setManager(OWLOntologyManager manager) {
	this.manager = manager;
    }
    public OWLOntology getOntology() {
	return ontology;
    }
    public void setOntology(OWLOntology ontology) {
	this.ontology = ontology;
    }
    public OWLDataFactory getFactory() {
	return factory;
    }
    public void setFactory(OWLDataFactory factory) {
	this.factory = factory;
    }

    public OWLObjectProperty getPositivelyRegulates() {
	return positivelyRegulates;
    }
    public void setPositivelyRegulates(OWLObjectProperty positivelyRegulates) {
	this.positivelyRegulates = positivelyRegulates;
    }
    public OWLObjectProperty getNegativelyRegulates() {
	return negativelyRegulates;
    }
    public void setNegativelyRegulates(OWLObjectProperty negativelyRegulates) {
	this.negativelyRegulates = negativelyRegulates;
    }
    public OWLObjectProperty getRegulates() {
	return regulates;
    }
    public void setRegulates(OWLObjectProperty regulates) {
	this.regulates = regulates;
    }
    public OWLObjectProperty getPartOf() {
	return partOf;
    }

    public void setPartOf(OWLObjectProperty partOf) {
	this.partOf = partOf;
    }

    public OWLObjectProperty getHasPart() {
	return hasPart;
    }
    public void setHasPart(OWLObjectProperty hasPart) {
	this.hasPart = hasPart;
    }

    public GoQuery(String path) throws IOException, ClassNotFoundException, OWLOntologyCreationException {

	GoGoGoDataset data = new GoGoGoDataset(path);
	this.setGo(data.getGo());

	this.setManager(OWLManager.createOWLOntologyManager());
	this.setPrefix("http://www.localhost:9000");
	File file = new File("data/go/go-skeleton.owl");

	this.setOntology(this.getManager().loadOntologyFromOntologyDocument(file));
	this.setFactory(this.getManager().getOWLDataFactory());

	HashMap<String, OWLObjectProperty> mapper = new HashMap<String, OWLObjectProperty>();

	IRI iriPositivelyRegulates = IRI.create(this.getPrefix() + "#positively-regulates");
	this.setPositivelyRegulates(factory.getOWLObjectProperty(iriPositivelyRegulates));
	mapper.put("positively_regulates", this.getPositivelyRegulates());

	IRI iriNegativelyRegulates = IRI.create(this.getPrefix() + "#negatively-regulates");
	this.setNegativelyRegulates(factory.getOWLObjectProperty(iriNegativelyRegulates));
	mapper.put("negatively_regulates", this.getNegativelyRegulates());

	IRI iriRegulates = IRI.create(this.getPrefix() + "#regulates");
	this.setRegulates(factory.getOWLObjectProperty(iriRegulates));
	mapper.put("regulates", this.getRegulates());

	IRI iripartOf = IRI.create(this.getPrefix() + "#part-of");
	this.setPartOf(factory.getOWLObjectProperty(iripartOf));
	mapper.put("part_of", this.getPartOf());

	IRI iriHasPart = IRI.create(this.getPrefix() + "#has-part");
	this.setHasPart(factory.getOWLObjectProperty(iriHasPart));
	mapper.put("has_part", this.getHasPart());

	this.setMapper(mapper);

    }

    public static void main(String[] args) throws OWLOntologyCreationException, IOException, ClassNotFoundException, OWLOntologyStorageException {

	System.out.println("loading data...");
	GoQuery querier = new GoQuery("data/integration/dataset.ser");

	System.out.println("starting conversion...");
	for (GoTerm term : querier.getGo().getTerms()) {
	    OWLClass owlChildTerm = querier.getFactory().getOWLClass(IRI.create(querier.getPrefix() + "#" + term.getId()));

	    querier.addAnnotation(OWLRDFVocabulary.RDFS_LABEL, term.getName(), owlChildTerm);

	    if(term.getRelations().size() == 0){
		OWLAxiom axiom = querier.getFactory().getOWLSubClassOfAxiom(owlChildTerm, querier.getFactory().getOWLThing());
		AddAxiom addAxiom = new AddAxiom(querier.getOntology(), axiom);
		querier.getManager().applyChange(addAxiom);
	    }

	    for (GoRelation relation : term.getRelations()) {
		OWLClass owlParentTerm = querier.getFactory().getOWLClass(IRI.create(querier.getPrefix() + "#" + relation.getTarget()));
		OWLAxiom axiom = null;
		if(relation.getType().equals("is_a")){
		    axiom = querier.getFactory().getOWLSubClassOfAxiom(owlChildTerm, owlParentTerm);
		}else{
		    if(querier.getMapper().get(relation.getType()) != null){
			OWLClassExpression relationSomeClass = querier.getFactory().getOWLObjectSomeValuesFrom(querier.getMapper().get(relation.getType()), owlParentTerm);
			axiom = querier.getFactory().getOWLSubClassOfAxiom(owlChildTerm, relationSomeClass);
		    }else{
			System.err.println("Strange relation: " + relation.getType());
		    }
		}

		if(axiom != null){
		    AddAxiom addAxiom = new AddAxiom(querier.getOntology(), axiom);
		    querier.getManager().applyChange(addAxiom);
		}

	    }
	}

	System.out.println("Saving...");
	File file = new File("data/go/go.owl");
	querier.getManager().saveOntology(querier.getOntology(), IRI.create(file.toURI()));

	OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
	ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
	OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
	OWLReasoner reasoner = reasonerFactory.createReasoner(querier.getOntology(), config);
	reasoner.precomputeInferences();
	boolean consistent = reasoner.isConsistent();
	System.out.println("Consistent: " + consistent);

	OWLClass apoptosis = querier.getFactory().getOWLClass(IRI.create(querier.getPrefix() + "#GO:0006915"));

	OWLClassExpression restrictionQuery = querier.getFactory().getOWLObjectSomeValuesFrom(querier.getRegulates(), apoptosis);

	NodeSet<OWLClass> subClses = reasoner.getSubClasses(restrictionQuery, false);
	Set<OWLClass> clses = subClses.getFlattened();
	System.out.println("Subclasses of regulates apoptosis: ");
	for(OWLClass cls : clses) {
	    System.out.println("    " + cls);
	}
	System.out.println("\n");

    }

    public void addAnnotation(OWLRDFVocabulary vocabulary,  String annotationText, OWLClass owlClassToAnnotate) {
	OWLAnnotationProperty annotationProperty = this.getFactory().getOWLAnnotationProperty(vocabulary.getIRI());
	OWLLiteral owlLiteral = factory.getOWLLiteral(annotationText);
	OWLAnnotation owlAnnot = factory.getOWLAnnotation(annotationProperty, owlLiteral);
	OWLAxiom axiom = factory.getOWLAnnotationAssertionAxiom(owlClassToAnnotate.getIRI(), owlAnnot);
	manager.applyChange(new AddAxiom(ontology, axiom));

    }

}
