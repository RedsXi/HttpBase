package org.redsxi.http.base;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.redsxi.http.base.IsReader.read;

public class RequestParser {
    public static List<Request> registeredRequests = new ArrayList<>();

    public static void register(Request request) {
        registeredRequests.add(request);
    }

    public static Request parse(InputStream stream) throws IOException {
        String methodName;
        String path;
        String protocolName;
        int maj;
        int min;
        StringBuilder strB = new StringBuilder();
        while(true) {
            byte b = read(stream);
            if(b == Bytes.SPACE[0]) {
                methodName = strB.toString();
                strB = new StringBuilder();
                break;
            }
            strB.append((char)b);
        }
        while(true) {
            byte b = read(stream);
            if(b == Bytes.SPACE[0]) {
                path = strB.toString();
                strB = new StringBuilder();
                break;
            }
            strB.append((char)b);
        }
        while(true) {
            byte b = read(stream);
            if(b == Bytes.SLASH[0]) {
                protocolName = strB.toString();
                strB = new StringBuilder();
                break;
            }
            strB.append((char)b);
        }
        while(true) {
            byte b = read(stream);
            if(b == Bytes.DOT[0]) {
                try {
                    maj = Integer.parseInt(strB.toString());
                } catch (NumberFormatException e) {
                    throw new IOException(e);
                }
                strB = new StringBuilder();
                break;
            }
            strB.append((char)b);
        }
        while(true) {
            byte b = read(stream);
            if(b == '\r') {
                if(stream.read() == '\n') {
                    try {
                        min = Integer.parseInt(strB.toString());
                    } catch (NumberFormatException e) {
                        throw new IOException(e);
                    }
                    strB = new StringBuilder();
                    break;
                } else throw new IllegalStateException("Excepted \\r");
            }
            if(b == '\n') throw new IllegalStateException("Excepted \\n");
            strB.append((char)b);
        }
        for(Request singleRequest : registeredRequests) {
            if(
                    singleRequest.getMethodName().equalsIgnoreCase(methodName) &&
                    singleRequest.getProtocolName().equalsIgnoreCase(protocolName) &&
                    singleRequest.getProtocolMajorVersion() == maj &&
                    singleRequest.getProtocolMinorVersion() == min
            ) {
                try {
                    Class<? extends Request> reqClass = singleRequest.getClass();
                    Constructor<? extends Request> constReq = reqClass.getConstructor(String.class);
                    return constReq.newInstance(path);
                } catch(NoSuchMethodException | InstantiationException | IllegalAccessException |
                        InvocationTargetException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }
}
