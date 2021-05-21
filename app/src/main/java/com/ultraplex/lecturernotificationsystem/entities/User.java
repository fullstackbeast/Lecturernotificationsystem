package com.ultraplex.lecturernotificationsystem.entities;

public class User {
    private String Id;
    private String FirstName;
    private String LastName;
    private String Username;
    private String Password;
    private String Type;


    public User(String id, String firstName, String lastName, String username, String password, String type) {
        Id = id;
        FirstName = firstName;
        LastName = lastName;
        Username = username;
        Password = password;
        Type = type;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }


}
