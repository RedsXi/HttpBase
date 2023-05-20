package org.redsxi.http.base;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.redsxi.http.base.Bytes.*;

public abstract class Response {

    public abstract int getResponseCode();
    public abstract String getResponseMessage();
    public abstract String getProtocolName();
    public abstract short getProtocolMajorVersion();
    public abstract short getProtocolMinorVersion();

    public String getProtocolVersion() {
        return getProtocolMajorVersion() + "." + getProtocolMinorVersion();
    }

    public void sendResponse(OutputStream stream, boolean... sendCrlf) throws IOException {
        stream.write(getProtocolName().getBytes(UTF_8));
        stream.write(SLASH);
        stream.write(getProtocolVersion().getBytes(UTF_8));
        stream.write(SPACE);
        stream.write(Integer.toString(getResponseCode()).getBytes(UTF_8));
        stream.write(SPACE);
        stream.write(getResponseMessage().getBytes(UTF_8));
        if(sendCrlf.length == 0) return;
        if(sendCrlf[0]) {
            stream.write(CRLF);
        }
    }
}