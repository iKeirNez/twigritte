package uk.ac.fifecollege.twigritte;

import uk.ac.fifecollege.twigritte.config.Configuration;
import uk.ac.fifecollege.twigritte.config.ConfigurationBuilder;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.TimeUnit;

public class Launcher {
    private static final FileFilter TWEET_FILE_FILTER = file -> file.getName().equalsIgnoreCase("tweet.txt");
    private static final FileFilter IMAGE_FILE_FILTER = file -> file.getName().equalsIgnoreCase("image.pdf");

    public static void main(String[] args) {
        Configuration configuration = ConfigurationBuilder.newBuilder()
                .withPollDirectory(new File("twitter"))
                .withPollTime(TimeUnit.SECONDS, 5)
                .withTweetFileFilter(TWEET_FILE_FILTER)
                .withImageFileFilter(IMAGE_FILE_FILTER)
                .removeKyoTag()
                .build();

        Twigritte twigritte = new Twigritte(configuration);
    }
}
