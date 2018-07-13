package nadav.tasher.handasaim.architecture.appcore.components;

import java.util.ArrayList;
import java.util.Arrays;

public class Classroom {

    public static final int UNKNOWN_GRADE = 0;
    public static final int NINTH_GRADE = 9;
    public static final int TENTH_GRADE = 10;
    public static final int ELEVENTH_GRADE = 11;
    public static final int TWELVETH_GRADE = 12;

    private String name;
    private ArrayList<Subject> subjects=new ArrayList<>();

    public Classroom(String name) {
        this.name = name;
    }

    public void addSubject(Subject subject){
        subjects.add(subject);
    }

    public String getName() {
        return name;
    }

    public ArrayList<Subject> getSubjects() {
        return subjects;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGrade(){
        if(name.startsWith("ט")){
            return NINTH_GRADE;
        }else if(name.startsWith("י")){
            if(name.contains("א")){
                return ELEVENTH_GRADE;
            }else if(name.contains("ב")){
                return TWELVETH_GRADE;
            }
            return TENTH_GRADE;
        }else{
            return UNKNOWN_GRADE;
        }
    }
}
