package nadav.tasher.handasaim.architecture.appcore.components;

import java.util.ArrayList;

public class Teacher {
    private ArrayList<Subject> subjects=new ArrayList<>();
    private ArrayList<String> names=new ArrayList<>();

    public Teacher(){}

    public void addSubject(Subject subject){
        subjects.add(subject);
    }

    public ArrayList<Subject> getSubjects() {
        return subjects;
    }

    public void addName(String name){
        names.add(name);
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public String getName(){
        if(names.size()>0) {
            int indexOfLongest = 0;
            for (int name = 0; name < names.size(); name++) {
                if (names.get(name).length() > names.get(indexOfLongest).length()) {
                    indexOfLongest = name;
                }
            }
            return names.get(indexOfLongest);
        }
        return "Unknown Name";
    }
}
