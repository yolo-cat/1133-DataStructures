import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class test extends Application {
  @Override
  public void start(Stage primaryStage) {
    Label label = new Label("Hello, JavaFX!");
    primaryStage.setScene(new Scene(label, 300, 200));
    primaryStage.show();
  }
  public static void main(String[] args) {
    launch(args);
  }
}