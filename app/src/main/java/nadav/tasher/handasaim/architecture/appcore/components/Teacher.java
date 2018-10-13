package nadav.tasher.handasaim.architecture.appcore.components;

import java.util.ArrayList;

public class Teacher {
    private ArrayList<Subject> subjects = new ArrayList<>();
    private String name = "";

    public Teacher() {
    }

    public ArrayList<Subject> getSubjects() {
        return subjects;
    }

    public String getName() {
        return name;
    }

    public Teacher setName(String name) {
        if (name.length() > this.name.length()) {
            this.name = name;
        }
        return this;
    }

    public Teacher addSubject(Subject subject) {
        subjects.add(subject);
        return this;
    }
}
