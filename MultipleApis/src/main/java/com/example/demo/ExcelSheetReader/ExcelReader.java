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


public class ExcelReader {
    static public List<Employee> getEmployeeList() {
        List<Employee> Employees = new ArrayList<>();

        try (FileInputStream file = new FileInputStream("C:\\Users\\akshat.nigam\\IdeaProjects\\Assignment2\\demo\\Accolite_Interview_Data.xlsx")) {
            Workbook workbook = WorkbookFactory.create(file);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            // Assuming you are working with the first sheet
            Sheet sheet = workbook.getSheetAt(0);
            int k = 0;
            for (Row row : sheet) {
                int i = 0;
                if (k == 0) {
                    k++;
                    continue;
                }

                // Iterate through cells in the row
                java.util.Date utilDate = row.getCell(i++).getDateCellValue();
                java.sql.Date sqlDate = null;
                if(utilDate != null) {
                    sqlDate = new java.sql.Date(utilDate.getTime());
                }

                Cell cell = row.getCell(i++); // Get the cell containing the formula
                CellValue cellValue = evaluator.evaluate(cell);
                String excelDateString = cellValue.getStringValue();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-yy");
                java.util.Date date = null;
                java.sql.Date sql_Date = null;
                if(excelDateString!=null) {
                    try {
                        date = dateFormat.parse(excelDateString);
                        // Now 'javaDate' can be used in your database insertion logic
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(date!=null) {
                       sql_Date = new java.sql.Date(date.getTime());
                    }
                }
                String Team = row.getCell(i++).getStringCellValue();
                String PanelName = row.getCell(i++).getStringCellValue();
                String Round = row.getCell(i++).getStringCellValue();
                String skill = row.getCell(i++).getStringCellValue();
                Cell timeCell = row.getCell(i++);
                Time timeValue = null;
                if (timeCell.getCellType() == CellType.NUMERIC) {
                    // If the cell type is numeric, convert it to LocalTime
                    timeValue = new Time((long) (timeCell.getNumericCellValue() * 24 * 60 * 60 * 1000));
                }
                Cell currcell=row.getCell(i++);
                CellValue curvalue=evaluator.evaluate(currcell);
                String CurrentLoc="";
                if(curvalue!=null){
                    CurrentLoc= curvalue.getStringValue();
                }
                Cell prefcell=row.getCell(i++);
                CellValue Prefvalue=evaluator.evaluate(prefcell);
                String PreferredLoc="";
                if(Prefvalue!=null){
                    PreferredLoc=Prefvalue.getStringValue();
                }
                Cell candicell=row.getCell(i++);
                CellValue candivalue=evaluator.evaluate(candicell);
                String CandidateName="";
                if(candivalue!=null){
                    CandidateName= candivalue.getStringValue();
                }
                Employee employee = new Employee(sqlDate,sql_Date, Team, PanelName, Round, skill, timeValue, CurrentLoc, PreferredLoc, CandidateName);
                Employees.add(employee);
            }
            workbook.close();
        } catch (IOException | EncryptedDocumentException e) {
            e.printStackTrace();
        }
        return Employees;
    }
}
