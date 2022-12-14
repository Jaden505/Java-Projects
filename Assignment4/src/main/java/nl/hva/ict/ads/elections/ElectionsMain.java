package nl.hva.ict.ads.elections;

import nl.hva.ict.ads.elections.models.*;
import nl.hva.ict.ads.utils.xml.XMLParser;
import nl.hva.ict.ads.utils.PathUtils;

import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class ElectionsMain {

    public static void main(String[] args) throws XMLStreamException, IOException {

        final int CDA_PARTY_ID = 3;
        final int D66_PARTY_ID = 4;
        final int CODE_ORANJE_PARTY_ID = 16;
        final int VOLT_PARTY_ID = 17;

        // PathUtils will try to find your specified data folder from the resources section of your project/module environment
        //  or from the /data-files folder at your project/module root.
        //  You can exclude the latter location from inclusion into your git repository by adding **/data-files/ to your .gitignore
        //  Check the olive (yellow/green) colour of that folder when intellij has completed re-indexing.
        //  Make sure to NOT ADD THE LARGE DATASET TO YOUR GIT.
        //  That will exceed capacity limits of your repository and block further access.

        final Election election = Election.importFromDataFolder(PathUtils.getResourcePath("/EML_bestanden_TK2021_HvA_UvA"));
        // final Election election = Election.importFromDataFolder(PathUtils.getResourcePath("/EML_bestanden_TK2021_NH"));
        // final Election election = Election.importFromDataFolder(PathUtils.getResourcePath("/EML_bestanden_TK2021"));

        final int randomPartyId = 1 + (int)Math.floor(Math.random() * election.getParties().size());

        System.out.println(election.prepareSummary());
        System.out.println(election.prepareSummary(VOLT_PARTY_ID));
    }
}
