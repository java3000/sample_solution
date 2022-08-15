package ru.vk.competition.minbenchmark.helpers;

import ru.vk.competition.minbenchmark.entity.Table;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class DatabaseHelper {

    public static void createTable(Table table) {

        Logger logger = Logger.getLogger("sql");

        Connection connection = null;
        Statement statement = null;
        String myTableName = String.format("CREATE TABLE IF NOT EXISTS %s (", table.getTableName());

        StringBuilder columnBuilder = new StringBuilder(myTableName);
        for (var column : table.getColumnInfos()) {
            if (column.getTitle().equals(table.getPrimaryKey())) {
                columnBuilder.append(String.format("%s %S NOT NULL AUTO_INCREMENT",column.getTitle(), column.getType()));
            } else {
                columnBuilder.append(String.format(", %s %s", column.getTitle(), column.getType()));
            }
        }
        columnBuilder.append(")");

        logger.info(columnBuilder.toString());

        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection("jdbc:h2:mem:mydb", "sa", "password");
            statement = connection.createStatement();
            statement.executeUpdate(columnBuilder.toString());
            statement.close();
            connection.close();
        }
        catch (SQLException e ) {
            System.out.println("An error has occured on Table Creation");
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            System.out.println("An Mysql drivers were not found");
        }
    }
}
