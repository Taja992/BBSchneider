import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public class Main extends Application {
    private double xOffset = 0;
    private double yOffset = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GUI/view/main.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
      //  scene.setFill(Color.TRANSPARENT);
     //   primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Schneider Rates");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/favicon2.png")).toExternalForm()));
        primaryStage.show();

        //windowControls(primaryStage, scene);
    }

//    public void windowControls(Stage primaryStage, Scene scene){
//        VBox mainVbox = (VBox) scene.lookup("#mainVbox");
//        Button closeButton = (Button) scene.lookup("#closeBtn");
//
//        // Add functionality to move the window around
//        mainVbox.setOnMousePressed(event -> {
//            xOffset = event.getSceneX();
//            yOffset = event.getSceneY();
//        });
//
//        mainVbox.setOnMouseDragged(event -> {
//            primaryStage.setX(event.getScreenX() - xOffset);
//            primaryStage.setY(event.getScreenY() - yOffset);
//        });
//
//        // Add functionality to close the window
//        closeButton.setOnAction(event -> primaryStage.close());
//    }

}
