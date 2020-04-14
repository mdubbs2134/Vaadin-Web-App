package com.example.application.views.login;

import com.example.application.backend.BackendService;
import com.example.application.backend.User;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.example.application.views.main.MainView;

import java.util.ArrayList;

@Route(value = "login", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Login")
@CssImport("styles/views/login/login-view.css")
public class LoginView extends Div {
    //fields and buttons
    private TextField username = new TextField();
    private PasswordField password = new PasswordField();

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Login");

    public LoginView() {
        VerticalLayout wrapper = createWrapper(); //make wrapper
        createTitle(wrapper);
        createFormLayout(wrapper);
        createButtonLayout(wrapper);

        username.setAutofocus(true); //autoficus to username field

        cancel.addClickListener(e -> { //user wishes to cancel
            //clear fields
            username.clear();
            password.clear();
        });

        save.addClickListener(e -> { //user wishes to log in
            //check to see if the username exists
            if(exists()){ //exists() also checks for correct password
                //redirect to successful login page
                getUI().ifPresent(ui -> ui.navigate("success")); //looks at the SuccessView class
            }
            else{
                Notification.show("Could not log in, please try again").setDuration(5000);
            }
        });

        add(wrapper);
    }

    public boolean exists(){ //function to see if username exists and checks password entered (hashes password) with password for that user
        ArrayList<User> users = (ArrayList<User>) BackendService.getUsers(); //get list of current users who have registered
        for(int i = 0; i< users.size(); i++){ //loop through current users
            //see if username entered exists, and hash password entered to check if password for that user is the same
            if(users.get(i).getUserName().equals(username.getValue()) && User.getHash(password.getValue()).equals(users.get(i).getPassWord())){
                return true;  //user exists and password is correct
            }
        }
        //user does not exist or password is entered incorrectly
        return false;
    }

    private void createTitle(VerticalLayout wrapper) { //make title
        H1 h1 = new H1("Login");
        wrapper.add(h1);
    }

    private VerticalLayout createWrapper() { //make wrapper
        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setId("wrapper");
        wrapper.setSpacing(false);
        return wrapper;
    }

    private void createFormLayout(VerticalLayout wrapper) { //format the fields
        FormLayout formLayout = new FormLayout();
        FormLayout.FormItem usernameFormItem = addFormItem(wrapper, formLayout,
                username, "Username");
        formLayout.setColspan(usernameFormItem, 2);
        FormLayout.FormItem passwordFormItem = addFormItem(wrapper, formLayout,
                password, "Password");
        formLayout.setColspan(passwordFormItem, 2);
    }

    private void createButtonLayout(VerticalLayout wrapper) { //format buttons
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout
                .setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(cancel);
        buttonLayout.add(save);
        wrapper.add(buttonLayout);
    }

    private FormLayout.FormItem addFormItem(VerticalLayout wrapper,
            FormLayout formLayout, Component field, String fieldName) {
        FormLayout.FormItem formItem = formLayout.addFormItem(field, fieldName);
        wrapper.add(formLayout);
        field.getElement().getClassList().add("full-width");
        return formItem;
    }

}
