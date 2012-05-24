/**
 * 
 */
package parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import gene_ontology.GeneOntology;
import goa.GoAnnotation;
import gogogo.GoGoGoDataset;
import drugbank.DrugBank;
import drugbank.Partner;

/**
 * @author Samuel Croset
 *
 */
public class GoaConnector {

    private DrugBank drugbank;
    private GeneOntology go;


    public DrugBank getDrugbank() {
	return drugbank;
    }

    public void setDrugbank(DrugBank drugbank) {
	this.drugbank = drugbank;
    }

    public GeneOntology getGo() {
	return go;
    }

    public void setGo(GeneOntology go) {
	this.go = go;
    }


    public GoaConnector(String drugBankPath, String goPath) throws FileNotFoundException, IOException, ClassNotFoundException {
	DrugBank drugBank = new DrugBank(drugBankPath);
	this.setDrugbank(drugBank);
	GeneOntology go = new GeneOntology(goPath);
	this.setGo(go);
    }

    public void fillPartnersWithGoTerms() throws IOException {
	//From code provided by http://www.ebi.ac.uk/QuickGO/clients/DownloadAnnotation.java

	int counter = 0;
	int total = this.getDrugbank().getPartners().size();

	for (Partner partner : this.getDrugbank().getPartners()) {
	    if(partner.getUniprotIdentifer() != null){
		String uniprotId = partner.getUniprotIdentifer();
		System.out.println("calling for " + uniprotId + " --> " + counter + "/" + total);
		counter++;
		URL u=new URL("http://www.ebi.ac.uk/QuickGO/GAnnotation?protein=" + uniprotId +"&format=tsv");
		HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();
		BufferedReader rd =new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

		String line = rd.readLine();
		ArrayList<GoAnnotation> annotations = new ArrayList<GoAnnotation>();

		while ((line=rd.readLine())!=null) {
		    String[] splittedLine = line.split("\t");
		    GoAnnotation goa = new GoAnnotation();
		    goa.setDatabase(splittedLine[0]);
		    goa.setDate(splittedLine[12]);
		    goa.setEvidence(splittedLine[9]);
		    goa.setEvidenceProvider(splittedLine[10]);
		    goa.setGoId(splittedLine[6]);
		    goa.setQualifier(splittedLine[5]);
		    goa.setReference(splittedLine[8]);
		    goa.setSource(splittedLine[13]);
		    goa.setTaxon(splittedLine[4]);
		    annotations.add(goa);
		}
		partner.setAnnotations(annotations);

	    }else{
		System.err.println("The partner " + partner.getName() + " has no Uniprot identifer");
	    }
	}
    }

    public void save(String path) throws FileNotFoundException, IOException {

	GoGoGoDataset data = new GoGoGoDataset();
	data.setDrugbank(this.getDrugbank());
	data.setGo(this.getGo());
	ObjectOutput out = null;
	out = new ObjectOutputStream(new FileOutputStream(path));
	out.writeObject(data);
	out.close();
    }

}
