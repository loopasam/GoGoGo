/**
 * 
 */
package analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import play.modules.elephant.errors.OWLClassQueryException;
import play.modules.elephant.errors.OWLEntityQueryException;

/**
 * @author Samuel Croset
 *
 */
public class BrainNonStatic {

    private OWLOntology ontology;
    private OWLReasoner reasoner;
    private OWLOntologyManager manager;
    private OWLDataFactory factory;
    private OWLReasonerFactory reasonerFactory;
    private ShortFormProvider shortFormProvider;
    private BidirectionalShortFormProvider bidiShortFormProvider;
    private OWLEntityChecker entityChecker;

    public BrainNonStatic(String pathToOntology) throws OWLOntologyCreationException {
	File ontologyFile = new File(pathToOntology);
	this.setManager(OWLManager.createOWLOntologyManager());
	this.setFactory(this.getManager().getOWLDataFactory());
	System.out.println("Loading ontology from file " + pathToOntology + "...");
	this.setOntology(this.getManager().loadOntologyFromOntologyDocument(ontologyFile));
	System.out.println("Loaded ontology: " + ontology.getOntologyID());
	System.out.println("Check ontology profile...");
	//http://www.w3.org/TR/owl2-profiles/#OWL_2_EL
	OWL2ELProfile profile = new OWL2ELProfile();
	OWLProfileReport report = profile.checkOntology(ontology);
	if(report.getViolations().size() == 0){
	    System.out.println("The ontology is in OWL 2 EL profile");
	}else{
	    for (OWLProfileViolation violation : report.getViolations()) {
		System.out.println("Violation of OWL 2 EL profile: " + violation.toString());
	    }
	}
	this.setReasonerFactory(new ElkReasonerFactory());
	this.setReasoner(this.getReasonerFactory().createReasoner(ontology));
	System.out.println("Precompute inferences...");
	this.getReasoner().precomputeInferences(InferenceType.CLASS_HIERARCHY);
	this.setShortFormProvider(new SimpleShortFormProvider());
	Set<OWLOntology> importsClosure = this.getOntology().getImportsClosure();
	this.setBidiShortFormProvider(new BidirectionalShortFormProviderAdapter(this.getManager(), importsClosure, this.getShortFormProvider()));
	this.setEntityChecker(new ShortFormEntityChecker(bidiShortFormProvider));
	System.out.println("Ontology loaded and ready!");
    }

    public OWLOntology getOntology() {
	return ontology;
    }
    public void setOntology(OWLOntology ontology) {
	this.ontology = ontology;
    }
    public OWLReasoner getReasoner() {
	return reasoner;
    }
    public void setReasoner(OWLReasoner reasoner) {
	this.reasoner = reasoner;
    }
    public OWLOntologyManager getManager() {
	return manager;
    }
    public void setManager(OWLOntologyManager manager) {
	this.manager = manager;
    }
    public OWLDataFactory getFactory() {
	return factory;
    }
    public void setFactory(OWLDataFactory factory) {
	this.factory = factory;
    }
    public OWLReasonerFactory getReasonerFactory() {
	return reasonerFactory;
    }
    public void setReasonerFactory(OWLReasonerFactory reasonerFactory) {
	this.reasonerFactory = reasonerFactory;
    }
    public ShortFormProvider getShortFormProvider() {
	return shortFormProvider;
    }
    public void setShortFormProvider(ShortFormProvider shortFormProvider) {
	this.shortFormProvider = shortFormProvider;
    }
    public BidirectionalShortFormProvider getBidiShortFormProvider() {
	return bidiShortFormProvider;
    }
    public void setBidiShortFormProvider(
	    BidirectionalShortFormProvider bidiShortFormProvider) {
	this.bidiShortFormProvider = bidiShortFormProvider;
    }
    public OWLEntityChecker getEntityChecker() {
	return entityChecker;
    }
    public void setEntityChecker(OWLEntityChecker entityChecker) {
	this.entityChecker = entityChecker;
    }


    /**
     * Returns the OWL entity matching the shortForm provided.
     * @param shotForm
     * @return owlEntity
     * @throws OWLEntityQueryException 
     */
    public OWLEntity getOWLEntity(String shotForm) throws OWLEntityQueryException {

	if(shotForm.equals("Nothing")){
	    return factory.getOWLNothing();
	}
	OWLEntity owlEntity = (OWLEntity) bidiShortFormProvider.getEntity(shotForm);
	if(owlEntity == null){
	    throw new OWLEntityQueryException("There is no entity in the ontology with '"+ shotForm + "' as a shortform");
	}
	return owlEntity;
    }

    /**
     * Returns the OWL class matching the shortForm provided
     * @param shortForm
     * @return owlClass
     * @throws OWLEntityQueryException 
     * @throws ShortFormException 
     */
    public OWLClass getOWLClass(String shortForm) throws OWLEntityQueryException {
	OWLEntity owlClass = getOWLEntity(shortForm);
	if(!owlClass.isOWLClass()){
	    throw new OWLClassQueryException("The entity '"+ shortForm +"' is not an OWL class");
	}
	return (OWLClass) owlClass;
    }


    /**
     * Returns the OWL class matching the IRI provided
     * @param iri
     * @return owlClass
     * @throws OWLClassQueryException 
     */
    public OWLClass getOWLClass(IRI iri) throws OWLClassQueryException {
	if(iri == null){
	    throw new OWLClassQueryException("The IRI has value 'null'");
	}else if(!ontology.containsClassInSignature(iri)){
	    throw new OWLClassQueryException("The ontology doesn't contain a class with the IRI '"+ iri +"'");
	}
	OWLClass owlClass = factory.getOWLClass(iri);
	return owlClass;
    }


    /**
     * The method returns the subClasses of the class given as input. The flag "direct"
     * specify whether or not only the direct subclasses should be retrieved.
     * The subClasses are returned sorted by IRI.
     * @param owlClass
     * @param direct
     * @return subClasses
     * @throws OWLSubClassQueryException 
     */
    public List<OWLClass> getSubClasses(OWLClass owlClass, boolean direct) {
	Set<OWLClass> subClasses = this.reasoner.getSubClasses(owlClass, direct).getFlattened();
	reasoner.flush();
	return sortClasses(subClasses);
    }

    /**
     * The method returns the super classes of the class given as input. The flag "direct"
     * specify whether or not only the direct super classes should be retrieved.
     * The super classes are returned sorted by IRI.
     * @param owlClass
     * @param direct
     * @return superClasses
     */
    public List<OWLClass> getSuperClasses(OWLClass owlClass, boolean direct) {
	Set<OWLClass> superClasses = reasoner.getSuperClasses(owlClass, direct).getFlattened();
	reasoner.flush();
	return sortClasses(superClasses);
    }


    /**
     * The method returns the equivalent classes of the class given as input.
     * The equivalent classes are returned sorted by IRI. The input class is not returned in the list.
     * @param owlClass
     * @return equivalentClasses
     */
    public List<OWLClass> getEquivalentClasses(OWLClass owlClass) {
	Set<OWLClass> equivalentClasses = reasoner.getEquivalentClasses(owlClass).getEntitiesMinus(owlClass);	
	return sortClasses(equivalentClasses);
    }


    /**
     * Retrieves the named subClasses corresponding to the class expression
     * @param expression
     * @param direct
     * @return subClasses
     * @throws ParserException 
     */
    public List<OWLClass> getSubClasses(String expression, boolean direct) throws ParserException {
	OWLClassExpression classExpression = parseClassExpression(expression);
	Set<OWLClass> subClasses;
	//Can be simplified once Elk would have implemented a better way to deal with anonymous classes
	if(namedClass(expression)){
	    subClasses = reasoner.getSubClasses(classExpression, direct).getFlattened();
	}else{
	    OWLClass anonymousClass = getTemporaryAnonymousClass(classExpression);
	    subClasses = reasoner.getSubClasses(anonymousClass, direct).getFlattened();
	    removeTemporaryAnonymousClass(anonymousClass);
	}
	return sortClasses(subClasses);
    }


    /**
     * Retrieves the named super classes corresponding to the class expression
     * @param expression
     * @param direct
     * @return superClasses
     * @throws ParserException 
     */
    public List<OWLClass> getSuperClasses(String expression, boolean direct) throws ParserException {
	OWLClassExpression classExpression = parseClassExpression(expression);
	Set<OWLClass> superClasses;
	//Can be simplified once Elk would have implemented a better way to deal with anonymous classes
	if(namedClass(expression)){
	    superClasses = reasoner.getSuperClasses(classExpression, direct).getFlattened();
	}else{
	    OWLClass anonymousClass = getTemporaryAnonymousClass(classExpression);
	    superClasses = reasoner.getSuperClasses(anonymousClass, direct).getFlattened();
	    removeTemporaryAnonymousClass(anonymousClass);
	}
	return sortClasses(superClasses);
    }

    /**
     * Retrieves the named equivalent classes corresponding to the class expression
     * @param expression
     * @param direct
     * @return equivalentClasses
     * @throws ParserException 
     */
    public List<OWLClass> getEquivalentClasses(String expression) throws ParserException {
	OWLClassExpression classExpression = parseClassExpression(expression);
	Set<OWLClass> equivalentClasses = null;
	//Can be simplified once Elk would have implemented a better way to deal with anonymous classes
	if(namedClass(expression)){
	    try {
		equivalentClasses = reasoner.getEquivalentClasses(classExpression).getEntitiesMinus(this.getOWLClass(expression));
	    } catch (OWLEntityQueryException e) {
		e.printStackTrace();
	    }
	}else{
	    OWLClass anonymousClass = getTemporaryAnonymousClass(classExpression);
	    equivalentClasses = reasoner.getEquivalentClasses(anonymousClass).getEntitiesMinus(anonymousClass);
	    removeTemporaryAnonymousClass(anonymousClass);
	}
	return sortClasses(equivalentClasses);
    }


    /**
     * Removes any class. Should be used to remove temporary classes used for expressions only at the moment.
     * @param anonymousClass
     */
    private void removeTemporaryAnonymousClass(OWLClass anonymousClass) {
	OWLEntityRemover remover = new OWLEntityRemover(manager, Collections.singleton(ontology));
	anonymousClass.accept(remover);
	manager.applyChanges(remover.getChanges());
	remover.reset();
	reasoner.flush();
    }

    /**
     * Add a temporary class to the ontology based on the expression. Helpful because Elk doesn't support
     * anonymous queries at the moment. To be removed in the future.
     * @param classExpression
     */
    private OWLClass getTemporaryAnonymousClass(OWLClassExpression classExpression) {
	IRI anonymousIri = IRI.create("temp");
	int counter = 0;
	while (ontology.containsClassInSignature(anonymousIri)) {
	    anonymousIri = IRI.create("temp" + counter);
	    counter++;
	}
	OWLClass anonymousClass = factory.getOWLClass(anonymousIri);
	OWLEquivalentClassesAxiom equivalenceAxiom = factory.getOWLEquivalentClassesAxiom(anonymousClass, classExpression);
	AddAxiom addAx = new AddAxiom(ontology, equivalenceAxiom);
	manager.applyChange(addAx);
	reasoner.flush();
	return anonymousClass;
    }

    /**
     * Check whether an expression is composed of only a named class. Useful for
     * anonymous queries at the moment. To be removed in the future.
     * @param classExpression
     * @return isANamedClass
     */
    public boolean namedClass(String expression) {
	try {
	    getOWLClass(expression);
	} catch (OWLEntityQueryException e) {
	    return false;
	}
	return true;
    }

    /**
     * Check whether an expression is composed of only a named entity. Useful for
     * anonymous queries at the moment. To be removed in the future.
     * @param classExpression
     * @return isANamedClass
     */
    public boolean namedEntity(String expression) {
	try {
	    getOWLEntity(expression);
	} catch (OWLEntityQueryException e) {
	    return false;
	}
	return true;
    }


    /**
     * Retrieves the shortForm of the OWL entity passed as argument.
     * @param owlEntity
     * @return shortForm
     */
    public String getShortForm(OWLEntity owlEntity) {
	return bidiShortFormProvider.getShortForm(owlEntity);
    }


    /**
     * Converts a string into an OWLExpression. If a problem is encountered, an error is thrown which can be catched-up
     * in order to know more about the error.
     * @param expression
     * @return owlExpression
     * @throws ParserException 
     */
    private OWLClassExpression parseClassExpression(String expression) throws ParserException {
	ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(factory, expression);
	parser.setDefaultOntology(ontology);
	parser.setOWLEntityChecker(entityChecker);
	OWLClassExpression owlExpression = null;
	owlExpression = parser.parseClassExpression();
	return owlExpression;
    }


    /**
     * Test whether an OWLClass is the subClass of an other.
     * @param owlClassToTest
     * @param owlClassToTestAgainst
     * @param direct
     * @return isASubClass
     */
    public boolean isSubClass(OWLClass owlClassToTest, OWLClass owlClassToTestAgainst, boolean direct) {
	Set<OWLClass> subClasses = reasoner.getSubClasses(owlClassToTestAgainst, direct).getFlattened();
	boolean contained = subClasses.contains(owlClassToTest);
	return contained;
    }

    /**
     * Test whether an OWLClass is the super class of an other.
     * @param owlClassToTest
     * @param owlClassToTestAgainst
     * @param direct
     * @return isASuperClass
     */
    public boolean isSuperClass(OWLClass owlClassToTest, OWLClass owlClassToTestAgainst, boolean direct) {
	Set<OWLClass> superClasses = reasoner.getSuperClasses(owlClassToTestAgainst, direct).getFlattened();
	boolean contained = superClasses.contains(owlClassToTest);
	return contained;
    }

    /**
     * Test whether an OWLClass is the equivalent class of an other.
     * @param owlClassToTest
     * @param owlClassToTestAgainst
     * @return isAnEquivalentClass
     */
    public boolean isEquivalentClass(OWLClass owlClassToTest, OWLClass owlClassToTestAgainst) {
	return reasoner.getEquivalentClasses((owlClassToTestAgainst)).contains(owlClassToTest);
    }

    /**
     * Sort the classes based on their shortForms.
     * @param classes
     * @return sortedClasses
     */
    private List<OWLClass> sortClasses(Set<OWLClass> classes) {
	OWLCompare classCompare = new OWLCompare();
	ArrayList<OWLClass> listClasses = new ArrayList<OWLClass>();
	for (OWLClass owlClass : classes) {
	    listClasses.add(owlClass);
	}
	Collections.sort(listClasses, classCompare);
	return listClasses;
    }

}
