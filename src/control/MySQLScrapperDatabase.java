package control;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import javax.sql.PooledConnection;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Implements the ScrapperDatabase interface using the MySQL database.
 */
public class MySQLScrapperDatabase implements ScraperDatabase {
    private final PooledConnection pool;
    private final Map<String, String> dbInfo;

    private MySQLScrapperDatabase() throws SQLException, FileNotFoundException {
        dbInfo = MySQLInfo.readDBInfo();
        MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();
        dataSource.setURL(dbInfo.get("database"));
        dataSource.setUser("username");
        if (dbInfo.get("password").length() > 0) {
            dataSource.setPassword(dbInfo.get("password"));
            pool = dataSource.getPooledConnection(dbInfo.get("username"), dbInfo.get("password"));
        } else
            pool = dataSource.getPooledConnection();

    }

    @Override
    public boolean addLink(String url, String src) {
        return false;
    }

    @Override
    public Map<String, List<String>> getAllLinks() {
        return null;
    }

    /**
     * Executes the passed sql query and returns fetched results.
     *
     * @param sql    a SQL statement to be sent to the database, typically a
     *               static SQL <code>SELECT</code> statement
     * @param values an array of values to replace "?" characters in the SQL statement
     * @return a <code>ResultSet</code> object that contains the data produced
     * by the given query; never <code>null</code>
     * @throws SQLException if a database access error occurs,
     *                      the given SQL statement produces anything other than a single
     *                      <code>ResultSet</code> object, the method is called on a
     *                      <code>PreparedStatement</code> or <code>CallableStatement</code>
     */
    public ResultSet executeQuery(String sql, Object... values) throws SQLException {
        return (ResultSet) execute(sql, values, false);
    }

    /**
     * Executes passed SQL query and returns number of rows affected.
     *
     * @param sql    a SQL Data Manipulation Language (DML) statement, such as <code>INSERT</code>,
     *               <code>UPDATE</code> or <code>DELETE</code>; or a SQL statement that returns nothing,
     * @param values an array of values to replace "?" characters in the SQL statement
     * @return either (1) the row count for SQL Data Manipulation Language (DML) statements
     * or (2) 0 for SQL statements that return nothing
     * @throws SQLException if a database access error occurs,
     *                      this method is called on a closed <code>Statement</code>, the given
     *                      SQL statement produces a <code>ResultSet</code> object, the method is called on a
     */
    public int executeUpdate(String sql, Object... values) throws SQLException {
        return (int) execute(sql, values, true);
    }


    /**
     * Connects to database, executes passed and returns either a <code>ResultSet</code> or an int,
     * based on what type of query was passed.
     *
     * @return a <code>ResultSet</code> if the query type is not update, an int denoting the number of rows if it is.
     * @throws SQLException if database access error occurs.
     */
    private Object execute(String sql, Object[] values, boolean isUpdate) throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = pool.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.executeQuery("USE " + dbInfo.get("database") + ";");
            if (values != null) {
                addParameters(statement, values);
            }

            if (isUpdate) {
                int nRows = statement.executeUpdate();
                statement.close();
                return nRows;
            } else {
                return statement.executeQuery();
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * adds given parameters to given PreparedStatement
     *
     * @param statement PreparedStatement which we need to add parameters to
     * @param values    parameters needed to be added to PreparedStatement
     * @throws SQLException if database access error occurs.
     */
    private void addParameters(PreparedStatement statement, Object[] values) throws SQLException {
        for (int i = 0; i < values.length; i++) {
            statement.setObject(i + 1, values[i]);
        }
    }
}
