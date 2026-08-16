package com.ilapa;

import com.ilapa.dashboard.DashboardController;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        DashboardController controller = new DashboardController();
        controller.show(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
