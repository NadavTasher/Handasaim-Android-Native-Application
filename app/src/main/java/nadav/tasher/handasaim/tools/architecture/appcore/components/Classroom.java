package nadav.tasher.handasaim.tools.architecture.appcore.components;

import java.util.ArrayList;

public class Classroom {
    public String name;
    public ArrayList<Subject> subjects;

    public Classroom(String name, ArrayList<Subject> subjects) {
        this.name = name.split(" ")[0];
        this.subjects = subjects;
    }

    public static class Subject {
        public int hour;
        public String name, fullName;

        public Subject(int hour, String name, String fullName) {
            this.hour = hour;
            this.name = name;
            this.fullName = fullName;
        }

        public static class Time{
            public int startH, finishH, startM, finishM;

            public Time(int sh, int fh, int sm, int fm) {
                startH = sh;
                startM = sm;
                finishH = fh;
                finishM = fm;
            }
        }
    }
}
