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
    private String name = "??";
    private ArrayList<Teacher> teachers = new ArrayList<>();
    private Classroom classroom;

    private Subject() {
    }

    public int getHour() {
        return hour;
    }

    private void setHour(int hour) {
        this.hour = hour;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
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

    private void removeAllTeachers() {
        teachers.clear();
    }

    private void addTeacher(Teacher teacher) {
        teachers.add(teacher);
    }

    public Classroom getClassroom() {
        return classroom;
    }

    private void setClassroom(Classroom classroom) {
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

    public static class Builder {
        private Subject subject = new Subject();


        public Builder() {
        }

        public static Builder fromSubject(Subject subject) {
            Builder builder = new Builder();
            builder.subject = subject;
            return builder;
        }

        public static Builder fromJSON(JSONObject json) {
            Builder builder = new Builder();
            try {
                builder.setName(json.getString(PARAMETER_NAME));
                builder.setHour(json.getInt(PARAMETER_HOUR));
                JSONArray teacherNamesJSON = json.getJSONArray(PARAMETER_TEACHERS);
                ArrayList<String> teacherNames = new ArrayList<>();
                for (int n = 0; n < teacherNamesJSON.length(); n++) {
                    teacherNames.add(teacherNamesJSON.getString(n));
                }
                builder.setNames(teacherNames);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return builder;
        }

        public String getName() {
            return subject.getName();
        }

        public Builder setName(String name) {
            subject.setName(name);
            return this;
        }

        public int getHour() {
            return subject.getHour();
        }

        public Builder setHour(int hour) {
            subject.setHour(hour);
            return this;
        }

        public Builder setNames(ArrayList<String> names) {
            for (String name : names) {
                addTeacher(new Teacher.Builder().setName(name).addSubject(this));
            }
            return this;
        }

        public Builder addTeacher(Teacher.Builder teacher) {
            subject.addTeacher(teacher.build());
            return this;
        }

        public Builder removeAllTeachers() {
            subject.removeAllTeachers();
            return this;
        }

        public ArrayList<Teacher> getTeachers() {
            return subject.getTeachers();
        }

        public Classroom getClassroom() {
            return subject.getClassroom();
        }

        public Builder setClassroom(Classroom.Builder classroom) {
            subject.setClassroom(classroom.build());
            return this;
        }

        public Subject build() {
            return subject;
        }
    }
}
