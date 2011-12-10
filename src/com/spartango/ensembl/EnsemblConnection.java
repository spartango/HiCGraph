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

    public EnsemblConnection() throws EnsemblException {
        // This actually connects the db -- this isnt fast, so startup may pause
        try {
            System.out.println("Setting up Ensembl link");

            ensemblRegistry = new DBRegistry(DataSource.ENSEMBLDB);

            // Hold the human species
            humanSpecies = ensemblRegistry.getSpeciesByAlias("human");

            // Setup caches for the chromosomes
            chromosomes = new CachedChromosome[ChromatinLocation.NUM_CHROMOSOMES];

            for (int i = 1; i <= ChromatinLocation.NUM_CHROMOSOMES - 2; i++) {
                Chromosome raw = humanSpecies.getChromosomeByName("" + i);
                chromosomes[i - 1] = new CachedChromosome(raw);
            }

            chromosomes[22] = new CachedChromosome(
                                                   humanSpecies.getChromosomeByName("X"));

            chromosomes[23] = new CachedChromosome(
                                                   humanSpecies.getChromosomeByName("Y"));
            System.out.println("Ensembl Ready");

        } catch (ConfigurationException e) {
            System.err.println("Configutration troubles in Ensembl registry, disabling it");
            throw new EnsemblException();
        } catch (DAOException e) {
            System.err.println("DAO troubles in Ensembl registry, disabling it");
            ensemblRegistry = null;
            throw new EnsemblException();
        }
    }

    public CachedChromosome getChromosome(int chromosomeNumber) {
        return chromosomes[chromosomeNumber - 1];

    }

}
