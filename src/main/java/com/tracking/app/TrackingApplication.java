package com.tracking.app;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import com.tracking.app.event.LogEventManager;
import com.tracking.app.util.InstantUtils;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.logging.Logger;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.helper.HelpScreenException;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class TrackingApplication {

    private static final Logger LOG = Logger.getLogger(TrackingApplication.class.getName());

    public static void main(String... args) {
        LOG.info("Parsing arguments...");
        final Arguments arguments = parseArguments(args);

        final LogEventManager logEventManager = new LogEventManager(
            arguments.getPath(),
            arguments.getFromTimestamp(),
            arguments.getToTimestamp()
        );
        LOG.info("Processing records in log file...");
        logEventManager.handleLogEvents();

        try {
            LOG.info("Writing output to file...");
            logEventManager.writeOutput(arguments.getOutputPath());
            LOG.info(format("Done! Output available in %s", arguments.getOutputPath()));
        } catch (IOException e) {
            LOG.severe(format("Something went wrong when writing output to file, \n%s", e.getMessage()));
            throw new UncheckedIOException(e);
        }
    }

    private static Arguments parseArguments(String... args) {
        final ArgumentParser parser = ArgumentParsers
            .newFor("java -jar tracking.jar").build()
            .description("Application to aggregate tracking data.")
            .defaultHelp(false);
        final ArgumentGroup requiredGroup = parser.addArgumentGroup("required arguments");
        final ArgumentGroup optionalGroup = parser.addArgumentGroup("optional arguments");
        requiredGroup.addArgument("--log-path", "-l")
            .required(true)
            .help("The path to the log file containing your dataset.");
        requiredGroup.addArgument("--date-range", "-d")
            .required(true)
            .help("The inclusive timestamp boundaries to aggregate the dataset on, "
                + "specified as yyyy-MM-dd HH:mm:ss,yyyy-MM-dd HH:mm:ss");
        optionalGroup.addArgument("--output-path", "-o")
            .required(false)
            .type(String.class)
            .setDefault(format("%s/%s", System.getProperty("user.home"), "log_aggregated.txt"))
            .help("DEFAULT: User home directory");

        Namespace ns = null;
        String fromTimestamp = "";
        String toTimestamp = "";
        try {
            ns = parser.parseArgs(args);
            final String[] dateRange = ns.getString("date_range").split(",");
            fromTimestamp = dateRange[0];
            toTimestamp = dateRange[1];
        } catch (HelpScreenException hse) {
            // Expected behavior - exit program.
            System.exit(0);
        } catch (ArgumentParserException ape) {
            System.err.println(
                parser.formatUsage() + TrackingApplication.class.getSimpleName() + ": error: " + ape.getMessage()
            );
            System.exit(0);
        }

        return new Arguments(
            ns.getString("log_path"),
            InstantUtils.newInstant(fromTimestamp),
            InstantUtils.newInstant(toTimestamp),
            ns.getString("output_path")
        );
    }

    private static class Arguments {
        private final String path;
        private final Instant fromTimestamp;
        private final Instant toTimestamp;
        private final String outputPath;

        private Arguments(
            final String path,
            final Instant fromTimestamp,
            final Instant toTimestamp,
            final String outputPath
        ) {
            this.path = requireNonNull(path);
            this.fromTimestamp = fromTimestamp;
            this.toTimestamp = toTimestamp;
            this.outputPath = requireNonNull(outputPath);
        }

        public String getPath() {
            return path;
        }

        public Instant getFromTimestamp() {
            return fromTimestamp;
        }

        public Instant getToTimestamp() {
            return toTimestamp;
        }

        public String getOutputPath() {
            return outputPath;
        }
    }
}
