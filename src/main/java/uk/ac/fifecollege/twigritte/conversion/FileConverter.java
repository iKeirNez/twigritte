package uk.ac.fifecollege.twigritte.conversion;

import java.io.File;
import java.io.IOException;

public interface FileConverter {

    /**
     * Converts a file to another format and returns the converted file.
     *
     * @param file the file to convert
     * @return the converted file
     */
    public File convert(File file) throws IOException;
}
