package nadav.tasher.handasaim.architecture.appcore.components;

import java.util.ArrayList;

public class Schedule {
    public static final int TYPE_REGULAR=0;
    public static final int TYPE_PREDICTED=1;
    private ArrayList<Classroom> classrooms;
    private ArrayList<Teacher> teachers;
    private ArrayList<String> messages;
    private int type=TYPE_REGULAR;

    public Schedule(int type){
        this.type=type;
    }

    public void setTeachers(ArrayList<Teacher> teachers) {
        this.teachers = teachers;
    }

    public ArrayList<Teacher> getTeachers() {
        return teachers;
    }

    public void setClassrooms(ArrayList<Classroom> classrooms) {
        this.classrooms = classrooms;
    }

    public ArrayList<Classroom> getClassrooms() {
        return classrooms;
    }

    public void setMessages(ArrayList<String> messages) {
        this.messages = messages;
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

    public int getType() {
        return type;
    }
}
