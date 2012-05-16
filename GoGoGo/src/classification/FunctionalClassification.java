/**
 * 
 */
package classification;

import gene_ontology.GeneOntology;
import gene_ontology.GoRelation;
import gene_ontology.GoTerm;
import gogogo.GoGoGoDataset;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;

/**
 * @author Samuel Croset
 *
 */
public class FunctionalClassification {

    private OWLObjectProperty positivelyRegulates;
    private OWLObjectProperty negativelyRegulates;
    private OWLObjectProperty regulates;
    private OWLObjectProperty partOf;
    private OWLObjectProperty perturbs;
    private OWLObjectProperty negativelyPerturbs;
    private OWLObjectProperty positivelyPerturbs;
    private OWLObjectProperty involved;

    private OWLClass drug;
    private OWLClass protein;

    private OWLOntologyManager manager;
    private OWLOntology ontology;
    private OWLDataFactory factory;
    private GeneOntology go;
    private String prefix;
    private HashMap<String, OWLObjectProperty> mapper;


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
    public OWLObjectProperty getPerturbs() {
	return perturbs;
    }
    public void setPerturbs(OWLObjectProperty perturbs) {
	this.perturbs = perturbs;
    }
    public OWLObjectProperty getNegativelyPerturbs() {
	return negativelyPerturbs;
    }
    public void setNegativelyPerturbs(OWLObjectProperty negativelyPerturbs) {
	this.negativelyPerturbs = negativelyPerturbs;
    }
    public OWLObjectProperty getPositivelyPerturbs() {
	return positivelyPerturbs;
    }
    public void setPositivelyPerturbs(OWLObjectProperty positivelyPerturbs) {
	this.positivelyPerturbs = positivelyPerturbs;
    }
    public OWLObjectProperty getInvolved() {
	return involved;
    }
    public void setInvolved(OWLObjectProperty involved) {
	this.involved = involved;
    }
    public OWLClass getDrug() {
	return drug;
    }
    public void setDrug(OWLClass drug) {
	this.drug = drug;
    }
    public OWLClass getProtein() {
	return protein;
    }
    public void setProtein(OWLClass protein) {
	this.protein = protein;
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
    public GeneOntology getGo() {
	return go;
    }
    public void setGo(GeneOntology go) {
	this.go = go;
    }
    public String getPrefix() {
	return prefix;
    }
    public void setPrefix(String prefix) {
	this.prefix = prefix;
    }
    public HashMap<String, OWLObjectProperty> getMapper() {
	return mapper;
    }
    public void setMapper(HashMap<String, OWLObjectProperty> mapper) {
	this.mapper = mapper;
    }

    public FunctionalClassification(String path) throws OWLOntologyCreationException, IOException, ClassNotFoundException {

	GoGoGoDataset data = new GoGoGoDataset(path);
	this.setGo(data.getGo());

	this.setManager(OWLManager.createOWLOntologyManager());
	this.setPrefix("http://www.gogogo.org/fuctional-skeleton.owl");
	File file = new File("data/fuctional-skeleton.owl");

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

	this.setMapper(mapper);

	IRI iriperturbs = IRI.create(this.getPrefix() + "#perturbs");
	this.setPerturbs(factory.getOWLObjectProperty(iriperturbs));

	IRI iriNegativelyPerturbs = IRI.create(this.getPrefix() + "#negatively-perturbs");
	this.setNegativelyPerturbs(factory.getOWLObjectProperty(iriNegativelyPerturbs));

	IRI iriPositivelyPerturbs = IRI.create(this.getPrefix() + "#positively-perturbs");
	this.setPositivelyPerturbs(factory.getOWLObjectProperty(iriPositivelyPerturbs));

	IRI iriInvolved = IRI.create(this.getPrefix() + "#involved");
	this.setInvolved(factory.getOWLObjectProperty(iriInvolved));

	IRI iriDrug = IRI.create(this.getPrefix() + "#Drug");
	this.setDrug(factory.getOWLClass(iriDrug));

	IRI iriProtein = IRI.create(this.getPrefix() + "#Protein");
	this.setProtein(factory.getOWLClass(iriProtein));


    }

    public void generateOwlOntology() {
	for (GoTerm term : this.getGo().getTerms()) {
	    IRI iriNewClass = IRI.create(this.getPrefix() + "#" + term.getId());
	    OWLClass childTerm = this.factory.getOWLClass(iriNewClass);
	    for (GoRelation relation : term.getRelations()) {
		IRI iriParentClass = IRI.create(this.getPrefix() + "#" + relation.getTarget());
		OWLClass owlParentTerm = this.factory.getOWLClass(iriParentClass);
		OWLAxiom axiom = null;
		if(relation.getType().equals("is_a")){
		    axiom = this.getFactory().getOWLSubClassOfAxiom(childTerm, owlParentTerm);
		}else{
		    OWLClassExpression relationSomeClass = this.getFactory().getOWLObjectSomeValuesFrom(this.getMapper().get(relation.getType()), owlParentTerm);
		    axiom = this.getFactory().getOWLSubClassOfAxiom(childTerm, relationSomeClass);
		}
		AddAxiom addAxiom = new AddAxiom(this.getOntology(), axiom);
		this.getManager().applyChange(addAxiom);
	    }
	}
    }

    public void save(String path) throws OWLOntologyStorageException {
	File file = new File(path);
	this.getManager().saveOntology(this.getOntology(), IRI.create(file.toURI()));
    }

    public boolean isConsistent() {
	OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
	ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
	OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
	OWLReasoner reasoner = reasonerFactory.createReasoner(this.getOntology(), config);
	reasoner.precomputeInferences();
	return reasoner.isConsistent();
    }

    public void generateAgentPatterns() {

	for (GoTerm term : this.getGo().getTerms()) {

	    boolean hasRegulationRelation = false;
	    for (GoRelation relation : term.getRelations()) {
		if(relation.getType().equals("negatively_regulates") || relation.getType().equals("positively_regulates")){
		    hasRegulationRelation = true;
		}
	    }

	    if(hasRegulationRelation){
		//TODO anti pro patterns goes there

	    }else{

		//Deal with easy classes first, no anti-pro patterns
		IRI iriTerm = IRI.create(this.getPrefix() + "#" + term.getId());
		OWLClass owlTerm = this.factory.getOWLClass(iriTerm);
		IRI iriAgent = IRI.create(this.getPrefix() + "#" + term.getName() + "_Agent");
		OWLClass owlAgent = this.factory.getOWLClass(iriAgent);
		OWLClassExpression pertubsSome = this.getFactory().getOWLObjectSomeValuesFrom(this.getPerturbs(), owlTerm);
		OWLClassExpression agentDefinition = factory.getOWLObjectIntersectionOf(this.getDrug(), pertubsSome);		
		OWLAxiom axiom = this.getFactory().getOWLEquivalentClassesAxiom(agentDefinition, owlAgent);
		AddAxiom addAxiom = new AddAxiom(this.getOntology(), axiom);
		this.getManager().applyChange(addAxiom);
	    }
	}
    }

}
