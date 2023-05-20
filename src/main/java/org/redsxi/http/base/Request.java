package org.redsxi.http.base;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static org.redsxi.http.base.Bytes.CRLF;

@SuppressWarnings("ALL")
public abstract class Request {

    public abstract String getMethodName();
    public abstract String getProtocolName();
    public abstract short getProtocolMajorVersion();
    public abstract short getProtocolMinorVersion();

    public String getProtocolVersion() {
        return getProtocolMajorVersion() + "." + getProtocolMinorVersion();
    }

    public void sendRequest(OutputStream stream, boolean... sendCrLf) throws IOException {
        stream.write(getMethodName().getBytes(StandardCharsets.UTF_8));
        stream.write(Bytes.SPACE);
        stream.write(path.getBytes(StandardCharsets.UTF_8));
        stream.write(Bytes.SPACE);
        stream.write(getProtocolName().getBytes(StandardCharsets.UTF_8));
        stream.write(Bytes.SLASH);
        stream.write(getProtocolVersion().getBytes(StandardCharsets.UTF_8));
        if(sendCrLf.length == 0) return;
        if(sendCrLf[0]) {
            stream.write(CRLF);
        }
    }

    String path;

    public Request(String path) {
        this.path = path;
    }

    public Request() {
        path = "/";
    }

    public String getPath() {
        return path;
    }
}
