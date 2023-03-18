package africa.jopen.database;

import africa.jopen.utils.XUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private Connection connection;
    private static DatabaseManager instance = null;
    private final String url = "jdbc:sqlite:"+ XUtils.PARENT_FOLDER.get()+ File.separator+"prime-cache.db";
    private void DatabaseManager(){connect();}
    public static DatabaseManager getInstance(){
        if (instance==null)
            instance = new DatabaseManager();
        return instance;
    }


    public DatabaseManager connect(){
        try {
            connection = DriverManager.getConnection(url);
           /* if(connection != null && !connection.isClosed()) {
                return true;
            }*/
        }catch (SQLException e){
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
        return this;
    }

    public DatabaseManager disconnect(){
        try {
            if(connection!=null && !connection.isClosed()) {
                connection.close();

            }
        }catch (SQLException e){
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
        return  this;
    }

    public DatabaseManager createTables(){
        try {
            String queryRepositories = """
                    CREATE TABLE IF NOT EXISTS repositories(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title VARCHAR(255)
                    );
""";
            String queryNav= """
                    CREATE TABLE IF NOT EXISTS navigation(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    repositoriesID INTEGER,
                    folderStructures TEXT,
                        FOREIGN KEY (repositoriesID) REFERENCES repositories(id)
                    );
""";
            String queryReq= """
                    CREATE TABLE IF NOT EXISTS requests(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    navigationID INTEGER,
                    url TEXT,
                    query TEXT,
                    headers TEXT,
                    auth TEXT,
                    body TEXT,
                    bodyType VARCHAR(255),
                    response TEXT,
                    responseHeaders TEXT,
                    responseCookies TEXT,
                    responseCode VARCHAR(255),
                    responseTimeSpent VARCHAR(255),
                    timeStamp VARCHAR(255),
                    FOREIGN KEY (navigationID) REFERENCES navigation(id)
                    );
""";

            Statement stmt = connection.createStatement();
            stmt.executeUpdate(queryRepositories);
            stmt.executeUpdate(queryNav);
            stmt.executeUpdate(queryReq);

            stmt.close();
            System.out.println("Tables created successfully");




        }catch (SQLException e){
            e.printStackTrace();
        }
        return this;
    }
}
