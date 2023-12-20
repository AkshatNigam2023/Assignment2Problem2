package com.example.demo;

import com.example.demo.ExcelSheetReader.ExcelReader;
import com.example.demo.Model.Employee;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);

		ExcelReader excelReader = new ExcelReader();
		List<Employee> Employees = excelReader.getEmployeeList();
	}
}
