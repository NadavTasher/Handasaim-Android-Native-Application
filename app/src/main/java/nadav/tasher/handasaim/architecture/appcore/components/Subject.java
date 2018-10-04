package nadav.tasher.handasaim.architecture.appcore.components;

import java.util.ArrayList;
import java.util.Arrays;

public class Subject {
    private int schoolHour;
    private String description, subjectName;
    private boolean isTest = false;
    private ArrayList<String> teacherStrings = new ArrayList<>();
    private ArrayList<Teacher> teachers = new ArrayList<>();
    private Classroom classroom;

    public Subject(Classroom classroom, int schoolHour, String description) {
        this.schoolHour = schoolHour;
        this.description = description;
        this.classroom = classroom;
        parseDescription();
    }

    public Subject(Classroom classroom, int schoolHour, String name, ArrayList<String> teacherNames) {
        this.schoolHour = schoolHour;
        this.classroom = classroom;
        this.subjectName = name;
        this.teacherStrings = teacherNames;
    }

    private void parseDescription() {
        this.subjectName = description.split("\\r?\\n")[0];
        this.subjectName = this.subjectName.replaceAll(",", "/");
        this.teacherStrings = new ArrayList<>(Arrays.asList(description.substring(description.indexOf("\n") + 1).trim().split("\\r?\\n")[0].split(",")));
    }

    public boolean isTest() {
        return isTest;
    }

    public void setTest(boolean isTest) {
        this.isTest = isTest;
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

    public void addTeacher(Teacher teacher) {
        teachers.add(teacher);
    }

    public ArrayList<Teacher> getTeachers() {
        return teachers;
    }

    public ArrayList<String> getTeacherNames() {
        ArrayList<String> names = new ArrayList<>();
        for (Teacher teacher : teachers) {
            names.add(teacher.getName());
        }
        return names;
    }

    public String getDescription() {
        return description;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public ArrayList<String> getTeacherStrings() {
        return teacherStrings;
    }
}
