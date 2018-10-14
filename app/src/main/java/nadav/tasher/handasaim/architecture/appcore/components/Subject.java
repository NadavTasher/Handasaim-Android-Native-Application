package nadav.tasher.handasaim.architecture.appcore.components;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import nadav.tasher.handasaim.architecture.appcore.AppCore;

import static nadav.tasher.handasaim.architecture.appcore.components.Schedule.PARAMETER_END_TIME;
import static nadav.tasher.handasaim.architecture.appcore.components.Schedule.PARAMETER_HOUR;
import static nadav.tasher.handasaim.architecture.appcore.components.Schedule.PARAMETER_NAME;
import static nadav.tasher.handasaim.architecture.appcore.components.Schedule.PARAMETER_START_TIME;
import static nadav.tasher.handasaim.architecture.appcore.components.Schedule.PARAMETER_TEACHERS;

public class Subject {
    private int hour;
    private String name = "?";
    private ArrayList<Teacher> teachers = new ArrayList<>();
    private Classroom classroom;

    public Subject() {
    }

    public static Subject fromJSON(JSONObject json) {
        Subject subject = new Subject();
        try {
            subject.setName(json.getString(PARAMETER_NAME));
            subject.setHour(json.getInt(PARAMETER_HOUR));
            JSONArray teacherNamesJSON = json.getJSONArray(PARAMETER_TEACHERS);
            ArrayList<String> teacherNames = new ArrayList<>();
            for (int n = 0; n < teacherNamesJSON.length(); n++) {
                teacherNames.add(teacherNamesJSON.getString(n));
            }
            subject.setNames(teacherNames);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subject;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public String getName() {
        return name;
    }

    public Subject setName(String name) {
        this.name = name;
        return this;
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

    public void setNames(ArrayList<String> names) {
        for (String name : names) {
            Teacher teacher = new Teacher();
            teacher.setName(name);
            teacher.addSubject(this);
            addTeacher(teacher);
        }
    }

    public void removeAllTeachers() {
        teachers.clear();
    }

    public void addTeacher(Teacher teacher) {
        teachers.add(teacher);
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public JSONObject toJSON() {
        JSONObject subjectJSON = new JSONObject();
        try {
            subjectJSON.put(PARAMETER_NAME, getName());
            subjectJSON.put(PARAMETER_HOUR, getHour());
            subjectJSON.put(PARAMETER_START_TIME, AppCore.getSchool().getStartingMinute(getHour()));
            subjectJSON.put(PARAMETER_END_TIME, AppCore.getSchool().getEndingMinute(getHour()));
            JSONArray teacherNamesJSON = new JSONArray();
            for (Teacher t : getTeachers()) {
                teacherNamesJSON.put(t.getName());
            }
            subjectJSON.put(PARAMETER_TEACHERS, teacherNamesJSON);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subjectJSON;
    }
}
