package com.example.demo.ApacheDatabase;
import com.example.demo.Model.Employee;
import org.apache.commons.dbcp2.BasicDataSource;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class DatabaseConnectivity {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/EmployeeHiring";
    private static final String USER = "root";
    private static final String PASSWORD = "Akshat@2023joiner";

    private static final BasicDataSource dataSource = new BasicDataSource();

    static {
        dataSource.setUrl(JDBC_URL);
        dataSource.setUsername(USER);
        dataSource.setPassword(PASSWORD);
        dataSource.setMinIdle(5);
        dataSource.setMaxIdle(10);
        dataSource.setMaxOpenPreparedStatements(100);
    }

    public static void Insertion(List<Employee> employees) {
        employees.forEach(DatabaseConnectivity::insertEmployee);
    }

    private static void insertEmployee(Employee emp) {
        String sql = "INSERT INTO employees (Idate, Imonth,team,PanelName,round,skill,Itime,Clocation,Plocation,Cname) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, emp.getDate());
            statement.setDate(2, emp.getMonth());
            statement.setString(3, emp.getTeam());
            statement.setString(4, emp.getPanelName());
            statement.setString(5, emp.getRound());
            statement.setString(6, emp.getSkill());
            statement.setTime(7, emp.getTime());
            statement.setString(8, emp.getCurrentLoc());
            statement.setString(9, emp.getPreferredLoc());
            statement.setString(10, emp.getCandidateName());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Data inserted successfully!");
            } else {
                System.out.println("Insertion Failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static DefaultCategoryDataset executeChartQuery(String sql, String chartTitle, String categoryLabel, String valueLabel) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String column1Value = resultSet.getString(1);
                int column2Value = resultSet.getInt(2);
                dataset.addValue(column2Value, valueLabel, column1Value);
                System.out.println(column1Value + ": " + column2Value);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return dataset;
    }

    static public JFreeChart maximumInterviews() {
        String sql = "SELECT team , COUNT(*) as count from Employees " +
                "WHERE MONTH(Imonth) IN (10, 11) AND YEAR(Imonth) = 2023 " +
                "GROUP BY team " +
                "ORDER BY count DESC " +
                " Limit 1";
        return createBarChart(sql, "Team with Maximum Interviews in Oct'23 and Nov'23", "Team", "Total Number Of Interviews");
    }

    static public JFreeChart minimumInterviews() {
        String sql = "SELECT team , COUNT(*) as count from Employees " +
                "WHERE MONTH(Imonth) IN (10, 11) AND YEAR(Imonth) = 2023 " +
                "GROUP BY team " +
                "ORDER BY count" +
                " Limit 1";
        return createBarChart(sql, "Team with Minimum Interviews in Oct'23 and Nov'23", "Team", "Total Number Of Interviews");
    }

    static public JFreeChart findTopThreeSkills() {
        createTopSkillsView();
        String sql = "SELECT skill, skill_count FROM top_skills_view ORDER BY skill_count DESC LIMIT 3";
        return createBarChart(sql, "Top 3 skills in the months October and November", "Skill", "Skill Count");
    }

    static void createTopSkillsView() {
        String createViewSql = "CREATE VIEW top_skills_view AS " +
                "SELECT skill, COUNT(*) as skill_count " +
                "FROM Employees " +
                "WHERE MONTH(Imonth) IN (10, 11) AND YEAR(Imonth) = 2023 " +
                "GROUP BY skill";
        executeViewCreation(createViewSql);
    }

    static public JFreeChart peakTimeTopThreeSkills() {
        createPeakTimeInterviewsView();
        String sql = "SELECT skill, skill_count FROM peak_time_interviews ORDER BY skill_count DESC LIMIT 3";
        return createBarChart(sql, "Top 3 skills in Peak Time BETWEEN (9 AND 17 )", "Skill", "Skill Count");
    }

    static void createPeakTimeInterviewsView() {
        String createViewSql = "CREATE VIEW peak_time_interviews AS " +
                "SELECT skill, COUNT(*) as skill_count " +
                "FROM Employees " +
                "WHERE EXTRACT(HOUR FROM Itime) BETWEEN 9 AND 17 " +
                "GROUP BY skill";
        executeViewCreation(createViewSql);
    }

    private static JFreeChart createBarChart(String sql, String chartTitle, String categoryLabel, String valueLabel) {
        DefaultCategoryDataset dataset = executeChartQuery(sql, chartTitle, categoryLabel, valueLabel);
        return ChartFactory.createBarChart(chartTitle, categoryLabel, valueLabel, dataset);
    }

    private static void executeViewCreation(String createViewSql) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement createViewStatement = connection.prepareStatement(createViewSql)) {
            createViewStatement.execute();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
