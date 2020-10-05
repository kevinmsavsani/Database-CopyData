package connector;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

public class Connector {
    public static void main(String[] args) {
        try {
// load the driver class
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Class.forName("org.hsqldb.jdbc.JDBCDriver");

// create  the connection object
            Connection oracleConnection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "sys as SYSDBA", "admin");
            Connection hsqlConnection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/testdb", "SA", "");

// create the statement object
            Statement oracleStatement = oracleConnection.createStatement();
            Statement hsqlStatement = hsqlConnection.createStatement();

// execute query
            List<Region> regionList = new ArrayList<Region>();
            ResultSet oracleRegion = oracleStatement.executeQuery("select * from regions");
            while (oracleRegion.next()) {
                Region region = new Region();
                region.setRegionId(oracleRegion.getInt("REGION_ID"));
                region.setRegionName(oracleRegion.getString("REGION_NAME"));
                regionList.add(region);
            }

            try {
                int resultValue=0;
//                To drop table "DROP TABLE REGION"
                resultValue = hsqlStatement.executeUpdate("CREATE TABLE REGION ( REGION_ID INT NOT NULL, REGION_NAME VARCHAR(50) NOT NULL, PRIMARY KEY (REGION_ID)); ");

                for (Region region : regionList) {
                    String temporary = "INSERT INTO REGION VALUES ("+region.getRegionId()+" , '"+region.getRegionName()+"')";
                    resultValue = hsqlStatement.executeUpdate(temporary);
                    hsqlConnection.commit();
                }

                ResultSet result = hsqlStatement.executeQuery("SELECT REGION_ID, REGION_NAME FROM REGION");

                while (result.next()) {
                    System.out.println(result.getInt("REGION_ID") + " | " +
                            result.getString("REGION_NAME") + " | ");

// close the connection object
                    oracleConnection.close();
                    hsqlConnection.close();

                }
            } catch (Exception e) {
                System.out.println(e);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
