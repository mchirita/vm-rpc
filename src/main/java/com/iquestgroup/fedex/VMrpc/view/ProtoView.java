package com.iquestgroup.fedex.VMrpc.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class ProtoView  extends VerticalLayout{

    TextArea protoContent = new TextArea("Insert proto file content");
    Button loadButton = new Button("Load");
    
    public ProtoView() {
        add(protoContent);
        add(loadButton);
    }
}
