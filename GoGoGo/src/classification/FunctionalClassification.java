/**
 * 
 */
package classification;

import gene_ontology.GeneOntology;
import gene_ontology.GoRelation;
import gene_ontology.GoTerm;
import goa.GoAnnotation;
import gogogo.GoGoGoDataset;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import drugbank.Drug;
import drugbank.DrugBank;
import drugbank.Partner;
import drugbank.TargetRelation;
import exceptions.MappingException;

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
    private OWLClass drugFromDrugBank;
    private OWLClass agent;
    
    private OWLOntologyManager manager;
    private OWLOntology ontology;
    private OWLDataFactory factory;
    private PrefixManager prefixManager;
    private GeneOntology go;
    private DrugBank drugBank;
    private String prefix;
    private HashMap<String, OWLObjectProperty> mapper;


    public void setDrugFromDrugBank(OWLClass drugFromDrugBank) {
	this.drugFromDrugBank = drugFromDrugBank;
    }
    public OWLClass getDrugFromDrugBank() {
	return drugFromDrugBank;
    }
    public void setAgent(OWLClass agent) {
	this.agent = agent;
    }
    public OWLClass getAgent() {
	return agent;
    }
    public void setPrefixManager(PrefixManager prefixManager) {
	this.prefixManager = prefixManager;
    }
    public PrefixManager getPrefixManager() {
	return prefixManager;
    }
    public void setDrugBank(DrugBank drugBank) {
	this.drugBank = drugBank;
    }
    public DrugBank getDrugBank() {
	return drugBank;
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

	this.setDrugBank(data.getDrugbank());

	this.setManager(OWLManager.createOWLOntologyManager());
	this.setPrefix("http://www.gogogo.org/fuctional-skeleton.owl");
	this.setPrefixManager(new DefaultPrefixManager(this.getPrefix() + "#"));
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

	IRI iriInvolved = IRI.create(this.getPrefix() + "#involved-in");
	this.setInvolved(factory.getOWLObjectProperty(iriInvolved));

	IRI iriDrug = IRI.create(this.getPrefix() + "#Drug");
	this.setDrug(factory.getOWLClass(iriDrug));
	
	IRI iriDrugbank = IRI.create(this.getPrefix() + "#Drug-from-DrugBank");
	this.setDrugFromDrugBank(factory.getOWLClass(iriDrugbank));

	IRI iriAgent = IRI.create(this.getPrefix() + "#Agent");
	this.setAgent(factory.getOWLClass(iriAgent));

	IRI iriProtein = IRI.create(this.getPrefix() + "#Protein");
	this.setProtein(factory.getOWLClass(iriProtein));


    }

    public void generateOwlOntology() {

	for (GoTerm term : this.getGo().getBioProcesses()) {

	    //	    IRI iriNewClass = IRI.create(this.getPrefix() + "#" + term.getId());
	    //	    OWLClass childTerm = this.factory.getOWLClass(iriNewClass);
	    OWLClass childTerm = factory.getOWLClass(":" + term.getId(), this.getPrefixManager());

	    this.addLabelToClass(childTerm, term.getName());

	    for (GoRelation relation : term.getRelations()) {
		//		IRI iriParentClass = IRI.create(this.getPrefix() + "#" + relation.getTarget());
		//		OWLClass owlParentTerm = this.factory.getOWLClass(iriParentClass);
		OWLClass owlParentTerm = factory.getOWLClass(":" + relation.getTarget(), this.getPrefixManager());

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
	
	OWLAxiom agentAxiom = this.getFactory().getOWLSubClassOfAxiom(owlProAgent, this.getAgent());
	AddAxiom addAgentAxiom = new AddAxiom(this.getOntology(), agentAxiom);
	this.getManager().applyChange(addAgentAxiom);

	
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
	
	OWLAxiom agentAxiom = this.getFactory().getOWLSubClassOfAxiom(owAntiAgent, this.getAgent());
	AddAxiom addAgentAxiom = new AddAxiom(this.getOntology(), agentAxiom);
	this.getManager().applyChange(addAgentAxiom);


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


    public void generateProteinandDrugAxioms() throws IOException, MappingException {

	HashMap<String, OWLObjectProperty> relationMapping = this.getRelationMapping("data/relation_mapping.map");

	for (Drug drug : this.getDrugBank().getNonExperimentalDrugs()) {

	    //	    IRI iriDrugClass = IRI.create(this.getPrefix() + "#" + drug.getId());
	    //	    OWLClass drugClass = this.factory.getOWLClass(iriDrugClass);
	    OWLClass drugClass = this.factory.getOWLClass(":" + drug.getId(), this.getPrefixManager());


	    this.addLabelToClass(drugClass, drug.getName());
	    OWLAxiom drugTypeAxiom = this.getFactory().getOWLSubClassOfAxiom(drugClass, this.getDrugFromDrugBank());
	    AddAxiom addDrugTypeAxiom = new AddAxiom(this.getOntology(), drugTypeAxiom);
	    this.getManager().applyChange(addDrugTypeAxiom);

	    for (TargetRelation relation : drug.getTargetRelations()) {
		Partner partner = this.getDrugBank().getPartner(relation.getPartnerId());
		//TODO add more control within here, to check the pharmacological action of the compound
		//TODO check whether the drug has any action at all
		if(partner.getUniprotIdentifer() != null){
		    //We are dealing with a protein
		    //TODO: change into URI uniprot
		    //		    IRI iriProtClass = IRI.create(this.getPrefix() + "#" + partner.getUniprotIdentifer());
		    //		    OWLClass protClass = this.factory.getOWLClass(iriProtClass);
		    OWLClass protClass = this.factory.getOWLClass(":" + partner.getUniprotIdentifer(), this.getPrefixManager());

		    this.addLabelToClass(protClass, partner.getName());
		    OWLAxiom protTypeAxiom = this.getFactory().getOWLSubClassOfAxiom(protClass, this.getProtein());
		    AddAxiom addProtTypeAxiom = new AddAxiom(this.getOntology(), protTypeAxiom);
		    this.getManager().applyChange(addProtTypeAxiom);

		    for (String action : relation.getActions()) {
			if(relationMapping.get(action) != null){
			    OWLObjectProperty property = relationMapping.get(action);
			    OWLClassExpression perturbsSome = this.getFactory().getOWLObjectSomeValuesFrom(property, protClass);
			    OWLAxiom drugActionAxiom = this.getFactory().getOWLSubClassOfAxiom(drugClass, perturbsSome);
			    AddAxiom addDrugActionAxiom = new AddAxiom(this.getOntology(), drugActionAxiom);
			    this.getManager().applyChange(addDrugActionAxiom);
			}else{
			    System.err.println("[INFO] Relation: '" + action + "' ignored as not mapped in the '.map' file.");
			}
		    }

		    for (GoAnnotation annotation : partner.getAnnotations()) {
			//TODO filter on type of evidence for comparison
			if(!annotation.getEvidence().equals("IEA") && this.getGo().getTerm(annotation.getGoId()).getNamespace().equals("biological_process")){

			    //			    IRI iriTerm = IRI.create(this.getPrefix() + "#" + annotation.getGoId());

			    //TODO stuck there


			    			    OWLClass goTerm = this.factory.getOWLClass(":" + annotation.getGoId(), this.getPrefixManager());
//			    OWLClass goTerm = this.getClassFromSignature(this.getPrefix() + "#" + annotation.getGoId());


			    OWLClassExpression involvedInSome = this.getFactory().getOWLObjectSomeValuesFrom(this.getInvolved(), goTerm);
			    OWLAxiom protAnnotationAxiom = this.getFactory().getOWLSubClassOfAxiom(protClass, involvedInSome);
			    AddAxiom addAnnotationAxiom = new AddAxiom(this.getOntology(), protAnnotationAxiom);
			    this.getManager().applyChange(addAnnotationAxiom);
			}
		    }

		}
	    }
	}

    }
    
    
    private HashMap<String, OWLObjectProperty> getRelationMapping(String path) throws IOException, MappingException {

	FileInputStream fstream = new FileInputStream(path);
	DataInputStream in = new DataInputStream(fstream);
	BufferedReader br = new BufferedReader(new InputStreamReader(in));
	String line;
	HashMap<String, OWLObjectProperty> relationMapping = new HashMap<String, OWLObjectProperty>();
	while ((line = br.readLine()) != null)   {

	    Pattern pattern = Pattern.compile("(.*) : (.*)");
	    Matcher matcher = pattern.matcher(line);
	    if (matcher.find()){
		OWLObjectProperty property = null;
		String textualRelation = matcher.group(2);

		if(textualRelation.equals("negatively perturbs")){
		    property = this.getNegativelyPerturbs();
		}else if(textualRelation.equals("positively perturbs")){
		    property = this.getPositivelyPerturbs();
		}else if(textualRelation.equals("perturbs")){
		    property = this.getPerturbs();
		}else{
		    throw new MappingException("The '.map' file contains a corrupted entry: " + line);

		}

		relationMapping.put(matcher.group(1), property);
	    }

	}
	return relationMapping;

    }
}