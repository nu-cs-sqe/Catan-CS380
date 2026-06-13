package ui;

import atlantafx.base.theme.PrimerLight;
import controller.SetupController;
import javafx.application.Application;
import javafx.stage.Stage;
import view.SetupView;

public class Main extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
    SetupView setupView = new SetupView();
    new SetupController(setupView, primaryStage);
    primaryStage.setTitle("Catan");
    primaryStage.setScene(setupView.getScene());
    primaryStage.show();
  }
}
