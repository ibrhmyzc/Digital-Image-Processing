package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import vpt.ByteImage;
import vpt.Image;
import vpt.algorithms.display.Display2D;
import vpt.algorithms.io.Save;


import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Ibrahim Yazici");

        // FileChooser
        FileChooser imageChooser = new FileChooser();
        imageChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        imageChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
        imageChooser.setTitle("Browse");

        FileChooser kernelChooser = new FileChooser();
        kernelChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        kernelChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("TXT", "*.txt")
        );
        imageChooser.setTitle("Browse");

        //Label
        Label imageLabel = new Label();
        imageLabel.setText("Choose an image");

        Label kernelLabel = new Label();
        kernelLabel.setText("Choose a kernel");

        final File[] files = new File[2];

        //Button
        Button imageButton = new Button();
        imageButton.setText("Browse Image");
        imageButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                files[0] = imageChooser.showOpenDialog(primaryStage);
            }
        });


        Button kernelButton = new Button();
        kernelButton.setText("Browse Kernel");
        kernelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                files[1] = kernelChooser.showOpenDialog(primaryStage);
            }
        });


        Button convolutionButton = new Button();
        convolutionButton.setText("Convolution ");
        convolutionButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                    Convolve c = new Convolve();
                    c.setStrategy(new ConvolutionStrategy(files[0], files[1]));
                    Display2D.invoke(c.convolveImage(), "c1");
            }
        });

        Button convolutionTheoremButton = new Button();
        convolutionTheoremButton.setText("Con Theorem");
        convolutionTheoremButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Convolve c = new Convolve();
                c.setStrategy(new ConvolutionTheoryStrategy(files[0], files[1]));
                c.convolveImage();
            }
        });

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(imageLabel, 0, 0);
        grid.add(imageButton, 1,0);
        grid.add(kernelLabel, 0, 1);
        grid.add(kernelButton, 1,1);
        grid.add(convolutionButton, 2, 2);
        grid.add(convolutionTheoremButton, 2, 3);

        Scene scene = new Scene(grid, 300, 175);

        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
