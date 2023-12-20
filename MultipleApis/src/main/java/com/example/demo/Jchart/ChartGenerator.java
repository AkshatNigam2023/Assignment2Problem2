package com.example.demo.Jchart;

import com.example.demo.ApacheDatabase.DatabaseConnectivity;
import com.example.demo.ExcelSheetReader.ExcelReader;
import com.example.demo.Model.Employee;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import org.jfree.chart.JFreeChart;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

;

public class ChartGenerator {

    static public void main(String[] args) {
        DatabaseConnectivity database = new DatabaseConnectivity();
        ExcelReader excelReader = new ExcelReader();

        List<Employee> Employees = excelReader.getEmployeeList();

        DatabaseConnectivity.Insertion(Employees);
        generateCharts(Employees);
    }
private static void generateCharts(List<Employee> employees) {
    String pdfPath = "Output.pdf";

    try (OutputStream os = new FileOutputStream(pdfPath);
         PdfWriter writer = new PdfWriter(os);
         PdfDocument pdfDocument = new PdfDocument(writer);
         Document document = new Document(pdfDocument)) {

        addChartToDocument(DatabaseConnectivity.findMaxInterview(), document);
        addChartToDocument(DatabaseConnectivity.findMinInterview(), document);
        addChartToDocument(DatabaseConnectivity.viewTop3Skill(), document);
        addChartToDocument(DatabaseConnectivity.viewTop3SkillsInPeakTime(), document);
        addChartToDocument(DatabaseConnectivity.getTop3Panels(employees), document);

        System.out.println("PDF Path: " + pdfPath);
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    private static void addChartToDocument(JFreeChart chart, Document document) {
        try {
            BufferedImage image = chart.createBufferedImage(800, 500);
            Image itextImage = new Image(ImageDataFactory.create(image, null));
            document.add(itextImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

