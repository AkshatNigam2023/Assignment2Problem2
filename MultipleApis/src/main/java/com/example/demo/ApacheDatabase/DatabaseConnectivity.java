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

    public static void Insertion(List<Employee> Employees) {
        // Insert data into the database using parallel streams
        for (Employee employee : Employees) {
            DatabaseConnectivity.insertEmployee(employee);
        }
    }

    private static void insertEmployee(Employee employee) {
        String sql = "INSERT INTO employees (Idate, Imonth,team,PanelName,round,skill,Itime,Clocation,Plocation,Cname) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, employee.getDate());
            statement.setDate(2, employee.getMonth());
            statement.setString(3, employee.getTeam());
            statement.setString(4, employee.getPanelName());
            statement.setString(5, employee.getRound());
            statement.setString(6, employee.getSkill());
            statement.setTime(7, employee.getTime());
            statement.setString(8, employee.getCurrentLoc());
            statement.setString(9, employee.getPreferredLoc());
            statement.setString(10, employee.getCandidateName());
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

    static public JFreeChart findMaxInterview() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String sql = "SELECT team , COUNT(*) as count from Employees "+
                     "WHERE MONTH(Imonth) IN (10, 11) AND YEAR(Imonth) = 2023 "+
                     "GROUP BY team " +
                     "ORDER BY count DESC "+
                     " Limit 1";
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(sql)) {
            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();
            // Process the result
            while (resultSet.next()) {
                // Access columns by name or index
                String column1Value = resultSet.getString("team");
                int column2Value = resultSet.getInt("count");
                dataset.addValue(column2Value,"Records",column1Value);
                // Process the values as needed
                System.out.println("Team: " + column1Value + " Count: " + column2Value );
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        JFreeChart chart = ChartFactory.createBarChart(
                "Team with Maximum Interviews in Oct'23 and Nov'23",
                "Team",
                "Total Number Of Interviews",
                dataset
        );

        return chart;
    }

    static public JFreeChart findMinInterview() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String sql = "SELECT team , COUNT(*) as count from Employees "+
                "WHERE MONTH(Imonth) IN (10, 11) AND YEAR(Imonth) = 2023 "+
                "GROUP BY team " +
                "ORDER BY count"+
                " Limit 1";
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(sql)) {
            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();
            // Process the result
            while (resultSet.next()) {
                // Access columns by name or index
                String column1Value = resultSet.getString("team");
                int column2Value = resultSet.getInt("count");
                dataset.addValue(column2Value,"Records",column1Value);
                // Process the values as needed
                System.out.println("Team: " + column1Value + " Count: " + column2Value );
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        JFreeChart chart = ChartFactory.createBarChart(
                "Team with Minimum Interviews in Oct'23 and Nov'23",
                "Team",
                "Total Number Of Interviews",
                dataset
        );

        return chart;
    }

    static public JFreeChart viewTop3Skill() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try (Connection connection = dataSource.getConnection()) {
            // Create the view if it doesn't exist
            createTopSkillsView(connection);

            // Execute the SELECT statement on the view
            String sql = "SELECT skill, skill_count FROM top_skills_view ORDER BY skill_count DESC LIMIT 3";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                // Execute the query
                ResultSet resultSet = preparedStatement.executeQuery();

                // Process the result
                while (resultSet.next()) {
                    // Access columns by name or index
                    String skill = resultSet.getString("skill");
                    int skillCount = resultSet.getInt("skill_count");
                    // Process the values as needed
                    dataset.addValue(skillCount,"Records",skill);
                    System.out.println("Skill: " + skill + " Count: " + skillCount);
                }
            }
            return ChartFactory.createBarChart("Top 3 skills in the months October and November", "Skill", "Skill Count", dataset, PlotOrientation.VERTICAL, true, true, false);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    static void createTopSkillsView(Connection connection) throws SQLException {
        // Create the view
        String createViewSql = "CREATE VIEW top_skills_view AS " +
                "SELECT skill, COUNT(*) as skill_count " +
                "FROM Employees " +
                "WHERE MONTH(Imonth) IN (10, 11) AND YEAR(Imonth) = 2023 " +
                "GROUP BY skill";

        try (PreparedStatement createViewStatement = connection.prepareStatement(createViewSql)) {
            createViewStatement.execute();
        }
    }

    static public JFreeChart viewTop3SkillsInPeakTime() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try (Connection connection = dataSource.getConnection()) {
            // Create the view if it doesn't exist
            createPeakTimeInterviewsView(connection);

            // Execute the SELECT statement on the view
            String sql = "SELECT skill, skill_count FROM peak_time_interviews ORDER BY skill_count DESC LIMIT 3";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                // Execute the query
                ResultSet resultSet = preparedStatement.executeQuery();

                // Process the result
                while (resultSet.next()) {
                    // Access columns by name or index
                    String skill = resultSet.getString("skill");
                    int skillCount = resultSet.getInt("skill_count");
                    dataset.addValue(skillCount,"Records",skill);
                    // Process the values as needed
                    System.out.println("Skill: " + skill + " Count: " + skillCount);
                }
            }
            return ChartFactory.createBarChart("Top 3 skills in Peak Time BETWEEN (9 AND 17 )", "Skill", "Skill Count", dataset, PlotOrientation.VERTICAL, true, true, false);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    static void createPeakTimeInterviewsView(Connection connection) throws SQLException {
        // Create the view for peak time interviews
            String createViewSql = "CREATE VIEW peak_time_interviews AS " +
                "SELECT skill, COUNT(*) as skill_count " +
                "FROM Employees " +
                "WHERE EXTRACT(HOUR FROM Itime) BETWEEN 9 AND 17 " + // Adjust peak time hours
                "GROUP BY skill";

        try (PreparedStatement createViewStatement = connection.prepareStatement(createViewSql)) {
            createViewStatement.execute();
        }
    }

    static public JFreeChart getTop3Panels(List<Employee> Employees) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
        Map<String, Integer> result = Employees.stream().filter(emp -> dateFormat.format(emp.getMonth()) != null && dateFormat.format(emp.getMonth()).equals("2023-10-01 00:00:00") || dateFormat.format(emp.getMonth()) != null && dateFormat.format(emp.getMonth()).equals("2023-11-01 00:00:00")).collect(Collectors.groupingBy(record -> record.getPanelName(), Collectors.summingInt(r -> 1)));

        List<Map.Entry<String,Integer>> top3Panels= result.entrySet().stream().sorted(Map.Entry.<String,Integer>comparingByValue().reversed()).limit(3).collect(Collectors.toList());
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        top3Panels.forEach(entry -> dataset.addValue(entry.getValue(), "Interviews", entry.getKey()));

        return ChartFactory.createBarChart("Top 3 panels in October and November 2023", "Panel", "Interview Count", dataset, PlotOrientation.VERTICAL, true, true, false);
    }


}
