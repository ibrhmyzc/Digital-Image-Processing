package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import vpt.ByteImage;
import vpt.Image;
import vpt.algorithms.display.Display2D;
import vpt.algorithms.io.Load;
import vpt.algorithms.io.Save;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Ibrahim Yazici");

        Button button = new Button();
        button.setText("Start");
        Label label = new Label();
        Label quality = new Label("Quality ");
        Slider slider = new Slider();

        slider.setMin(0);
        slider.setMax(10);
        slider.setValue(5);
        slider.setBlockIncrement(1);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(5);
        slider.setMinorTickCount(1);

        Button browse = new Button("Browse");
        final File[] selectedImage = {null};
        browse.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("png files", "*.png"));
                selectedImage[0] = fileChooser.showOpenDialog(primaryStage);
            }
        });

        ComboBox comboBox = new ComboBox();
        comboBox.getItems().add("English");
        comboBox.getItems().add("Turkce");
        comboBox.getItems().add("Deutsch");
        comboBox.getSelectionModel().selectFirst();
        comboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String choice = (String) comboBox.getValue();
                if(choice.equals("Turkce")) {
                    button.setText("Calistir");
                    browse.setText("Sec");
                } else if(choice.equals("English")) {
                    button.setText("Run");
                    browse.setText("Browse");
                } else {
                    button.setText("ICH LASS' DAS PROGRAMM LAUFEN");
                    browse.setText("DURCHSUCHEN");
                }

            }
        });

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.add(browse, 0, 0);
        gridPane.add(comboBox, 1, 0);
        gridPane.add(button,0,1);
        gridPane.add(quality, 0, 2);
        gridPane.add(slider, 1, 2);
        gridPane.add(label, 0, 3);

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String info = runDemo(selectedImage[0].getName(), (int)slider.getValue());
                label.setText(info);

            }
        });

        Scene scene = new Scene(gridPane, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public String runDemo(String filename, int quality) {
        long start = 0;
        long finish = 0;
        // Load the source image
        Image src = Load.invoke(filename);
        start = System.nanoTime();
        ImageCorrector imageCorrector = new ImageCorrector(src);
        src = imageCorrector.runCorrector();

        // Split the image into blocks
        ImageBlock imageBlock = new ImageBlock(8, src);
        Image[] blocks = imageBlock.getBlocks();

        // Each block has 3 different colour channels. So for every block
        // there must be 3 double[][] for storing dct results
        ImageReconstructor imageReconstructor = new ImageReconstructor(src.getXDim(), src.getYDim());
        long compressedImageSize = 0;
        for(int i = 0 ; i < imageBlock.numberOBlocks; ++i) {
            // apply dct to block[i] 3 times, for every colour channel
            DCT2D dct2D = new DCT2D(blocks[i]);

            //*******************//
            // ENCODING PROCESS  //
            //*******************//

            // dct2d function will take one parameter for colour channel
            double[][] out0 = dct2D.dct2d(0);
            double[][] out1 = dct2D.dct2d(1);
            double[][] out2 = dct2D.dct2d(2);

            // now we have blocks that have taken dct of
            // next step is quantization
            // out0 out1 and out2 must be quantized
            Quantization quantization0 = new Quantization(out0);
            int[][] quantized0 = quantization0.quantize();
            Quantization quantization1 = new Quantization(out1);
            int[][] quantized1 = quantization1.quantize();
            Quantization quantization2 = new Quantization(out2);
            int[][] quantized2 = quantization2.quantize();

            // now zigzag
            ZigzagTraverse zigzagTraverse0 = new ZigzagTraverse(quantized0);
            int[] zigzag0 = zigzagTraverse0.zigzag();
            ZigzagTraverse zigzagTraverse1 = new ZigzagTraverse(quantized1);
            int[] zigzag1 = zigzagTraverse1.zigzag();
            ZigzagTraverse zigzagTraverse2 = new ZigzagTraverse(quantized2);
            int[] zigzag2 = zigzagTraverse2.zigzag();

            // Huffman part
            IbrahimsHuffman ibrahimsHuffman0 = new IbrahimsHuffman(zigzag0, quality);
            String ibrResult0 = ibrahimsHuffman0.runEncode();
            IbrahimsHuffman ibrahimsHuffman1 = new IbrahimsHuffman(zigzag1, quality);
            String ibrResult1 = ibrahimsHuffman1.runEncode();
            IbrahimsHuffman ibrahimsHuffman2 = new IbrahimsHuffman(zigzag2, quality);
            String ibrResult2 = ibrahimsHuffman2.runEncode();
            compressedImageSize += ibrResult0.length() + ibrResult1.length() + ibrResult2.length();

            //*******************//
            // DECODING PROCESS  //
            //*******************//

            // Huffman decode
            int[] ibrResultDecoded0 = ibrahimsHuffman0.runDecode(ibrResult0);
            int[] ibrResultDecoded1 = ibrahimsHuffman1.runDecode(ibrResult1);
            int[] ibrResultDecoded2 = ibrahimsHuffman2.runDecode(ibrResult2);

            // Zigzag to 2d array
            int[][] quantizedDecoded0 = zigzagTraverse0.inverseZigzag(ibrResultDecoded0);
            int[][] quantizedDecoded1 = zigzagTraverse1.inverseZigzag(ibrResultDecoded1);
            int[][] quantizedDecoded2 = zigzagTraverse2.inverseZigzag(ibrResultDecoded2);

            // De-quantization
            double[][] deQuantized0 = quantization0.deQuantize(quantizedDecoded0);
            double[][] deQuantized1 = quantization1.deQuantize(quantizedDecoded1);
            double[][] deQuantized2 = quantization2.deQuantize(quantizedDecoded2);

            // Inverse discrete cosine transform
            Image tmpBlock0 = dct2D.inverseDct(deQuantized0);
            Image tmpBlock1 = dct2D.inverseDct(deQuantized1);
            Image tmpBlock2 = dct2D.inverseDct(deQuantized2);

            // Decode the jpeg block by block
            imageReconstructor.reConstruct(i, tmpBlock0, 0);
            imageReconstructor.reConstruct(i, tmpBlock1, 1);
            imageReconstructor.reConstruct(i, tmpBlock2, 2);

        }
        System.out.println("Compression ratio: " + (imageBlock.numberOBlocks * 8 * 3 * 64) / (double)compressedImageSize);
        Image result = imageReconstructor.getResult();
        result = imageCorrector.runPostResizer(result);
        finish = System.nanoTime();
        double error[] = calculateMeanSquareError(Load.invoke(filename), result);
        String info = "Compression ratio is : " + (imageBlock.numberOBlocks * 8 * 3 * 64) / (double)compressedImageSize
                + System.lineSeparator() + "Completed in " + (finish - start) / (1000000000) + " seconds" + System.lineSeparator()
                + "Mean square error for RED is " + error[0] + System.lineSeparator()
                + "Mean square error for GREEN is " + error[1] + System.lineSeparator()
                + "Mean square error for BLUE is " + error[2] ;
        Display2D.invoke(result);
        Save.invoke(result, "decodedImage.png");
        if(MyDebugger.isEnabled()) {
            System.out.println("Finished");
        }
        return info;
    }

    public static double[] calculateMeanSquareError(Image src, Image result) {
        double[] error = new double[3];
        System.out.println("Original size " + src.getXDim() + "x" + src.getYDim());
        System.out.println("Result size  " + result.getXDim() + "x" + result.getYDim());
        for(int i = 2 ; i < src.getXDim() - 2 ; ++i) {
            for(int j = 2; j < src.getYDim() - 2; ++j) {
                for(int c = 0; c < 3; ++c) {
                    error[c] += Math.pow((src.getXYCByte(i, j, c) - result.getXYCByte(i, j, c)), 2);
                }
            }
        }
        error[0] = error[0] / (src.getXDim() * src.getYDim());
        error[1] = error[1] / (src.getXDim() * src.getYDim());
        error[2] = error[2] / (src.getXDim() * src.getYDim());
        return error;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
