package africa.jopen.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.List;

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
    TreeItem<String> rootNode =
            new TreeItem<>("Root", new ImageView(new Image(getClass().getResourceAsStream("/images/settings@32-px.png"))));

    public TreeItem<String> generateTree(){
        rootNode.setExpanded(true);
        ObjectMapper mapper = new ObjectMapper();
        try {
            Structure[] structures = mapper.readValue(json, Structure[].class);
            for (Structure structure : structures) {
                rootNode.getChildren().add(structure.toTreeItem());
            }
            return rootNode;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public static final class TextFieldTreeCellImpl extends TreeCell<String> {

        private TextField textField;
        private ContextMenu addFolderMenu = new ContextMenu();
        private ContextMenu addFileMenu = new ContextMenu();

        public TextFieldTreeCellImpl() {
            MenuItem addFolderMenuItem = new MenuItem("Add Folder");
            addFolderMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    TreeItem<String> newFolder =
                            new TreeItem<>("New Folder", new ImageView(new Image(getClass().getResourceAsStream("/images/settings@32-px.png"))));
                    getTreeItem().getChildren().add(newFolder);
                    newFolder.setExpanded(true);
                }
            });

            MenuItem addFileMenuItem = new MenuItem("Add File");
            addFileMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    TreeItem<String> newFile =
                            new TreeItem<>("New File", new ImageView(new Image(getClass().getResourceAsStream("/images/settings@32-px.png"))));
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
