package uk.ac.fifecollege.twigritte.config;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.TimeUnit;

public class ConfigurationBuilder {

    public static ConfigurationBuilder newBuilder() {
        return new ConfigurationBuilder();
    }

    private Configuration instance = new Configuration();

    private ConfigurationBuilder() {}

    public ConfigurationBuilder withPollDirectory(File directory) {
        if (!directory.exists()) {
            throw new IllegalArgumentException("Directory does not exist.");
        }

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("File is not a directory.");
        }

        if (!directory.canRead() || !directory.canWrite()) {
            throw new IllegalArgumentException("Directory is not readable/writable.");
        }

        instance.pollDirectory = directory;
        return this;
    }

    public ConfigurationBuilder withPollTime(TimeUnit timeUnit, long time) {
        instance.pollTimeUnit = timeUnit;
        instance.pollTime = time;
        return this;
    }

    public ConfigurationBuilder withTweetFileFilter(FileFilter filter) {
        instance.tweetFileFilter = filter;
        return this;
    }

    public ConfigurationBuilder withImageFileFilter(FileFilter filter) {
        instance.imageFileFilter = filter;
        return this;
    }

    public ConfigurationBuilder removeKyoTag() {
        instance.keepKyoTag = false;
        return this;
    }

    public Configuration build() {
        // TODO prevent this being called more than once per instance

        if (instance.pollDirectory == null) {
            throw new NotConfiguredException("pollDirectory");
        }

        if (instance.tweetFileFilter == null) {
            throw new NotConfiguredException("tweetFileFilter");
        }

        if (instance.imageFileFilter == null) {
            throw new NotConfiguredException("imageFileFilter");
        }

        return instance;
    }
}
