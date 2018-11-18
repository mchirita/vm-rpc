package com.iquestgroup.fedex.VMrpc.util;

import com.google.common.collect.Maps;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Label;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Type;

import java.util.Map;

public class FieldMappings {
    public static Map<String, Type> SIGNATURE_TYPES = Maps.newHashMap();
    public static Map<String, Label> LABEL_TYPES = Maps.newHashMap();

    static {
        SIGNATURE_TYPES.put("bool", Type.TYPE_BOOL);
        SIGNATURE_TYPES.put("string", Type.TYPE_STRING);
        SIGNATURE_TYPES.put("bytes", Type.TYPE_BYTES);

        SIGNATURE_TYPES.put("double", Type.TYPE_DOUBLE);
        SIGNATURE_TYPES.put("float", Type.TYPE_FLOAT);

        SIGNATURE_TYPES.put("int32", Type.TYPE_INT32);
        SIGNATURE_TYPES.put("int64", Type.TYPE_INT64);
        SIGNATURE_TYPES.put("uint32", Type.TYPE_UINT32);
        SIGNATURE_TYPES.put("uint64", Type.TYPE_UINT64);
        SIGNATURE_TYPES.put("sint32", Type.TYPE_SINT32);
        SIGNATURE_TYPES.put("sint64", Type.TYPE_SINT64);
        SIGNATURE_TYPES.put("fixed32", Type.TYPE_FIXED32);
        SIGNATURE_TYPES.put("fixed64", Type.TYPE_FIXED64);
        SIGNATURE_TYPES.put("sfixed32", Type.TYPE_SFIXED32);
        SIGNATURE_TYPES.put("sfixed64", Type.TYPE_SFIXED64);

        LABEL_TYPES.put("repeated", Label.LABEL_REPEATED);
        LABEL_TYPES.put("optional", Label.LABEL_OPTIONAL);
        LABEL_TYPES.put("required", Label.LABEL_REQUIRED);
    }

    public static String getKeyFromLabel(Label label) {
        for (Map.Entry<String, Label> entry : LABEL_TYPES.entrySet()) {
            if (entry.getValue().equals(label)) {
                return entry.getKey();
            }
        }
        throw new IllegalArgumentException("No key for label: " + label);
    }

    public static String getKeyFromType(Type type) {
        for (Map.Entry<String, Type> entry : SIGNATURE_TYPES.entrySet()) {
            if (entry.getValue().equals(type)) {
                return entry.getKey();
            }
        }
        throw new IllegalArgumentException("No key for type: " + type);
    }
}
