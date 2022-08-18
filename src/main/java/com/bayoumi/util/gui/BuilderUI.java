package com.bayoumi.util.gui;

import com.bayoumi.util.Constants;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Bayoumi
 */
public class BuilderUI {
    public static void showExceptionDialog(Throwable throwable) {
        throwable.printStackTrace();/*w ww. j a  va2s.  c  o m*/

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(Constants.APP_NAME);
        alert.setHeaderText("Thrown Exception");
        alert.setContentText("App has thrown an exception.");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    public static void showOkAlert(Alert.AlertType alertType, String text, boolean isRTL) {
        Alert alert = new Alert(alertType);
        alert.setTitle("");
        alert.setHeaderText(null);
        alert.setContentText(text);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(BuilderUI.class.getResource("/com/bayoumi/css/main.css").toExternalForm());
        if (isRTL) {
            (dialogPane.getChildren().get(1)).setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        }
        HelperMethods.SetIcon((Stage) alert.getDialogPane().getScene().getWindow());
        alert.showAndWait();
    }

    /*
        public static boolean showConfirmAlert(boolean isDanger, String text) {
            try {
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                HelperMethods.SetIcon(stage);
                FXMLLoader loader = new FXMLLoader(BuilderUI.class.getResource(Locations.ConfirmAlert.toString()));
                stage.setScene(new Scene(loader.load()));
                ConfirmAlertController controller = loader.getController();
                controller.setData(isDanger, text);
                stage.showAndWait();
                return controller.isConfirmed;
            } catch (Exception ex) {
                Logger.error(null, ex, BuilderUI.class.getName() + ".showConfirmAlert()");
                return false;
            }
        }*/
/*
    public static String showEditTextField(String prompt, String value) {
        try {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            HelperMethods.SetIcon(stage);
            FXMLLoader loader = new FXMLLoader(BuilderUI.class.getResource(Locations.EditTextField.toString()));
            stage.setScene(new Scene(loader.load()));
            EditTextFieldController controller = loader.getController();
            controller.setData(prompt, value);
            stage.showAndWait();
            return controller.returnValue;
        } catch (Exception ex) {
            Logger.error(null, ex, BuilderUI.class.getName() + ".showEditTextField()");
            return "";
        }
    }
*/
    public static Stage initStageDecorated(Scene scene, String title) {
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle(title == null ? "" : title);
        stage.initModality(Modality.APPLICATION_MODAL);
        HelperMethods.SetIcon(stage);
        return stage;
    }
}
