package com.ultraplex.lecturernotificationsystem;

public class CourseListItem {
    private String TextCourseTitle;
    private String TextCourseDepartment;
    private String TextCourseLecturer;
    private String TextCourseCode;

    public CourseListItem(String textCourseTitle, String textCourseDepartment, String textCourseLecturer, String textCourseCode) {
        TextCourseTitle = textCourseTitle;
        TextCourseDepartment = textCourseDepartment;
        TextCourseLecturer = textCourseLecturer;
        TextCourseCode = textCourseCode;
    }

    public String getTextCourseTitle() {
        return TextCourseTitle;
    }

    public void setTextCourseTitle(String textCourseTitle) {
        TextCourseTitle = textCourseTitle;
    }

    public String getTextCourseDepartment() {
        return TextCourseDepartment;
    }

    public void setTextCourseDepartment(String textCourseDepartment) {
        TextCourseDepartment = textCourseDepartment;
    }

    public String getTextCourseLecturer() {
        return TextCourseLecturer;
    }

    public void setTextCourseLecturer(String textCourseLecturer) {
        TextCourseLecturer = textCourseLecturer;
    }

    public String getTextCourseCode() {
        return TextCourseCode;
    }

    public void setTextCourseCode(String textCourseCode) {
        TextCourseCode = textCourseCode;
    }


}
