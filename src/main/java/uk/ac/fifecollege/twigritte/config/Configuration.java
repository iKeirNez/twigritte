package uk.ac.fifecollege.twigritte.config;

import uk.ac.fifecollege.twigritte.conversion.FileConverter;

import java.io.File;
import java.io.FileFilter;
import java.util.Objects;

public class Configuration {
    protected File watchDirectory = null;
    protected FileFilter tweetFileFilter = null;
    protected FileFilter imageFileFilter = null;
    protected FileConverter imageConverter = null;
    protected boolean keepKyoTag = true;

    protected Configuration() {}

    public Configuration(File watchDirectory, FileFilter tweetFileFilter, FileFilter imageFileFilter,
                         FileConverter imageConverter, boolean keepKyoTag) {
        this.watchDirectory = watchDirectory;
        this.tweetFileFilter = tweetFileFilter;
        this.imageFileFilter = imageFileFilter;
        this.imageConverter = imageConverter;
        this.keepKyoTag = keepKyoTag;
    }

    public File getWatchDirectory() {
        return watchDirectory;
    }

    public void setWatchDirectory(File watchDirectory) {
        this.watchDirectory = watchDirectory;
    }

    public FileFilter getTweetFileFilter() {
        return tweetFileFilter;
    }

    public void setTweetFileFilter(FileFilter tweetFileFilter) {
        this.tweetFileFilter = tweetFileFilter;
    }

    public FileFilter getImageFileFilter() {
        return imageFileFilter;
    }

    public void setImageFileFilter(FileFilter imageFileFilter) {
        this.imageFileFilter = imageFileFilter;
    }

    public FileConverter getImageConverter() {
        return imageConverter;
    }

    public void setImageConverter(FileConverter imageConverter) {
        this.imageConverter = imageConverter;
    }

    public boolean isKeepKyoTag() {
        return keepKyoTag;
    }

    public void setKeepKyoTag(boolean keepKyoTag) {
        this.keepKyoTag = keepKyoTag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Configuration)) return false;
        Configuration that = (Configuration) o;
        return keepKyoTag == that.keepKyoTag &&
                Objects.equals(watchDirectory, that.watchDirectory) &&
                Objects.equals(tweetFileFilter, that.tweetFileFilter) &&
                Objects.equals(imageFileFilter, that.imageFileFilter) &&
                Objects.equals(imageConverter, that.imageConverter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(watchDirectory, tweetFileFilter, imageFileFilter, imageConverter, keepKyoTag);
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "watchDirectory=" + watchDirectory +
                ", tweetFileFilter=" + tweetFileFilter +
                ", imageFileFilter=" + imageFileFilter +
                ", imageConverter=" + imageConverter +
                ", keepKyoTag=" + keepKyoTag +
                '}';
    }
}
