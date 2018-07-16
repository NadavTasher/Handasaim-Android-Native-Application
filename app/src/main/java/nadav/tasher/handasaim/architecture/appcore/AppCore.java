package nadav.tasher.handasaim.architecture.appcore;

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
import java.util.List;

import nadav.tasher.handasaim.architecture.appcore.components.Classroom;
import nadav.tasher.handasaim.architecture.appcore.components.Schedule;
import nadav.tasher.handasaim.architecture.appcore.components.Subject;

public class AppCore {

    public static double APPCORE_VERSION = 1.5;

    /*
        Note That XSSF Resemmbles XLSX,
        While HSSF Resembles XLS.
        XSSF Is The Newer Format.
     */

    private static int startReadingRow(Sheet s) {
        Cell secondCell = s.getRow(0).getCell(1);
        if (!readCell(secondCell).isEmpty()) {
            return 0;
        } else {
            return 1;
        }
    }

    private static void parseMessages(Schedule.Builder builder, Sheet sheet) {
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
                                    builder.addMessage(mString.getString());
                                }
                            }
                        } catch (NullPointerException ignored) {
                        }
                    }
                }
            } else {
                XSSFSheet convertedSheet = (XSSFSheet) sheet;
                XSSFDrawing drawing = convertedSheet.createDrawingPatriarch();
                List<XSSFShape> shapes = drawing.getShapes();
                for (int s = 0; s < shapes.size(); s++) {
                    if (shapes.get(s) instanceof XSSFSimpleShape) {
                        try {
                            XSSFSimpleShape mShape = (XSSFSimpleShape) shapes.get(s);
                            if (mShape != null) {
                                if (mShape.getText() != null) {
                                    String mString = mShape.getText();
                                    if (mString != null) {
                                        builder.addMessage(mString);
                                    }
                                }
                            }
                        } catch (NullPointerException ignored) {
                        }
                    }
                }
            }
        } catch (Exception e) {
            builder.addMessage("Failed: Reading Messages");
        }
    }

    private static Sheet getSheet(File f) {
        try {
            if (f.toString().endsWith(".xls")) {
                POIFSFileSystem fileSystem = new POIFSFileSystem(new FileInputStream(f));
                Workbook workBook = new HSSFWorkbook(fileSystem);
                Sheet foundSheet = null;
                for (int s = 0; s < workBook.getNumberOfSheets() && foundSheet == null; s++) {
                    Sheet current = workBook.getSheetAt(s);
                    if (current.getLastRowNum() - 1 > 0) {
                        foundSheet = current;
                    }
                }
                return foundSheet;
            } else {
                XSSFWorkbook workBook = new XSSFWorkbook(new FileInputStream(f));
                Sheet foundSheet = null;
                for (int s = 0; s < workBook.getNumberOfSheets() && foundSheet == null; s++) {
                    Sheet current = workBook.getSheetAt(s);
                    if (current.getLastRowNum() - 1 > 0) {
                        foundSheet = current;
                    }
                }
                return foundSheet;
            }
        } catch (IOException ignored) {
            return null;
        }
    }

    public static Schedule getSchedule(File excel) {
        Schedule.Builder mBuilder = new Schedule.Builder(Schedule.TYPE_REGULAR);
        Sheet sheet = getSheet(excel);
        if (sheet != null) {
            parseClassrooms(mBuilder, sheet);
            parseMessages(mBuilder, sheet);
            mBuilder.setDay(getDay(sheet));
        }
        return mBuilder.build();
    }

    private static String readCell(Cell cell) {
        if(cell != null) {
            try {
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        return cell.getStringCellValue();
                    case Cell.CELL_TYPE_NUMERIC:
                        return String.valueOf((int) cell.getNumericCellValue());
                    case Cell.CELL_TYPE_BOOLEAN:
                        return String.valueOf(cell.getBooleanCellValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private static void parseClassrooms(Schedule.Builder builder, Sheet sheet) {
        try {
            int startReadingRow = startReadingRow(sheet);
            int rows = sheet.getLastRowNum();
            int cols = sheet.getRow(startReadingRow).getLastCellNum();
            for (int c = 1; c < cols; c++) {
                String name = readCell(sheet.getRow(startReadingRow).getCell(c)).split(" ")[0];
                Classroom cClassroom = new Classroom(name);
                int readStart = startReadingRow + 1;
                for (int r = readStart; r < rows; r++) {
                    Row row = sheet.getRow(r);
                    cClassroom.addSubject(new Subject(cClassroom, r - readStart, readCell(row.getCell(c))));
                }
                builder.addClassroom(cClassroom);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getBreak(int hour1, int hour2) {
        return getStartMinute(hour2) - getEndMinute(hour1);
    }

    public static int getStartMinute(int schoolHour) {
        switch (schoolHour) {
            case 0:
                return 465;
            case 1:
                return 510;
            case 2:
                return 555;
            case 3:
                return 615;
            case 4:
                return 660;
            case 5:
                return 730;
            case 6:
                return 775;
            case 7:
                return 830;
            case 8:
                return 875;
            case 9:
                return 930;
            case 10:
                return 975;
            case 11:
                return 1020;
            case 12:
                return 1065;
        }
        return getStartMinute(0);
    }

    public static int getEndMinute(int schoolHour) {
        return getStartMinute(schoolHour) + 45;
    }

    public static String convertMinuteToTime(int minute) {
        int hours = minute / 60;
        int minutes = minute % 60;
        StringBuilder timeBuilder = new StringBuilder();
        timeBuilder.append(hours);
        timeBuilder.append(":");
        if (minutes < 10) timeBuilder.append(0);
        timeBuilder.append(minutes);
        return timeBuilder.toString();
    }

    private static String getDay(Sheet s) {
        return readCell(s.getRow(0).getCell(0));
    }

    public static String getGrades(ArrayList<Classroom> classrooms) {
        int previeusGrade = Classroom.UNKNOWN_GRADE;
        for (Classroom currentClassroom : classrooms) {
            if (currentClassroom.getGrade() == previeusGrade || previeusGrade == Classroom.UNKNOWN_GRADE) {
                previeusGrade = currentClassroom.getGrade();
            } else {
                previeusGrade = Classroom.UNKNOWN_GRADE;
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
        StringBuilder allGrades = new StringBuilder();
        for (Classroom currentClassroom : classrooms) {
            if (allGrades.length() != 0) allGrades.append(", ");
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
