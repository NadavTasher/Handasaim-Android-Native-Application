package nadav.tasher.handasaim.architecture.appcore.components;

import java.util.ArrayList;
import java.util.Arrays;

public class Classroom {
    private String name;
    private ArrayList<Subject> subjects;

    public Classroom(String name, ArrayList<Subject> subjects) {
        this.name = name.split(" ")[0];
        this.subjects = subjects;
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

    public void setSubjects(ArrayList<Subject> subjects) {
        this.subjects = subjects;
    }

    public static class Subject {
        private int hour;
        private String name, fullName;
        private boolean isTest=false;
        private ArrayList<String> teachers;

        public Subject(int hour, String fullName) {
            this.hour = hour;
            this.fullName = fullName;
            this.name = fullName.split("\\r?\\n")[0];
            this.teachers= new ArrayList<>(Arrays.asList(fullName.substring(fullName.indexOf("\n") + 1).trim().split("\\r?\\n")[0].split(",")));

        }

        public void setTest(boolean isTest){
            this.isTest=isTest;
        }

        public boolean isTest(){
            return isTest;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getHour() {
            return hour;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
            this.name = fullName.split("\\r?\\n")[0];
            this.teachers= new ArrayList<>(Arrays.asList(fullName.substring(fullName.indexOf("\n") + 1).trim().split("\\r?\\n")[0].split(",")));
        }

        public ArrayList<String> getTeachers() {
            return teachers;
        }

        public void setTeachers(ArrayList<String> teachers) {
            this.teachers = teachers;
        }


        public static class Time{
            private int startH, finishH, startM, finishM;

            public Time(int sh, int fh, int sm, int fm) {
                startH = sh;
                startM = sm;
                finishH = fh;
                finishM = fm;
            }

            public int getFinishHour() {
                return finishH;
            }
            public int getFinishMinute() {
                return finishM;
            }
            public int getStartHour() {
                return startH;
            }public int getStartMinute() {
                return startM;
            }

        }
    }
}
