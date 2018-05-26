package nadav.tasher.handasaim.tools.architecture.appcore.components;

import java.util.ArrayList;

public class Teacher {
    public ArrayList<Lesson> teaching;
    public ArrayList<String> subjects;
    public String mainName;

    public boolean teaches(String l) {
        for (int tc = 0; tc < subjects.size(); tc++) {
            if (subjects.get(tc).equals(l) || subjects.get(tc).contains(l) || l.contains(subjects.get(tc))) {
                return true;
            }
        }
        return false;
    }
    public static class Lesson {
        public String className, lessonName;
        public int hour;

        public Lesson(String className, String lessonName, int hour) {
            this.hour = hour;
            this.className = className;
            this.lessonName = lessonName;
            this.lessonName = this.lessonName.replaceAll(",", "/");
        }
    }
}
