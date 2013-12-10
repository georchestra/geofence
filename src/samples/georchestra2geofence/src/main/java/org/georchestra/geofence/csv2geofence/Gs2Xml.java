package org.georchestra.geofence.csv2geofence;

import java.io.*;
import java.util.Iterator;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.georchestra.geofence.csv2geofence.config.model.internal.RunInfo;


/**
 * Application main file.
 *
 * Parses the input params and performs some validations on them.
 */
public class Gs2Xml {

    protected static final Log LOGGER = LogFactory.getLog(Gs2Xml.class.getPackage().getName());

    protected static final char CLI_GROUPFILE_CHAR = 'g';
    protected static final char CLI_RULEFILE_CHAR = 'r';
    protected static final char CLI_OUTPUTFILE_CHAR = 'o';
    protected static final char CLI_SEND_CHAR = 's';
    protected static final char CLI_CONFIGFILE_CHAR = 'c';

    protected static final String CLI_DELETERULES_LONG = "deleterules";
    protected static final String CLI_DELETERULES_CHAR = "d";

    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {

        LOGGER.info("Running " + Gs2Xml.class.getSimpleName());
        
        RunInfo runInfo = parse(args);
        if( ! validate(runInfo))
            return;
        Runner runner = new Runner(runInfo);
        runner.run();
    }
    
    protected static RunInfo parse(String[] args) {

        Options options = createCLIOptions();

        if (isHelpRequested(args)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(Gs2Xml.class.getSimpleName(), options);
            System.exit(0);
        }

        CommandLineParser parser = new PosixParser();
        CommandLine cli = null;
        try {
            cli = parser.parse(options, args);
        } catch (ParseException ex) {
            LOGGER.warn(ex.getMessage());

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(Gs2Xml.class.getSimpleName(), options);
            
            System.exit(1);
        }

        RunInfo runInfo = new RunInfo();

        String cfgFileName = cli.getOptionValue(CLI_CONFIGFILE_CHAR);
        LOGGER.info("Config file is " + cfgFileName);
        runInfo.setConfigurationFile(new File(cfgFileName));

        String[] groupFiles = cli.getOptionValues(CLI_GROUPFILE_CHAR);
        String[] ruleFiles = cli.getOptionValues(CLI_RULEFILE_CHAR);

        // just print out the full list of input files
        if(groupFiles != null) {
            LOGGER.info("Requested group definition files:");
            for (String groupFile : groupFiles) {
                LOGGER.info(" group file '" + groupFile+"'");
                runInfo.getGroupFiles().add(new File(groupFile));
            }
        }
        else
            LOGGER.info("No user definition file");

        if(ruleFiles != null) {
            LOGGER.info("Requested rule definition files:");
            for (String ruleFile : ruleFiles) {
                LOGGER.info(" rule file '" + ruleFile+"'");
                runInfo.getRuleFiles().add(new File(ruleFile));
            }
        }
        else
            LOGGER.info("No rule definition file");


        final String xmlOutputFileName = cli.getOptionValue(CLI_OUTPUTFILE_CHAR);
        if(xmlOutputFileName != null) {
            File xmlWriterFile = new File(xmlOutputFileName);
            runInfo.setOutputFile(xmlWriterFile);
        }

        runInfo.setSendRequested(cli.hasOption(CLI_SEND_CHAR));

        if(cli.hasOption(CLI_DELETERULES_LONG))
            runInfo.setDeleteObsoleteRules(true);

        return runInfo;
    }

    protected static boolean validate(RunInfo runInfo) {
        
        if(runInfo.getConfigurationFile() != null) {
            if( ! runInfo.getConfigurationFile().getAbsoluteFile().getParentFile().canWrite() ) {
                LOGGER.error("Can't write configuration file " + runInfo.getConfigurationFile().getAbsolutePath());
                return false;
            }
        }

        
        if(runInfo.getOutputFile() != null) {            
            if( ! runInfo.getOutputFile().getAbsoluteFile().getParentFile().canWrite() ) {
                LOGGER.error("Can't write xml command file " + runInfo.getOutputFile().getAbsolutePath());
                return false;
            }
        }

        for (Iterator it = runInfo.getGroupFiles().iterator(); it.hasNext();) {
            File groupFile = (File)it.next();
            if( ! groupFile.exists() || ! groupFile.isFile() || ! groupFile.canRead()) {
                LOGGER.error("Can't read group file " + groupFile.getAbsolutePath() + ". Skipping file.");
                it.remove();
            }
        }

        for (Iterator it = runInfo.getRuleFiles().iterator(); it.hasNext();) {
            File ruleFile = (File)it.next();
            if( ! ruleFile.exists() || ! ruleFile.isFile() || ! ruleFile.canRead()) {
                LOGGER.error("Can't read rule file " + ruleFile + ". Skipping file.");
                it.remove();
            }
        }

        if(runInfo.getGroupFiles().size() + runInfo.getRuleFiles().size() == 0) {
            LOGGER.error("Neither group or rule file to process. Skipping out.");
            return false;
        }

        if(runInfo.getOutputFile() == null && ! runInfo.isSendRequested()) {
            LOGGER.error("Neither output file or send to geofence was requested. Skipping out.");
            return false;
        }

        return true;

    }

    protected static boolean isHelpRequested(String[] args) {
        for (String arg : args) {
            if("-h".equals(arg) || "--help".equals(arg))
                return true;
        }
        return false;
    }

    protected static Options createCLIOptions() throws IllegalArgumentException {
        // create Options object
        Options options = new Options();
        options.addOption(OptionBuilder
                .withArgName("file")
                .hasArgs()
                .withDescription("config file .properties")
                .withLongOpt("configurationFile")
                .create(CLI_CONFIGFILE_CHAR));
        options.addOption(OptionBuilder
                .withArgName("file")
                .hasArgs()
                .withDescription("the CSV groups file (0 or more)")
                .withLongOpt("groupFile")
                .create(CLI_GROUPFILE_CHAR));
        options.addOption(OptionBuilder
                .withArgName("file")
                .hasArgs()
                .withDescription("the CSV groups/rules file (0 or more)")
                .withLongOpt("ruleFile")
                .create(CLI_RULEFILE_CHAR));
        options.addOption(OptionBuilder
                .withArgName("file")
                .hasArgs()
                .withDescription("the output XML GeoFence command")
                .withLongOpt("output")
                .create(CLI_OUTPUTFILE_CHAR));
        options.addOption(OptionBuilder
                .withDescription("Send commands to GeoFence")
                .withLongOpt("send")
                .create(CLI_SEND_CHAR));
        options.addOption(OptionBuilder
                .withDescription("Delete obsolete rules")
                .withLongOpt(CLI_DELETERULES_LONG)
                .create(CLI_DELETERULES_CHAR));
        return options;
    }
}
