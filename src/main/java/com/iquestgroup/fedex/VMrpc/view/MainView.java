package com.iquestgroup.fedex.VMrpc.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route
public class MainView extends VerticalLayout {
    
    private final ProtoView protoView;
    
    public MainView(ProtoView protoView) {
        this.protoView = protoView;
        add(protoView);
    }

}
