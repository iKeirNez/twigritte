package uk.ac.fife.twigritte.config;

import uk.ac.fife.twigritte.conversion.FileConverter;

import java.io.File;
import java.io.FileFilter;

public class ConfigurationBuilder {

    public static ConfigurationBuilder newBuilder() {
        return new ConfigurationBuilder();
    }

    private Configuration instance = new Configuration();

    private ConfigurationBuilder() {}

    public ConfigurationBuilder withWatchDirectory(File directory) {
        if (!directory.exists()) {
            throw new IllegalArgumentException("Directory does not exist.");
        }

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("File is not a directory.");
        }

        if (!directory.canRead() || !directory.canWrite()) {
            throw new IllegalArgumentException("Directory is not readable/writable.");
        }

        instance.watchDirectory = directory;
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

    public ConfigurationBuilder withImageConverter(FileConverter imageConverter) {
        instance.imageConverter = imageConverter;
        return this;
    }

    public ConfigurationBuilder withTweetSuffix(String suffix) {
        instance.tweetSuffix = suffix;
        return this;
    }

    public Configuration build() {
        // TODO prevent this being called more than once per instance

        if (instance.watchDirectory == null) {
            throw new NotConfiguredException("watchDirectory");
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
