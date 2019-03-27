package mysql;

import java.sql.*;

public class ConnectionTest {
    public static void main(String[] args) {
        Connection connection = null;
        Statement st = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql:192.168.99.100:33061/kts",
                    "root", "root");
            st = connection.createStatement();

            System.out.println("connect!");

//            String sql;
//            sql = "select * FROM table;";
//
//            ResultSet rs = st.executeQuery(sql);
//
//            while (rs.next()) {
//                String sqlRecipeProcess = rs.getString("column명");
//            }
//
//            rs.close();
            st.close();
            connection.close();
        } catch (SQLException se1) {
            se1.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (st != null)
                    st.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
}