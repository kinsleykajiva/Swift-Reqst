package africa.jopen;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.Screen;
public class FullScreenAnimation extends Application {

    private AnimationTimer animationTimer;
    private Stage stage;
    private double targetWidth;
    private double targetHeight;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.setTitle("Full Screen Animation");

        // Create a rectangle that will be animated
        Rectangle rectangle = new Rectangle(100, 100, Color.RED);
        StackPane root = new StackPane(rectangle);

        Scene scene = new Scene(root, 300, 300);
        stage.setScene(scene);
        stage.show();
        Screen screen = Screen.getPrimary();

        Rectangle2D bounds = screen.getBounds();
        double screenWidth = bounds.getWidth();
        double screenHeight = bounds.getHeight();

        System.out.println("xxxScreen width: " + screenWidth);
        System.out.println("xxScreen height: " + screenHeight);

        // Register the animation timer
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double currentWidth = stage.getWidth();
                double currentHeight = stage.getHeight();

                // Calculate the new size using interpolation
                double newWidth = currentWidth + 0.1 * (targetWidth - currentWidth);
                double newHeight = currentHeight + 0.1 * (targetHeight - currentHeight);

                // Update the stage size
                stage.setWidth(newWidth);
                stage.setHeight(newHeight);

                // Stop the animation when the stage reaches the target size
                if (Math.abs(newWidth - targetWidth) < 1 && Math.abs(newHeight - targetHeight) < 1) {
                    stop();
                }
            }
        };

        // Register a listener to detect when the stage is maximized
       /* stage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Start the animation
                targetWidth = bounds.getWidth();
                targetHeight = screenHeight;
                stage.setWidth(0);
                stage.setHeight(0);
                animationTimer.start();
            }
        });*/
    }

    public static void main(String[] args) {
        launch(args);
    }
}

