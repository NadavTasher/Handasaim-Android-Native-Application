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
import java.util.List;

import nadav.tasher.handasaim.architecture.appcore.components.Classroom;
import nadav.tasher.handasaim.architecture.appcore.components.Schedule;
import nadav.tasher.handasaim.architecture.appcore.components.School;
import nadav.tasher.handasaim.architecture.appcore.components.Subject;

public class AppCore {

    public static double APPCORE_VERSION = 1.9;

    /*
        Note That XSSF Resemmbles XLSX,
        While HSSF Resembles XLS.
        XSSF Is The Newer Format.

        Since 1.8, AppCore is ONLY for essentials (e.g. reading excel, returning a school)
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

    private static String readCell(Cell cell) {
        if (cell != null) {
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
                Classroom classroom = new Classroom(name);
                int readStart = startReadingRow + 1;
                for (int r = readStart; r < rows; r++) {
                    Row row = sheet.getRow(r);
                    String description = readCell(row.getCell(c));
                    if (!description.isEmpty())
                        classroom.addSubject(new Subject(classroom, r - readStart, description));
                }
                builder.addClassroom(classroom);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getDay(Sheet s) {
        return readCell(s.getRow(0).getCell(0));
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

    public static Schedule getSchedule(File excel, String name, String date, String origin) {
        Schedule.Builder builder = Schedule.Builder.fromSchedule(getSchedule(excel));
        builder.setName(name);
        builder.setOrigin(origin);
        builder.setDate(date);
        return builder.build();
    }

    public static School getSchool() {
        return new School(new int[]{465, 510, 555, 615, 660, 730, 775, 830, 875, 930, 975, 1020, 1065});
    }
}
