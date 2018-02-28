package beans;

import utilities.ConnectionFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionBean {
    private ResultSet data;
    private Connection connection;
    private Statement stmt;

    public ConnectionBean(String sql) throws SQLException, ClassNotFoundException {
        this.connection = ConnectionFactory.getConnection();
        Statement stmt = connection.createStatement();
        this.data = stmt.executeQuery(sql);
    }

    public ConnectionBean(String sql, int status) throws SQLException, ClassNotFoundException {
        this.connection = ConnectionFactory.getConnection();
        this.stmt = connection.createStatement();
        stmt.executeUpdate(sql);
    }

    public ResultSet getData() {
        return data;
    }

    public void executeUpdate(String sql) throws SQLException {
        this.stmt.executeUpdate(sql);
    }

    public void closeConnection() throws SQLException {
        this.connection.close();
    }
}
