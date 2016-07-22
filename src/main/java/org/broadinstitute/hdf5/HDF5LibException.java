package org.broadinstitute.hdf5;

public final class HDF5LibException extends RuntimeException{
    private static final long serialVersionUID = -8707336411511157520L;

    public HDF5LibException(String message, Throwable cause) {
        super(message, cause);
    }

    public HDF5LibException(String message) {
        super(message);
    }

}
