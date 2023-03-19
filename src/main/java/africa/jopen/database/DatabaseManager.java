package africa.jopen.database;

import africa.jopen.models.NavigationEntity;
import africa.jopen.models.RepositoriesEntity;
import africa.jopen.utils.XUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    public boolean checkIfNavFolderExists(String title){
        try {
            PreparedStatement stmt = connection.prepareStatement("""
                    SELECT * FROM navigation WHERE title=?
                    """);
            stmt.setString(1,title);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return true;
            }
            stmt.closeOnCompletion();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public NavigationEntity createNewNavigationFolder (NavigationEntity navigationEntity){
        try {
        PreparedStatement stmt = connection.prepareStatement("""
                INSERT INTO navigation(repositoriesID,title,folderStructures) VALUES(?,?,?)
                """);
        stmt.setInt(1,navigationEntity.repositoriesID());
            stmt.setString(2,navigationEntity.title());
            stmt.setString(3,navigationEntity.folderStructures());
        stmt.executeUpdate();
        stmt.closeOnCompletion();
            Optional<NavigationEntity> navigationEntity1 = getNavigationsFolders(0, navigationEntity.title()).stream().findFirst();


            return navigationEntity1.orElse(null);
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }

    public void updateNavigationFolder( NavigationEntity navigationEntity){
        try {
        PreparedStatement stmt = connection.prepareStatement("""
                UPDATE navigation SET title=?,folderStructures=? WHERE id=?
                """);
        stmt.setString(1,navigationEntity.title());
        stmt.setString(2,navigationEntity.folderStructures());
        stmt.setInt(3,navigationEntity.id());
        stmt.executeUpdate();
        stmt.closeOnCompletion();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<NavigationEntity> getNavigationsFolders( int repositoriesID,String title){
        try {
            PreparedStatement stmt;
            if(repositoriesID > 0) {
                 stmt = connection.prepareStatement("""
                    SELECT * FROM navigation WHERE repositoriesID=?
                    """);
                stmt.setInt(1,repositoriesID);
            }else {
                 stmt = connection.prepareStatement("""
                    SELECT * FROM navigation WHERE title=?
                    """);
                stmt.setString(1,title);
            }

            ResultSet rs = stmt.executeQuery();

            ObservableList<NavigationEntity> navigations = FXCollections.observableArrayList();
            while (rs.next()){
                navigations.add(new NavigationEntity(rs.getInt("id"),rs.getInt("repositoriesID"),rs.getString("title") ,rs.getString("folderStructures") ));
            }
            stmt.closeOnCompletion();
            return navigations;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return FXCollections.emptyObservableList();
    }



    public void saveNewRepository(RepositoriesEntity repositoriesEntity){
        try {
            PreparedStatement stmt = connection.prepareStatement("""
                    INSERT INTO repositories(title) VALUES(?)
                    """);
            stmt.setString(1,repositoriesEntity.title());
            stmt.executeUpdate();
            stmt.closeOnCompletion();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateRepository(RepositoriesEntity repositoriesEntity){
        try {
            PreparedStatement stmt = connection.prepareStatement("""
                    UPDATE repositories SET title=? WHERE id=?
                    """);
            stmt.setString(1,repositoriesEntity.title());
            stmt.setInt(2,repositoriesEntity.id());
            stmt.executeUpdate();
            stmt.closeOnCompletion();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRepository(RepositoriesEntity repositoriesEntity){
        try {
            PreparedStatement stmt = connection.prepareStatement("""
                    DELETE FROM repositories WHERE id=?
                    """);
            stmt.setInt(1,repositoriesEntity.id());
            stmt.executeUpdate();
            stmt.closeOnCompletion();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<RepositoriesEntity> getRepositories(){

        try {
            PreparedStatement stmt = connection.prepareStatement("""
                    SELECT * FROM repositories
                    """);
            ResultSet rs = stmt.executeQuery();
            ObservableList<RepositoriesEntity> repositories = FXCollections.observableArrayList();
            System.out.println("Repositories" + rs.getFetchSize());
            while (rs.next()){
                repositories.add(new RepositoriesEntity(rs.getInt("id"),rs.getString("title")));
            }
            stmt.closeOnCompletion();
            return repositories;
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return  FXCollections.emptyObservableList();
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
                    title TEXT,
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
