package com.bayoumi.util.gui;

import com.bayoumi.util.Constants;
import com.bayoumi.util.Utility;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.HashMap;

public class HelperMethods {

    public static void ExitKeyCodeCombination(Scene scene, Stage stage) {
        HashMap<KeyCombination, Runnable> hashMap = new HashMap<>();
        // [CTRL + W] => close stage
        hashMap.put(new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN), Utility::exitProgramAction);
        scene.getAccelerators().putAll(hashMap);
    }

    public static void SetAppDecoration(Stage stage) {
        stage.setTitle(Constants.APP_NAME);
        SetIcon(stage);
    }

    public static void SetIcon(Stage stage) {
        stage.getIcons().clear();
        stage.getIcons().add(new Image("/com/bayoumi/images/Logo_x1.png"));
    }

    public static void setStageListener(Node node, Callback<Stage, Void> callback) {
        node.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (oldScene == null && newScene != null) {
                newScene.windowProperty().addListener((observable, oldWindow, newWindow) -> {
                    if (oldWindow == null && newWindow != null) {
                        callback.call((Stage) newWindow);
                    }
                });
            }
        });
    }

}