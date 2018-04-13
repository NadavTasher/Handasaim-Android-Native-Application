package nadav.tasher.handasaim.tools.architecture;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;

import nadav.tasher.handasaim.architecture.StudentClass;
import nadav.tasher.handasaim.architecture.Teacher;

public class AppCore {
    public static int startReadingRow(Sheet s) {
        Cell secondCell = s.getRow(0).getCell(1);
        if (secondCell != null) {
            return 0;
        } else {
            return 1;
        }
    }

    public static String getRealTimeForHourNumber(int hour) {
        switch (hour) {
            case 0:
                return "07:45";
            case 1:
                return "08:30";
            case 2:
                return "09:15";
            case 3:
                return "10:15";
            case 4:
                return "11:00";
            case 5:
                return "12:10";
            case 6:
                return "12:55";
            case 7:
                return "13:50";
            case 8:
                return "14:35";
            case 9:
                return "15:30";
            case 10:
                return "16:15";
            case 11:
                return "17:00";
            case 12:
                return "17:45";
        }
        return null;
    }

    public static String getRealEndTimeForHourNumber(int hour) {
        switch (hour) {
            case 0:
                return "08:30";
            case 1:
                return "09:15";
            case 2:
                return "10:00";
            case 3:
                return "11:00";
            case 4:
                return "11:45";
            case 5:
                return "12:55";
            case 6:
                return "13:40";
            case 7:
                return "14:35";
            case 8:
                return "15:20";
            case 9:
                return "16:15";
            case 10:
                return "17:00";
            case 11:
                return "17:45";
            case 12:
                return "18:30";
        }
        return null;
    }

    public static ArrayList<StudentClass> readExcelFile(File f) {
        try {
            ArrayList<StudentClass> classes = new ArrayList<>();
            POIFSFileSystem myFileSystem = new POIFSFileSystem(new FileInputStream(f));
            Workbook myWorkBook = new HSSFWorkbook(myFileSystem);
            Sheet mySheet = myWorkBook.getSheetAt(0);
            int startReadingRow = startReadingRow(mySheet);
            int rows = mySheet.getLastRowNum();
            int cols = mySheet.getRow(startReadingRow).getLastCellNum();
            for (int c = 1; c < cols; c++) {
                ArrayList<StudentClass.Subject> subs = new ArrayList<>();
                for (int r = startReadingRow + 1; r < rows; r++) {
                    Row row = mySheet.getRow(r);
                    subs.add(new StudentClass.Subject(r - (startReadingRow + 1), row.getCell(c).getStringCellValue().split("\\r?\\n")[0], row.getCell(c).getStringCellValue()));
                }
                classes.add(new StudentClass(mySheet.getRow(startReadingRow).getCell(c).getStringCellValue(), subs));
            }
            return classes;
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<StudentClass> readExcelFileXLSX(File f) {
        try {
            ArrayList<StudentClass> classes = new ArrayList<>();
            XSSFWorkbook myWorkBook = new XSSFWorkbook(new FileInputStream(f));
            Sheet mySheet = myWorkBook.getSheetAt(0);
            int startReadingRow = startReadingRow(mySheet);
            int rows = mySheet.getLastRowNum();
            int cols = mySheet.getRow(startReadingRow).getLastCellNum();
            for (int c = 1; c < cols; c++) {
                ArrayList<StudentClass.Subject> subs = new ArrayList<>();
                for (int r = startReadingRow + 1; r < rows; r++) {
                    Row row = mySheet.getRow(r);
                    subs.add(new StudentClass.Subject(r - (startReadingRow + 1), row.getCell(c).getStringCellValue().split("\\r?\\n")[0], row.getCell(c).getStringCellValue()));
                }
                classes.add(new StudentClass(mySheet.getRow(startReadingRow).getCell(c).getStringCellValue(), subs));
            }
            return classes;
        } catch (Exception e) {
            return null;
        }
    }

    public static int minuteOfDay(int h, int m) {
        return h * 60 + m;
    }

    public static int getBreak(int washour) {
        switch (washour) {
            case 2:
                return 15;
            case 4:
                return 25;
            case 6:
                return 10;
            case 8:
                return 10;
            case 10:
                return 5;
        }
        return -1;
    }

    public static StudentClass.Subject.Time getTimeForLesson(int hour) {
        switch (hour) {
            case 0:
                return new StudentClass.Subject.Time(7, 8, 45, 30);
            case 1:
                return new StudentClass.Subject.Time(8, 9, 30, 15);
            case 2:
                return new StudentClass.Subject.Time(9, 10, 15, 0);
            case 3:
                return new StudentClass.Subject.Time(10, 11, 15, 0);
            case 4:
                return new StudentClass.Subject.Time(11, 11, 0, 45);
            case 5:
                return new StudentClass.Subject.Time(12, 12, 10, 55);
            case 6:
                return new StudentClass.Subject.Time(12, 13, 55, 40);
            case 7:
                return new StudentClass.Subject.Time(13, 14, 50, 35);
            case 8:
                return new StudentClass.Subject.Time(14, 15, 35, 20);
            case 9:
                return new StudentClass.Subject.Time(15, 16, 30, 15);
            case 10:
                return new StudentClass.Subject.Time(16, 17, 15, 0);
            case 11:
                return new StudentClass.Subject.Time(17, 17, 0, 45);
            case 12:
                return new StudentClass.Subject.Time(17, 18, 45, 30);
        }
        return new StudentClass.Subject.Time(-1, -1, -1, -1);
    }

    public static int isTheSameTeacher(String a, String b) {
        ArrayList<String> aSplit = new ArrayList<>(Arrays.asList(a.split(" ")));
        ArrayList<String> bSplit = new ArrayList<>(Arrays.asList(b.split(" ")));
        if (aSplit.size() > 1 && bSplit.size() > 1) {
            if (aSplit.get(0).equals(bSplit.get(0))) {
                if (aSplit.get(1).contains(bSplit.get(1))) {
                    return 2;
                } else if (bSplit.get(1).contains(aSplit.get(1))) {
                    return 2;
                } else {
                    return 0;
                }
            }
            return 0;
        } else if (a.contains(b) || b.contains(a)) {
            return 1;
        }
        return 0;
    }

    public static int getGrade(ArrayList<Teacher.Lesson> classNames) {
        if (classNames.size() > 0) {
            int grade = getGrade(classNames.get(0));
            for (int cTl = 1; cTl < classNames.size(); cTl++) {
                int cGrade = getGrade(classNames.get(cTl));
                if (cGrade != grade) {
                    return -1;
                }
            }
            return grade;
        }
        return -1;
    }

    public static int getGrade(Teacher.Lesson s) {
        String parsing = s.className;
        if (parsing.contains("י")) {
            if (parsing.contains("א")) {
                return 2;
            } else if (parsing.contains("ב")) {
                return 3;
            } else {
                return 1;
            }
        } else {
            return 0;
        }
    }

    public static String getGrade(int grade) {
        switch (grade) {
            case 0:
                return "ט'";
            case 1:
                return "י'";
            case 2:
                return "יא'";
            case 3:
                return "יב'";
        }
        return "";
    }

    public static ArrayList<Teacher> getTeacherSchudleForClasses(ArrayList<StudentClass> classes) {
        ArrayList<Teacher> teacherList = new ArrayList<>();
        for (int currentClass = 0; currentClass < classes.size(); currentClass++) {
            StudentClass cClass = classes.get(currentClass);
            cClass.name = cClass.name.split(" ")[0];
            for (int currentSubject = 0; currentSubject < cClass.subjects.size(); currentSubject++) {
                StudentClass.Subject cSubject = cClass.subjects.get(currentSubject);
                ArrayList<String> cSubjectTeachers = new ArrayList<>(Arrays.asList(cSubject.fullName.substring(cSubject.fullName.indexOf("\n") + 1).trim().split("\\r?\\n")[0].split(",")));
                Teacher.Lesson cLesson = new Teacher.Lesson(cClass.name, cSubject.name, cSubject.hour);
                for (int currentTeacherOfSubject = 0; currentTeacherOfSubject < cSubjectTeachers.size(); currentTeacherOfSubject++) {
                    String nameOfTeacher = cSubjectTeachers.get(currentTeacherOfSubject);
                    boolean foundTeacher = false;
                    if (!cSubject.name.equals("")) {
                        for (int currentTeacher = 0; currentTeacher < teacherList.size(); currentTeacher++) {
                            Teacher cTeacher = teacherList.get(currentTeacher);
                            if (isTheSameTeacher(cTeacher.mainName, nameOfTeacher) == 1) {
                                if (!cTeacher.mainName.equals(nameOfTeacher)) {
                                    if (cTeacher.teaches(cSubject.name)) {
                                        cTeacher.teaching.add(cLesson);
                                        foundTeacher = true;
                                        break;
                                    }
                                } else {
                                    if (!cTeacher.teaches(cSubject.name)) {
                                        cTeacher.subjects.add(cSubject.name);
                                    }
                                    cTeacher.teaching.add(cLesson);
                                    foundTeacher = true;
                                    break;
                                }
                            } else if (isTheSameTeacher(cTeacher.mainName, nameOfTeacher) == 2) {
                                if (!cTeacher.teaches(cSubject.name)) {
                                    cTeacher.subjects.add(cSubject.name);
                                }
                                cTeacher.teaching.add(cLesson);
                                foundTeacher = true;
                                break;
                            }
                        }
                        if (!foundTeacher) {
                            Teacher teacher = new Teacher();
                            teacher.mainName = nameOfTeacher;
                            teacher.subjects = new ArrayList<>();
                            teacher.subjects.add(cSubject.name);
                            teacher.teaching = new ArrayList<>();
                            teacher.teaching.add(cLesson);
                            if (!nameOfTeacher.equals(""))
                                teacherList.add(teacher);
                        }
                    }
                }
            }
        }
        return teacherList;
    }
}
