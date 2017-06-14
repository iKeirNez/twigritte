package uk.ac.fife.twigritte;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.fife.twigritte.config.Configuration;
import uk.ac.fife.twigritte.config.ConfigurationBuilder;
import uk.ac.fife.twigritte.conversion.PdfToJpgConverter;

import java.io.File;
import java.io.FileFilter;

public class Launcher {
    private static final Log LOG = LogFactory.getLog(Launcher.class);
    private static final FileFilter TWEET_FILE_FILTER = file -> file.getName().equalsIgnoreCase("tweet.txt");
    private static final FileFilter IMAGE_FILE_FILTER = file -> file.getName().equalsIgnoreCase("image.pdf");

    public static void main(String[] args) {
        // Java 8 introduces new colour management (LittleCMS) which slows down PDF rendering
        // swap the colour management for the old KCMS (Kodak Color Management System)
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");

        File jobDirectory = getJobDirectory();
        if (jobDirectory != null) {
            Configuration configuration = ConfigurationBuilder.newBuilder()
                    .withWatchDirectory(jobDirectory)
                    .withTweetFileFilter(TWEET_FILE_FILTER)
                    .withImageFileFilter(IMAGE_FILE_FILTER)
                    .withImageConverter(new PdfToJpgConverter())
                    .removeKyoTag() // TODO remove this for competition
                    .build();

            LOG.info("Configuration: " + configuration);

            Twigritte twigritte = new Twigritte(configuration);
            Runtime.getRuntime().addShutdownHook(new Thread(twigritte::stop));
            twigritte.start();
        }
    }

    private static File getJobDirectory() {
        File jobDirectory = new File("twitter_jobs");
        LOG.info("Job directory is configured as: " + jobDirectory.getAbsolutePath());

        if (jobDirectory.exists()) {
            if (jobDirectory.isDirectory()) {
                LOG.debug("Job directory is available");

                if (!jobDirectory.canRead() || !jobDirectory.canWrite()) {
                    LOG.error("Cannot read/write to job directory!");
                    return null;
                }
            } else {
                LOG.error("Job directory exists but is not a directory");
                return null;
            }
        } else {
            LOG.info("Job directory doesn't exist, creating");

            if (!jobDirectory.mkdir()) {
                LOG.error("Failed to create job directory");
                return null;
            }
        }

        return jobDirectory;
    }
}
