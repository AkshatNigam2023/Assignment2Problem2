package com.example.demo.ExcelSheetReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import com.example.demo.Model.Employee;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
public class DataReader {
    static private final String FILE_PATH = "C:\\Users\\akshat.nigam\\IdeaProjects\\Assignment2\\demo\\Accolite_Interview_Data.xlsx";
    static private final SimpleDateFormat EXCEL_DATE_FORMAT = new SimpleDateFormat("MMM-yy");

    public static List<Employee> getEmployeeList() {
        List<Employee> employees = new ArrayList<>();

        try (FileInputStream file = new FileInputStream(FILE_PATH)) {
            Workbook workbook = WorkbookFactory.create(file);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; // Skip header row
                }

                employees.add(createEmployeeFromRow(row, evaluator));
            }

            workbook.close();
        } catch (IOException | EncryptedDocumentException e) {
            e.printStackTrace();
        }

        return employees;
    }

    private static Employee createEmployeeFromRow(Row row, FormulaEvaluator evaluator) {
        int i = 0;

        java.sql.Date startDate = getSqlDateFromCell(row.getCell(i++), evaluator);
        java.sql.Date endDate = getSqlDateFromExcelString(row.getCell(i++), evaluator);

        String team = getStringCellValue(row.getCell(i++));
        String panelName = getStringCellValue(row.getCell(i++));
        String round = getStringCellValue(row.getCell(i++));
        String skill = getStringCellValue(row.getCell(i++));
        Time timeValue = getTimeCellValue(row.getCell(i++));
        String currentLoc = getStringCellValue(row.getCell(i++), evaluator);
        String preferredLoc = getStringCellValue(row.getCell(i++), evaluator);
        String candidateName = getStringCellValue(row.getCell(i), evaluator);

        return new Employee(startDate, endDate, team, panelName, round, skill, timeValue, currentLoc, preferredLoc, candidateName);
    }

    private static java.sql.Date getSqlDateFromCell(Cell cell, FormulaEvaluator evaluator) {
        java.util.Date utilDate = cell.getDateCellValue();
        return (utilDate != null) ? new java.sql.Date(utilDate.getTime()) : null;
    }

    private static java.sql.Date getSqlDateFromExcelString(Cell cell, FormulaEvaluator evaluator) {
        String excelDateString = evaluator.evaluate(cell).getStringValue();
        java.util.Date date = null;
        java.sql.Date sqlDate = null;
        if (excelDateString != null) {
            try {
                date = EXCEL_DATE_FORMAT.parse(excelDateString);
                if (date != null) {
                    sqlDate = new java.sql.Date(date.getTime());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return sqlDate;
    }

    private static String getStringCellValue(Cell cell) {
        return (cell != null) ? cell.getStringCellValue() : null;
    }

    private static String getStringCellValue(Cell cell, FormulaEvaluator evaluator) {
        CellValue cellValue = evaluator.evaluate(cell);
        return (cellValue != null) ? cellValue.getStringValue() : null;
    }

    private static Time getTimeCellValue(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC) {
            return null;
        }
        return new Time((long) (cell.getNumericCellValue() * 24 * 60 * 60 * 1000));
    }
}

