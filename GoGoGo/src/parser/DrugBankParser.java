/**
 * 
 */
package parser;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import core.XMLBurger;

import drugbank.Drug;
import drugbank.DrugBank;
import drugbank.Partner;
import drugbank.Species;
import drugbank.TargetRelation;

/**
 * @author Samuel Croset
 *
 */
public class DrugBankParser extends Parser {

    private DrugBank drugbank;

    public void setDrugbank(DrugBank drugbank) {
	this.drugbank = drugbank;
    }


    public DrugBank getDrugbank() {
	return drugbank;
    }


    /**
     * @param path
     */
    public DrugBankParser(String pathIn, String pathOut) {
	super(pathIn, pathOut);
	this.setDrugbank(new DrugBank());
    }


    /* (non-Javadoc)
     * @see parser.Parser#parse()
     */
    @Override
    public void parse() {
	XMLBurger burger = new XMLBurger(this.getPathToFile());
	while(burger.isNotOver()){
	    if(burger.tag("drugs")){

		ArrayList<Drug> drugs = new ArrayList<Drug>();

		while(burger.inTag("drugs")){
		    if(burger.tag("drug")){

			Drug drug = new Drug();
			String type = burger.getTagAttribute("type");
			drug.setType(type);
			String name = null;
			while(burger.inTag("drug")){
			    if(burger.tag("drugbank-id")){
				drug.setId(burger.getTagText());
				name = "drug-name";
			    }
			    if(burger.tag("name")){
				if(name.equals("drug-name")){
				    drug.setName(burger.getTagText());
				}
			    }

			    if(burger.tag("groups")){
				ArrayList<String> groups = new ArrayList<String>();
				while(burger.inTag("groups")){
				    if(burger.tag("group")){
					groups.add(burger.getTagText());
				    }
				}
				drug.setGroups(groups);
			    }

			    if(burger.tag("mixtures")){while(burger.inTag("mixtures")){}}
			    if(burger.tag("packagers")){while(burger.inTag("packagers")){}}

			    if(burger.tag("atc-codes")){
				ArrayList<String> atcCodes = new ArrayList<String>();
				while(burger.inTag("atc-codes")){
				    if(burger.tag("atc-code")){
					atcCodes.add(burger.getTagText());
				    }
				}
				drug.setAtcCodes(atcCodes);
			    }

			    if(burger.tag("drug-interactions")){while(burger.inTag("drug-interactions")){}}

			    if(burger.tag("targets")){
				while(burger.inTag("targets")){
				    if(burger.tag("target")){
					TargetRelation targetRelation = new TargetRelation();
					targetRelation.setPartnerId(Integer.parseInt(burger.getTagAttribute("partner")));
					while(burger.inTag("target")){
					    if(burger.tag("actions")){
						ArrayList<String> actions = new ArrayList<String>();
						while(burger.inTag("actions")){
						    if(burger.tag("action")){
							String action = burger.getTagText();
							actions.add(action);
						    }
						}
						if(actions.size() == 0){
						    actions.add("unknown");
						}
						targetRelation.setActions(actions);

					    }
					    if(burger.tag("known-action")){
						targetRelation.setKnowAction(burger.getTagText());
					    }
					}
					drug.getTargetRelations().add(targetRelation);
				    }
				}
			    }
			}
			drugs.add(drug);
		    }

		    if(burger.tag("partners")){
			ArrayList<Partner> partners = new ArrayList<Partner>();
			while(burger.inTag("partners")){
			    if(burger.tag("partner")){
				Partner partner = new Partner();
				partner.setId(Integer.parseInt(burger.getTagAttribute("id")));
				while(burger.inTag("partner")){
				    if(burger.tag("name")){
					partner.setName(burger.getTagText());
				    }

				    if(burger.tag("species")){
					Species species = new Species();
					while(burger.inTag("species")){
					    if(burger.tag("category")){
						species.setCategory(burger.getTagText());
					    }
					    if(burger.tag("name")){
						species.setName(burger.getTagText());
					    }
					    if(burger.tag("uniprot-taxon-id")){
						species.setTaxonId(Integer.parseInt(burger.getTagText()));
					    }

					}
					partner.setSpecies(species);
				    }

				    if(burger.tag("external-identifiers")){
					String resourceName = null;
					while(burger.inTag("external-identifiers")){
					    if(burger.tag("resource")){
						resourceName = burger.getTagText();
					    }
					    if(burger.tag("identifier")){
						if(resourceName != null && resourceName.equals("UniProtKB")){
						    partner.setUniprotIdentifer(burger.getTagText());
						    resourceName = null;
						}
					    }
					}

				    }

				    if(burger.tag("pfams")){while(burger.inTag("pfams")){}}
				}
				partners.add(partner);
			    }
			}
			this.getDrugbank().setPartners(partners);
		    }
		}
		this.getDrugbank().setDrugs(drugs);
	    }
	}

    }


    /* (non-Javadoc)
     * @see parser.Parser#save()
     */
    @Override
    public void save() throws FileNotFoundException, IOException {
	ObjectOutput out = null;
	out = new ObjectOutputStream(new FileOutputStream(this.getPathOut()));
	out.writeObject(this.getDrugbank());
	out.close();
    }

}
