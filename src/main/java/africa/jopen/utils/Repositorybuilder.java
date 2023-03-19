package africa.jopen.utils;


import atlantafx.base.theme.Tweaks;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.List;

import static atlantafx.base.theme.Styles.DENSE;
import static atlantafx.base.theme.Styles.toggleStyleClass;

public class Repositorybuilder extends Node {

    public record Structure(String title, String type, List<Structure> children) {
        public TreeItem<String> toTreeItem() {
            TreeItem<String> treeItem = new TreeItem<>(title);
            if ("folder".equals(type) && children != null) {
                for (Structure child : children) {
                    treeItem.getChildren().add(child.toTreeItem());
                }
            }
            return treeItem;
        }
    }


    private String json= """
            [
            {"title" : "Companies Folder"  ,"type":"folder" , "children":[          
            {"title" : "3FileYYYY" ,"type":"file" , "children":null}
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

    final double  imageSize =24;
    ImageView folderIcon = new ImageView(new Image( getClass().getResourceAsStream("/images/folder@32-px.png"),imageSize ,imageSize ,true,true ));
    ImageView fileIcon = new ImageView(new Image( getClass().getResourceAsStream("/images/file@32-px.png"),imageSize ,imageSize ,true,true ));
    TreeItem<String> rootNode =new TreeItem<>("Root", folderIcon);

    public Accordion  generateTree(Accordion accordion ) throws JsonProcessingException {

        accordion.getPanes().clear();
        ObjectMapper objectMapper = new ObjectMapper();
        Folder[] folders = objectMapper.readValue(json, Folder[].class);
        for (Folder folder : folders) {
            if (folder.getType().equals("folder")) {
                TitledPane titledPane = new TitledPane();
                titledPane.setText(folder.getTitle());
                VBox vBox = new VBox();
                JFXButton btnAdd = new JFXButton("Add New Request");
                Label ignore = new Label("-");
                // add styling using inlined css
                btnAdd.setStyle("-fx-background-color: #394b59; -fx-text-fill: #afb8be;");

                btnAdd.getStyleClass().add("button-raised");

                btnAdd.setOnAction(e -> {

                });
                vBox.getChildren().add(btnAdd);
                vBox.getChildren().add(ignore);
                ListView<String> listView = new ListView<>();



                if (folder.getChildren() != null) {
                    for (Folder childFolder : folder.getChildren()) {
                        listView.getItems().add(childFolder.getTitle());
                    }
                    listView.setOnMouseClicked(e -> {
                        String selectedItem = listView.getSelectionModel().getSelectedItem();
                    /*if (selectedItem != null) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Item Clicked");
                        alert.setHeaderText(null);
                        alert.setContentText(selectedItem + " was clicked.");
                        alert.showAndWait();
                    }*/
                    });

                    //listView.setStyle("-fx-background-color: #394b59; -fx-text-fill: #afb8be;");
                    // set list margin  20px
                    vBox.getChildren().add(listView);
                }

                titledPane.setContent(vBox);
                accordion.getPanes().add(titledPane);
            }
        }

        accordion.getPanes().forEach(p -> toggleStyleClass(p, Tweaks.ALT_ICON));
      //  accordion.getPanes().forEach(p -> toggleStyleClass(p, DENSE));
      return accordion;
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





    public static final class TextFieldTreeCellImpl extends TreeCell<String> {
        final double  imageSize =24;
        ImageView folderIcon = new ImageView(new Image( getClass().getResourceAsStream("/images/folder@32-px.png"),imageSize ,imageSize ,true,true ));
        ImageView fileIcon = new ImageView(new Image( getClass().getResourceAsStream("/images/file@32-px.png"),imageSize ,imageSize ,true,true ));
        private TextField textField;
        private ContextMenu addFolderMenu = new ContextMenu();
        private ContextMenu addFileMenu = new ContextMenu();

        public TextFieldTreeCellImpl() {
            MenuItem addFolderMenuItem = new MenuItem("Add Folder");
            addFolderMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    TreeItem<String> newFolder =
                            new TreeItem<>("New Folder", folderIcon   );
                    getTreeItem().getChildren().add(newFolder);
                    newFolder.setExpanded(true);
                }
            });

            MenuItem addFileMenuItem = new MenuItem("Add File");
            addFileMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    TreeItem<String> newFile =
                            new TreeItem<>("New File", fileIcon  );
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

            addFolderMenu.getItems().addAll(addFolderMenuItem, addFileMenuItem ,deleteFileMenuItem);
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
                    }else{
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
