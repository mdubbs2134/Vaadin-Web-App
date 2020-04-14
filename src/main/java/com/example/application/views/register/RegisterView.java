package com.example.application.views.register;

//for passay
import com.vaadin.flow.component.html.Label;
import org.passay.Rule;
import org.passay.LengthRule;
import org.passay.WhitespaceRule;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordValidator;
import org.passay.PasswordData;
import org.passay.RuleResult;

//for google phone number validation
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

//for email validation
import org.apache.commons.validator.routines.EmailValidator;

import com.example.application.backend.*;
import java.util.ArrayList;
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
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.EmailField;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.example.application.views.main.MainView;

@Route(value = "register", layout = MainView.class)
@PageTitle("Register")
@CssImport("styles/views/register/register-view.css")
public class RegisterView extends Div {
    //fields and buttons
    private final TextField firstname = new TextField();
    private final TextField lastname = new TextField();
    private final EmailField username = new EmailField();
    private final PasswordField password = new PasswordField();
    private final PasswordField password2 = new PasswordField();
    private final TextField phonenumber = new TextField();
    private final TextField countryCode = new TextField();
    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Register"); //done registering

    private String userPhone = ""; //will set validated phone number to this variable

    //Notifications
    private ArrayList<Notification> notifications = new ArrayList<>(); //hold notifications for user
    private Notification badNames = new Notification("Please enter name");
    private Notification userExistsAlready = new Notification("User exists already");
    private Notification noMatch = new Notification("Passwords must match");
    private Notification badEmail = new Notification("Invalid email");
    private Notification badCC = new Notification("Enter country code");
    private Notification badPhoneNum = new Notification("Invalid phone number");
    private Notification badPassWord = new Notification("Invalid password, see password rules button");

    //make label for password rules for user
    Label rules = new Label("Password must contain an uppercase letter, a lowercase letter, a digit, and a symbol. " +
            "Password must not contain any whitespace. Password must be between 8 and 20 characters.");

    public RegisterView() {
        setId("register-view");
        //make wrapper and format fields
        VerticalLayout wrapper = createWrapper();
        createTitle(wrapper);
        createFormLayout(wrapper);
        createButtonLayout(wrapper);

        rules.setVisible(false); //set rules label to be invisible, user will have option to see rules with a button
        Button passRules = new Button("See Password Rules");
        passRules.addClickListener(e -> {
            //if rules label is visible, set to not visible
            //if rules label is not visible, set to visible
            rules.setVisible(!rules.isVisible());
        });


        //add each notification made above to the notifications array to start
        notifications.add(badNames);
        notifications.add(userExistsAlready);
        notifications.add(noMatch);
        notifications.add(badEmail);
        notifications.add(badCC);
        notifications.add(badPhoneNum);

        firstname.setAutofocus(true); //autofocus to first field
        username.setPlaceholder("This will be your username to login"); //message for the user

        //format country code field
        countryCode.setWidth("100px");
        countryCode.setMaxLength(5);
        countryCode.setPlaceholder("+1");

        //user wishes to cancel current registration
        cancel.addClickListener(e -> {
            notifications.forEach(notification -> notification.close()); //close out notifications
            rules.setVisible(false); //set rules to be invisible again
            reset(); //reset all of the fields
        });

        //user wishes to complete registration
        save.addClickListener(e -> {
            notifications.forEach(notification -> notification.close()); //close out notifications
            if(goodInput() & userDoesNotExist()){ //make sure input is good and the username does not exist already
                //if input is good and is a new username, make the new user
                User newUser = new User(username.getValue(), password.getValue(),
                        firstname.getValue(), lastname.getValue(), userPhone);

                //add this new user to the list holding current users in the BackendService class
                //The BackendService class will represent a backend database holding list of current users
                BackendService.users.add(newUser);

                Notification.show("You have successfully registered").setDuration(5000); //successful registration message for user
                //reset the fields and set rules label to be false again
                rules.setVisible(false);
                reset();

            }
            else{ //bad input or username exists already
                notifications.forEach(notification -> { //notify user of possible errors in their input
                    notification.setDuration(10000); //set each notification to last 10s
                    notification.open(); //open the notification
                });
            }
        });
        add(wrapper); //add wrapper
        //add button for seeing rules and label for password rules to wrapper
        wrapper.add(passRules);
        wrapper.add(rules);
    }

    public boolean userDoesNotExist(){ //make sure the user is not already registered
        ArrayList<User> users = (ArrayList<User>) BackendService.getUsers(); //get current users registered
        for(int i = 0; i< users.size(); i++){ //loop through each user
            if(username.getValue().equals(users.get(i).getUserName())){ //if username entered in field already exists
                //notify user that the username exists already
                if(!notifications.contains(userExistsAlready)){ //only add this notification if it is not already in the list of notifications
                    notifications.add(userExistsAlready);
                    //this check was made to keep the user's screen from overflowing with notifications
                        //do not want to add this notification to the notifications list if it already exists in the list, keeps each notification
                            //from being displayed twice to the user
                }
                return false;
            }
        }
        //if code has gotten to here, it means that the username entered does not exists already and is good to go
        notifications.remove(userExistsAlready); //remove notification of user exists already
        return true;
    }

    public boolean goodInput(){ //checks to make sure input entered is good/valid, calls helper functions
        return goodNames() & passWordComplexity() & passWordMatch() & emailValid() & goodPhoneNumber();
    }

    public boolean goodNames(){ //makes both first and last name fields are not empty
        if(firstname.getValue().length() > 0 && lastname.getValue().length() > 0){ //good input
            notifications.remove(badNames); //remove notification b/c fields are not empty
            return true;
        }
        else{ //empty fields for first and last names
            if(!notifications.contains(badNames)){ //make sure this notification does not exist already
                notifications.add(badNames); //add notification to list of notifications that will be displayed to user
            }
            return false;
        }
    }

    public boolean passWordMatch(){ //make sure both password fields match, will check for password complexity in function below
        //checks that both password fields are not empty so it does not mistakenly match 2 empty password fields
            //and check if both password fields match
        if(password.getValue().length() > 0 && password2.getValue().length() > 0 && password.getValue().equals(password2.getValue())){
            //both password fields match
            notifications.remove(noMatch); //remove this notification of not matching passwords
            return true;
        } //passwords do not match
        else{
            if(!notifications.contains(noMatch)){
                notifications.add(noMatch);
            }
            return false;
        }
    }

    public boolean passWordComplexity(){ //check for password complexity using passay library
        //referenced tutorialspoint for standard password complexity validation policy
        ArrayList<Rule> rules = new ArrayList<>();  //make list of rules
        //add each rule to list of rules
        rules.add(new LengthRule(8, 20));  //length should be between 8 and 20
        rules.add(new WhitespaceRule());  //should not contain whitespace
        rules.add(new CharacterRule(EnglishCharacterData.UpperCase, 1));  //have at least 1 uppercase letter
        rules.add(new CharacterRule(EnglishCharacterData.LowerCase, 1));  //have at least 1 lowercase letter
        rules.add(new CharacterRule(EnglishCharacterData.Digit, 1));     //have at least 1 digit
        rules.add(new CharacterRule(EnglishCharacterData.Special, 1));  //have at least 1 special character
        PasswordValidator validator = new PasswordValidator(rules);
        PasswordData p = new PasswordData(password.getValue());  //get password from 1st password field
        RuleResult result = validator.validate(p);  //get results
        if(result.isValid()){ //valid password
            notifications.remove(badPassWord); //remove invalid password notification
            return true;
        }
        else{ //not a valid password
            if(!notifications.contains(badPassWord)){ //make sure notification is not already in notifications list
                notifications.add(badPassWord);
            }
            return false;
        }
    }

    public boolean emailValid(){  //check to validate email using EmailValidator
        EmailValidator validator = EmailValidator.getInstance();   //make an EmailValidator
        if (!validator.isValid(username.getValue())) {  //if the username entered is invalid
            if(!notifications.contains(badEmail)){
                notifications.add(badEmail); //notify user of invalid email
            }
            return false;
        }
        //if code has gotten to this point, it means email is valid
        notifications.remove(badEmail);  //remove invalid email notification
        return true;
    }

    public boolean goodPhoneNumber(){  //validate phone using google's phone number validation library
        //referenced java67.com for implementing google's phone number validation
        //make sure the user has filled out a country code and make sure it starts with '+' if user has filled one out
        if(countryCode.isEmpty() || countryCode.getValue().toCharArray()[0] != '+'){ //n
            if(!notifications.contains(badCC)){
                notifications.add(badCC); //notify user
            }
            return false;  //return false here because we want to make sure user enters a country code to be validated
        }
        else{ //country code is entered
            notifications.remove(badCC);  //remove no country code entered notification
            PhoneNumberUtil phone = PhoneNumberUtil.getInstance(); //get phoneNumber util instance
            PhoneNumber phoneNumber = null; //set phone number to be null
            try{
                //parse phone number entered, combine country code and phone number in parse()
                phoneNumber = phone.parse(countryCode.getValue() + phonenumber.getValue(), "IN");
            } catch (NumberParseException e){
                if(!notifications.contains(badPhoneNum)){
                    notifications.add(badPhoneNum); //notify user of bad phone number input
                }
                return false; //phone number cannot be parsed so return false here
            }
            if(phone.isValidNumber(phoneNumber)){ //valid phone number
                notifications.remove(badPhoneNum);
                // create/format the phone number combined with country code to be stored in user's phone number
                userPhone = "+" + phoneNumber.getCountryCode() + " " + phoneNumber.getNationalNumber(); //will be used for the user's phone number
                return true;
            }
            else{ //not valid
                if(!notifications.contains(badPhoneNum)){
                    notifications.add(badPhoneNum); //notify user
                }
                return false;
            }
        }
    }

    public void reset(){ //reset fields
        firstname.clear();
        lastname.clear();
        username.clear();
        password.clear();
        password2.clear();
        phonenumber.clear();
        countryCode.clear();
    }

    private void createTitle(VerticalLayout wrapper) {  //make title for page
        H1 h1 = new H1("Register");
        wrapper.add(h1);
    }

    private VerticalLayout createWrapper() { //make wrapper for page
        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setId("wrapper");
        wrapper.setSpacing(false);
        return wrapper;
    }

    private void createFormLayout(VerticalLayout wrapper) {
        //format layout of the fields
        FormLayout formLayout = new FormLayout();
        addFormItem(wrapper, formLayout, firstname, "First Name");
        addFormItem(wrapper, formLayout, lastname, "Last Name");
        FormLayout.FormItem emailFormItem = addFormItem(wrapper, formLayout,
                username, "Email");
        formLayout.setColspan(emailFormItem, 2);
        FormLayout.FormItem passWordFormItem = addFormItem(wrapper, formLayout,
                password, "Password");
        formLayout.setColspan(passWordFormItem, 2);
        FormLayout.FormItem passWordFormItem2 = addFormItem(wrapper, formLayout,
                password2, "Re-enter Password");
        formLayout.setColspan(passWordFormItem2, 2);
        addFormItem(wrapper, formLayout, phonenumber, "Phone Number");
        addFormItem(wrapper, formLayout, countryCode, "Country Code");

    }

    private void createButtonLayout(VerticalLayout wrapper) {
        //format buttons
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

    private FormLayout.FormItem addFormItem(VerticalLayout wrapper, FormLayout formLayout, Component field, String fieldName) {
        FormLayout.FormItem formItem = formLayout.addFormItem(field, fieldName);
        wrapper.add(formLayout);
        field.getElement().getClassList().add("full-width");
        return formItem;
    }

}
