package com.bayoumi;

import com.bayoumi.controllers.home.HomeController;
import com.bayoumi.util.Constants;
import com.bayoumi.util.Logger;
import com.bayoumi.util.Utility;
import com.bayoumi.util.gui.HelperMethods;
import com.bayoumi.util.gui.load.Locations;
import com.bayoumi.util.validation.SingleInstance;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {
    // for logging purpose
    public static Long startTime;

    public static void main(String[] args) {
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
        launch(args);
    }

    @Override
    public void stop() {
        System.out.println("stop()...");
        Utility.exitProgramAction();
    }

    @Override
    public void init() {
        startTime = System.currentTimeMillis();

        // --- Create Needed Folder if not exist ---
        Utility.createDirectory(Constants.assetsPath + "/logs");

        // --- initialize Logger ---
        Logger.init();
        Logger.info("App Launched");
    }

    public void start(Stage primaryStage) {
        try {
            // --- load Homepage FXML ---
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Locations.Home.toString()));
            Scene scene = new Scene(loader.load());
            HomeController homeController = loader.getController();
            // add loaded scene to primaryStage
            primaryStage.setScene(scene);
            // set Title and Icon to primaryStage
            HelperMethods.SetAppDecoration(primaryStage);
            HelperMethods.ExitKeyCodeCombination(scene, primaryStage);
            primaryStage.show();
            primaryStage.setOnCloseRequest(event -> Utility.exitProgramAction());
            // assign current primaryStage to SingleInstance Class
            SingleInstance.getInstance().setCurrentStage(primaryStage);
        } catch (Exception ex) {
            Logger.error(ex.getLocalizedMessage(), ex, getClass().getName() + ".start()");
        }
    }
}
