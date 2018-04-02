package com.screenripper.tests.webdriver;

public class Lesson {
    private String courseURL = "";
    private String courseTitle = "";
    private String lessonURL = "";
    private String lessonTitle = "";
    private int numberOfVideos;

    private static final String separator = "|";

    public Lesson(String courseTitle,
                  String courseURL,
                  String lessonTitle,
                  String lessonURL,
                  int numberOfVideos) {

        this.courseURL = courseURL;
        this.courseTitle = courseTitle;
        this.lessonURL = lessonURL;
        this.lessonTitle = lessonTitle;
        this.numberOfVideos = numberOfVideos;
    }

    public String getCourseTitleURLLessonTitleURLInOneString() {
        String s = "";
        return s.concat(courseTitle)
                .concat(separator)
                .concat(courseURL)
                .concat(separator)
                .concat(lessonTitle)
                .concat(separator)
                .concat(lessonURL)
                .concat(separator)
                .concat(Integer.toString(numberOfVideos));
    }

    public String getCourseURL() {
        return courseURL;
    }

    public void setCourseURL(String courseURL) {
        this.courseURL = courseURL;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getLessonURL() {
        return lessonURL;
    }

    public void setLessonURL(String lessonURL) {
        this.lessonURL = lessonURL;
    }

    public String getLessonTitle() {
        return lessonTitle;
    }

    public void setLessonTitle(String lessonTitle) {
        this.lessonTitle = lessonTitle;
    }

    public int getNumberOfVideos() {
        return numberOfVideos;
    }

    public void setNumberOfVideos(int numberOfVideos) {
        this.numberOfVideos = numberOfVideos;
    }
}
