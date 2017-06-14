package uk.ac.fife.twigritte;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import twitter4j.*;
import uk.ac.fife.twigritte.config.Configuration;
import uk.ac.fife.twigritte.conversion.FileConverter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

public class Twigritte {
    private static final Log LOG = LogFactory.getLog(Twigritte.class);

    private final Configuration configuration;
    private final IncomingFileWatcher incomingWatcher;
    private boolean running;
    private CompletableFuture<Void> tweetComponentsFuture = null;

    public Twigritte(Configuration configuration) {
        this.configuration = configuration;
        this.incomingWatcher = new IncomingFileWatcher(configuration.getWatchDirectory());
    }

    public void start() {
        running = true;
        incomingWatcher.start();

        while (running) {
            getAndHandleJob();
        }
    }

    private void getAndHandleJob() {
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

        LOG.info("Ready for job!");
        tweetComponentsFuture = CompletableFuture.allOf(tweetFileFuture, imageFileFuture);

        try {
            tweetComponentsFuture.join();
        } catch (CancellationException e) {
            if (running) {
                LOG.warn("Tweet components future was cancelled", e);
            }
        }

        handleJob(tweetFileFuture.join(), imageFileFuture.join());
    }

    public void stop() {
        running = false;
        incomingWatcher.stop();

        if (tweetComponentsFuture != null) {
            tweetComponentsFuture.cancel(false);
        }
    }

    private void handleJob(File tweetFile, File imageFile) {
        File targetImageFile = null;

        try {
            LOG.debug("Handling tweet");
            targetImageFile = getTweetImage(imageFile);
            String tweetText = getTweetText(tweetFile);

            if (targetImageFile != null && tweetText != null) {
                postTweet(tweetText, targetImageFile);
            }
        } finally {
            postJobCleanup(tweetFile, imageFile, targetImageFile);
        }
    }

    private void postTweet(String tweetText, File imageFile) {
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
        }
    }

    private static String loadAsString(File tweetFile) throws IOException {
        return new String(Files.readAllBytes(tweetFile.toPath()), Charset.forName("UTF-8"));
    }

    private String getTweetText(File tweetFile) {
        String tweetText = null;

        try {
            int maxTweetLength = 118;
            tweetText = loadAsString(tweetFile).trim();

            String tweetSuffix = configuration.getTweetSuffix();
            if (tweetSuffix != null) {
                maxTweetLength -= tweetSuffix.length();
            }

            if (tweetText.length() > maxTweetLength) {
                String cutIndicator = "...";
                maxTweetLength -= cutIndicator.length();

                String trimmedTweetText = tweetText.substring(0, maxTweetLength);
                tweetText = trimmedTweetText + cutIndicator;
            }

            if (tweetSuffix != null) {
                tweetText += tweetSuffix;
            }
        } catch (IOException e) {
            LOG.error("Error during tweet text loading", e);
        }

        return tweetText;
    }

    private File getTweetImage(File imageFile) {
        File targetImage = imageFile;
        FileConverter imageConverter = configuration.getImageConverter();

        if (imageConverter != null) {
            try {
                LOG.debug("Converting image");
                targetImage = imageConverter.convert(imageFile);
            } catch (IOException e) {
                LOG.error("Error during image conversion", e);
                targetImage = null;
            }
        }

        return targetImage;
    }

    private void postJobCleanup(File tweetFile, File originalImageFile, File targetImageFile) {
        LOG.info("Cleaning up job files");
        boolean tweetFileDeleted = tweetFile.delete();
        boolean imageFileDeleted = originalImageFile.delete();
        boolean convertedFileDeleted = false;

        // is converted image
        if (targetImageFile != null && !targetImageFile.equals(originalImageFile)) {
            convertedFileDeleted = targetImageFile.delete();
        }

        LOG.debug("Tweet file deleted: " + tweetFileDeleted);
        LOG.debug("Image file deleted: " + imageFileDeleted);
        LOG.debug("Converted file deleted: " + convertedFileDeleted);
    }
}
