/**
 * 
 */
package analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Samuel Croset
 *
 */
public class Reports {

    ArrayList<Report> reports;
    int numberOfDrugsFromAtc;
    int numberOfDrugsFromFtc;

    /**
     * 
     */
    public Reports() {
	reports = new ArrayList<Report>();
    }

    /**
     * @param report
     */
    public void addReport(Report report) {
	reports.add(report);
    }

    /**
     * @throws IOException 
     * 
     */
    public void getAnalysis(String pathToExport) throws IOException {
	FileWriter fstream = new FileWriter(pathToExport);
	BufferedWriter out = new BufferedWriter(fstream);
	out.write("---REPORT---\n");
	out.write("Total number of mappings: " + reports.size() + "\n");
	out.write("Number of drugs from the ATC considered for the mapping: " + numberOfDrugsFromAtc + "\n");
	out.write("Number of drugs from the FTC considered for the mapping: " + numberOfDrugsFromFtc + "\n\n\n");

	int totalTP = 0;
	int totalFP = 0;
	int totalFN = 0;
	for (Report report : reports) {
	    totalFN += report.FN;
	    totalFP += report.FP;
	    totalTP += report.TP;
	    out.write("---------------------------------------------------------------------------------------------\n");
	    out.write("ATC: " + report.classAtc + " - " + report.classAtcLabel + "\n");
	    out.write("FTC: " + report.classFtc + " - " + report.classFtcLabel + "\n");
//	    out.write("Atc list: " + report.compoundsAtc + "\n");
//	    out.write("Ftc list: " + report.compoundsFtc + "\n");
	    out.write("\nPredictions:\n");
	    for (String prediction : report.predictionList) {
		out.write(prediction + "\n");
	    }
	    out.write("\n");
	}

	out.write("tots: TP: " + totalTP + " - FP: " + totalFP + " - FN: " + totalFN + "\n");
	
	float sensitivity = totalTP/(float) (totalTP+totalFN);
	out.write("Sensitivity/Recall (TP/TP+FN): " + sensitivity + "\n");
	
	float precision = totalTP/(float) (totalTP+totalFP);
	out.write("Specificity/Precision (TP/TP+FP): " + precision + "\n");

	out.close();
    }


}
