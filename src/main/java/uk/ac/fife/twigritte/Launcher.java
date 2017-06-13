package uk.ac.fife.twigritte;

import uk.ac.fife.twigritte.config.Configuration;
import uk.ac.fife.twigritte.config.ConfigurationBuilder;
import uk.ac.fife.twigritte.conversion.PdfToJpgConverter;

import java.io.File;
import java.io.FileFilter;

public class Launcher {
    private static final FileFilter TWEET_FILE_FILTER = file -> file.getName().equalsIgnoreCase("tweet.txt");
    private static final FileFilter IMAGE_FILE_FILTER = file -> file.getName().equalsIgnoreCase("image.pdf");

    public static void main(String[] args) {
        // Java 8 introduces new colour management (LittleCMS) which slows down PDF rendering
        // swap the colour management for the old KCMS (Kodak Color Management System)
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");

        Configuration configuration = ConfigurationBuilder.newBuilder()
                .withWatchDirectory(new File("twitter_jobs"))
                .withTweetFileFilter(TWEET_FILE_FILTER)
                .withImageFileFilter(IMAGE_FILE_FILTER)
                .withImageConverter(new PdfToJpgConverter())
                .removeKyoTag() // TODO remove this for competition
                .build();

        Twigritte twigritte = new Twigritte(configuration);
        Runtime.getRuntime().addShutdownHook(new Thread(twigritte::stop));
        twigritte.start();
    }
}
