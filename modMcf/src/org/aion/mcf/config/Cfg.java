/*
 * Copyright (c) 2017-2018 Aion foundation.
 *
 *     This file is part of the aion network project.
 *
 *     The aion network project is free software: you can redistribute it
 *     and/or modify it under the terms of the GNU General Public License
 *     as published by the Free Software Foundation, either version 3 of
 *     the License, or any later version.
 *
 *     The aion network project is distributed in the hope that it will
 *     be useful, but WITHOUT ANY WARRANTY; without even the implied
 *     warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *     See the GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with the aion network project source files.
 *     If not, see <https://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     Aion foundation.
 */
package org.aion.mcf.config;

import java.io.File;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.aion.mcf.types.AbstractBlock;

/** @author chris */
public abstract class Cfg {

    private final String configFile = "config.xml";
    private final String genesisFile = "genesis.json";
    private final String keystoreDirectory = "keystore";
    private final String configDirectory = "config";

    private File logDirectory = null;
    private File databaseDirectory = null;
    private File execDirectory = null;

    private String network = "mainnet";

    protected String BASE_PATH = System.getProperty("user.dir");
    protected final String INITIAL_PATH = System.getProperty("user.dir");
    protected final File oldConfigDir = new File(INITIAL_PATH, configDirectory);

    /** @implNote modified only from {@link #setNetwork(String)} */
    protected File newConfigDir = new File(oldConfigDir, network);

    private boolean useOldSetup = false;
    private boolean ignoreOldSetup = false;

    protected String mode;

    protected String id;

    protected CfgApi api;

    protected CfgNet net;

    protected CfgConsensus consensus;

    protected CfgSync sync;

    protected CfgDb db;

    protected CfgLog log;

    protected CfgTx tx;

    protected CfgReports reports;

    protected CfgGui gui;

    public void setId(final String _id) {
        this.id = _id;
    }

    public void setNet(final CfgNet _net) {
        this.net = _net;
    }

    public void setApi(final CfgApi _api) {
        this.api = _api;
    }

    public void setDb(final CfgDb _db) {
        this.db = _db;
    }

    public void setLog(final CfgLog _log) {
        this.log = _log;
    }

    public void setTx(final CfgTx _tx) {
        this.tx = _tx;
    }

    public String getId() {
        return this.id;
    }

    protected String getMode() {
        return this.mode;
    }

    public CfgNet getNet() {
        return this.net;
    }

    public CfgSync getSync() {
        return this.sync;
    }

    public CfgApi getApi() {
        return this.api;
    }

    public CfgDb getDb() {
        return this.db;
    }

    public CfgLog getLog() {
        return this.log;
    }

    public CfgTx getTx() {
        return this.tx;
    }

    public CfgReports getReports() {
        return this.reports;
    }

    public CfgGui getGui() {
        return this.gui;
    }

    public String[] getNodes() {
        return this.net.getNodes();
    }

    public CfgConsensus getConsensus() {
        return this.consensus;
    }

    public void setConsensus(CfgConsensus _consensus) {
        this.consensus = _consensus;
    }

    /** @return the base dir where all configuration + persistence is managed */
    public String getBasePath() {
        return getExecDirectory().getAbsolutePath();
    }

    /** Resets internal data containing network and path. */
    public void resetInternal() {
        logDirectory = null;
        databaseDirectory = null;
        execDirectory = null;
        network = "mainnet";

        BASE_PATH = INITIAL_PATH;

        useOldSetup = false;
        ignoreOldSetup = false;
    }

    /** Returns the directory location where the kernel configuration and persistence is managed. */
    public File getExecDirectory() {
        if ((!ignoreOldSetup && useOldSetup) || execDirectory == null) {
            return new File(INITIAL_PATH);
        } else {
            return new File(execDirectory, network.toString());
        }
    }

    /**
     * Sets the directory where the kernel data containing setup and execution information will be
     * stored.
     *
     * @param _execDirectory the directory chosen for execution
     * @implNote Using this method overwrites the use of old kernel setup.
     */
    public void setExecDirectory(File _execDirectory) {
        this.execDirectory = _execDirectory;
        // default network when only datadir is set
        this.ignoreOldSetup = true;
    }

    /**
     * Sets the network to be used by the kernel.
     *
     * @param _network the network chosen for execution
     * @implNote Using this method overwrites the use of old kernel setup.
     */
    public void setNetwork(String _network) {
        this.network = _network;
        this.newConfigDir = new File(oldConfigDir, network);
        this.execDirectory = new File(INITIAL_PATH);
        this.ignoreOldSetup = true;
    }

    public String getNetwork() {
        return network;
    }

    public String getLogPath() {
        if (logDirectory != null) {
            // set when using absolute paths
            return logDirectory.getAbsolutePath();
        } else {
            return new File(getExecDirectory(), getLog().getLogPath()).getAbsolutePath();
        }
    }

    public void setLogDirectory(File _logDirectory) {
        this.logDirectory = _logDirectory;
    }

    public String getDatabasePath() {
        if (databaseDirectory != null) {
            // set when using absolute paths
            return databaseDirectory.getAbsolutePath();
        } else {
            return new File(getExecDirectory(), getDb().getPath()).getAbsolutePath();
        }
    }

    public void setDatabaseDirectory(File _databaseDirectory) {
        this.databaseDirectory = _databaseDirectory;
    }

    public String getKeystorePath() {
        // todo-Ale
        return new File(getBasePath(), keystoreDirectory).getAbsolutePath();
    }

    /** Returns the configuration directory location for the kernel execution. */
    public File getExecConfigDirectory() {
        // TODO check different input
        return new File(getExecDirectory(), configDirectory);
    }
    /** Returns the location where the config file is saved for kernel execution. */
    public File getExecConfigFile() {
        return new File(getExecConfigDirectory(), configFile);
    }

    /** Returns the location where the config file is saved for kernel execution. */
    public String getExecConfigPath() {
        return getExecConfigFile().getAbsolutePath();
    }

    /** Returns the location where the genesis file is saved for kernel execution. */
    public String getExecGenesisPath() {
        return new File(getExecConfigDirectory(), genesisFile).getAbsolutePath();
    }

    /** @implNote Maintains the old setup if the config file is present in the old location. */
    public File getInitialConfigFile() {
        // TODO-Ale: may want to consider exec path as well
        // use old config location for compatibility with old kernels
        File config = new File(oldConfigDir, configFile);

        // TODO: read mainnet config when ignore set
        if (ignoreOldSetup || !config.exists()) {
            config = new File(newConfigDir, configFile);
            if (execDirectory == null) {
                execDirectory = new File(INITIAL_PATH);
            }
        } else {
            useOldSetup = true;
        }

        return config;
    }

    /** @implNote Maintains the old setup if the config file is present in the old location. */
    public String getInitialConfigPath() {
        return getInitialConfigFile().getAbsolutePath();
    }

    public String getInitialGenesisPath() {
        // use old genesis location for compatibility with old kernels
        File genesis = new File(oldConfigDir, genesisFile);

        if (!genesis.exists()) {
            genesis = new File(newConfigDir, genesisFile);
            if (execDirectory == null) {
                execDirectory = new File(INITIAL_PATH);
            }
        }

        return genesis.getAbsolutePath();
    }

    public static String readValue(final XMLStreamReader sr) throws XMLStreamException {
        StringBuilder str = new StringBuilder();
        readLoop:
        while (sr.hasNext()) {
            int eventType = sr.next();
            switch (eventType) {
                case XMLStreamReader.CHARACTERS:
                    str.append(sr.getText());
                    break;
                case XMLStreamReader.END_ELEMENT:
                    break readLoop;
            }
        }
        return str.toString();
    }

    public static void skipElement(final XMLStreamReader sr) throws XMLStreamException {
        skipLoop:
        while (sr.hasNext()) {
            int eventType = sr.next();
            switch (eventType) {
                case XMLStreamReader.END_ELEMENT:
                    break skipLoop;
            }
        }
    }

    /**
     * @return boolean value used return to also determine if we need to write back to file with
     *     current config
     */
    public abstract boolean fromXML();

    /**
     * @return boolean value used return to also determine if we need to write back to file with
     *     current config * return true which means should save back to xml config return true which
     *     means should save back to xml config if in the config.xml id is set as default
     *     [NODE-ID-PLACEHOLDER] return true which means should save back to xml config
     */
    public abstract boolean fromXML(File configFile);

    public abstract void toXML(final String[] args);

    public abstract void toXML(final String[] args, File file);

    public abstract void setGenesis();

    public abstract AbstractBlock<?, ?> getGenesis();
}
