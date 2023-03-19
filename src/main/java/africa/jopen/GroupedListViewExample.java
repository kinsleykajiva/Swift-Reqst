package africa.jopen;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GroupedListViewExample extends Application {



    private String json1 = """
            [
            {"title" : "Companies Folder"  ,"type":"folder" , "children":[
            {"title" : "3FileYYYY" ,"type":"file" , "children":null},
            {"title" : "Ft33" ,"type":"file" , "children":null}
            ]} ,
            {"title" : "Accounting Folder"  ,"type":"folder" , "children":[]} ,
            {"title" : "Clients Folder" ,"type":"folder" , "children":[]} ,
            {"title" : "e-Commerce Folder" ,"type":"folder" , "children":[
            {"title" : "1File" ,"type":"file" , "children":null},
            {"title" : "8File" ,"type":"file" , "children":null},
            {"title" : "AAAAAA9File" ,"type":"folder" , "children":[
            {"title" : "3FileYYYY" ,"type":"file" , "children":null}
            ]},
            {"title" : "10File" ,"type":"file" , "children":null},
            {"title" : "12File" ,"type":"file" , "children":null}
            ]} ,
            {"title" : "Users Folder"  ,"type":"folder" , "children":null }
            ]
            """;
    @Override
    public void start(Stage primaryStage) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Folder[] folders = objectMapper.readValue(json1, Folder[].class);
        Accordion accordion = new Accordion();
        for (Folder folder : folders) {
            if (folder.getType().equals("folder")) {
                TitledPane titledPane = new TitledPane();
                titledPane.setText(folder.getTitle());
                VBox vBox = new VBox();
                ListView<String> listView = new ListView<>();
                if (folder.getChildren() != null) {
                    for (Folder childFolder : folder.getChildren()) {
                        listView.getItems().add(childFolder.getTitle());
                    }
                }
                listView.setOnMouseClicked(e -> {
                    String selectedItem = listView.getSelectionModel().getSelectedItem();
                    if (selectedItem != null) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Item Clicked");
                        alert.setHeaderText(null);
                        alert.setContentText(selectedItem + " was clicked.");
                        alert.showAndWait();
                    }
                });
                vBox.getChildren().add(listView);
                titledPane.setContent(vBox);
                accordion.getPanes().add(titledPane);
            }
        }
        Scene scene = new Scene(accordion, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    static class Folder {
        private String title;
        private String type;
        private Folder[] children;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Folder[] getChildren() {
            return children;
        }

        public void setChildren(Folder[] children) {
            this.children = children;
        }
    }
}













