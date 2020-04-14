package com.example.application.backend;

import org.apache.commons.codec.digest.DigestUtils;

//pojo for user object
public class User {
    //attributes
    private String userName;
    private String passWord;
    private String firstName;
    private String lastName;
    private String phoneNumber;

    //constructor
    public User(String userName, String passWord, String firstName, String lastName, String phoneNumber){
        this.userName = userName;
        this.passWord = getHash(passWord); //hash password entered from form and set to be the password for this user
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    //getters and setters for each field
    public String getUserName(){
        return this.userName;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getPassWord(){
        return this.passWord;
    }
    public void setPassWord(String passWord){
        this.passWord = getHash(passWord);
    } //still want to hash password

    public String getFirstName(){
        return this.firstName;
    }
    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public String getLastName(){
        return this.lastName;
    }
    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public String getPhoneNumber(){
        return this.phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    //for sha256 hashing
    public static String getHash(String pass){ //get sha256 hash of given string and return it
        String hash = DigestUtils.sha256Hex(pass);
        return hash;
    }
}
