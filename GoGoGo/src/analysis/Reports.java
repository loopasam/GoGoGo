/**
 * 
 */
package analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author Samuel Croset
 *
 */
public class Reports {

    ArrayList<Report> reports;
    ArrayList<String> allAtcDrugs;
    ArrayList<String> allFtcDrugs;
    HashSet<String> compoundsAnalyzed;

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
	out.write("Number of drugs in the ATC: " + allAtcDrugs.size() + "\n");
	out.write("Number of drugs in the FTC: " + allFtcDrugs.size() + "\n");

	int numberOfCommonCompounds = 0;
	for (String atcDrug : allAtcDrugs) {
	    if(allFtcDrugs.contains(atcDrug)){
		numberOfCommonCompounds++;
	    }
	}

	out.write("Number of drugs in the FTC and in the ATC: " + numberOfCommonCompounds + "\n");
	
	out.write("Number of drugs in the FTC and in the ATC considered in the analysis: " + compoundsAnalyzed.size() + "\n");


	int totalTP = 0;
	int totalFP = 0;
	int totalFN = 0;
	for (Report report : reports) {
	    totalFN += report.FN;
	    totalFP += report.FP;
	    totalTP += report.TP;
	    out.write("---------------------------------------------------------------------------------------------\n");
	    out.write("Indication: " + report.indication + "\n");
	    out.write("ATC expression: " + report.classAtc + "\n");
	    out.write("FTC expression: " + report.classFtc + "\n");
	    out.write("TP: " + report.TP + " - FN: " + report.FN + " - FP: " + report.FP + "\n");
	    out.write("Drugs in ATC: " + report.compoundsAtc + "\n");
	    out.write("Drugs in FTC: " + report.compoundsFtc + "\n");
	    out.write("False positives: " + report.fpList + "\n");
	    out.write(report.fpString);
	}
	out.write("---------------------------------------------------------------------------------------------\n");
	out.write("tots: TP: " + totalTP + " - FP: " + totalFP + " - FN: " + totalFN + "\n");

	float sensitivity = totalTP/(float) (totalTP+totalFN);
	out.write("Sensitivity/Recall (TP/TP+FN): " + sensitivity + "\n");

	float precision = totalTP/(float) (totalTP+totalFP);
	out.write("Specificity/Precision (TP/TP+FP): " + precision + "\n");

	out.close();
    }


}
