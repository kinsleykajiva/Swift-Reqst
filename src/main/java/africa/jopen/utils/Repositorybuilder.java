package africa.jopen.utils;


import africa.jopen.database.DatabaseManager;
import africa.jopen.models.NavigationEntity;
import africa.jopen.utils.fxalert.FXAlert;
import atlantafx.base.theme.Tweaks;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static atlantafx.base.theme.Styles.toggleStyleClass;

public class Repositorybuilder extends Node {

public Accordion accordion;


    public Repositorybuilder(Accordion accordion) {
        this.accordion = accordion;
        accordion.getPanes().clear();
        json = "";
    }

    private String json = """
            [
            {"title" : "Companies Folder"  ,"type":"folder" , "url":"" , "requestType":"", "requestBodyString":"" ,"auth":null , "query":"" , "headers":[], "children":[          
            {"title" : "3FileYYYY" ,"type":"file" ,"url":"" , "requestType":"", "requestBodyString":"" ,"auth":{"type":"token"} , "query":[{"k":"v"}] , "headers":[{"k":"v"}], "children":null}
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
            {"title" : "Users Folder"  ,"type":"folder" , "children":[] }         
            ]
            """;
    List<Folder> folderList = new ArrayList<>();
    final double imageSize = 24;
    ImageView folderIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/folder@32-px.png"), imageSize, imageSize, true, true));
    ImageView fileIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/file@32-px.png"), imageSize, imageSize, true, true));


    public void generateTree() throws JsonProcessingException {

        if(json==null || json.isEmpty()) return;

        accordion.getPanes().clear();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //  Folder[] folders = objectMapper.readValue(json, Folder[].class);
        folderList = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Folder.class));

        for (Folder folder : folderList) {
            if (folder.getType().equals("folder")) {
                BuildNewTile tiles = getBuildNewTile(folder);
                tiles.titledPane().setContent(tiles.vBox());
                accordion.getPanes().add(tiles.titledPane());
            }
        }
        accordion.getPanes().forEach(p -> toggleStyleClass(p, Tweaks.ALT_ICON));

    }

    public void addFolderToTree( String folderName,int repositoryID) throws JsonProcessingException {

        if( DatabaseManager.getInstance().checkIfNavFolderExists(folderName)){
            FXAlert.showWarning("Folder already exists");
            return;
        }

     //  accordion.getPanes().clear();
        ObjectMapper objectMapper = new ObjectMapper();
      //  folderList = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Folder.class));
        Folder newFolder = new Folder();
        newFolder.setTitle(folderName);
        newFolder.setType("folder");
        newFolder.setChildren(null);
        newFolder.setHeaders(null);
        newFolder.setQuery(null);
        newFolder.setAuth(null);
        newFolder.setRequestBodyString(null);
        newFolder.setUrl(null);
        newFolder.setRequestType(null);
        // convert Folder object to json string

        String jsonString =   objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(newFolder);
        NavigationEntity result = DatabaseManager.getInstance().createNewNavigationFolder(new NavigationEntity(0,repositoryID,folderName, jsonString));
        newFolder.setNavigationID(result.id());

         jsonString =   objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(newFolder);

        DatabaseManager.getInstance().updateNavigationFolder(new NavigationEntity(result.id(),repositoryID,folderName, jsonString));

        System.out.println("cccccc-jsonString = " + jsonString);

        folderList.add(newFolder);
        folderList.stream()
                .filter(folder -> folder.getType().equals("folder")).map(this::getBuildNewTile)
                .forEach(tiles -> {
                    tiles.titledPane().setContent(tiles.vBox());
                    accordion.getPanes().add(tiles.titledPane());
                });
        accordion.getPanes().forEach(p -> toggleStyleClass(p, Tweaks.ALT_ICON));

    }

    @NotNull
    private BuildNewTile getBuildNewTile(Folder folder) {
        EditableTitledPane titledPane = new EditableTitledPane(folder.getTitle());
        ObservableList<String> items = FXCollections.observableArrayList();

        titledPane.setStyle("-fx-background-color: #394b59;-fx-text-fill: white;");
        ListView<String> listView = new ListView<>(items);
        VBox vBox = new VBox();
        JFXButton btnAdd = new JFXButton("Add New Request");
        Label ignore = new Label("-");
        // add styling using inlined css
        btnAdd.setStyle("-fx-background-color: #394b59; -fx-text-fill: #afb8be;");
        btnAdd.getStyleClass().add("button-raised");
        btnAdd.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog("New Request");
            dialog.setTitle("New Request File");
            dialog.setHeaderText("Enter the name of the new request");
            dialog.setContentText("Request Name:");
            dialog.showAndWait().ifPresent(name -> {

                name = name.trim();
                if (name.isEmpty()) {
                    FXAlert.showWarning("Empty Name ", "Request name cannot be empty ");
                    return;
                }
                // remove duplicates from list
                if (items.contains(name)) {
                    FXAlert.showWarning("Duplicate Found ", "Request name already exists in this folder ");
                    return;
                }
                ObjectMapper mapper = new ObjectMapper();
                //  items.remove(name);
                /*items.add(name);
                listView.getItems().setAll(items);*/

                // find the corresponding folder in folderList
                for (Folder folder1 : folderList) {
                    if (folder1.getTitle().equals(titledPane.getText())) {
                        // create new child folder and add it to the children list
                        Folder newChild = new Folder();
                        newChild.setTitle(name);
                        newChild.setType("file");
                        newChild.setChildren(null);
                        if(folder1.getChildren() ==null){
                            folder1.setChildren(new ArrayList<>());
                        }
                        folder1.getChildren().add(newChild);
                        for (Folder childFolder : folder1.getChildren()) {
                            if(!items.contains(childFolder.getTitle())){
                                items.add(childFolder.getTitle());

                                try {
                                    String jsonString =   mapper.writerWithDefaultPrettyPrinter().writeValueAsString(folder1);
                                    DatabaseManager.getInstance().updateNavigationFolder(new NavigationEntity(folder1.getNavigationID(),-1,folder1.getTitle(), jsonString));
                                } catch (JsonProcessingException e1) {

                                    FXAlert.showException(e1.getCause() , "Error while updating navigation folder");
                                    throw new RuntimeException(e1);
                                }
                            }

                        }
                        //listView.getItems().addAll(items);
                        break;
                    }
                }

                try {

                    String json = mapper.writeValueAsString(folderList);
                    System.out.println("||||json = " + json);
                } catch (JsonProcessingException jsonProcessingException) {
                    jsonProcessingException.printStackTrace();
                }
            });
        });
        vBox.getChildren().add(btnAdd);
        vBox.getChildren().add(ignore);


        if (folder.getChildren() != null) {
            for (Folder childFolder : folder.getChildren()) {
                items.add(childFolder.getTitle());
            }
            listView.getItems().addAll(items);

        }

        listView.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item);
                }
            };
            cell.setOnMouseClicked(e -> {
                if (!cell.isEmpty()) {
                    System.out.println("You clicked on " + cell.getItem());
                    e.consume();
                }
            });
            return cell;
        });

        listView.setOnMouseClicked(e -> {
            System.out.println("You clicked on an empty cell");
        });
        vBox.getChildren().add(listView);
        return new BuildNewTile(titledPane, vBox);

    }

    private record BuildNewTile(EditableTitledPane titledPane, VBox vBox) {
    }

    static record Auth(String type){}
    static record Query(String keyValue){}
    static record Headers(String keyValue){}
    public static class Folder {
        private int navigationID;
        private String title;
        private String type;
        private String url;
        private String requestType;
        private String requestBodyString;
        private Auth auth;
        private List<Query> query;
        private List<Headers> headers;
        private List<Folder> children;

        public int getNavigationID() {
            return navigationID;
        }

        public void setNavigationID(int navigationID) {
            this.navigationID = navigationID;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getRequestType() {
            return requestType;
        }

        public void setRequestType(String requestType) {
            this.requestType = requestType;
        }

        public String getRequestBodyString() {
            return requestBodyString;
        }

        public void setRequestBodyString(String requestBodyString) {
            this.requestBodyString = requestBodyString;
        }

        public Auth getAuth() {
            return auth;
        }

        public void setAuth(Auth auth) {
            this.auth = auth;
        }

        public List<Query> getQuery() {
            return query;
        }

        public void setQuery(List<Query> query) {
            this.query = query;
        }

        public List<Headers> getHeaders() {
            return headers;
        }

        public void setHeaders(List<Headers> headers) {
            this.headers = headers;
        }

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

        public List<Folder> getChildren() {
            return children;
        }

        public void setChildren(List<Folder> children) {
            this.children = children;
        }
    }

    public class EditableTitledPane extends TitledPane {

        private TextField textField;
        final String style = "-fx-text-fill: white;";
        final double imageSize = 24;
        ImageView folderIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/folder@32-px.png"), imageSize, imageSize, true, true));

        /*

          try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(folderList);
            System.out.println("||||json = " + json);
        } catch (JsonProcessingException jsonProcessingException) {
            jsonProcessingException.printStackTrace();
        }
          */


        public EditableTitledPane(String title) {
            super(title, null);
            textField = new TextField(title);
            /*textField.setOnAction(event -> {
                var label = new Label(textField.getText());
                label.setStyle(style);
                setText(textField.getText());
                setGraphic(label);
            });*/
            var lable = new Label(title, folderIcon);
            lable.setStyle(style);
            setGraphic(lable);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    setGraphic(textField);
                    textField.selectAll();
                    textField.requestFocus();
                }
            });


            textField.setOnKeyReleased(t -> {
                // listen to the enter key
                System.out.println("||||bbbb tt = " +t.getCode() );
                ObjectMapper mapper = new ObjectMapper();
                if (t.getCode() == KeyCode.ENTER) {
                    try {

                        String json = mapper.writeValueAsString(folderList);
                        System.out.println("XXXX|json = " + json);
                    } catch (JsonProcessingException jsonProcessingException) {
                        jsonProcessingException.printStackTrace();
                    }
                    final String folderName = textField.getText().trim();

                    for (Folder folder1 : folderList) {
                        if (folder1.getTitle().equals(title)) {
                            folder1.setTitle(folderName);
                            try {
                                String jsonString =   mapper.writerWithDefaultPrettyPrinter().writeValueAsString(folder1);
                                DatabaseManager.getInstance().updateNavigationFolder(new NavigationEntity(folder1.getNavigationID(),-1,folderName, jsonString));
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }

                            break;
                        }
                    }

                    try {

                        String json = mapper.writeValueAsString(folderList);
                        System.out.println("MMMM|json = " + json);
                    } catch (JsonProcessingException jsonProcessingException) {
                        jsonProcessingException.printStackTrace();
                    }


                    var label = new Label(textField.getText());
                    label.setStyle(style);
                    setText(textField.getText());
                    //  setGraphic(null);
                    //  setContent(null);
                    setGraphic(label);

                } else if (t.getCode() == KeyCode.ESCAPE) {
                    // cancelEdit();


                    var label = new Label(textField.getText());
                    label.setStyle(style);
                    setText(textField.getText());
                    //  setGraphic(null);
                    //  setContent(null);
                    setGraphic(label);


                }
            });


        }
    }


    public static final class TextFieldTreeCellImpl extends TreeCell<String> {
        final double imageSize = 24;
        ImageView folderIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/folder@32-px.png"), imageSize, imageSize, true, true));
        ImageView fileIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/file@32-px.png"), imageSize, imageSize, true, true));
        private TextField textField;
        private ContextMenu addFolderMenu = new ContextMenu();
        private ContextMenu addFileMenu = new ContextMenu();

        public TextFieldTreeCellImpl() {
            MenuItem addFolderMenuItem = new MenuItem("Add Folder");
            addFolderMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    TreeItem<String> newFolder =
                            new TreeItem<>("New Folder", folderIcon);
                    getTreeItem().getChildren().add(newFolder);
                    newFolder.setExpanded(true);
                }
            });

            MenuItem addFileMenuItem = new MenuItem("Add File");
            addFileMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    TreeItem<String> newFile =
                            new TreeItem<>("New File", fileIcon);
                    getTreeItem().getChildren().add(newFile);
                }
            });
            MenuItem deleteFileMenuItem = new MenuItem("Delete File");
            deleteFileMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    TreeItem<String> selectedItem = getTreeItem();
                    TreeItem<String> parentItem = selectedItem.getParent();
                    if (parentItem != null) {
                        parentItem.getChildren().remove(selectedItem);
                    }
                }
            });

            addFolderMenu.getItems().addAll(addFolderMenuItem, addFileMenuItem, deleteFileMenuItem);
            addFileMenu.getItems().addAll(deleteFileMenuItem);
        }

        @Override
        public void startEdit() {
            super.startEdit();

            if (textField == null) {
                createTextField();
            }
            setText(null);
            setGraphic(textField);
            textField.selectAll();
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            setText((String) getItem());
            setGraphic(getTreeItem().getGraphic());
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
                setContextMenu(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(getTreeItem().getGraphic());
                    if (!getTreeItem().isLeaf() /*&& getTreeItem().getParent() != null*/) {
                        setContextMenu(addFolderMenu);
                    } else {
                        setContextMenu(addFileMenu);
                    }
                }
            }
        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.setOnKeyReleased(t -> {
                if (t.getCode() == KeyCode.ENTER) {
                    commitEdit(textField.getText());
                } else if (t.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            });

        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    }


}
