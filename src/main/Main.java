import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
        //scene.setFill(Color.TRANSPARENT);
        //primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Schneider Rates");
        primaryStage.setScene(scene);
        primaryStage.show();

       // windowControls(primaryStage, scene);
    }

//    public void windowControls(Stage primaryStage, Scene scene){
//        TabPane mainBp = (TabPane) scene.lookup("#mainTabPane");
//        Button closeButton = (Button) scene.lookup("#closeBtn");
//
//        // Add functionality to move the window around
//        mainBp.setOnMousePressed(event -> {
//            xOffset = event.getSceneX();
//            yOffset = event.getSceneY();
//        });
//
//        mainBp.setOnMouseDragged(event -> {
//            primaryStage.setX(event.getScreenX() - xOffset);
//            primaryStage.setY(event.getScreenY() - yOffset);
//        });
//
//        // Add functionality to close the window
//        closeButton.setOnAction(event -> primaryStage.close());
//    }

}
