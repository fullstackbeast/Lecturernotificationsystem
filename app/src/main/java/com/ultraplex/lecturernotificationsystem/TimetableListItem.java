package com.ultraplex.lecturernotificationsystem;

public class TimetableListItem {

    public  String getId(){
        return Id;
    }

    public String getStartTime() {
        return StartTime;
    }

    public String getStoptime() {
        return Stoptime;
    }

    public String getCourseTitle() {
        return CourseTitle;
    }

    public String getCourseCode() {
        return CourseCode;
    }

    public String getLevel() {
        return Level;
    }

    private String Id;
    private String StartTime;
    private String Stoptime;
    private String CourseTitle;
    private String CourseCode;
    private String Level;


    public TimetableListItem(String id,String startTime, String stoptime, String courseTitle, String courseCode, String level) {
        Id = id;
        StartTime = startTime;
        Stoptime = stoptime;
        CourseTitle = courseTitle;
        CourseCode = courseCode;
        Level = level;
    }
}
