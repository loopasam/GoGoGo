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

//TODO look why some drugs aren't classified


public class FTC {

    private OWLObjectProperty positivelyRegulates;
    private OWLObjectProperty negativelyRegulates;
    private OWLObjectProperty regulates;
    private OWLObjectProperty partOf;
    private OWLObjectProperty perturbs;
    private OWLObjectProperty negativelyPerturbs;
    private OWLObjectProperty positivelyPerturbs;
    private OWLObjectProperty involved;
    private OWLObjectProperty hasFunction;


    private OWLClass therapeuticCompound;
    private OWLClass geneProduct;
    private OWLClass agent;

    private OWLOntologyManager manager;
    private OWLOntology ontology;
    private OWLDataFactory factory;
    private PrefixManager prefixManager;
    private GeneOntology go;
    private DrugBank drugBank;
    private GoGoGoDataset data;
    private String prefix;
    private HashMap<String, OWLObjectProperty> mapper;


    public void setData(GoGoGoDataset data) {
	this.data = data;
    }
    public GoGoGoDataset getData() {
	return data;
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
    public OWLObjectProperty getHasFunction() {
	return hasFunction;
    }
    public void setHasFunction(OWLObjectProperty hasFunction) {
	this.hasFunction = hasFunction;
    }
    public OWLClass getTherapeuticCompound() {
	return therapeuticCompound;
    }
    public void setTherapeuticCompound(OWLClass therapeuticCompound) {
	this.therapeuticCompound = therapeuticCompound;
    }
    public OWLClass getGeneProduct() {
	return geneProduct;
    }
    public void setGeneProduct(OWLClass geneProduct) {
	this.geneProduct = geneProduct;
    }
    public OWLClass getAgent() {
	return agent;
    }
    public void setAgent(OWLClass agent) {
	this.agent = agent;
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
    public PrefixManager getPrefixManager() {
	return prefixManager;
    }
    public void setPrefixManager(PrefixManager prefixManager) {
	this.prefixManager = prefixManager;
    }
    public GeneOntology getGo() {
	return go;
    }
    public void setGo(GeneOntology go) {
	this.go = go;
    }
    public DrugBank getDrugBank() {
	return drugBank;
    }
    public void setDrugBank(DrugBank drugBank) {
	this.drugBank = drugBank;
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

    public FTC(String path) throws OWLOntologyCreationException, IOException, ClassNotFoundException {
	this.setData(new GoGoGoDataset(path));	
	this.setGo(this.getData().getGo());
	this.setDrugBank(this.getData().getDrugbank());
	this.setManager(OWLManager.createOWLOntologyManager());
	this.setPrefix("http://www.gogogo.org/fuctional-skeleton.owl");
	this.setPrefixManager(new DefaultPrefixManager(this.getPrefix() + "#"));
	File file = new File("data/fuctional-skeleton.owl");
	this.setOntology(this.getManager().loadOntologyFromOntologyDocument(file));
	this.setFactory(this.getManager().getOWLDataFactory());
	HashMap<String, OWLObjectProperty> mapper = new HashMap<String, OWLObjectProperty>();
	this.setPositivelyRegulates(factory.getOWLObjectProperty(":positively-regulates", this.getPrefixManager()));
	mapper.put("positively_regulates", this.getPositivelyRegulates());
	this.setNegativelyRegulates(factory.getOWLObjectProperty(":negatively-regulates", this.getPrefixManager()));
	mapper.put("negatively_regulates", this.getNegativelyRegulates());
	this.setRegulates(factory.getOWLObjectProperty(":regulates", this.getPrefixManager()));
	mapper.put("regulates", this.getRegulates());
	this.setPartOf(factory.getOWLObjectProperty(":part-of", this.getPrefixManager()));
	mapper.put("part_of", this.getPartOf());
	this.setMapper(mapper);
	this.setPerturbs(factory.getOWLObjectProperty(":perturbs", this.getPrefixManager()));
	this.setNegativelyPerturbs(factory.getOWLObjectProperty(":negatively-perturbs", this.getPrefixManager()));
	this.setPositivelyPerturbs(factory.getOWLObjectProperty(":positively-perturbs", this.getPrefixManager()));
	this.setInvolved(factory.getOWLObjectProperty(":involved-in", this.getPrefixManager()));
	this.setHasFunction(factory.getOWLObjectProperty(":has-function", this.getPrefixManager()));
	this.setAgent(factory.getOWLClass(":Agent", this.getPrefixManager()));
	this.setTherapeuticCompound(factory.getOWLClass(":Therapeutic-Compound", this.getPrefixManager()));
	this.setGeneProduct(factory.getOWLClass(":Gene-Product", this.getPrefixManager()));
    }

    public void generateOwlOntology() {
	for (GoTerm term : this.getGo().getBioProcessesAndMolecularFunctions()) {
	    OWLClass childTerm = factory.getOWLClass(":" + term.getId(), this.getPrefixManager());
	    this.addLabelToClass(childTerm, term.getName());
	    for (GoRelation relation : term.getRelations()) {
		OWLClass owlParentTerm = factory.getOWLClass(":" + relation.getTarget(), this.getPrefixManager());
		OWLAxiom axiom = null;
		if(relation.getType().equals("is_a")){
		    axiom = this.getFactory().getOWLSubClassOfAxiom(childTerm, owlParentTerm);
		}else{
		    OWLClassExpression relationSomeClass = this.getFactory().getOWLObjectSomeValuesFrom(this.getMapper().get(relation.getType()), owlParentTerm);
		    axiom = this.getFactory().getOWLSubClassOfAxiom(childTerm, relationSomeClass);
		}
		this.addAxiom(axiom);
	    }
	}
    }

    private void addAxiom(OWLAxiom axiom) {
	AddAxiom addAxiom = new AddAxiom(this.getOntology(), axiom);
	this.getManager().applyChange(addAxiom);
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

    public void generateProteinandDrugAxioms() throws IOException, MappingException {

	HashMap<String, OWLObjectProperty> relationMapping = this.getRelationMapping("data/relation_mapping.map");

	//Iterates over the classifiable drugs and generates axioms
	for (Drug drug : this.getData().getClassifiableDrugs(relationMapping)) {

	    OWLClass drugClass = this.factory.getOWLClass(":" + drug.getId(), this.getPrefixManager());
	    this.addLabelToClass(drugClass, drug.getName());
	    OWLAxiom drugTypeAxiom = this.getFactory().getOWLSubClassOfAxiom(drugClass, this.getTherapeuticCompound());
	    this.addAxiom(drugTypeAxiom);

	    //Iterates over the targets of the drugs
	    for (TargetRelation relation : drug.getTargetRelations()) {
		Partner partner = this.getDrugBank().getPartner(relation.getPartnerId());

		//Check if the partner has some annotations non IEA nor CC
		//TODO add more flexibility for the type of annotations to include
		if(partner.getNonIEAAnnotationsNonCC().size() > 0 ){
		    OWLClass protClass = this.factory.getOWLClass(":" + partner.getUniprotIdentifer(), this.getPrefixManager());
		    this.addLabelToClass(protClass, partner.getName());
		    OWLAxiom protTypeAxiom = this.getFactory().getOWLSubClassOfAxiom(protClass, this.getGeneProduct());
		    this.addAxiom(protTypeAxiom);

		    for (String action : relation.getActions()) {
			//Check is the relation is meaningful

			if(relationMapping.get(action) != null){

			    //Add a relation between the drug and the partner. The partner is relevant as it is linked to the drug and has some annotations.
			    OWLObjectProperty property = relationMapping.get(action);
			    OWLClassExpression perturbsSome = this.getFactory().getOWLObjectSomeValuesFrom(property, protClass);
			    OWLAxiom drugActionAxiom = this.getFactory().getOWLSubClassOfAxiom(drugClass, perturbsSome);
			    this.addAxiom(drugActionAxiom);

			    //TODO add more flexibility for the type of annotations to include
			    for (GoAnnotation annotation : partner.getNonIEAAnnotationsNonCC()) {

				if(this.getGo().isTermABioProcess(annotation.getGoId())){
				    //If term is a bio-process then create the BP patterns
				    OWLClass goTerm = this.factory.getOWLClass(":" + annotation.getGoId(), this.getPrefixManager());
				    OWLClassExpression involvedInSome = this.getFactory().getOWLObjectSomeValuesFrom(this.getInvolved(), goTerm);
				    OWLAxiom protAnnotationAxiom = this.getFactory().getOWLSubClassOfAxiom(protClass, involvedInSome);
				    this.addAxiom(protAnnotationAxiom);

				    GoTerm currentTerm = this.getGo().getTerm(annotation.getGoId());
				    for (GoRelation parentRelation : currentTerm.getRelations()) {
					if(parentRelation.getType().equals("positively_regulates")){
					    this.addAgentPatternForPositiveRegulation(currentTerm, this.getGo().getTerm(parentRelation.getTarget()));
					}else if(parentRelation.getType().equals("negatively_regulates")){
					    this.addAgentPatternForNegativeRegulation(currentTerm, this.getGo().getTerm(parentRelation.getTarget()));
					}
				    }		
				}else if(this.getGo().isTermAMolecularFunction(annotation.getGoId())){
				    //If term is a MF, then creates the MF patterns
				    OWLClass goTerm = this.factory.getOWLClass(":" + annotation.getGoId(), this.getPrefixManager());
				    OWLClassExpression hasFunctionSome = this.getFactory().getOWLObjectSomeValuesFrom(this.getHasFunction(), goTerm);
				    OWLAxiom protAnnotationAxiom = this.getFactory().getOWLSubClassOfAxiom(protClass, hasFunctionSome);
				    this.addAxiom(protAnnotationAxiom);
				    GoTerm currentTerm = this.getGo().getTerm(annotation.getGoId());
				    this.addAgentPatternForFunction(currentTerm);
				}

			    }
			}else{
			    System.err.println("[INFO] Relation: '" + action + "' ignored as not mapped in the '.map' file.");
			}
		    }
		}
	    }
	}

    }

    private void addAgentPatternForFunction(GoTerm currentTerm) {
	//Get the OWLClass of the function term.
	OWLClass functionTerm = this.factory.getOWLClass(":" + currentTerm.getId(), this.getPrefixManager());
	//Anti-pattern
	OWLClass owAntiAgent = this.factory.getOWLClass(":Anti-" + currentTerm.getId(), this.getPrefixManager());
	//Add a label to the OWL class
	this.addLabelToClass(owAntiAgent, "Anti-" + currentTerm.getName());
	//Get an 'Agent Restriction' axiom
	OWLClassExpression drugAndNegativelyPertubsSome = this.getFunctionalAgentRestrictionAxiom(this.getNegativelyPerturbs(), functionTerm);
	OWLAxiom antiAgentAxiom = this.getFactory().getOWLSubClassOfAxiom(owAntiAgent, this.getAgent());
	this.addAxiom(antiAgentAxiom);
	//Assert equivalence between the agent class and the logical expression
	OWLAxiom antiAxiom = this.getFactory().getOWLEquivalentClassesAxiom(drugAndNegativelyPertubsSome, owAntiAgent);
	//Add the axiom to the ontology
	this.addAxiom(antiAxiom);

	//Pro-pattern
	OWLClass owlProAgent = this.factory.getOWLClass(":Pro-" + currentTerm.getId(), this.getPrefixManager());
	//Add a label to the OWL class
	this.addLabelToClass(owlProAgent, "Pro-" + currentTerm.getName());
	//Get an 'Agent Restriction' axiom
	OWLClassExpression drugAndPositivelyPertubsSome = this.getFunctionalAgentRestrictionAxiom(this.getPositivelyPerturbs(), functionTerm);
	OWLAxiom proAgentAxiom = this.getFactory().getOWLSubClassOfAxiom(owlProAgent, this.getAgent());
	this.addAxiom(proAgentAxiom);
	//Assert equivalence between the agent class and the logical expression
	OWLAxiom proAxiom = this.getFactory().getOWLEquivalentClassesAxiom(drugAndPositivelyPertubsSome, owlProAgent);
	//Add the axiom to the ontology
	this.addAxiom(proAxiom);
    }

    private void addAgentPatternForNegativeRegulation(GoTerm negativeRegulationGoTerm, GoTerm goRegulatedTerm) {
	//Get the OWLClass of the negative_regulation_term
	OWLClass negativeRegulationTerm = this.factory.getOWLClass(":" + negativeRegulationGoTerm.getId(), this.getPrefixManager());
	//Anti-pattern
	this.addAntiAgentPattern(this.getPositivelyPerturbs(), negativeRegulationTerm, goRegulatedTerm);
	//Pro-pattern
	this.addProAgentPattern(this.getNegativelyPerturbs(), negativeRegulationTerm, goRegulatedTerm);
    }

    private void addAgentPatternForPositiveRegulation(GoTerm positiveRegulationGoTerm, GoTerm goRegulatedTerm) {
	//Get the OWLClass of the positive_regulation_term
	OWLClass positiveRegulationTerm = this.factory.getOWLClass(":" + positiveRegulationGoTerm.getId(), this.getPrefixManager());
	//Anti-pattern
	this.addAntiAgentPattern(this.getNegativelyPerturbs(), positiveRegulationTerm, goRegulatedTerm);
	//Pro-pattern
	this.addProAgentPattern(this.getPositivelyPerturbs(), positiveRegulationTerm, goRegulatedTerm);
    }

    private void addAntiAgentPattern(OWLObjectProperty perturbation, OWLClass regulatingTerm, GoTerm goRegulatedTerm) {
	//Create OWL class corresponding to the IRI
	OWLClass owAntiAgent = this.factory.getOWLClass(":Anti-" + goRegulatedTerm.getId() + "_agent", this.getPrefixManager());
	//Add a label to the OWL class
	this.addLabelToClass(owAntiAgent, "Anti-" + goRegulatedTerm.getName() + "-agent");
	//Get an 'Agent Restriction' axiom
	OWLClassExpression drugAndPertubsSome = this.getAgentRestrictionAxiom(perturbation, regulatingTerm);
	OWLAxiom agentAxiom = this.getFactory().getOWLSubClassOfAxiom(owAntiAgent, this.getAgent());
	this.addAxiom(agentAxiom);
	//Assert equivalence between the agent class and the logical expression
	OWLAxiom antiAxiom = this.getFactory().getOWLEquivalentClassesAxiom(drugAndPertubsSome, owAntiAgent);
	//Add the axiom to the ontology
	this.addAxiom(antiAxiom);
    }

    private void addProAgentPattern(OWLObjectProperty perturbation, OWLClass regulatingTerm, GoTerm goRegulatedTerm) {
	//Create OWL class corresponding to the IRI
	OWLClass owlProAgent = this.factory.getOWLClass(":Pro-" + goRegulatedTerm.getId() + "_agent", this.getPrefixManager());
	//Add a label to the OWL class
	this.addLabelToClass(owlProAgent, "Pro-" + goRegulatedTerm.getName() + "-agent");
	//Get an 'Agent Restriction' axiom
	OWLClassExpression drugAndPertubsSome = this.getAgentRestrictionAxiom(perturbation, regulatingTerm);
	OWLAxiom agentAxiom = this.getFactory().getOWLSubClassOfAxiom(owlProAgent, this.getAgent());
	this.addAxiom(agentAxiom);
	//Assert equivalence between the agent class and the logical expression
	OWLAxiom proAxiom = this.getFactory().getOWLEquivalentClassesAxiom(drugAndPertubsSome, owlProAgent);
	//Add the axiom to the ontology
	this.addAxiom(proAxiom);
    }



    private OWLClassExpression getAgentRestrictionAxiom(OWLObjectProperty perturbation, OWLClass perturbedClass) {
	//(involved-in some term)
	OWLClassExpression involvedInSome = this.getFactory().getOWLObjectSomeValuesFrom(this.getInvolved(), perturbedClass);
	//(Protein and (involved-in some term))
	OWLClassExpression protAndInvolvedInSome = this.getFactory().getOWLObjectIntersectionOf(this.getGeneProduct(), involvedInSome);
	//(?perturbs some (Protein and (involved-in some term)))
	OWLClassExpression pertubsSome = this.getFactory().getOWLObjectSomeValuesFrom(perturbation, protAndInvolvedInSome);
	//Drug and (?perturb some (Protein and (involved-in some term)))
	return this.getFactory().getOWLObjectIntersectionOf(this.getTherapeuticCompound(), pertubsSome);
    }

    private OWLClassExpression getFunctionalAgentRestrictionAxiom(OWLObjectProperty perturbation, OWLClass function) {
	//(has-function some term)
	OWLClassExpression hasFunctionSome = this.getFactory().getOWLObjectSomeValuesFrom(this.getHasFunction(), function);
	//(Protein and (has-function some term))
	OWLClassExpression protAndHasFunctionSome = this.getFactory().getOWLObjectIntersectionOf(this.getGeneProduct(), hasFunctionSome);
	//(?perturbs some (Protein and (has-function some term)))
	OWLClassExpression pertubsSome = this.getFactory().getOWLObjectSomeValuesFrom(perturbation, protAndHasFunctionSome);
	//Drug and (?perturb some (Protein and (has-function some term)))
	return this.getFactory().getOWLObjectIntersectionOf(this.getTherapeuticCompound(), pertubsSome);
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
