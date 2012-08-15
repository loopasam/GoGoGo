/**
 * 
 */
package analysis;




/**
 * @author Samuel Croset
 *
 */
public class Mapping {

    String atcGround;
    String[] atcList;
    String[] ftcList;

    public String[] getFtcClasses() {
	return ftcList;
    }

    /**
     * @return
     */
    public String[] getAtcClasses() {
	return atcList;
    }

    public String getAtcString() {
	String atcString = "";
	boolean isFirst = true;
	for (String atcClassString : atcList) {
	    if(isFirst){
		atcString += atcClassString;
		isFirst = false;
	    }else{
		atcString += "or" + atcClassString;
	    }
	}
	return atcString;
    }

    public String getFtcString() {
	String ftcString = "";
	boolean isFirst = true;
	for (String ftcClassString : ftcList) {
	    if(isFirst){
		ftcString += ftcClassString;
		isFirst = false;
	    }else{
		ftcString += "or" + ftcClassString;
	    }
	}
	return ftcString;
    }


}
