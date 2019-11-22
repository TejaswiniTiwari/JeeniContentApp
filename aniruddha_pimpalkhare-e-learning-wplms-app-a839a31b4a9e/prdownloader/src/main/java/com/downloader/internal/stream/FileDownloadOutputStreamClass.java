package com.downloader.internal.stream;


import java.io.IOException;
import java.io.OutputStream;

public abstract class FileDownloadOutputStreamClass extends OutputStream {

    /**
     * Writes <code>len</code> bytes from the specified byte array
     * starting at offset <code>off</code> to this file.
     */
//    abstract void write(byte b[], int off, int len) throws IOException;

    /**
     * Flush all buffer to system and force all system buffers to synchronize with the underlying
     * device.
     */
    public abstract void flushAndSync() throws IOException;

    /**
     * Closes this output stream and releases any system resources associated with this stream. The
     * general contract of <code>close</code> is that it closes the output stream. A closed stream
     * cannot perform output operations and cannot be reopened.
     */
//    abstract void close() throws IOException;

    /**
     * Sets the file-pointer offset, measured from the beginning of this file, at which the next
     * read or write occurs.  The offset may be set beyond the end of the file.
     */
    public abstract void seek(long offset) throws IOException, IllegalAccessException;

    /**
     * Sets the length of this file.
     */
    public abstract void setLength(final long newLength) throws IOException, IllegalAccessException;

}