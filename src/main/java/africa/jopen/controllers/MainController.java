package africa.jopen.controllers;


import africa.jopen.BaseApplication;

import africa.jopen.database.DatabaseManager;
import africa.jopen.enums.SendingRequestTypes;
import africa.jopen.models.FieldState;
import africa.jopen.models.NavigationEntity;
import africa.jopen.models.RepositoriesEntity;
import africa.jopen.utils.EverestUtilities;
import africa.jopen.utils.HTTPConstants;
import africa.jopen.utils.Repositorybuilder;
import africa.jopen.utils.XUtils;
import africa.jopen.utils.codearea.EverestCodeArea;
import africa.jopen.utils.codearea.highlighters.HighlighterFactory;
import africa.jopen.utils.fxalert.FXAlert;
import africa.jopen.utils.okhttputils.OkHttpUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.jfoenix.controls.JFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainController implements Initializable {
    private AnimationTimer animationTimer;

    private double targetWidth;
    private OkHttpUtils okHttpUtils = new OkHttpUtils();
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
    public WebView webView;

    @FXML
    TextField addressField;
    @FXML
    ComboBox<String> httpMethodBox;

    @FXML
    public VBox vBoxTree;
    @FXML
    private MFXFontIcon closeIcon;

    @FXML
    private MFXFontIcon minimizeIcon;
    @FXML
    private JFXButton btnNewFolder;
    private EverestCodeArea rawInputArea;
    @FXML
    private MFXProgressSpinner SystemSpinnerLoader;

    @FXML
    private VBox fieldsBox;
    @FXML
    private MFXProgressBar progressBar;

    String json = """                
            """;
    private StringBuilder jsSb = new StringBuilder();
    private StringBuilder cssSb = new StringBuilder();
    private WebEngine engine;
    private IntegerProperty paramsCountProperty;
    private List<StringKeyValueFieldController> paramsControllers;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SystemSpinnerLoader.setVisible(true);
        progressBar.setVisible(false);
        paramsControllers = new ArrayList<>();
        paramsCountProperty = new SimpleIntegerProperty(0);
        //   DatabaseManager.getInstance().saveNewRepository(new RepositoriesEntity(0,"REst API Data"));
        httpMethodBox.getItems().addAll(
                HTTPConstants.GET,
                HTTPConstants.POST,
                HTTPConstants.PUT,
                HTTPConstants.PATCH,
                HTTPConstants.DELETE);

        // Select GET by default
        httpMethodBox.getSelectionModel().select(HTTPConstants.GET);

        ObservableList<RepositoriesEntity> repositoriesEntityObservableList = DatabaseManager.getInstance()
                .getRepositories();

        RepositoriesEntity repository = repositoriesEntityObservableList.stream().findFirst().get();



        List<Repositorybuilder.Folder> folderList = new ArrayList<>();
        repositoriesEntityObservableList.forEach(repositoriesEntity -> {
            System.out.println("hhhh === " + repository.title());
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


        closeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            Platform.exit();


            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            System.out.println("System exiting..");
                            System.exit(0);

                        }
                    },
                    3_000
            );




        });

        minimizeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> ((Stage) mainRootAnchorPane.getScene().getWindow()).setIconified(true));
        btnNewFolder.setOnAction(event -> {
            try {
                j.addFolderToTree("New Folder Request", repository.id());
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


        // WebView webView = new WebView();
        engine = webView.getEngine();
        InputStreamReader streamReader = new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/html/prettyprint/js/pretty-print-json.js")), StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);

        try {
            for (String line; (line = reader.readLine()) != null; ) {
                jsSb.append(line);
            }
            streamReader.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        streamReader = new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/html/prettyprint/css/pretty-print-json.dark-mode.css")), StandardCharsets.UTF_8);
        reader = new BufferedReader(streamReader);

        try {
            for (String line; (line = reader.readLine()) != null; ) {
                cssSb.append(line);
            }
            streamReader.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String html = """
                    <html>
                    <head>                                       
                    </head>
                    <body style="background-color:#25323b;padding: 0 30px 10px 15px;">
                       
                    </body>
                    </html>
                    """;
        engine.loadContent(html);
        fieldsBox.getChildren().clear();

        addParamField(); // Adds a blank param field

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        XUtils.invoke(()-> SystemSpinnerLoader.setVisible(false));

                    }
                },
                6_000
        );

    }

    private void addParamField() {
        addParamField("", "", false);
    }
    private void addParamField(FieldState state) {
        addParamField(state.key, state.value, state.checked);
    }



    private void addParamField(String key, String value, boolean checked) {
        /*
            Re-uses previous field if it is empty, else loads a new one.
            A value of null for the 'event' parameter indicates that the method call
            came from code and not from the user. This call is made while recovering
            the application state.
         */
        if (paramsControllers.size() > 0) {
            StringKeyValueFieldController previousController = paramsControllers.get(paramsControllers.size() - 1);

            if (previousController.isKeyFieldEmpty() &&
                    previousController.isValueFieldEmpty()) {
                previousController.setKeyField(key);
                previousController.setValueField(value);
                previousController.setChecked(checked);

                /*
                    For when the last field is loaded from setState.
                    This makes sure an extra blank field is always present.
                */
                if (!(key.equals("") && value.equals("")))
                    addParamField();

                return;
            }
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/StringKeyValueField.fxml"));
            Parent headerField = loader.load();
            StringKeyValueFieldController controller = loader.getController();
            controller.setKeyField(key);
            controller.setValueField(value);
            controller.setChecked(checked);
            paramsControllers.add(controller);
            paramsCountProperty.set(paramsCountProperty.get() + 1);
            controller.deleteButton.visibleProperty().bind(Bindings.greaterThan(paramsCountProperty, 1));
            controller.deleteButton.setOnAction(e -> {
                fieldsBox.getChildren().remove(headerField);
                paramsControllers.remove(controller);
                paramsCountProperty.set(paramsCountProperty.get() - 1);
                appendParams();
            });
            controller.setKeyHandler(keyEvent -> addParamField());
            controller.getSelectedProperty().addListener(e -> appendParams());
            controller.getKeyProperty().addListener(e -> appendParams());
            controller.getValueProperty().addListener(e -> appendParams());
            fieldsBox.getChildren().add(headerField);
        } catch (IOException e) {
            e.printStackTrace();
           // LoggingService.logSevere("Could not append params field.", e, LocalDateTime.now());
        }


    }

    private void appendParams() {
        StringBuilder url = new StringBuilder();
        url.append(addressField.getText().split("\\?")[0]);

        boolean addedQuestionMark = false;
        String key, value;
        for (StringKeyValueFieldController controller : paramsControllers) {
            if (controller.isChecked()) {
                if (!addedQuestionMark) {
                    url.append("?");
                    addedQuestionMark = true;
                } else {
                    url.append("&");
                }

                key = controller.getHeader().getKey();
                value = controller.getHeader().getValue();
                url.append(key);
                url.append("=");
                url.append(value);
            }
        }

        addressField.clear();
        addressField.setText(EverestUtilities.encodeURL(url.toString()));
    }
    @FXML
    public void sendRequest() {


        try {
            String address = addressField.getText().trim();
            if (address.equals("")) {
                FXAlert.showWarning("Please enter an address.");

                return;
            }

            if (!(address.startsWith("https://") || address.startsWith("http://"))) {
                address = "https://" + address;
                //responseArea.requestFocus();
            }

            // Set again in case the address is manipulated by the above logic
            addressField.setText(address);

            String httpMethod = httpMethodBox.getSelectionModel().getSelectedItem();
            System.out.println(" httpMethod " + httpMethod);

            switch (httpMethodBox.getValue()) {
                case HTTPConstants.GET:
                    progressBar.setVisible(true);
                    Map<String, String> params = new HashMap<>();
                    Map<String, String> headers = new HashMap<>();
                    okHttpUtils.getWithParams(address, params, headers, new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {

                            e.printStackTrace();

                            String html = """
                                    <html>
                                    <head>
                                    <style>                                               
                                            
                                    </style>
                                          
                                       
                                    </head>
                                    <body style="background-color:#25323b;padding: 0 30px 10px 15px;">
                                        <h3 style="color:red;"> Error Occurred  ! </h3>
                                    </body>
                                    </html>
                                    """;
                            XUtils.invoke(() -> engine.loadContent(html));
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String jsonResponse = response.body().string();
                            System.out.println(jsonResponse);

                            String js = jsSb.toString();
                            String css = cssSb.toString();
                            String html = """
                                    <html>
                                    <head>
                                    <style>
                                             """ + css + """        
                                            
                                    </style>
                                          
                                        <script type="text/javascript">
                                        """ + js + """
                                        function displayJSON(json) {                                              
                                            document.getElementById("json").innerHTML = prettyPrintJson.toHtml(json);                                                  
                                        }
                                        </script>
                                    </head>
                                    <body style="background-color:#25323b;padding: 0 30px 10px 15px;">
                                        <pre id="json"></pre>
                                    </body>
                                    </html>
                                    """;
                            XUtils.invoke(() -> {

                                engine.loadContent(html);
                                engine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
                                    progressBar.setVisible(false);
                                    if (newState == Worker.State.SUCCEEDED) {
                                        engine.executeScript("displayJSON(" + jsonResponse + ")");
                                    } else {
                                        // error handling
                                    }
                                });

                            });

                        }
                    });
                    break;

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
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
