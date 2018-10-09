package nadav.tasher.handasaim.architecture.appcore.components;

import java.util.ArrayList;

public class Teacher {
    private ArrayList<Subject> subjects = new ArrayList<>();
    private String name = "";

    private Teacher() {
    }

    public ArrayList<Subject> getSubjects() {
        return subjects;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        if (name.length() > this.name.length()) {
            this.name = name;
        }
    }

    private void addSubject(Subject subject) {
        subjects.add(subject);
    }

    public static class Builder {
        private Teacher teacher = new Teacher();

        public Builder() {
        }

        public String getName() {
            return teacher.getName();
        }

        public Builder setName(String name) {
            teacher.setName(name);
            return this;
        }

        public Builder addSubject(Subject.Builder subject) {
            teacher.addSubject(subject.build());
            return this;
        }

        public ArrayList<Subject> getSubjects() {
            return teacher.getSubjects();
        }

        public Teacher build() {
            return teacher;
        }
    }
}
