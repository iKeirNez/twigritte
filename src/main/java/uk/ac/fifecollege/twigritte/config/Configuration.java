package uk.ac.fifecollege.twigritte.config;

import java.io.File;
import java.io.FileFilter;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Configuration {
    protected File pollDirectory = null;
    protected TimeUnit pollTimeUnit = TimeUnit.SECONDS;
    protected long pollTime = 5;
    protected FileFilter tweetFileFilter = null;
    protected FileFilter imageFileFilter = null;
    protected boolean keepKyoTag = true;

    protected Configuration() {}

    public Configuration(File pollDirectory, TimeUnit pollTimeUnit, long pollTime, FileFilter tweetFileFilter, FileFilter imageFileFilter, boolean keepKyoTag) {
        this.pollDirectory = pollDirectory;
        this.pollTimeUnit = pollTimeUnit;
        this.pollTime = pollTime;
        this.tweetFileFilter = tweetFileFilter;
        this.imageFileFilter = imageFileFilter;
        this.keepKyoTag = keepKyoTag;
    }

    public File getPollDirectory() {
        return pollDirectory;
    }

    public void setPollDirectory(File pollDirectory) {
        this.pollDirectory = pollDirectory;
    }

    public TimeUnit getPollTimeUnit() {
        return pollTimeUnit;
    }

    public void setPollTimeUnit(TimeUnit pollTimeUnit) {
        this.pollTimeUnit = pollTimeUnit;
    }

    public long getPollTime() {
        return pollTime;
    }

    public void setPollTime(long pollTime) {
        this.pollTime = pollTime;
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
        return pollTime == that.pollTime &&
                keepKyoTag == that.keepKyoTag &&
                Objects.equals(pollDirectory, that.pollDirectory) &&
                pollTimeUnit == that.pollTimeUnit &&
                Objects.equals(tweetFileFilter, that.tweetFileFilter) &&
                Objects.equals(imageFileFilter, that.imageFileFilter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pollDirectory, pollTimeUnit, pollTime, tweetFileFilter, imageFileFilter, keepKyoTag);
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "pollDirectory=" + pollDirectory +
                ", pollTimeUnit=" + pollTimeUnit +
                ", pollTime=" + pollTime +
                ", tweetFileFilter=" + tweetFileFilter +
                ", imageFileFilter=" + imageFileFilter +
                ", keepKyoTag=" + keepKyoTag +
                '}';
    }
}
