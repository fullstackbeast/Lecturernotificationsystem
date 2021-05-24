package com.ultraplex.lecturernotificationsystem.entities;

public class Course {

    public String Id;

    private String Code;

    private String Title;

    private String DepartmentId;

    private String LevelId;

    private String LecturerId;

    public Course(String id, String code, String title, String departmentId, String levelId, String lecturerId) {
        Id = id;
        Code = code;
        Title = title;
        DepartmentId = departmentId;
        LevelId = levelId;
        LecturerId = lecturerId;
    }

    public String getId() {
        return Id;
    }

    public String getCode() {
        return Code;
    }

    public String getTitle() {
        return Title;
    }

    public String getDepartmentId() {
        return DepartmentId;
    }

    public String getLevelId() {
        return LevelId;
    }

    public String getLecturerId() {
        return LecturerId;
    }


}
