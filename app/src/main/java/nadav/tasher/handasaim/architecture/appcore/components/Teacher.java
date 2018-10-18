package nadav.tasher.handasaim.architecture.appcore.components;

import java.util.ArrayList;

public class Teacher {
    private ArrayList<Subject> subjects = new ArrayList<>();
    private String name = "";

    Teacher() {
    }

    public ArrayList<Subject> getSubjects() {
        return subjects;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name.length() > this.name.length() && name.startsWith(this.name)) {
            this.name = name;
        }
    }

    public void addSubject(Subject subject) {
        subjects.add(subject);
    }
}
