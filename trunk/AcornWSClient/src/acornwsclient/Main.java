/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acornwsclient;

import acornwsclient.download.DBDataDownloader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.dbStructs.NameStruct;

/**
 *
 * @author markos
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnsupportedEncodingException {
        DBDataDownloader prov = new DBDataDownloader();

        List<String> modelNameList = prov.getModels();
        String modelName = modelNameList.get(0);
        List<org.dbStructs.NameStruct> reactionsStruct = prov.getAllReactionsByModelName(modelName);
        List<NameStruct> speciesStruct = prov.getAllSpeciesByModelName(modelName);

        System.out.println("Model name: " + modelName);
        System.out.println("REACTIONS:");
        for (NameStruct str : reactionsStruct) {
            System.out.println(str);
        }
        System.out.println("SPECIES:");
        for (NameStruct str : speciesStruct) {
            System.out.println(str);
        }
        String reactSid = "R_CBPSn";
        System.out.print("============\n" + reactSid + "\n");
        List<NameStruct> reactants = prov.getSpeciesForReaction(modelName, reactSid, true);
        List<NameStruct> products = prov.getSpeciesForReaction(modelName, reactSid, false);

        System.out.println("  SUBSTRATY : ");
        for (NameStruct str : reactants) {
            System.out.println(str);
        }
        System.out.println("  PRODUKTY : ");
        for (NameStruct str : products) {
            System.out.println(str);
        }

        String speciesSid = "M_pi_c";
        System.out.print("============\n" + speciesSid + "\n");
        reactants = prov.getReactionsForSpecies(modelName, speciesSid, true);
        products = prov.getReactionsForSpecies(modelName, speciesSid, false);

        System.out.println("  REAKCJA ZRODLOWA : ");
        for (NameStruct str : reactants) {
            System.out.println(str);
        }
        System.out.println("  REAKCJA DOCELOWA : ");
        for (NameStruct str : products) {
            System.out.println(str);
        }
    }
}