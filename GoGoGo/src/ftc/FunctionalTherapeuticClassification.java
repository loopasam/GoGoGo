/**
 * 
 */
package ftc;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.semanticweb.elk.owlapi.ElkReasonerFactory;
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
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owlapi.util.OWLEntityRemover;
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
public class FunctionalTherapeuticClassification {

    private OWLObjectProperty positivelyRegulates;
    private OWLObjectProperty negativelyRegulates;
    private OWLObjectProperty regulates;
    private OWLObjectProperty partOf;
    private OWLObjectProperty hasPart;
    private OWLObjectProperty perturbs;
    private OWLObjectProperty negativelyPerturbs;
    private OWLObjectProperty positivelyPerturbs;
    private OWLObjectProperty involvedIn;
    private OWLObjectProperty hasFunction;

    private OWLClass therapeuticCompound;
    private OWLClass geneProduct;
    private OWLClass agent;
    private OWLClass drug;

    private OWLOntologyManager manager;
    private OWLOntology ontology;
    private OWLDataFactory factory;
    private PrefixManager prefixManager;
    private GeneOntology go;
    private DrugBank drugBank;
    private GoGoGoDataset data;
    private String prefix;
    private HashMap<String, OWLObjectProperty> mapper;


    public void setHasPart(OWLObjectProperty hasPart) {
	this.hasPart = hasPart;
    }
    public OWLObjectProperty getHasPart() {
	return hasPart;
    }
    public void setDrug(OWLClass drug) {
	this.drug = drug;
    }
    public OWLClass getDrug() {
	return drug;
    }
    public void setData(GoGoGoDataset data) {
	this.data = data;
    }
    public GoGoGoDataset getData() {
	return data;
    }
    public OWLObjectProperty getPositivelyRegulates() {
	return positivelyRegulates;
    }
    public void setInvolvedIn(OWLObjectProperty involvedIn) {
	this.involvedIn = involvedIn;
    }
    public OWLObjectProperty getInvolvedIn() {
	return involvedIn;
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

    public FunctionalTherapeuticClassification(String path) throws OWLOntologyCreationException, IOException, ClassNotFoundException {
	this.setData(new GoGoGoDataset(path));
	this.setGo(this.getData().getGo());
	this.setDrugBank(this.getData().getDrugbank());
	this.setManager(OWLManager.createOWLOntologyManager());
	this.setPrefix("http://www.ebi.ac.uk/ftc/");
	this.setPrefixManager(new DefaultPrefixManager(this.getPrefix()));
	File file = new File("data/ftc/ftc.min.owl");
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
	this.setHasPart(factory.getOWLObjectProperty(":has-part", this.getPrefixManager()));
	mapper.put("has_part", this.getPartOf());
	this.setMapper(mapper);
	this.setPerturbs(factory.getOWLObjectProperty(":perturbs", this.getPrefixManager()));
	this.setNegativelyPerturbs(factory.getOWLObjectProperty(":negatively-perturbs", this.getPrefixManager()));
	this.setPositivelyPerturbs(factory.getOWLObjectProperty(":positively-perturbs", this.getPrefixManager()));
	this.setInvolvedIn(factory.getOWLObjectProperty(":involved-in", this.getPrefixManager()));
	this.setHasFunction(factory.getOWLObjectProperty(":has-function", this.getPrefixManager()));
	this.setAgent(factory.getOWLClass(":FTC:02", this.getPrefixManager()));
	this.setTherapeuticCompound(factory.getOWLClass(":FTC:01", this.getPrefixManager()));
	this.setGeneProduct(factory.getOWLClass(":FTC:04", this.getPrefixManager()));
	this.setDrug(factory.getOWLClass(":FTC:03", this.getPrefixManager()));
    }

    public void generateOwlOntology() {
	ArrayList<String> notConsideredRelations = new ArrayList<String>();
	for (GoTerm term : this.getGo().getBioProcessesAndMolecularFunctions()) {
	    OWLClass childTerm = factory.getOWLClass(":" + term.getId(), this.getPrefixManager());
	    this.addLabelToClass(childTerm, term.getName());
	    this.addCommentToClass(childTerm, term.getDefinition().replaceAll("\"", ""));
	    for (GoRelation relation : term.getRelations()) {
		if(this.getMapper().get(relation.getType()) != null || relation.getType().equals("is_a")){
		    OWLClass owlParentTerm = factory.getOWLClass(":" + relation.getTarget(), this.getPrefixManager());
		    OWLAxiom axiom = null;
		    if(relation.getType().equals("is_a")){
			axiom = this.getFactory().getOWLSubClassOfAxiom(childTerm, owlParentTerm);
		    }else{
			OWLClassExpression relationSomeClass = this.getFactory().getOWLObjectSomeValuesFrom(this.getMapper().get(relation.getType()), owlParentTerm);
			axiom = this.getFactory().getOWLSubClassOfAxiom(childTerm, relationSomeClass);
		    }
		    this.addAxiom(axiom);
		}else{
		    if(!notConsideredRelations.contains(relation.getType())){
			System.err.println("The relation '" + relation.getType() + "' is not going to be considered");
			notConsideredRelations.add(relation.getType());
		    }
		}
	    }
	}
    }

    private void addCommentToClass(OWLClass owlClass, String comment) {
	OWLAnnotationProperty labelproperty = this.getFactory().getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_COMMENT.getIRI());
	OWLLiteral literal = this.getFactory().getOWLLiteral(comment);
	OWLAnnotation labelAnnot = this.getFactory().getOWLAnnotation(labelproperty, literal);
	OWLAxiom ax = this.getFactory().getOWLAnnotationAssertionAxiom(owlClass.getIRI(), labelAnnot);
	this.getManager().applyChange(new AddAxiom(this.getOntology(), ax));
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
	OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
	OWLReasoner reasoner = reasonerFactory.createReasoner(this.getOntology());
	reasoner.precomputeInferences();
	return reasoner.isConsistent();
    }

    //Generate only the FTC classes that are used to classify the compound. In other words, each classes of the resulting FTC should have a compound
    //attached to it.
    public void generateMininumProteinandDrugAxioms() throws IOException, MappingException {
	//List of the mapping from drugbak drugs actions on targets normalized into +p or -p.
	HashMap<String, OWLObjectProperty> relationMapping = this.getRelationMapping("data/drugbank/relation_mapping.map");

	//Iterates over the classifiable drugs and generates axioms
	//Look at getClassifiableDrugs() for more info on the selected drugs.
	for (Drug drug : this.getData().getClassifiableDrugs(relationMapping)) {

	    //Creates and OWLClass for the drug.
	    OWLClass drugClass = this.factory.getOWLClass(":" + drug.getId(), this.getPrefixManager());
	    //Add an rdfs:label with the name of the drug
	    this.addLabelToClass(drugClass, drug.getName());
	    //Asserts that the drugBank compound is a "ftc:Drug"
	    OWLAxiom drugTypeAxiom = this.getFactory().getOWLSubClassOfAxiom(drugClass, this.getDrug());
	    this.addAxiom(drugTypeAxiom);

	    //Iterates over the relations linking a target to the drug via the target ID.
	    for (TargetRelation relation : drug.getTargetRelations()) {
		//Get the partner object from the list of partners
		Partner partner = this.getDrugBank().getPartner(relation.getPartnerId());
		//Check if the partner has some annotations non IEA nor CC
		//Check if the partner is a human protein
		//TODO add more flexibility for the type of annotations to include
		if(partner.getSpecies().getCategory() != null && partner.getSpecies().getCategory().equals("human")){
		    if(partner.getNonIEAAnnotationsNonCC().size() > 0){
			//Create an OWL class with Uniprot identifier as id.
			OWLClass protClass = this.factory.getOWLClass(":" + partner.getUniprotIdentifer(), this.getPrefixManager());
			//Add as rdfs:label the name of the prot.
			this.addLabelToClass(protClass, partner.getName());
			//Asserts that the protein is one.
			OWLAxiom protTypeAxiom = this.getFactory().getOWLSubClassOfAxiom(protClass, this.getGeneProduct());
			this.addAxiom(protTypeAxiom);

			//get the type of action the drug has on the target
			for (String action : relation.getActions()) {
			    //Check is the relation is meaningful
			    if(relationMapping.get(action) != null){

				//Add a relation between the drug and the partner. The partner is relevant as it is linked to the drug and has some annotations within
				//the desired scope (--> human and non IEA nor CC).
				//Get the normalised action
				OWLObjectProperty property = relationMapping.get(action);
				OWLClassExpression perturbsSome = this.getFactory().getOWLObjectSomeValuesFrom(property, protClass);
				OWLAxiom drugActionAxiom = this.getFactory().getOWLSubClassOfAxiom(drugClass, perturbsSome);
				this.addAxiom(drugActionAxiom);

				//TODO add more flexibility for the type of annotations to include
				for (GoAnnotation annotation : partner.getNonIEAAnnotationsNonCC()) {

				    if(this.getGo().isTermABioProcess(annotation.getGoId())){
					//If term is a bio-process then create the BP patterns
					//Retrieves the owlClass corresponding to the annotation
					OWLClass goTerm = this.factory.getOWLClass(":" + annotation.getGoId(), this.getPrefixManager());
					//Generate an involved_in relation (because it is a BP)
					OWLClassExpression involvedInSome = this.getFactory().getOWLObjectSomeValuesFrom(this.getInvolvedIn(), goTerm);
					//Add the subclass axiom.
					OWLAxiom protAnnotationAxiom = this.getFactory().getOWLSubClassOfAxiom(protClass, involvedInSome);
					this.addAxiom(protAnnotationAxiom);

					//retrieve the GO term object
					GoTerm currentTerm = this.getGo().getTerm(annotation.getGoId());
					//For this term, check if there is the some relations interesting for us.
					for (GoRelation parentRelation : currentTerm.getRelations()) {
					    if(parentRelation.getType().equals("positively_regulates")){
						//If there is a positively_regulates relation, we can create an agent pattern
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
			    }
			}
		    }

		}
	    }
	}

	//TODO cleaning of categories with nothing under
	System.out.println("Removing classes with no drugs inside");
	removeAgentClassesWithNoDrugs();
    }



    private void removeAgentClassesWithNoDrugs() {	
	OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
	OWLReasoner reasoner = reasonerFactory.createReasoner(this.getOntology());
	reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);

	//Retrieves ALL the drugs
	Set<OWLClass> drugs = reasoner.getSubClasses(this.getDrug(), false).getFlattened();
	System.out.println("Get all the drugs aka DB compounds: " + drugs.size());
	OWLClass nothing = this.getFactory().getOWLNothing();
	drugs.remove(nothing);
	System.out.println("Removed Nothing: " + drugs.size());
	
	//Retrieves ALL the therapeutic compounds
	Set<OWLClass> therapeuticCompounds = reasoner.getSubClasses(this.getTherapeuticCompound(), false).getFlattened();
	System.out.println("gets all the therapeutical classes: " + therapeuticCompounds.size());
	int numberOfTherapeuticCompounds = therapeuticCompounds.size();
        System.out.println("Number of classes: " + this.getOntology().getClassesInSignature().size());
	//reasoner.flush();
	//For each therapeutic compound, check if they have a drug as subclasses
	int counterToRemove = 0;
	OWLEntityRemover remover = new OWLEntityRemover(this.getManager(), Collections.singleton(this.getOntology()));

	for (OWLClass therapeuticCompound : therapeuticCompounds) {
	    Set<OWLClass> subClassesToCheckForDrug = reasoner.getSubClasses(therapeuticCompound, false).getFlattened();
	    boolean hasdrug = false;
	    for (OWLClass subClassToCheck : subClassesToCheckForDrug) {
		if(drugs.contains(subClassToCheck)){
		    hasdrug = true;
		}		    
	    }

	    if(drugs.contains(therapeuticCompound)){
		hasdrug = true;
	    }

	    if(hasdrug == false){
		counterToRemove++;
		therapeuticCompound.accept(remover);
		System.out.println("to remove: " + therapeuticCompound.getIRI());
	    }

	}
	
        this.getManager().applyChanges(remover.getChanges());
        System.out.println("Number of classes: " + this.getOntology().getClassesInSignature().size());
        // At this point, if we wanted to reuse the entity remover, we would have to reset it
        remover.reset();

	System.err.println("number of therapeutic compound: " + numberOfTherapeuticCompounds);
	System.err.println("number of them to remove: " + counterToRemove);
    }


    private void addAgentPatternForFunction(GoTerm currentTerm) {
	//Prepare the name of the category
	String antiCategoryName = currentTerm.getId().replaceFirst("GO:", "A");
	//Get the OWLClass of the function term.
	OWLClass functionTerm = this.factory.getOWLClass(":" + currentTerm.getId(), this.getPrefixManager());
	//Anti-pattern
	OWLClass owAntiAgent = this.factory.getOWLClass(":" + antiCategoryName, this.getPrefixManager());
	//Add a label to the OWL class
	this.addLabelToClass(owAntiAgent, "Anti-" + currentTerm.getName() + " Agent");
	//Get an 'Agent Restriction' axiom
	OWLClassExpression drugAndNegativelyPertubsSome = this.getFunctionalAgentRestrictionAxiom(this.getNegativelyPerturbs(), functionTerm);
	OWLAxiom antiAgentAxiom = this.getFactory().getOWLSubClassOfAxiom(owAntiAgent, this.getAgent());
	this.addAxiom(antiAgentAxiom);
	//Assert equivalence between the agent class and the logical expression
	OWLAxiom antiAxiom = this.getFactory().getOWLEquivalentClassesAxiom(drugAndNegativelyPertubsSome, owAntiAgent);
	//Add the axiom to the ontology
	this.addAxiom(antiAxiom);

	//Prepare the name of the category
	String proCategoryName = currentTerm.getId().replaceFirst("GO:", "P");
	//Pro-pattern
	OWLClass owlProAgent = this.factory.getOWLClass(":" + proCategoryName, this.getPrefixManager());
	//Add a label to the OWL class
	this.addLabelToClass(owlProAgent, "Pro-" + currentTerm.getName() + " Agent");
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
	//Prepare the name of the category
	String categoryName = goRegulatedTerm.getId().replaceFirst("GO:", "A");
	//Create OWL class corresponding to the IRI
	OWLClass owAntiAgent = this.factory.getOWLClass(":" + categoryName, this.getPrefixManager());
	//Add a label to the OWL class
	this.addLabelToClass(owAntiAgent, "Anti-" + goRegulatedTerm.getName() + " Agent");
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
	//Prepare the name of the category
	String categoryName = goRegulatedTerm.getId().replaceFirst("GO:", "P");
	//Create OWL class corresponding to the IRI
	OWLClass owlProAgent = this.factory.getOWLClass(":" + categoryName, this.getPrefixManager());
	//Add a label to the OWL class
	this.addLabelToClass(owlProAgent, "Pro-" + goRegulatedTerm.getName() + " Agent");
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
	OWLClassExpression involvedInSome = this.getFactory().getOWLObjectSomeValuesFrom(this.getInvolvedIn(), perturbedClass);
	//(Protein and (involved-in some term))
	OWLClassExpression protAndInvolvedInSome = this.getFactory().getOWLObjectIntersectionOf(this.getGeneProduct(), involvedInSome);
	//(?perturbs some (Protein and (involved-in some term)))
	OWLClassExpression pertubsSome = this.getFactory().getOWLObjectSomeValuesFrom(perturbation, protAndInvolvedInSome);
	//Drug and (?perturb some (Protein and (involved-in some term)))
	OWLClassExpression therapeuticAndPerturbsSome = this.getFactory().getOWLObjectIntersectionOf(this.getTherapeuticCompound(), pertubsSome);
	return therapeuticAndPerturbsSome;
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


    public void classify(String path) throws OWLOntologyCreationException, OWLOntologyStorageException {

	OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
	OWLReasoner reasoner = reasonerFactory.createReasoner(this.getOntology());
	reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);

	List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
	gens.add(new InferredSubClassAxiomGenerator());
	InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);	
	OWLOntology infOnt = this.getManager().createOntology();
	iog.fillOntology(this.getManager(), infOnt);
	File file = new File(path);
	this.getManager().saveOntology(infOnt, IRI.create(file.toURI()));
	reasoner.flush();
    }

    public void classify() throws OWLOntologyCreationException, OWLOntologyStorageException {

	OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
	OWLReasoner reasoner = reasonerFactory.createReasoner(this.getOntology());
	reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
	List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
	gens.add(new InferredSubClassAxiomGenerator());
	InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);
	iog.fillOntology(this.getManager(), this.getOntology());
	reasoner.dispose();
    }


    public void generateFullProteinAndDrugAxioms() throws IOException, MappingException {

	System.out.println("generate all classes...");
	int counter = 0;

	for (GoTerm term : this.getGo().getTerms()) {

	    for (GoRelation parentRelation : term.getRelations()) {
		System.out.println("Number of GO classes: " + counter);
		if(parentRelation.getType().equals("positively_regulates")){
		    counter++;
		    this.addAgentPatternForPositiveRegulation(term, this.getGo().getTerm(parentRelation.getTarget()));
		}else if(parentRelation.getType().equals("negatively_regulates")){
		    this.addAgentPatternForNegativeRegulation(term, this.getGo().getTerm(parentRelation.getTarget()));
		    counter++;
		} 
	    }
	    if(this.getGo().isTermAMolecularFunction(term.getId())){
		this.addAgentPatternForFunction(term);
		counter++;
	    }
	}

	System.out.println("generate prot axioms...");

	HashMap<String, OWLObjectProperty> relationMapping = this.getRelationMapping("data/drugbank/relation_mapping.map");

	//Iterates over the classifiable drugs and generates axioms
	for (Drug drug : this.getData().getClassifiableDrugs(relationMapping)) {

	    OWLClass drugClass = this.factory.getOWLClass(":" + drug.getId(), this.getPrefixManager());
	    this.addLabelToClass(drugClass, drug.getName());
	    OWLAxiom drugTypeAxiom = this.getFactory().getOWLSubClassOfAxiom(drugClass, this.getDrug());
	    this.addAxiom(drugTypeAxiom);

	    //Iterates over the targets of the drugs
	    for (TargetRelation relation : drug.getTargetRelations()) {
		Partner partner = this.getDrugBank().getPartner(relation.getPartnerId());
		//Check if the partner has some annotations non IEA nor CC
		//Check if the partner is a human protein
		//TODO add more flexibility for the type of annotations to include
		if(partner.getSpecies().getCategory() != null && partner.getSpecies().getCategory().equals("human")){
		    if(partner.getNonIEAAnnotationsNonCC().size() > 0){
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
					OWLClassExpression involvedInSome = this.getFactory().getOWLObjectSomeValuesFrom(this.getInvolvedIn(), goTerm);
					OWLAxiom protAnnotationAxiom = this.getFactory().getOWLSubClassOfAxiom(protClass, involvedInSome);
					this.addAxiom(protAnnotationAxiom);

				    }else if(this.getGo().isTermAMolecularFunction(annotation.getGoId())){
					//If term is a MF, then creates the MF patterns
					OWLClass goTerm = this.factory.getOWLClass(":" + annotation.getGoId(), this.getPrefixManager());
					OWLClassExpression hasFunctionSome = this.getFactory().getOWLObjectSomeValuesFrom(this.getHasFunction(), goTerm);
					OWLAxiom protAnnotationAxiom = this.getFactory().getOWLSubClassOfAxiom(protClass, hasFunctionSome);
					this.addAxiom(protAnnotationAxiom);
				    }

				}
			    }
			}
		    }

		}
	    }
	}

    }


}
