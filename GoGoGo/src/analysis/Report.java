/**
 * 
 */
package analysis;

import java.util.ArrayList;


/**
 * @author Samuel Croset
 *
 */
public class Report {

    int TP;
    int FP;
    int FN;
    ArrayList<String> tpList;
    ArrayList<String> fpList;
    ArrayList<String> fnList;
    String classAtc;
    String classFtc;
    ArrayList<String> compoundsAtc;
    ArrayList<String> compoundsFtc;
    String classAtcLabel;
    String classFtcLabel;
    ArrayList<String> predictionList;
    String indication;
    String fpString;

    public Report(int tP, int fP, int fN) {
	TP = tP;
	FP = fP;
	FN = fN;
    }

}
