package com.example.application.backend;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BackendService {

    //will simulate a backend holding list of all users
    public static List<User> users;

    {
        users = new ArrayList<User>(); //will hold list of all current users
        //add first sample user
        users.add(new User("random.user@gmail.com", "@Example1", "Matt", "Smith",
        "+1 4128315622"));
    }

    public static List<User> getUsers() {
        return users;
    }

}
