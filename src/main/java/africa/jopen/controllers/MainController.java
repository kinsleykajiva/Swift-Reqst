package africa.jopen.controllers;


import africa.jopen.BaseApplication;

import africa.jopen.database.DatabaseManager;
import africa.jopen.enums.SendingRequestTypes;
import africa.jopen.models.NavigationEntity;
import africa.jopen.models.RepositoriesEntity;
import africa.jopen.utils.HTTPConstants;
import africa.jopen.utils.Repositorybuilder;
import africa.jopen.utils.codearea.EverestCodeArea;
import africa.jopen.utils.codearea.highlighters.HighlighterFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.jfoenix.controls.JFXButton;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;
import org.fxmisc.flowless.VirtualizedScrollPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    public Accordion accordioFolders;

    @FXML
    public VBox vBoxTree;
    @FXML
    private MFXFontIcon closeIcon;

    @FXML
    private MFXFontIcon minimizeIcon;
    @FXML
    private JFXButton btnNewFolder;
    private EverestCodeArea rawInputArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
     //   DatabaseManager.getInstance().saveNewRepository(new RepositoriesEntity(0,"REst API Data"));


        ObservableList<RepositoriesEntity> repositoriesEntityObservableList = DatabaseManager.getInstance()
               .getRepositories();

        RepositoriesEntity repository = repositoriesEntityObservableList.stream().findFirst().get();

        System.out.println("ggg === "+repository.title());

        List<Repositorybuilder.Folder> folderList = new ArrayList<>();
        repositoriesEntityObservableList.forEach(repositoriesEntity -> {
            System.out.println("hhhh === "+repository.title());
          //  folderList.add(new Repositorybuilder.Folder(repositoriesEntity.getName(), repositoriesEntity.getId()));

        });

       /* ObservableList<NavigationEntity> getNavigationsFolders = DatabaseManager.getInstance()
                .connect().getNavigationsFolders(repositoriesEntityObservableList.get(0).id());
        getNavigationsFolders.forEach(navigationEntity -> {
            System.out.println(navigationEntity.title());
        });*/


        var j = new Repositorybuilder(accordioFolders);
        comboBoxSendingType.getItems().setAll(SendingRequestTypes.values());
        Screen screen = Screen.getPrimary();

        Rectangle2D bounds = screen.getBounds();
        double screenWidth = bounds.getWidth();
        double screenHeight = bounds.getHeight();


        closeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> Platform.exit());

        minimizeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> ((Stage) mainRootAnchorPane.getScene().getWindow()).setIconified(true));
        btnNewFolder.setOnAction(event -> {
            try {
                j.addFolderToTree("New Folder Request" ,repository.id());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        });


        try {
            j.generateTree(repository.id());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }



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





        //  vBoxTree.getChildren().add();
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
