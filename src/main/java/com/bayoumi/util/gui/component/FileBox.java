package com.bayoumi.util.gui.component;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class FileBox extends HBox {
    public FileBox(String fileNameValue, int numberOfPagesValue) {
        getStyleClass().add("file-container");
        FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.FILE_TEXT_ALT);
        icon.setStyle("-fx-fill: white;-fx-font-size:50;");
        VBox vBox = new VBox();
        getChildren().addAll(vBox);
        HBox.setHgrow(vBox, Priority.ALWAYS);
        Label fileName = new Label("File Name: " + fileNameValue);
        Label numberOfPages = new Label("Number of pages: " + numberOfPagesValue);
        vBox.getChildren().addAll(fileName, numberOfPages);
    }
}
