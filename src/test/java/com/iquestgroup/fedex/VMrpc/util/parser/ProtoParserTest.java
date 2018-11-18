package com.iquestgroup.fedex.VMrpc.util.parser;

import com.google.common.collect.Lists;
import com.iquestgroup.fedex.VMrpc.model.ProtoField;
import com.iquestgroup.fedex.VMrpc.model.ProtoFile;
import com.iquestgroup.fedex.VMrpc.model.ProtoMessageType;
import com.iquestgroup.fedex.VMrpc.model.ProtoMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
public class ProtoParserTest {

    private ProtoParser protoParser = new ProtoParser();

    @Test
    public void protoParserFromFile_onHappyFlow() throws IOException {
        // Given
        String filePath = "helloWorld-test.proto";

        // When
        ProtoFile protoFile = protoParser.parseFile(filePath);

        // Then
        assertThat(protoFile).isNotNull();
        assertThat(protoFile.getPackageName()).isEqualTo("helloworld");

        assertServices(protoFile);

        assertMessageTypes(protoFile);
        assertEnumTypes(protoFile);

        assertThat(protoFile.getEnumTypes().size()).isEqualTo(3);
    }

    private void assertServices(ProtoFile protoFile) {
        assertThat(protoFile.getServices().size()).isEqualTo(1);
        assertThat(protoFile.getServices().get(0).getMethods()).isEqualTo(createExpectedServiceMethods());
    }

    private void assertMessageTypes(ProtoFile protoFile) {
        assertThat(protoFile.getMessageTypes().size()).isEqualTo(2);
        ProtoMessageType protoMessageType = protoFile.getMessageTypes().get(0);
        assertThat(protoMessageType.getName()).isEqualTo("HelloRequest");
        assertThat(protoMessageType.getFields().size()).isEqualTo(1);
        protoMessageType = protoFile.getMessageTypes().get(1);
        assertThat(protoMessageType.getName()).isEqualTo("HelloReply");
        assertThat(protoMessageType.getFields().size()).isEqualTo(2);
    }

    private void assertEnumTypes(ProtoFile protoFile) {
        assertThat(protoFile.getEnumTypes().size()).isEqualTo(3);
        for (int i = 0; i < protoFile.getEnumTypes().size(); i++) {
            assertThat(protoFile.getEnumTypes().get(i).getName()).isEqualTo("TestEnum" + i);
            assertThat(protoFile.getEnumTypes().get(i).getFields()).isEqualTo(createExpectedEnumFields());
        }
    }

    private ArrayList<ProtoField> createExpectedEnumFields() {
        return Lists.newArrayList(
                ProtoField.newBuilder().fieldName("UNIVERSAL").position(0).build(),
                ProtoField.newBuilder().fieldName("WEB").position(1).build()
        );
    }

    private ArrayList<ProtoMethod> createExpectedServiceMethods() {
        return Lists.newArrayList(
                ProtoMethod.newBuilder().name("SayHello").inputType("HelloRequest").outputType("HelloReply").build(),
                ProtoMethod.newBuilder().name("SayHola").inputType("HelloRequest").outputType("HelloReply").build()
        );
    }
}
