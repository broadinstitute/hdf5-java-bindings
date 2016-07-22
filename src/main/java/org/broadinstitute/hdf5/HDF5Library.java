package org.broadinstitute.hdf5;

import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.exceptions.HDF5LibraryException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.broadinstitute.gatk.nativebindings.NativeLibrary;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static ncsa.hdf.hdf5lib.H5.H5PATH_PROPERTY_KEY;

/**
 * Represents the HDF5 IO library.
 *
 * <p>
 *     This is a singleton class whose only instance acts a token for the existence of a working and initialized
 *     HDF support library.
 * </p>
 *
 * <p>
 *     Other components that require of a initialized HDF support library to function must acquire a non-null reference
 *     to this class singleton instance to assert that they can go ahead in performing HDF5 dependent functionality.
 * </p>
 *
 * @author Valentin Ruano-Rubio &lt;valentin@broadinstitute.org&gt;
 */
public final class HDF5Library implements NativeLibrary {
    private static final Logger logger = LogManager.getLogger(HDF5Library.class);

    private static boolean loaded = false;
    public static final String HDF5_LIB_NAME = "jhdf5.2.11.0";

    @Override
    public synchronized boolean load(final File tempDir) {
        if (loaded) {
            return true;
        } else {
            return loadLibrary(tempDir);
        }
    }

    /**
     * Checks whether HDF5 is supported in the current system.
     * <p>
     *     This method won't result in an exception if HDF5 is not currently supported, just return {@code false}.
     * </p>
     * <p>
     *     This method will load the corresponding HDF5 library for further use if available.
     * </p>
     *
     * @param tempDir the temporary directory to which all files, including the library itself, are to be extracted
     *                or {@code null} if the default temporary-file directory is to be used.
     *
     * This method is synchronized to avoid multiple threads loading the library multiple times.
     * @return {@code true} this library is supported on the current hardware+software configuration, {@code false} otherwise.
     */
    public static synchronized boolean loadLibrary(final File tempDir) {
        if (loaded) {
            return true;
        }
        final String resourcePath = System.mapLibraryName(HDF5_LIB_NAME);
        final URL inputUrl = HDF5Library.class.getResource(resourcePath);
        if (inputUrl == null) {
            logger.warn("Unable to find HDF5 library: " + resourcePath);
            return false;
        }

        logger.info(String.format("Trying to load HDF5 library from:\n\t%s", inputUrl.toString()));

        try {

            final File temp = File.createTempFile(FilenameUtils.getBaseName(resourcePath),
                "." + FilenameUtils.getExtension(resourcePath), tempDir);
            FileUtils.copyURLToFile(inputUrl, temp);
            temp.deleteOnExit();
            logger.debug(String.format("Extracted HDF5 to %s\n", temp.getAbsolutePath()));

            final String fileName = temp.getAbsolutePath();

            //we use this property to inform H5 where the native library file is
            System.setProperty(H5PATH_PROPERTY_KEY, fileName);
            final int code = H5.H5open();
            if (code < 0) {
                logger.warn("could not instantiate the HDF5 library. H5open returned a negative value: " + code);
                return false;
            }
            loaded = true;
            return true;
        } catch (final HDF5LibraryException | UnsatisfiedLinkError | IOException | SecurityException e) {
            logger.warn("could not instantiate the HDF5 Library, due to an exception.", e);
            return false;
        }
    }
}
