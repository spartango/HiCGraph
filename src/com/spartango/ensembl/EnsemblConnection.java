package com.spartango.ensembl;

import uk.ac.roslin.ensembl.config.DBConnection.DataSource;
import uk.ac.roslin.ensembl.dao.database.DBRegistry;
import uk.ac.roslin.ensembl.exception.ConfigurationException;
import uk.ac.roslin.ensembl.exception.DAOException;
import uk.ac.roslin.ensembl.model.core.Chromosome;
import uk.ac.roslin.ensembl.model.core.Species;
import uk.ac.roslin.ensembl.model.database.Registry;

import com.spartango.hicgraph.model.ChromatinLocation;

public class EnsemblConnection {

    private Registry           ensemblRegistry;
    private Species            humanSpecies;

    private CachedChromosome[] chromosomes;

    public EnsemblConnection() {
        // This actually connects the db -- this isnt fast, so startup may pause
        try {
            System.out.println("Setting up Ensembl link");

            ensemblRegistry = new DBRegistry(DataSource.ENSEMBLDB);

            // Hold the human species
            humanSpecies = ensemblRegistry.getSpeciesByAlias("human");

            // Setup caches for the chromosomes
            chromosomes = new CachedChromosome[ChromatinLocation.NUM_CHROMOSOMES];

            int index = 0;
            for (Chromosome c : humanSpecies.getChromosomes().values()) {
                chromosomes[index] = new CachedChromosome(c);
                index++;
            }

            System.out.println("Ensembl Ready");
        } catch (ConfigurationException e) {
            System.out.println("Configutration troubles in Ensembl registry, disabling it");
            ensemblRegistry = null;
        } catch (DAOException e) {
            System.out.println("DAO troubles in Ensembl registry, disabling it");
            ensemblRegistry = null;
        }
    }

}
