package com.bayoumi.controllers.home;

import com.bayoumi.util.ConvertUtil;
import com.bayoumi.util.Logger;
import com.bayoumi.util.Utility;
import com.bayoumi.util.gui.BuilderUI;
import com.bayoumi.util.validation.SingleInstance;
import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    private File chosenPDF, outputDir;
    @FXML
    private Label numberOfPages, fileName, status;
    @FXML
    private VBox fileDetailsBox, chooseFileBox, progressBox;
    @FXML
    private AnchorPane inputBox;
    @FXML
    private TextField outputDirTextField;
    @FXML
    private Button convertButton, chooseOutputDirButton;
    @FXML
    private JFXProgressBar progressBar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        convertButton.setDisable(true);
        convertButton.setText("Not ready to convert yet");

        fileDetailsBox.setVisible(false);
        chooseFileBox.setVisible(true);
        progressBox.visibleProperty().bind(convertButton.disabledProperty().not());
        progressBox.visibleProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("gggggggggggg");
            progressBar.setProgress(0);
            status.setText("");
        });
    }

    @FXML
    private void removeFile() {
        chosenPDF = null;
        setFileData(null);

        convertButton.setDisable(true);
        convertButton.setText("Not ready to convert yet");
    }

    private void setFileData(File file) {
        if (file == null) {
            fileName.setText("");
            numberOfPages.setText("0");

            fileDetailsBox.setVisible(false);
            chooseFileBox.setVisible(true);
            inputBox.setOnMouseClicked(event -> chooseFile());
        } else {
            fileDetailsBox.setVisible(true);
            chooseFileBox.setVisible(false);
            inputBox.setOnMouseClicked(null);

            try (final PDDocument DOCUMENT = PDDocument.load(file)) {
                fileName.setText(file.getName());
                numberOfPages.setText(String.valueOf(DOCUMENT.getNumberOfPages()));
            } catch (Exception ex) {
                Logger.error(ex.getLocalizedMessage(), ex, getClass().getName() + ".setFileData()");
            }
        }

    }

    @FXML
    private void chooseFile() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose PDF");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF Only", "*.pdf", "*.PDF"));
        chosenPDF = fileChooser.showOpenDialog(SingleInstance.getInstance().getCurrentStage());
        if (chosenPDF != null) {
            setFileData(chosenPDF);
        }
        if (canConvert()) {
            convertButton.setDisable(false);
            convertButton.setText("Convert");
        }
    }

    @FXML
    private void chooseOutputDir() {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        outputDir = directoryChooser.showDialog(SingleInstance.getInstance().getCurrentStage());
        directoryChooser.setTitle("Choose output Folder");
        if (canConvert()) {
            convertButton.setDisable(false);
            convertButton.setText("Convert");
            outputDirTextField.setText(outputDir.getAbsolutePath());
        }
    }

    @FXML
    private void convert() {
        convertButton.setVisible(false);
        inputBox.setDisable(true);
        chooseOutputDirButton.setDisable(true);
        /*
         * 600 dpi give good image clarity but size of each image is 2x times of 300 dpi.
         * Ex:  1. For 300dpi img.png expected size is 797 KB
         *      2. For 600dpi img.png expected size is 2.42 MB
         */
        // use less dpi for to save more space in hard-disk. For professional usage you can use more than 300dpi
        final int DPI = 300;
        final String FILE_EXTENSION = "png";
        new Thread(() -> {
            try {
                ConvertUtil.convert(chosenPDF, outputDir, FILE_EXTENSION, DPI, (currentPage, totalNumberOfPages) -> {
                    Platform.runLater(() -> {
                        progressBar.setProgress((currentPage + 1) * 1.0 / totalNumberOfPages);

                        status.setText("Page " + (currentPage + 1) + " Converted " +
                                Utility.formatNum(((currentPage + 1) * 1.0 / totalNumberOfPages) * 100) + "%");
                    });

                    System.out.println("Page " + (currentPage + 1) + " Converted ==> " +
                            Utility.formatNum(((currentPage + 1) * 1.0 / totalNumberOfPages) * 100) + "%");

                    if ((currentPage + 1) == totalNumberOfPages) {
                        Platform.runLater(() -> {
                            convertButton.setVisible(true);
                            inputBox.setDisable(false);
                            chooseOutputDirButton.setDisable(false);
                        });
                    }
                });
            } catch (Exception ex) {
                Logger.error(ex.getLocalizedMessage(), ex, getClass().getName() + ".convert()");
                Platform.runLater(() -> BuilderUI.showExceptionDialog(ex));
            }
        }).start();
    }

    private boolean canConvert() {
        return (chosenPDF != null && chosenPDF.exists()) && (outputDir != null);
    }
}
