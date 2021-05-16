package com.ultraplex.lecturernotificationsystem.entities;

public class User {
    public String Id;
    public String FirstName;
    public String LastName;
    public String Username;
    public String Password;

    public User(String id, String firstName, String lastName, String username, String password) {
        Id = id;
        FirstName = firstName;
        LastName = lastName;
        Username = username;
        Password = password;
    }
}
