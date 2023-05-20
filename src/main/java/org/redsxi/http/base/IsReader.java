package org.redsxi.http.base;

import java.io.IOException;
import java.io.InputStream;

public class IsReader {
    public static byte read(InputStream stream) throws IOException {
        int b = stream.read();
        if(b == -1) throw new IOException("Connection Closed");
        return (byte) b;
    }
}
