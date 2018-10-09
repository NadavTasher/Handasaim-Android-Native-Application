package nadav.tasher.handasaim.architecture.appcore.components;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static nadav.tasher.handasaim.architecture.appcore.components.Schedule.PARAMETER_GRADE;
import static nadav.tasher.handasaim.architecture.appcore.components.Schedule.PARAMETER_NAME;
import static nadav.tasher.handasaim.architecture.appcore.components.Schedule.PARAMETER_SUBJECTS;

public class Classroom {

    public static final int UNKNOWN_GRADE = 0;
    public static final int NINTH_GRADE = 9;
    public static final int TENTH_GRADE = 10;
    public static final int ELEVENTH_GRADE = 11;
    public static final int TWELVETH_GRADE = 12;

    private String name = "";
    private int grade = UNKNOWN_GRADE;
    private ArrayList<Subject> subjects = new ArrayList<>();

    private Classroom() {
    }

    private void addSubject(Subject subject) {
        subjects.add(subject);
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public ArrayList<Subject> getSubjects() {
        return subjects;
    }

    public int getGrade() {
        return grade;
    }

    private void setGrade(int grade) {
        this.grade = grade;
    }

    public JSONObject toJSON() {
        JSONObject classroomJSON = new JSONObject();
        try {
            classroomJSON.put(PARAMETER_NAME, getName());
            classroomJSON.put(PARAMETER_GRADE, getGrade());
            JSONArray subjectsJSON = new JSONArray();
            for (Subject s : getSubjects()) {
                subjectsJSON.put(s.toJSON());
            }
            classroomJSON.put(PARAMETER_SUBJECTS, subjectsJSON);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classroomJSON;
    }

    public static class Builder {
        private Classroom classroom = new Classroom();

        public Builder() {
        }

        public static int getGrade(String name) {
            if (name.startsWith("ט")) {
                return NINTH_GRADE;
            } else if (name.startsWith("י")) {
                if (name.contains("א")) {
                    return ELEVENTH_GRADE;
                } else if (name.contains("ב")) {
                    return TWELVETH_GRADE;
                }
                return TENTH_GRADE;
            } else {
                return UNKNOWN_GRADE;
            }
        }

        public static Builder fromJSON(JSONObject json) {
            Builder builder = new Builder();
            try {
                builder.setName(json.getString(PARAMETER_NAME));
                JSONArray subjectsJSON = json.getJSONArray(PARAMETER_SUBJECTS);
                for (int s = 0; s < subjectsJSON.length(); s++) {
                    Subject.Builder subject = Subject.Builder.fromJSON(subjectsJSON.getJSONObject(s));
                    subject.setClassroom(builder);
                    builder.addSubject(subject);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return builder;
        }

        public void addSubject(Subject.Builder subject) {
            classroom.addSubject(subject.build());
        }

        public void setGrade(int grade) {
            classroom.setGrade(grade);
        }

        public void setName(String name) {
            classroom.setName(name);
            setGrade(getGrade(name));
        }

        public Classroom build() {
            return classroom;
        }
    }
}
