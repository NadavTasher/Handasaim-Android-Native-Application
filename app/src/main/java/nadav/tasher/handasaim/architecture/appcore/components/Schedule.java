package nadav.tasher.handasaim.architecture.appcore.components;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Schedule {

    public static final String PARAMETER_NAME = "name";
    public static final String PARAMETER_DAY = "day";
    public static final String PARAMETER_DATE = "date";
    public static final String PARAMETER_ORIGIN = "origin";
    public static final String PARAMETER_MESSAGES = "messages";
    public static final String PARAMETER_GRADE = "grade";
    public static final String PARAMETER_CLASSROOMS = "classrooms";
    public static final String PARAMETER_SUBJECTS = "subjects";
    public static final String PARAMETER_HOUR = "hour";
    public static final String PARAMETER_START_TIME = "start_minute";
    public static final String PARAMETER_END_TIME = "end_minute";
    public static final String PARAMETER_TEACHERS = "teachers";

    private ArrayList<Classroom> classrooms = new ArrayList<>();
    private ArrayList<Teacher> teachers = new ArrayList<>();
    private ArrayList<String> messages = new ArrayList<>();
    private String name = "Generic Schedule", date = "Unknown Date", origin = "Unknown Origin", day = "?";

    private Schedule() {
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    private void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    private void setDay(String day) {
        this.day = day;
    }

    public String getOrigin() {
        return origin;
    }

    private void removeAllTeachers() {
        teachers.clear();
    }

    private void setOrigin(String origin) {
        this.origin = origin;
    }

    public ArrayList<Teacher> getTeachers() {
        return teachers;
    }

    public ArrayList<Classroom> getClassrooms() {
        return classrooms;
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

    private void addMessage(String message) {
        messages.add(message);
    }

    private void addClassroom(Classroom classroom) {
        classrooms.add(classroom);
    }

    private void addTeacher(Teacher teacher) {
        teachers.add(teacher);
    }

    public JSONObject toJSON() {
        JSONObject scheduleJSON = new JSONObject();
        try {
            scheduleJSON.put(PARAMETER_DAY, day);
            scheduleJSON.put(PARAMETER_NAME, name);
            scheduleJSON.put(PARAMETER_DATE, date);
            scheduleJSON.put(PARAMETER_ORIGIN, origin);
            JSONArray messagesJSON = new JSONArray();
            for (String m : messages) {
                messagesJSON.put(m);
            }
            JSONArray classroomsJSON = new JSONArray();
            for (Classroom c : classrooms) {
                classroomsJSON.put(c.toJSON());

            }
            scheduleJSON.put(PARAMETER_CLASSROOMS, classroomsJSON);
            scheduleJSON.put(PARAMETER_MESSAGES, messagesJSON);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return scheduleJSON;
    }

    public static class Builder {
        private Schedule schedule = new Schedule();

        public Builder() {
        }

        public static Builder fromSchedule(Schedule schedule) {
            Builder builder = new Builder();
            builder.schedule = schedule;
            return builder;
        }

        public static Builder fromJSON(JSONObject object) {
            Builder builder = new Builder();
            try {
                String day = object.getString(PARAMETER_DAY);
                String origin = object.getString(PARAMETER_ORIGIN);
                String date = object.getString(PARAMETER_DATE);
                String name = object.getString(PARAMETER_NAME);
                JSONArray messages = object.getJSONArray(PARAMETER_MESSAGES);
                JSONArray classrooms = object.getJSONArray(PARAMETER_CLASSROOMS);
                for (int m = 0; m < messages.length(); m++) {
                    builder.addMessage(messages.getString(m));
                }
                for (int c = 0; c < classrooms.length(); c++) {
                    builder.addClassroom(Classroom.Builder.fromJSON(classrooms.getJSONObject(c)));
                }
                builder.setDate(date);
                builder.setOrigin(origin);
                builder.setDay(day);
                builder.setName(name);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return builder;
        }

        public void addMessage(String message) {
            schedule.addMessage(message);
        }

        public void addClassroom(Classroom.Builder classroom) {
            schedule.addClassroom(classroom.build());
        }

        public void setName(String name) {
            schedule.setName(name);
        }

        public void setDate(String date) {
            schedule.setDate(date);
        }

        public void setOrigin(String origin) {
            schedule.setOrigin(origin);
        }

        public void setDay(String day) {
            schedule.setDay(day);
        }

        private void assembleTeachers() {
            ArrayList<Teacher.Builder> allTeachers = new ArrayList<>();
            for (Classroom c : schedule.getClassrooms()) {
                for (Subject s : c.getSubjects()) {
                    Subject.Builder builder = Subject.Builder.fromSubject(s);
                    ArrayList<Teacher> currentTeachers = builder.getTeachers();
                    ArrayList<Teacher.Builder> newTeachers = new ArrayList<>();
                    for (int currentIndex = 0; currentIndex < currentTeachers.size(); currentIndex++) {
                        Teacher currentTeacher = currentTeachers.get(currentIndex);
                        boolean found = false;
                        for (int allIndex = 0; allIndex < allTeachers.size() && !found; allIndex++) {
                            Teacher.Builder teacherBuilder = allTeachers.get(allIndex);
                            if (teacherBuilder.getName().contains(currentTeacher.getName()) || currentTeacher.getName().contains(teacherBuilder.getName())) {
                                // Merge
                                teacherBuilder.addSubject(builder);
                                teacherBuilder.setName(currentTeacher.getName());
                                newTeachers.add(teacherBuilder);
                                found = true;
                            }
                        }
                        if (!found) {
                            // Create new teacher
                            Teacher.Builder teacherBuilder = new Teacher.Builder();
                            teacherBuilder.setName(currentTeacher.getName());
                            teacherBuilder.addSubject(builder);
                            allTeachers.add(teacherBuilder);
                        }
                    }
                    if (newTeachers.size() > 0) {
                        builder.removeAllTeachers();
                        for (Teacher.Builder teacher : newTeachers) builder.addTeacher(teacher);
                    }
                }
            }
            schedule.removeAllTeachers();
            for (Teacher.Builder teacherBuilder : allTeachers)
                schedule.addTeacher(teacherBuilder.build());
        }

        public Schedule build() {
            assembleTeachers();
            return schedule;
        }
    }
}
