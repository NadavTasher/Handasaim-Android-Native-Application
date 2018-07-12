package nadav.tasher.handasaim.architecture.appcore.components;

import java.util.ArrayList;
import java.util.Arrays;

public class Subject {
    private int schoolHour,beginingMinute,endingMinute;
    private String description, subjectName;
    private boolean isTest=false;
    private ArrayList<String> teacherNames;
    private Classroom classroom;

    public Subject(Classroom classroom,int schoolHour, String description) {
        this.schoolHour = schoolHour;
        this.description = description;
        this.classroom=classroom;
        parseDescription();
    }

    private void parseDescription(){
        this.subjectName = description.split("\\r?\\n")[0];
        this.teacherNames= new ArrayList<>(Arrays.asList(description.substring(description.indexOf("\n") + 1).trim().split("\\r?\\n")[0].split(",")));
    }

    public void setTest(boolean isTest){
        this.isTest=isTest;
    }

    public boolean isTest(){
        return isTest;
    }

    public String getName() {
        return subjectName;
    }

    public void setName(String name) {
        this.subjectName = name;
    }

    public int getSchoolHour() {
        return schoolHour;
    }

    public void setHour(int hour) {
        this.schoolHour = hour;
    }

    public String getDescription() {
        return description;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public ArrayList<String> getTeacherNames() {
        return teacherNames;
    }

    public int getBeginingMinute() {
        return beginingMinute;
    }

    public int getEndingMinute() {
        return endingMinute;
    }
}
