package nadav.tasher.handasaim.architecture;

import java.util.ArrayList;

public class StudentClass {
    public String name;
    public ArrayList<Subject> subjects;

    public StudentClass(String name, ArrayList<Subject> subjects) {
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
