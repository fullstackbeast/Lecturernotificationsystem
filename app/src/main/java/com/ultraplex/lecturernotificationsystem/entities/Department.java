package com.ultraplex.lecturernotificationsystem.entities;

public class Department {
    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    private String Id;
    private String Name;

    public Department(String id, String name) {
        Id = id;
        Name = name;
    }


}
