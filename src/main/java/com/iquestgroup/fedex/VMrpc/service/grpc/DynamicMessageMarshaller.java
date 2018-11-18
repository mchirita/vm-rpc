package com.iquestgroup.fedex.VMrpc.service.grpc;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.protobuf.DynamicMessage;
import io.grpc.MethodDescriptor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DynamicMessageMarshaller implements MethodDescriptor.Marshaller<Object> {
    public static final DynamicMessageMarshaller INSTANCE = new DynamicMessageMarshaller();

    @Override
    public InputStream stream(Object value) {
        DynamicMessage msg = (DynamicMessage) value;
        return msg.toByteString().newInput();
    }

    @Override
    public Object parse(InputStream stream) {
        try {
            return CharStreams.toString(new InputStreamReader(stream, Charsets.UTF_8));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}