package africa.jopen;


import africa.jopen.database.DatabaseManager;
import africa.jopen.utils.PropertiesDb;
import africa.jopen.utils.XUtils;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;


public class BaseApplication extends Application {

    private double offsetX,offsetY;
    private Scene scene;
    private AnimationTimer animationTimer;
    private double targetWidth;
    private double targetHeight;
    private static Stage stage;


    public static Stage getStage() {
        return BaseApplication.stage;
    }

    public static void setStage(Stage stage) {
        BaseApplication.stage = stage;
    }

    public static void main(String[] args) {

        launch(args);
    }

    @Override
    public void start(Stage primaryStage){

        XUtils.loadSystem();

       // System.out.printf("xxxxxThe User Home Directory is %s", XUtils.PARENT_FOLDER.get());
        new PropertiesDb(false).updateProperty("test", "test11");

        DatabaseManager.getInstance()
                .connect().createTables();

        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/views/main.fxml")));
            scene = new Scene(root, null);
            primaryStage.setScene(scene);
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            primaryStage.setTitle("Swift Reqst");
            primaryStage.setFullScreenExitHint("");
            primaryStage.getIcons().add(new Image(getClass().getResource("/images/app-icon@24-px.png").toExternalForm()));

            primaryStage.getScene().getRoot().setEffect(new DropShadow());
            primaryStage.getScene().setFill(Color.TRANSPARENT);
            scene.setOnMousePressed(event -> {
                offsetX = event.getSceneX();
                offsetY = event.getSceneY();
            });

            scene.setOnMouseDragged(event -> {
                primaryStage.setX(event.getScreenX()-offsetX);
                primaryStage.setY(event.getScreenY()-offsetY);
            });
            primaryStage.show();

            Screen screen = Screen.getPrimary();

            Rectangle2D bounds = screen.getBounds();
            double screenWidth = bounds.getWidth();
            double screenHeight = bounds.getHeight();
            System.out.println("xxxScreen width: " + screenWidth);
            System.out.println("xxScreen height: " + screenHeight);

            animationTimer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    double currentWidth = primaryStage.getWidth();
                    double currentHeight = primaryStage.getHeight();

                    // Calculate the new size using interpolation
                    double newWidth = currentWidth + 0.1 * (targetWidth - currentWidth);
                    double newHeight = currentHeight + 0.1 * (targetHeight - currentHeight);

                    // Update the stage size
                    primaryStage.setWidth(newWidth);
                    primaryStage.setHeight(newHeight);

                    // Stop the animation when the stage reaches the target size
                    if (Math.abs(newWidth - targetWidth) < 1 && Math.abs(newHeight - targetHeight) < 1) {
                        stop();
                    }
                }
            };
            /*primaryStage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    // Start the animation
                    targetWidth = bounds.getWidth();
                    targetHeight = screenHeight;
                    primaryStage.setWidth(0);
                    primaryStage.setHeight(0);
                    animationTimer.start();
                }
            });*/
            setStage(primaryStage);
           /* new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            System.out.println("Timer task started");

                        }
                    },
                    29_000
            );*/

        } catch (IOException e) {
           e.printStackTrace();
        }


    }



}
