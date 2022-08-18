package com.bayoumi.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.io.File;

/**
 * @author venkataudaykiranp
 * @version 2.0.16(Apache PDFBox version support)
 */
public class ConvertUtil {

    public static void convert(File sourceFile, File destinationFile, String FILE_EXTENSION, int DPI, ProgressNotification progressNotification) throws Exception {
        if (!destinationFile.exists()) {
            destinationFile.mkdir();
            System.out.println("Folder Created -> " + destinationFile.getAbsolutePath());
        }
        if (sourceFile.exists()) {
            System.out.println("Images copied to Folder Location: " + destinationFile.getAbsolutePath());
            final PDDocument DOCUMENT = PDDocument.load(sourceFile);
            final PDFRenderer PDF_RENDER = new PDFRenderer(DOCUMENT);

            final int numberOfPages = DOCUMENT.getNumberOfPages();
            System.out.println("Total files to be converting -> " + numberOfPages);

            final String fileName = sourceFile.getName().replace(".pdf", "");
            for (int i = 0; i < numberOfPages; ++i) {
                final File outPutFile = new File(destinationFile.getAbsolutePath() + "/" + fileName + " - " + (i + 1) + "." + FILE_EXTENSION);
                ImageIO.write(PDF_RENDER.renderImageWithDPI(i, DPI, ImageType.RGB), FILE_EXTENSION, outPutFile);
                progressNotification.run(i, numberOfPages);
            }

            DOCUMENT.close();
            System.out.println("Converted Images are saved at -> " + destinationFile.getAbsolutePath());
        } else {
            System.err.println(sourceFile.getName() + " File not exists");
        }
    }

    public static void main(String[] args) {
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
        try {
            // Pdf files are read from this folder
            final File sourceFile = new File("C:\\Users\\abdel\\Desktop\\القواعد المثلى.pdf");
            // converted images from pdf document are saved here
            final File destinationFile = new File("C:\\Users\\abdel\\Desktop\\New folder (2)/");

            /*
             * 600 dpi give good image clarity but size of each image is 2x times of 300 dpi.
             * Ex:  1. For 300dpi 04-Request-Headers_2.png expected size is 797 KB
             *      2. For 600dpi 04-Request-Headers_2.png expected size is 2.42 MB
             */
            // use less dpi for to save more space in hard-disk. For professional usage you can use more than 300dpi
            final int DPI = 300;
            convert(sourceFile, destinationFile, "png", DPI, (currentPage, totalNumberOfPages) -> {
                System.out.println("Page " + currentPage + " Converted ==> " +
                        (formatNum(currentPage * 1.0 / totalNumberOfPages) * 100) + "%");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double formatNum(double num) {
        double returnedVal = Double.parseDouble(String.format("%.2f", num));
        return Math.abs(returnedVal) == 0 ? 0 : returnedVal;
    }
}
