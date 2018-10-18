package nadav.tasher.handasaim.architecture.appcore.components;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Schedule {

    static final String PARAMETER_NAME = "name";
    static final String PARAMETER_DAY = "day";
    static final String PARAMETER_ORIGIN = "origin";
    static final String PARAMETER_MESSAGES = "messages";
    static final String PARAMETER_GRADE = "grade";
    static final String PARAMETER_CLASSROOMS = "classrooms";
    static final String PARAMETER_SUBJECTS = "subjects";
    static final String PARAMETER_HOUR = "hour";
    static final String PARAMETER_START_TIME = "start_minute";
    static final String PARAMETER_END_TIME = "end_minute";
    static final String PARAMETER_TEACHERS = "teachers";

    private ArrayList<Classroom> classrooms = new ArrayList<>();
    private ArrayList<Teacher> teachers = new ArrayList<>();
    private ArrayList<String> messages = new ArrayList<>();
    private String name = "Generic Schedule", origin = "Unknown Origin", day = "?";

    private Schedule() {
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
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

    private void setOrigin(String origin) {
        this.origin = origin;
    }

    private void removeAllTeachers() {
        teachers.clear();
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
                String name = object.getString(PARAMETER_NAME);
                JSONArray messages = object.getJSONArray(PARAMETER_MESSAGES);
                JSONArray classrooms = object.getJSONArray(PARAMETER_CLASSROOMS);
                for (int m = 0; m < messages.length(); m++) {
                    builder.addMessage(messages.getString(m));
                }
                for (int c = 0; c < classrooms.length(); c++) {
                    builder.addClassroom(Classroom.fromJSON(classrooms.getJSONObject(c)));
                }
                builder.setOrigin(origin);
                builder.setDay(day);
                builder.setName(name);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return builder;
        }

        public Builder addMessage(String message) {
            schedule.addMessage(message);
            return this;
        }

        public Builder addClassroom(Classroom classroom) {
            schedule.addClassroom(classroom);
            return this;

        }

        public Builder setName(String name) {
            schedule.setName(name);
            return this;

        }

        public Builder setOrigin(String origin) {
            schedule.setOrigin(origin);
            return this;

        }

        public Builder setDay(String day) {
            schedule.setDay(day);
            return this;

        }

        private void assembleTeachers() {
            ArrayList<Teacher> allTeachers = new ArrayList<>();
            for (Classroom c : schedule.getClassrooms()) {
                for (Subject s : c.getSubjects()) {
                    ArrayList<Teacher> newTeachers = new ArrayList<>();
                    for (Teacher currentTeacher : s.getTeachers()) {
                        boolean found = false;
                        for (Teacher teacher : allTeachers) {
                            if (teacher.getName().contains(currentTeacher.getName()) || currentTeacher.getName().contains(teacher.getName())) {
                                // Merge
                                teacher.addSubject(s);
                                teacher.setName(currentTeacher.getName());
                                newTeachers.add(teacher);
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            // Create new teacher
                            Teacher teacher = new Teacher();
                            teacher.setName(currentTeacher.getName());
                            teacher.addSubject(s);
                            allTeachers.add(teacher);
                        }
                    }
                    if (newTeachers.size() > 0) {
                        s.removeAllTeachers();
                        for (Teacher teacher : newTeachers) s.addTeacher(teacher);
                    }
                }
            }
            schedule.removeAllTeachers();
            for (Teacher teacher : allTeachers) schedule.addTeacher(teacher);
        }

        public Schedule build() {
            assembleTeachers();
            return schedule;
        }
    }
}
