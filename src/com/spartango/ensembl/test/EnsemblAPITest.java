package com.spartango.ensembl.test;

import java.util.List;

import uk.ac.roslin.ensembl.config.DBConnection.DataSource;
import uk.ac.roslin.ensembl.dao.database.DBRegistry;
import uk.ac.roslin.ensembl.exception.ConfigurationException;
import uk.ac.roslin.ensembl.exception.DAOException;
import uk.ac.roslin.ensembl.model.core.Chromosome;
import uk.ac.roslin.ensembl.model.core.Gene;
import uk.ac.roslin.ensembl.model.core.Species;
import uk.ac.roslin.ensembl.model.database.Registry;

public class EnsemblAPITest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            System.out.println("Connecting");            
            Registry eReg = new DBRegistry(DataSource.ENSEMBLDB);
            System.out.println("Connected: v"+eReg.getHighestDBVersion());            

            System.out.println("Getting species: human");
            Species sp = eReg.getSpeciesByAlias("human");
            System.out.println("Got species: "+sp.getShortName());            

            System.out.println("Gettng chromosome: 2");
            Chromosome chrDefault = sp.getChromosomeByName("2");
            System.out.println("Got Chromosome: "+chrDefault.getLength()+"b long");            
            
            System.out.println("Getting genes around 223,064,607-223,163,715");
            List<? extends Gene> genes = chrDefault.getGenesOnRegion(223064900, 223163500);
            
            for(Gene o : genes) {
                System.out.println(">> "+o.getMappings().first().getTargetCoordinates().getStart() +" => "+o.getDescription());
                
            }
            System.out.println("---");
            
            
        } catch (ConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DAOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
