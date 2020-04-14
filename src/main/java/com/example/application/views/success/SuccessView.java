package com.example.application.views.success;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.example.application.views.main.MainView;

@Route(value = "success", layout = MainView.class)
@PageTitle("Success")
@CssImport("styles/views/success/success-view.css")
public class SuccessView extends Div {

    //when redirected to this page, it will display successful log in message
    public SuccessView() {
        setId("success-view");
        add(new Label("You have successfully logged in!"));

    }

}
