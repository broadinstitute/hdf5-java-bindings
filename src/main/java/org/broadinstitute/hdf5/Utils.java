package org.broadinstitute.hdf5;

import java.io.File;
import java.io.IOException;

public final class Utils {
    private Utils(){}

    /**
     * Checks that an {@link Object} is not {@code null} and returns the same object or throws an {@link IllegalArgumentException}
     * @param object any Object
     * @param message the text message that would be passed to the exception thrown when {@code o == null}.
     * @return the same object
     * @throws IllegalArgumentException if a {@code o == null}
     */
    public static <T> T nonNull(final T object, final String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
        return object;
    }

    /**
     * Checks that an Object {@code object} is not null and returns the same object or throws an {@link IllegalArgumentException}
     * @param object any Object
     * @return the same object
     * @throws IllegalArgumentException if a {@code o == null}
     */
    public static <T> T nonNull(final T object) {
        return Utils.nonNull(object, "Null object is not allowed here.");
    }

    /**
     * Creates a temp file that will be deleted on exit
     *
     * @param name Prefix of the file.
     * @param extension Extension to concat to the end of the file.
     * @return A file in the temporary directory starting with name, ending with extension, which will be deleted after the program exits.
     */
    public static File createTempFile(String name, String extension) {
        try {
            final File file = File.createTempFile(name, extension);
            file.deleteOnExit();
            // Mark corresponding indices for deletion on exit as well just in case an index is created for the temp file:
            return file;
        } catch (IOException ex) {
            throw new HDF5LibException("Cannot create temp file: " + ex.getMessage(), ex);
        }
    }
}
