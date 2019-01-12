package sample;

import vpt.ByteImage;
import vpt.DoubleImage;
import vpt.Image;
import vpt.algorithms.display.Display2D;
import vpt.algorithms.io.Load;
import vpt.algorithms.io.Save;
import vpt.util.Tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ConvolutionTheoryStrategy implements Part1Strategy {
    private Image image;
    private Image borderedImg;
    private Image kernelImage;
    private Image filter;
    private DoubleImage realPartKernel;
    private DoubleImage imaginaryPartKernel;
    private DoubleImage realPart;
    private DoubleImage imaginaryPart;
    private int thickness;
    private int[][] kernel;
    private int[][] zeroPadKernel;

    public ConvolutionTheoryStrategy(File imageFile, File kernelFile) {

        try{
            ReadKernelFromText k1 = new ReadKernelFromText(kernelFile.toString());
            kernel = k1.getKernel();
            thickness = kernel.length / 2;
            thickness *= 2;
            Convolve c = new Convolve();
            c.setStrategy(new ConvolutionStrategy(imageFile, kernelFile));
            image = Load.invoke(imageFile.toString());
            zeroPadKernel = new int[image.getXDim() + thickness][image.getXDim() + thickness];
            kernelImage = new ByteImage(zeroPadKernel.length, zeroPadKernel[0].length);
            filter = c.convolveImage();
        } catch(IOException ex) {
            System.out.println("Error in reading kernel from file");
        }

        if(MyDebugger.isEnabled()){
            System.out.println("Image from " + imageFile.toString() + " is loading.");
            System.out.println("Kernel from " + kernelFile.toString() + " is loading.");
        }
    }

    @Override
    public Image run() {
        Image bordered = addBorders(image);
        zeroPad();
        dftKernel();
        dft(bordered);
        multiplication();
        return null;
    }

    private void multiplication() {
        //multiplication in frequency domain
        DoubleImage real = new DoubleImage(borderedImg.getXDim(), borderedImg.getYDim());
        DoubleImage imaginary = new DoubleImage(borderedImg.getXDim(), borderedImg.getYDim());
        for(int i = 0; i < borderedImg.getXDim(); ++i) {
            for(int j = 0 ; j < borderedImg.getYDim(); ++j) {

                double real_part = realPart.getXYDouble(i, j) * realPartKernel.getXYDouble(i, j)
                        - imaginaryPart.getXYDouble(i, j) * imaginaryPartKernel.getXYDouble(i ,j);
                double img_part = realPart.getXYDouble(i, j) * imaginaryPartKernel.getXYDouble(i, j)
                        + imaginaryPart.getXYDouble(i, j) * realPartKernel.getXYDouble(i ,j);
                real.setXYDouble(i, j, real_part);
                imaginary.setXYDouble(i, j, img_part);

            }
        }
        inverseDft(borderedImg, realPart, imaginaryPart);
    }

    private Image addBorders(Image image) {
        int thickness = kernel.length / 2;
        int size = image.getXDim();
        borderedImg = new ByteImage(size + 2*thickness, size + 2*thickness);
        for(int i = 0; i < borderedImg.getXDim(); ++i){
            for(int j = 0; j < borderedImg.getYDim(); ++j){
                if(i < thickness || i >= borderedImg.getXDim() - thickness
                        || j < thickness || j >= borderedImg.getYDim() - thickness){
                    borderedImg.setXYByte(i, j, 127);
                } else{
                    borderedImg.setXYByte(i, j, filter.getXYByte(i - thickness, j - thickness));
                }
            }
        }
        return borderedImg;
    }

    private void zeroPad() {

        for(int i = 0; i < kernel.length; ++i)
            for(int j = 0; j < kernel.length; ++j)
                zeroPadKernel[i][j] = kernel[i][j];
        for(int i = 0; i < zeroPadKernel.length; ++i){
            for(int j = 0; j < zeroPadKernel[0].length; ++j)
                kernelImage.setXYByte(i, j, zeroPadKernel[i][j]);
        }
//        ArrayList<Integer> tmp = new ArrayList<Integer>();
//        for(int i = 0; i < kernel.length; ++i)
//            for(int j = 0; j < kernel.length; ++j){
//                tmp.add(kernel[i][j]);
//                //zeroPadKernel[i][j] = kernel[i][j];
//            }
//
//
//        if(MyDebugger.isEnabled()){
//            System.out.println(tmp.toString());
//        }
//
//        for(int i = 0; i < zeroPadKernel.length; ++i){
//            for(int j = 0; j < zeroPadKernel[0].length; ++j){
//                if(!tmp.isEmpty()){
//                    if(MyDebugger.isEnabled()){
//                        System.out.println(i + ", " + j + " is set to" + tmp.get(0));
//                    }
//                    kernelImage.setXYByte(i, j, tmp.remove(0));
//                } else{
//                    kernelImage.setXYByte(i, j, 0);
//                }
//
//
//                //kernelImage.setXYByte(i, j, zeroPadKernel[i][j]);
//            }
//        }


//        for(int i = 0; i < kernelImage.getXDim(); ++i){
//            for(int j = 0 ; j < kernelImage.getYDim(); ++j){
//                System.out.print(kernelImage.getXYByte(i, j) + "    ");
//            }
//            System.out.println();
//        }
    }

    private void dft(Image sourceImage) {
        // first i need to create 2 double images for storing realPart and imaginaryPart parts
        realPart = new DoubleImage(sourceImage.getXDim(), sourceImage.getYDim());
        imaginaryPart = new DoubleImage(sourceImage.getXDim(), sourceImage.getYDim());
        DoubleImage mag = new DoubleImage(sourceImage.getXDim(), sourceImage.getYDim());

        int N = sourceImage.getXDim() * sourceImage.getYDim();
        double mul_fac = 1 / Math.sqrt(N);

        double[] real_img = new double[2];
        double PI = 3.14159;

        for(int i =  0; i < sourceImage.getXDim(); ++i) {
            for(int j = 0; j < sourceImage.getYDim(); ++j) {
                for(int x = 0; x < sourceImage.getXDim(); ++x) {
                    for(int y = 0; y < sourceImage.getYDim(); ++y) {
                        double a = (double)x * i / sourceImage.getXDim();
                        double b =  (double)y * j / sourceImage.getYDim();
                        real_img[0] += sourceImage.getXYDouble(x, y) * Math.cos((a + b) * -2 * PI);
                        real_img[1] += sourceImage.getXYDouble(x, y) * Math.sin((a + b) * -2 * PI);
                    }
                }
                realPart.setXYDouble(i, j, real_img[0] * mul_fac);
                imaginaryPart.setXYDouble(i, j, real_img[1] * mul_fac);
                mag.setXYDouble(i, j, Math.sqrt(Math.pow(real_img[0] * mul_fac, 2) + Math.pow(real_img[1] * mul_fac, 2)));
                real_img[0] = 0.0;
                real_img[1] = 0.0;
            }
        }
        mag = (DoubleImage) Tools.shiftOrigin(mag);
        if(MyDebugger.isEnabled()) {
            Display2D.invoke(realPart, "realPart");
            Display2D.invoke(imaginaryPart,  "sourceImage");
            Display2D.invoke(mag, "mag");
        }
    }

    private void inverseDft(Image sourceImage, DoubleImage inverseRealPart, DoubleImage inverseImaginaryPart) {
        int N = sourceImage.getXDim() * sourceImage.getYDim();
        double mul_fac = 1 / Math.sqrt(N);

        double[] real_img = new double[2];
        double PI = 3.14159;

        DoubleImage result = new DoubleImage(sourceImage.getXDim(), sourceImage.getYDim());

        for(int i =  0; i < sourceImage.getXDim(); ++i) {
            for(int j = 0; j < sourceImage.getYDim(); ++j) {
                for(int x = 0; x < sourceImage.getXDim(); ++x) {
                    for(int y = 0; y < sourceImage.getYDim(); ++y) {
                        double a = (double)x * i / sourceImage.getXDim();
                        double b =  (double)y * j / sourceImage.getYDim();
                        real_img[0] += inverseRealPart.getXYDouble(x, y) * Math.cos((a+b) * PI * 2)
                                - inverseImaginaryPart.getXYDouble(x, y) * Math.sin((a+b) * PI * 2);
                        real_img[1] += inverseImaginaryPart.getXYDouble(x, y) * Math.cos((a+b) * PI * 2)
                                + inverseRealPart.getXYDouble(x, y) * Math.sin((a+b) * PI * 2);
                    }
                }
                result.setXYDouble(i, j, Math.sqrt(Math.pow(real_img[0] * mul_fac, 2) + Math.pow(real_img[1] * mul_fac, 2)));
                real_img[0] = 0.0;
                real_img[1] = 0.0;
            }
        }
        removeBorders(result);
    }

    private void removeBorders(DoubleImage result) {
        DoubleImage res = new DoubleImage(result.getXDim() - thickness, result.getYDim() - thickness);
        for(int i = 0; i < res.getXDim(); ++i)
            for(int j = 0; j < res.getYDim(); ++j)
                res.setXYDouble(i, j, result.getXYDouble(i+thickness, j+thickness));

        // here we obtain the same image so convolution theorem is in fact working
        Display2D.invoke(res, "res");
    }

    private void dftKernel() {
        // first i need to create 2 double images for storing realPart and imaginaryPart parts
        realPartKernel = new DoubleImage(kernelImage.getXDim(), kernelImage.getYDim());
        imaginaryPartKernel = new DoubleImage(kernelImage.getXDim(), kernelImage.getYDim());
        DoubleImage magKernel = new DoubleImage(kernelImage.getXDim(), kernelImage.getYDim());

        int N = kernelImage.getXDim() * kernelImage.getYDim();
        double mul_fac = 1 / Math.sqrt(N);

        double[] real_img = new double[2];
        double PI = 3.14159;

        for(int i =  0; i < kernelImage.getXDim(); ++i) {
            for(int j = 0; j < kernelImage.getYDim(); ++j) {
                for(int x = 0; x < kernelImage.getXDim(); ++x) {
                    for(int y = 0; y < kernelImage.getYDim(); ++y) {
                        double a = (double)x * i / kernelImage.getXDim();
                        double b =  (double)y * j / kernelImage.getYDim();
                        real_img[0] += kernelImage.getXYDouble(x, y) * Math.cos((a + b) * -2 * PI);
                        real_img[1] += kernelImage.getXYDouble(x, y) * Math.sin((a + b) * -2 * PI);
                    }
                }
                realPartKernel.setXYDouble(i, j, real_img[0] * mul_fac);
                imaginaryPartKernel.setXYDouble(i, j, real_img[1] * mul_fac);
                magKernel.setXYDouble(i, j, Math.sqrt(Math.pow(real_img[0] * mul_fac, 2) + Math.pow(real_img[1] * mul_fac, 2)));
                real_img[0] = 0.0;
                real_img[1] = 0.0;
            }
        }
        magKernel = (DoubleImage) Tools.shiftOrigin(magKernel);
        if(MyDebugger.isEnabled()) {
            Display2D.invoke(realPartKernel, "realPart");
            Display2D.invoke(imaginaryPartKernel,  "sourceImage");
            Display2D.invoke(magKernel, "mag");
        }
    }
}
