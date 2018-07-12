package nadav.tasher.handasaim.architecture.appcore;

import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.hssf.usermodel.HSSFTextbox;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFSimpleShape;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nadav.tasher.handasaim.architecture.appcore.components.Classroom;
import nadav.tasher.handasaim.architecture.appcore.components.Teacher;

public class AppCore {

    public static double APPCORE_VERSION=1.4;

    /*
        Note That XSSF Resemmbles XLSX,
        While HSSF Resembles XLS.
        XSSF Is The Newer Format.
     */

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

    public static ArrayList<String> getMessages(Sheet sheet) {
        ArrayList<String> messages = new ArrayList<>();
        try {
            if (sheet.getWorkbook() instanceof HSSFWorkbook) {
                HSSFPatriarch patriarch = (HSSFPatriarch) sheet.createDrawingPatriarch();
                List<HSSFShape> shapes = patriarch.getChildren();
                for (int s = 0; s < shapes.size(); s++) {
                    if (shapes.get(s) instanceof HSSFTextbox) {
                        try {
                            HSSFShape mShape = shapes.get(s);
                            if (mShape != null) {
                                HSSFTextbox mTextShape = (HSSFTextbox) mShape;
                                HSSFRichTextString mString = mTextShape.getString();
                                if (mString != null) {
                                    messages.add(mString.getString());
                                }
                            }
                        } catch (NullPointerException ignored) {
                        }
                    }
                }
            } else {
                //            messages.add("Could Not Load Messages");
                XSSFSheet convertedSheet = (XSSFSheet) sheet;
                XSSFDrawing drawing = convertedSheet.createDrawingPatriarch();
                List<XSSFShape> shapes = drawing.getShapes();
                for (int s = 0; s < shapes.size(); s++) {
                    Log.i("Shape", shapes.get(s).toString());
                    if (shapes.get(s) instanceof XSSFSimpleShape) {
                        Log.i("Here", "Reached");
                        try {
                            XSSFSimpleShape mShape = (XSSFSimpleShape) shapes.get(s);
                            if (mShape != null) {
                                if (mShape.getText() != null) {
                                    String mString = mShape.getText();
                                    if (mString != null) {
                                        messages.add(mString);
                                    }
                                }
                            }
                        } catch (NullPointerException ignored) {
                        }
                    }
                }
            }
        }catch (Exception e){
            messages.add("A Reading Error Occurred.");
        }
        return messages;
    }

    public static Sheet getSheet(File f) {
        try {
            if (f.toString().endsWith(".xls")) {
                POIFSFileSystem fileSystem = new POIFSFileSystem(new FileInputStream(f));
                Workbook workBook = new HSSFWorkbook(fileSystem);
                Sheet foundSheet=null;
                for(int s=0;s<workBook.getNumberOfSheets()&&foundSheet==null;s++){
                    Sheet current=workBook.getSheetAt(s);
                    if(current.getLastRowNum()-1>0){
                        foundSheet=current;
                    }
                }
                return foundSheet;
            } else {
                XSSFWorkbook workBook = new XSSFWorkbook(new FileInputStream(f));
                Sheet foundSheet=null;
                for(int s=0;s<workBook.getNumberOfSheets()&&foundSheet==null;s++){
                    Sheet current=workBook.getSheetAt(s);
                    if(current.getLastRowNum()-1>0){
                        foundSheet=current;
                    }
                }
                return foundSheet;
            }
        } catch (IOException ignored) {
            return null;
        }
    }

    public static ArrayList<Classroom> getClasses(Sheet sheet) {
        try {
            ArrayList<Classroom> classes = new ArrayList<>();
            int startReadingRow = startReadingRow(sheet);
            int rows = sheet.getLastRowNum();
            int cols = sheet.getRow(startReadingRow).getLastCellNum();
            for (int c = 1; c < cols; c++) {
                ArrayList<Classroom.Subject> subs = new ArrayList<>();
                for (int r = startReadingRow + 1; r < rows; r++) {
                    Row row = sheet.getRow(r);
                    subs.add(new Classroom.Subject(r - (startReadingRow + 1), row.getCell(c).getStringCellValue()));
                }
                classes.add(new Classroom(sheet.getRow(startReadingRow).getCell(c).getStringCellValue(), subs));
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

    public static Classroom.Subject.Time getTimeForLesson(int hour) {
        switch (hour) {
            case 0:
                return new Classroom.Subject.Time(7, 8, 45, 30);
            case 1:
                return new Classroom.Subject.Time(8, 9, 30, 15);
            case 2:
                return new Classroom.Subject.Time(9, 10, 15, 0);
            case 3:
                return new Classroom.Subject.Time(10, 11, 15, 0);
            case 4:
                return new Classroom.Subject.Time(11, 11, 0, 45);
            case 5:
                return new Classroom.Subject.Time(12, 12, 10, 55);
            case 6:
                return new Classroom.Subject.Time(12, 13, 55, 40);
            case 7:
                return new Classroom.Subject.Time(13, 14, 50, 35);
            case 8:
                return new Classroom.Subject.Time(14, 15, 35, 20);
            case 9:
                return new Classroom.Subject.Time(15, 16, 30, 15);
            case 10:
                return new Classroom.Subject.Time(16, 17, 15, 0);
            case 11:
                return new Classroom.Subject.Time(17, 17, 0, 45);
            case 12:
                return new Classroom.Subject.Time(17, 18, 45, 30);
        }
        return new Classroom.Subject.Time(-1, -1, -1, -1);
    }

    public static int getStartMinute(){}

    public static String getDay(Sheet s) {
        try {
            return s.getRow(0).getCell(0).getStringCellValue();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getGrade(ArrayList<Classroom> classrooms) {
        int previeusGrade=Classroom.UNKNOWN_GRADE;
        for(Classroom currentClassroom : classrooms){
            if(currentClassroom.getGrade()==previeusGrade||previeusGrade==Classroom.UNKNOWN_GRADE){
                previeusGrade=currentClassroom.getGrade();
            }else{
                previeusGrade=Classroom.UNKNOWN_GRADE;
                break;
            }
        }
        switch (previeusGrade) {
            case Classroom.NINTH_GRADE:
                return "ט'";
            case Classroom.TENTH_GRADE:
                return "י'";
            case Classroom.ELEVENTH_GRADE:
                return "יא'";
            case Classroom.TWELVETH_GRADE:
                return "יב'";
        }
        StringBuilder allGrades=new StringBuilder();
        for(Classroom currentClassroom : classrooms){
            if(allGrades.length()!=0)allGrades.append(", ");
            allGrades.append(currentClassroom.getName());
        }
        return allGrades.toString();
    }

//    public static ArrayList<Teacher> getTeacherSchudleForClasses(ArrayList<Classroom> classes) {
//        ArrayList<Teacher> teacherList = new ArrayList<>();
//        for (int currentClass = 0; currentClass < classes.size(); currentClass++) {
//            Classroom cClass = classes.get(currentClass);
//            for (int currentSubject = 0; currentSubject < cClass.getSubjects().size(); currentSubject++) {
//                Classroom.Subject cSubject = cClass.getSubjects().get(currentSubject);
//                Teacher.Lesson cLesson = new Teacher.Lesson(cClass.getName(), cSubject.getName(), cSubject.getHour());
//                for (int currentTeacherOfSubject = 0; currentTeacherOfSubject < cSubject.getTeachers().size(); currentTeacherOfSubject++) {
//                    String nameOfTeacher =  cSubject.getTeachers().get(currentTeacherOfSubject);
//                    boolean foundTeacher = false;
//                    if (!cSubject.getName().equals("")) {
//                        for (int currentTeacher = 0; currentTeacher < teacherList.size(); currentTeacher++) {
//                            Teacher cTeacher = teacherList.get(currentTeacher);
//                            if (isTheSameTeacher(cTeacher.mainName, nameOfTeacher) == 1) {
//                                if (!cTeacher.mainName.equals(nameOfTeacher)) {
//                                    if (cTeacher.teaches(cSubject.getName())) {
//                                        cTeacher.teaching.add(cLesson);
//                                        foundTeacher = true;
//                                        break;
//                                    }
//                                } else {
//                                    if (!cTeacher.teaches(cSubject.getName())) {
//                                        cTeacher.subjects.add(cSubject.getName());
//                                    }
//                                    cTeacher.teaching.add(cLesson);
//                                    foundTeacher = true;
//                                    break;
//                                }
//                            } else if (isTheSameTeacher(cTeacher.mainName, nameOfTeacher) == 2) {
//                                if (!cTeacher.teaches(cSubject.getName())) {
//                                    cTeacher.subjects.add(cSubject.getName());
//                                }
//                                cTeacher.teaching.add(cLesson);
//                                foundTeacher = true;
//                                break;
//                            }
//                        }
//                        if (!foundTeacher) {
//                            Teacher teacher = new Teacher();
//                            teacher.mainName = nameOfTeacher;
//                            teacher.subjects = new ArrayList<>();
//                            teacher.subjects.add(cSubject.getName());
//                            teacher.teaching = new ArrayList<>();
//                            teacher.teaching.add(cLesson);
//                            if (!nameOfTeacher.equals(""))
//                                teacherList.add(teacher);
//                        }
//                    }
//                }
//            }
//        }
//        return teacherList;
//    }
}
