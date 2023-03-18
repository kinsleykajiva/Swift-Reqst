package africa.jopen.controllers;


import africa.jopen.BaseApplication;
import africa.jopen.TreeViewSample;
import africa.jopen.enums.SendingRequestTypes;
import africa.jopen.utils.HTTPConstants;
import africa.jopen.utils.Repositorybuilder;
import africa.jopen.utils.codearea.EverestCodeArea;
import africa.jopen.utils.codearea.highlighters.HighlighterFactory;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import org.fxmisc.flowless.VirtualizedScrollPane;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainController implements Initializable {
    private AnimationTimer animationTimer;

    private double targetWidth;
    private double targetHeight;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @FXML
    private AnchorPane mainRootAnchorPane;
    @FXML
    private Pane codearePane;
    @FXML
    private ComboBox<SendingRequestTypes> comboBoxSendingType;
    @FXML
    public ImageView imgBtnFullScreen;

    @FXML
    public VBox vBoxTree;


    private EverestCodeArea rawInputArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        comboBoxSendingType.getItems().setAll(SendingRequestTypes.values());
        Screen screen = Screen.getPrimary();

        Rectangle2D bounds = screen.getBounds();
        double screenWidth = bounds.getWidth();
        double screenHeight = bounds.getHeight();
        imgBtnFullScreen.setOnMouseClicked(e -> {
            // Stage stage = currentStage();
           /* stage.setX(0);
            stage.setY(0);

            Animation animation = new Timeline(
                    new KeyFrame( Duration.seconds(1), new KeyValue(mainRootAnchorPane.prefWidthProperty(), screenWidth)),
                    new KeyFrame( Duration.seconds(1), new KeyValue(mainRootAnchorPane.prefHeightProperty(), screenHeight)),
                    new KeyFrame( Duration.seconds(1), new KeyValue(stage.minWidthProperty(), screenWidth)),
                    new KeyFrame( Duration.seconds(1), new KeyValue(stage.minHeightProperty(), screenHeight))
            );
            animation.play();*/


            //  stage.setFullScreen(!stage.isFullScreen());

            BaseApplication.getStage().setFullScreen(!BaseApplication.getStage().isFullScreen());

            imgBtnFullScreen.setImage(new Image(getClass().getResource(BaseApplication.getStage().isFullScreen() ? "/images/exit-full-screen@32-px.png" : "/images/fullscreen@32-px.png").toString()));
        });


        rawInputArea = new EverestCodeArea();
        rawInputArea.setMaxWidth(Double.MAX_VALUE);
        rawInputArea.setMaxHeight(Double.MAX_VALUE);
        rawInputArea.setPrefHeight(codearePane.getPrefHeight());
        rawInputArea.setPrefWidth(codearePane.getPrefWidth());


        codearePane.getChildren().add(new VirtualizedScrollPane<>(rawInputArea));

        rawInputArea.prefWidthProperty().bind(codearePane.widthProperty());
        rawInputArea.prefHeightProperty().bind(codearePane.heightProperty());


        rawInputArea.setHighlighter(HighlighterFactory.getHighlighter(HTTPConstants.JSON));

        var j = new Repositorybuilder();
        TreeView<String> treeView = new TreeView<String>(j.generateTree());
        treeView.setEditable(true);

        treeView.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>(){
            @Override
            public TreeCell<String> call(TreeView<String> p) {
                return new Repositorybuilder.TextFieldTreeCellImpl();
            }
        });

        vBoxTree.getChildren().add(treeView);
        // TextFieldTreeCellImpl
        // vBoxTree.getChildren().add(j.generateTree());

    }

    @FXML
    public void sendRequest() {

    }

    private Stage currentStage() {
        return (Stage) mainRootAnchorPane.getScene().getWindow();
    }

    private void execute(Runnable runnable) {
        try {
            executor.submit(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
