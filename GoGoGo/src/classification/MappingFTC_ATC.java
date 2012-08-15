/**
 * 
 */
package classification;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import analysis.Distribution;

import querier.DLQueryPrinter;

/**
 * @author Samuel Croset
 *
 */
public class MappingFTC_ATC {

    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, OWLOntologyCreationException {

	ATC atc = new ATC("data/atc/atc.ser");

	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	OWLDataFactory factory = manager.getOWLDataFactory();

	File atcFile = new File("data/atc/atc.owl");
	OWLOntology owlAtc = manager.loadOntologyFromOntologyDocument(atcFile);
	System.out.println("Loaded ontology: " + owlAtc.getOntologyID());
	OWLReasoner atcReasoner = createReasoner(owlAtc);
	ShortFormProvider atcSF = new SimpleShortFormProvider();
	DLQueryPrinter atcQuerier = new DLQueryPrinter(atcReasoner, atcSF);

	File ftcFile = new File("data/ftc/ftc.min.out.owl");
	OWLOntology owlFtc = manager.loadOntologyFromOntologyDocument(ftcFile);
	System.out.println("Loaded ontology: " + owlFtc.getOntologyID());
	OWLReasoner ftcReasoner = createReasoner(owlFtc);
	ShortFormProvider ftcSF = new SimpleShortFormProvider();
	DLQueryPrinter ftcQuerier = new DLQueryPrinter(ftcReasoner, ftcSF);

	FileWriter fstream = new FileWriter("data/mapping/ATC_FTC/mapper-ext.html");
	BufferedWriter writer = new BufferedWriter(fstream);

	writer.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"><html><head><script type=\"text/javascript\"	src=\"http://code.jquery.com/jquery-latest.js\"></script><script type=\"text/javascript\"	src=\"mapper.js\"></script><link type=\"text/css\" href=\"mapping.css\" rel=\"Stylesheet\" /><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><title>Mapper Ext</title></head><body>");

		for (ATCTerm term : atc.getFourLettersTerms()) {
//		for (ATCTerm term : atc.getThreeLettersTerms()) {
//	for (ATCTerm term : atc.getTwoLettersTerms()) {

	    System.out.println("ATC code: " + term.getCode() + " - " + term.getLabel());
	    writer.append("<div class='entry'>");
	    writer.append("<div class='toggler'></div><div class='atc-category'><a href='http://www.whocc.no/atc_ddd_index/?code="+ term.getCode() +"' target=_BLANK>" + term.getCode() + "</a> - "  + term.getLabel() + "</div>");
	    String classExpression = term.getCode() + " and DrugBankCompound";
	    System.out.println("Getting the subclasses...");
	    Set<OWLClass> resultsAtc = atcQuerier.returnSubClasses(classExpression);

	    System.out.println("Number of subclasses: " + resultsAtc.size());
	    Distribution<String> distribtionFtc = new Distribution<String>();
	    writer.append("<div class='atc-sub-categories'>");
	    for (OWLClass owlClass : resultsAtc) {
		writer.append(atcSF.getShortForm(owlClass) + ", ");
	    }
	    writer.append("</div>");

	    HashMap<String, String> labels = new HashMap<String, String>();

	    for (OWLClass owlClass : resultsAtc) {
		System.out.println("\t" + atcSF.getShortForm(owlClass));
		String classExpressionFtc = atcSF.getShortForm(owlClass);

		if(!classExpressionFtc.equals("Nothing")){
		    System.out.println("Getting super classes...");
		    Set<OWLClass> resultsFtc = ftcQuerier.returnSuperClasses(classExpressionFtc);
		    if(resultsFtc != null){
			for (OWLClass owlClassFtc : resultsFtc) {
			    System.out.println("Number of superclasses: " + resultsFtc.size());
			    distribtionFtc.add(ftcSF.getShortForm(owlClassFtc));
			    OWLClass clsAgent = factory.getOWLClass(IRI.create("http://www.ebi.ac.uk/ftc/" + ftcSF.getShortForm(owlClassFtc)));
			    OWLAnnotationProperty label = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
			    for (OWLAnnotation annotation : clsAgent.getAnnotations(owlFtc, label)) {
				if (annotation.getValue() instanceof OWLLiteral) {
				    OWLLiteral val = (OWLLiteral) annotation.getValue();
				    labels.put(ftcSF.getShortForm(owlClassFtc), val.getLiteral());
				}
			    }

			}
		    }else{
			//Deal with the fact that the drug is not in FTC
			System.out.println("Class not in the ATC");
		    }
		}
	    }

	    writer.append("<table>");
	    for (String key : distribtionFtc.getDistributionMap().keySet()) {
		if(!key.equals("Drug")){
		    writer.append("<tr class='row'>");
		    writer.append("<td>" + key + "</td>");
		    writer.append("<td class='label'>" + labels.get(key) + "</td>");
		    writer.append("<td class='frequency'>" + distribtionFtc.getDistributionMap().get(key) + "</td>");
		    writer.append("</tr>");
		}
	    }
	    writer.append("</table></div>");
	}

	writer.close();
    }

    private static OWLReasoner createReasoner(OWLOntology rootOntology) {
	OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
	return reasonerFactory.createReasoner(rootOntology);
    }

}
