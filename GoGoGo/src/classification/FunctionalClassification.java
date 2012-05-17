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
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

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

	for (GoTerm term : this.getGo().getBioProcesses()) {

	    IRI iriNewClass = IRI.create(this.getPrefix() + "#" + term.getId());
	    OWLClass childTerm = this.factory.getOWLClass(iriNewClass);
	    this.addLabelToClass(childTerm, term.getName());

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

    private void addLabelToClass(OWLClass owlClass, String label) {
	OWLAnnotationProperty labelproperty = this.getFactory().getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
	OWLLiteral literal = this.getFactory().getOWLLiteral(label);
	OWLAnnotation labelAnnot = this.getFactory().getOWLAnnotation(labelproperty, literal);

	OWLAxiom ax = this.getFactory().getOWLAnnotationAssertionAxiom(owlClass.getIRI(), labelAnnot);
	this.getManager().applyChange(new AddAxiom(this.getOntology(), ax));
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
	for (GoTerm term : this.getGo().getBioProcesses()) {
	    for (GoRelation relation : term.getRelations()) {
		//TODO negative regulation
		if(relation.getType().equals("positively_regulates")){
		    this.addAgentPatternForPositiveRegulation(term, this.getGo().getTerm(relation.getTarget()));
		}else if(relation.getType().equals("negatively_regulates")){
		    this.addAgentPatternForNegativeRegulation(term, this.getGo().getTerm(relation.getTarget()));
		}
	    }
	}
    }


    private void addAgentPatternForNegativeRegulation(GoTerm negativeRegulationGoTerm, GoTerm goRegulatedTerm) {
	//Retrieve the term pointing to a parent via a r-
	IRI iriNegativeRegulationTerm = IRI.create(this.getPrefix() + "#" + negativeRegulationGoTerm.getId());
	//Get the corresponding OWL class from the ontology
	OWLClass negativeRegulationTerm = this.factory.getOWLClass(iriNegativeRegulationTerm);
	//Anti-pattern
	this.addAntiAgentPattern(this.getPositivelyPerturbs(), negativeRegulationTerm, goRegulatedTerm);
	//Pro-pattern
	this.addProAgentPattern(this.getNegativelyPerturbs(), negativeRegulationTerm, goRegulatedTerm);
    }

    private void addAgentPatternForPositiveRegulation(GoTerm positiveRegulationGoTerm, GoTerm goRegulatedTerm) {
	//Retrieve the term pointing to a parent via a r+
	IRI iriPositiveRegulationTerm = IRI.create(this.getPrefix() + "#" + positiveRegulationGoTerm.getId());
	//Get the corresponding OWL class from the ontology
	OWLClass positiveRegulationTerm = this.factory.getOWLClass(iriPositiveRegulationTerm);
	//Anti-pattern
	this.addAntiAgentPattern(this.getNegativelyPerturbs(), positiveRegulationTerm, goRegulatedTerm);
	//Pro-pattern
	this.addProAgentPattern(this.getPositivelyPerturbs(), positiveRegulationTerm, goRegulatedTerm);
    }


    private void addProAgentPattern(OWLObjectProperty perturbation, OWLClass regulatingTerm, GoTerm goRegulatedTerm) {
	//Create new IRI
	IRI iriProAgent = IRI.create(this.getPrefix() + "#Pro-" + goRegulatedTerm.getId() + "_agent");
	//Create OWL class corresponding to the IRI
	OWLClass owlProAgent = this.factory.getOWLClass(iriProAgent);
	//Add a label to the OWL class
	this.addLabelToClass(owlProAgent, "Pro-" + goRegulatedTerm.getName() + "-agent");
	//Get an 'Agent Restriction' axiom
	OWLClassExpression drugAndPertubsSome = this.getAgentRestrictionAxiom(perturbation, regulatingTerm);
	//Assert equivalence between the agent class and the logical expression
	OWLAxiom proAxiom = this.getFactory().getOWLEquivalentClassesAxiom(drugAndPertubsSome, owlProAgent);
	//Add the axiom to the ontology
	AddAxiom addProAxiom = new AddAxiom(this.getOntology(), proAxiom);
	this.getManager().applyChange(addProAxiom);
    }

    private void addAntiAgentPattern(OWLObjectProperty perturbation, OWLClass regulatingTerm, GoTerm goRegulatedTerm) {
	//Create new IRI
	IRI iriAntiAgent = IRI.create(this.getPrefix() + "#Anti-" + goRegulatedTerm.getId() + "_agent");
	//Create OWL class corresponding to the IRI
	OWLClass owAntiAgent = this.factory.getOWLClass(iriAntiAgent);
	//Add a label to the OWL class
	this.addLabelToClass(owAntiAgent, "Anti-" + goRegulatedTerm.getName() + "-agent");
	//Get an 'Agent Restriction' axiom
	OWLClassExpression drugAndPertubsSome = this.getAgentRestrictionAxiom(perturbation, regulatingTerm);
	//Assert equivalence between the agent class and the logical expression
	OWLAxiom antiAxiom = this.getFactory().getOWLEquivalentClassesAxiom(drugAndPertubsSome, owAntiAgent);
	//Add the axiom to the ontology
	AddAxiom addAntiAxiom = new AddAxiom(this.getOntology(), antiAxiom);
	this.getManager().applyChange(addAntiAxiom);

    }


    private OWLClassExpression getAgentRestrictionAxiom(OWLObjectProperty perturbation, OWLClass perturbedClass) {
	//(involved-in some term)
	OWLClassExpression involvedInSome = this.getFactory().getOWLObjectSomeValuesFrom(this.getInvolved(), perturbedClass);
	//(Protein and (involved-in some term))
	OWLClassExpression protAndInvolvedInSome = this.getFactory().getOWLObjectIntersectionOf(this.getProtein(), involvedInSome);
	//(?perturbs some (Protein and (involved-in some term)))
	OWLClassExpression pertubsSome = this.getFactory().getOWLObjectSomeValuesFrom(perturbation, protAndInvolvedInSome);
	//Drug and (?perturb some (Protein and (involved-in some term)))
	return this.getFactory().getOWLObjectIntersectionOf(this.getDrug(), pertubsSome);
    }


    public void generateProteinAxioms() {

	
    }

}
