package org.redsxi.http.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.redsxi.http.base.Bytes.CRLF;
import static org.redsxi.http.base.IsReader.read;

@SuppressWarnings("all")
public class Headers {
    private Map<String, String> headers = new HashMap<>();

    public Headers() {

    }

    public Headers put(String key, String value) {
        if(!headers.containsKey(key)) headers.put(key, value);
        return this;
    }

    public String get(String key) {
        return headers.getOrDefault(key, "");
    }

    public boolean contains(String key) {
        return headers.containsKey(key);
    }

    public static Headers parse(InputStream stream) throws IOException {
        Headers h = new Headers();
        while(true) {
            String keyStr;
            String valueStr;

            StringBuilder builder = new StringBuilder();

            while(true) {

                byte b = read(stream);
                if(b == '\r') {
                    b = read(stream);
                    if(b == '\n') {
                        return h;
                    } else throw new IOException("Unexpected \\r");
                }
                if(b == ':') {
                    read(stream);
                    keyStr = builder.toString();
                    break;
                }
                builder.append((char)b);
            }

            builder = new StringBuilder();

            while(true) {

                byte b = read(stream);
                if(b == '\r') {
                    b = read(stream);
                    if(b == '\n') {
                        valueStr = builder.toString();
                        break;
                    } else throw new IOException("Unexpected \\r");
                }
                builder.append((char)b);
            }

            h.put(keyStr, valueStr);
        }
    }

    public void sendHeaders(OutputStream stream, boolean... sendTwiceCrlf) throws IOException {
        int index = 0;
        for(Map.Entry<String, String> line : headers.entrySet()) {
            stream.write((line.getKey() + ": " + line.getValue()).getBytes(StandardCharsets.UTF_8));
            if(!(index + 1 == headers.size())) stream.write(CRLF);
            index++;
        }
        if(sendTwiceCrlf.length == 0) return;
        if(sendTwiceCrlf[0]) {
            stream.write(CRLF);
            stream.write(CRLF);
        }
    }

    public Map<String, String> map() {return headers;}
}
