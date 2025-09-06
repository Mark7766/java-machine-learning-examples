package com.sandy.ml.image.classification;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImagePreprocessor {
    public static void preprocessImages(String inputDir, String outputDir, int width, int height) throws IOException {
        System.out.println("input:"+inputDir);
        File input = new File(inputDir);
        File output = new File(outputDir);
        if (!output.exists()) output.mkdirs();
        for (File file : input.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".jpg")) {
                try {
                    System.out.println(inputDir+"/"+file.getName());
                    BufferedImage img = ImageIO.read(file);
                    BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                    resized.getGraphics().drawImage(img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
                    ImageIO.write(resized, "jpg", new File(output, file.getName()));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        preprocessImages("/Users/mark/work/gitspace/opensource/java-machine-learning-examples/image-classification/src/main/resources/kagglecatsanddogs_5340/PetImages/Cat", "cats_vs_dogs/processed/cats", 64, 64);
        preprocessImages("/Users/mark/work/gitspace/opensource/java-machine-learning-examples/image-classification/src/main/resources/kagglecatsanddogs_5340/PetImages/Dog", "cats_vs_dogs/processed/dogs", 64, 64);
    }
}