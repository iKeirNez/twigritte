package uk.ac.fifecollege.twigritte;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import twitter4j.*;
import uk.ac.fifecollege.twigritte.config.Configuration;
import uk.ac.fifecollege.twigritte.conversion.FileConverter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;

public class Twigritte {
    private static final Log LOG = LogFactory.getLog(Twigritte.class);

    private final Configuration configuration;
    private final IncomingFileWatcher incomingWatcher;

    public Twigritte(Configuration configuration) {
        this.configuration = configuration;
        this.incomingWatcher = new IncomingFileWatcher(configuration.getWatchDirectory());
    }

    public void start() {
        incomingWatcher.start();

        CompletableFuture<File> tweetFileFuture = incomingWatcher.watchFor(configuration.getTweetFileFilter())
                .whenComplete((tweetFile, throwable) -> {
                    if (throwable == null) {
                        LOG.info("Processing incoming tweet file: " + tweetFile.getAbsolutePath());
                    } else {
                        LOG.error("Error whilst processing incoming tweet file", throwable);
                    }
                });

        CompletableFuture<File> imageFileFuture = incomingWatcher.watchFor(configuration.getImageFileFilter())
                .whenComplete((imageFile, throwable) -> {
                    if (throwable == null) {
                        LOG.info("Processing incoming image file: " + imageFile.getAbsolutePath());
                    } else {
                        LOG.error("Error whilst processing incoming image file", throwable);
                    }
                });

        CompletableFuture.allOf(tweetFileFuture, imageFileFuture).whenComplete((aVoid, throwable) -> {
            if (throwable == null) {
                handleTweet(tweetFileFuture.join(), imageFileFuture.join());
            } else {
                LOG.error("Error whilst processing incoming files", throwable);
            }
        });
    }

    private void handleTweet(File tweetFile, File imageFile) {
        LOG.debug("Handling tweet");

        FileConverter imageConverter = configuration.getImageConverter();
        if (imageConverter != null) {
            try {
                LOG.debug("Converting image");
                imageFile = imageConverter.convert(imageFile);
            } catch (IOException e) {
                LOG.error("Error during image conversion", e);
                return;
            }
        }

        String tweetText;

        try {
            tweetText = loadAsString(tweetFile);
        } catch (IOException e) {
            LOG.error("Unable to load tweet text", e);
            return;
        }

        Twitter twitter = TwitterFactory.getSingleton();
        StatusUpdate statusUpdate = new StatusUpdate(tweetText);
        statusUpdate.setMedia(imageFile);

        try {
            LOG.info("Posting tweet to account: " + twitter.getScreenName());
            Status status = twitter.updateStatus(statusUpdate);
            LOG.info("Tweet posted");
            LOG.info("https://twitter.com/statuses/" + status.getId());
        } catch (TwitterException e) {
            LOG.error("Error whilst posting tweet", e);
            return;
        }
    }

    private static String loadAsString(File tweetFile) throws IOException {
        return new String(Files.readAllBytes(tweetFile.toPath()), Charset.forName("UTF-8"));
    }
}
