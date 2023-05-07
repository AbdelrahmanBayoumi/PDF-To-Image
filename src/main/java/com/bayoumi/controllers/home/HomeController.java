package com.bayoumi.controllers.home;

import com.bayoumi.util.ConvertUtil;
import com.bayoumi.util.FileUtils;
import com.bayoumi.util.Logger;
import com.bayoumi.util.Utility;
import com.bayoumi.util.gui.BuilderUI;
import com.bayoumi.util.gui.component.FileBox;
import com.bayoumi.util.validation.SingleInstance;
import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

public class HomeController implements Initializable {
    private List<File> files = new ArrayList<>();
    private File outputDir;
    @FXML
    private Label status;
    @FXML
    private VBox fileDetailsBox, chooseFileBox, progressBox, filesContainer;
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
            progressBar.setProgress(0);
            status.setText("");
        });
    }

    /**
     * Handle what happened when mouse drag a file or list of files to the box
     */
    @FXML
    private void handleFileOverEvent(DragEvent event) {
        if (files != null && !files.isEmpty())
            return;
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        } else {
            event.consume();
        }
    }

    /**
     * Handle what happened when mouse drag then drop a file or list of files to the box
     */
    @FXML
    private void handleFileDroppedEvent(DragEvent event) {
        if (files != null && !files.isEmpty())
            return;
        files = event.getDragboard().getFiles();
        if (files == null) {
            files = new ArrayList<>();
        } else {
            // filter files
            files.removeIf(file -> !FileUtils.getFileExtension(file).equals(".pdf"));
        }
        setFilesData(files);

        if (canConvert()) {
            convertButton.setDisable(false);
            convertButton.setText("Convert");
        }
    }

    @FXML
    private void removeAllFiles() {
        if (files == null) {
            files = new ArrayList<>();
        }
        files.clear();
        setFilesData(files);

        convertButton.setDisable(true);
        convertButton.setText("Not ready to convert yet");
    }


    private void setFilesData(List<File> filesChosen) {
        System.out.println(filesChosen);
        if ((filesChosen == null || filesChosen.isEmpty())) {
            filesContainer.getChildren().clear();
            fileDetailsBox.setVisible(false);
            chooseFileBox.setVisible(true);
            inputBox.setOnMouseClicked(event -> chooseFile());
        } else {
            fileDetailsBox.setVisible(true);
            chooseFileBox.setVisible(false);
            inputBox.setOnMouseClicked(null);
            filesContainer.getChildren().clear();
            filesChosen.forEach(file -> {
                try (final PDDocument DOCUMENT = PDDocument.load(file)) {
                    filesContainer.getChildren().add(new FileBox(file.getName(), DOCUMENT.getNumberOfPages()));
                } catch (Exception ex) {
                    Logger.error(ex.getLocalizedMessage(), ex, getClass().getName() + ".setFilesData()");
                }
            });
        }

    }

    @FXML
    private void chooseFile() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose PDF");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF Only", "*.pdf", "*.PDF"));
        files.clear();
        final List<File> list = fileChooser.showOpenMultipleDialog(SingleInstance.getInstance().getCurrentStage());
        if (list != null) {
            files.addAll(list);
        }
        if (files != null) {
            setFilesData(files);
        }
        if (canConvert()) {
            convertButton.setDisable(false);
            convertButton.setText("Convert");
        }
    }

    /**
     * Opens Directory Chooser to choose Output directory
     */
    @FXML
    private void chooseOutputDir() {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose output Folder");
        outputDir = directoryChooser.showDialog(SingleInstance.getInstance().getCurrentStage());
        if (outputDir != null) {
            outputDirTextField.setText(outputDir.getAbsolutePath());
        }
        if (canConvert()) {
            convertButton.setDisable(false);
            convertButton.setText("Convert");
        }
    }

    private int getNumberOfPages(List<File> filesList) {
        int count = 0;
        for (File file : filesList) {
            try (final PDDocument DOCUMENT = PDDocument.load(file)) {
                count += DOCUMENT.getNumberOfPages();
            } catch (Exception ex) {
                Logger.error(ex.getLocalizedMessage(), ex, getClass().getName() + ".setFilesData()");
            }
        }
        return count;
    }

    @FXML
    private void convert() {
        convertButton.setVisible(false);
        inputBox.setDisable(true);
        chooseOutputDirButton.setDisable(true);
        /*
         * 600 dpi give good image clarity but size of each image is 2x times of 300
         * dpi.
         * Ex: 1. For 300dpi img.png expected size is 797 KB
         * 2. For 600dpi img.png expected size is 2.42 MB
         */
        // use less dpi for to save more space in hard-disk. For professional usage you
        // can use more than 300dpi
        final int DPI = 300;
        final String FILE_EXTENSION = "png";
        final int totalNumOfPagesOfFiles = getNumberOfPages(files);
        AtomicInteger currentPageStat = new AtomicInteger();
        new Thread(() ->
                files.forEach(file -> {
                    try {
                        ConvertUtil.convert(file, outputDir, FILE_EXTENSION, DPI, (currentPage, totalNumberOfPages) -> {
                            Platform.runLater(() -> {
                                progressBar.setProgress((currentPageStat.get() + 1) * 1.0 / totalNumOfPagesOfFiles);

                                status.setText("Page " + (currentPageStat.get() + 1) + " Converted " +
                                        Utility.formatNum(((currentPageStat.get() + 1) * 1.0 / totalNumOfPagesOfFiles) * 100) + "%");
                            });

                            System.out.println("Page " + (currentPageStat.get() + 1) + " Converted ==> " +
                                    Utility.formatNum(((currentPageStat.get() + 1) * 1.0 / totalNumOfPagesOfFiles) * 100) + "%");
                            currentPageStat.getAndIncrement();
                        });
                        // if last file
                        if (files.indexOf(file) == (files.size() - 1)) {
                            Platform.runLater(() -> {
                                convertButton.setVisible(true);
                                inputBox.setDisable(false);
                                chooseOutputDirButton.setDisable(false);
                            });
                        }
                    } catch (Exception ex) {
                        Logger.error(ex.getLocalizedMessage(), ex, getClass().getName() + ".convert()");
                        Platform.runLater(() -> BuilderUI.showExceptionDialog(ex));
                    }
                })).start();
    }

    private boolean canConvert() {
        return (files != null && !files.isEmpty()) && (outputDir != null);
    }

    @FXML
    private void openAbout() {
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/AbdelrahmanBayoumi"));
        } catch (Exception ex) {
            Logger.error(ex.getLocalizedMessage(), ex, getClass().getName() + ".openAbout()");
        }
    }
}
