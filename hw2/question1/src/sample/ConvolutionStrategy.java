package sample;

import vpt.ByteImage;
import vpt.Image;
import vpt.algorithms.display.Display2D;
import vpt.algorithms.io.Load;
import vpt.algorithms.io.Save;

import java.io.File;
import java.io.IOException;

public class ConvolutionStrategy implements Part1Strategy{
    private Image image;
    private int[][] kernel;

    public ConvolutionStrategy(File imageFile, File kernelFile) {

        image = Load.invoke(imageFile.toString());
        try{
            ReadKernelFromText k1 = new ReadKernelFromText(kernelFile.toString());
            kernel = k1.getKernel();
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
        Image result = new ByteImage(image.getXDim(), image.getYDim());
        if(MyDebugger.isEnabled()) {
            Display2D.invoke(image, "Original");
            System.out.println("Convolution Strategy is run");
        }

        if(kernel.length != kernel[0].length || kernel.length % 2 != 1){
            System.out.println("Invalid kernel matrix is given.Its size must be NxN where n is an odd number");
            System.out.println(kernel.length + " x " + kernel[0].length);
            return result;
        }

        int thickness = kernel.length / 2;
        Image borderedImg = new ByteImage(image.getXDim() + 2*thickness, image.getYDim() + 2*thickness);
        for(int i = 0; i < borderedImg.getXDim(); ++i){
            for(int j = 0; j < borderedImg.getYDim(); ++j){
                if(i < thickness || i >= borderedImg.getXDim() - thickness
                        || j < thickness || j >= borderedImg.getYDim() - thickness){
                    borderedImg.setXYByte(i, j, 127);
                } else{
                    borderedImg.setXYByte(i, j, image.getXYByte(i - thickness, j - thickness));
                }
            }
        }
        int sum = 0;
        for(int i = 0; i < kernel.length; ++i)
            for(int j = 0; j < kernel[0].length; ++j)
                sum += kernel[i][j];

        if(MyDebugger.isEnabled()){
            System.out.println("Sum is " + sum);
            System.out.println("Thickness is " + thickness);
            System.out.println(borderedImg.getXDim() + "x" + borderedImg.getYDim());
            //testWithMatrix(borderedImg);
        }
        for(int i = thickness; i < borderedImg.getXDim() - thickness; ++i){
            for(int j = thickness; j < borderedImg.getYDim() - thickness; ++j){
                int newVal = 0;
                for(int k = -thickness; k <= thickness; ++k){
                    for(int p = -thickness; p <= thickness; ++p){
                        try{
                            //System.out.print(borderedImg.getXYByte(i+k, j+p) + "*" + findSymmetric(k+thickness, p+thickness));
                            newVal += borderedImg.getXYByte(i + k, j + p) * findSymmetric(k + thickness, p + thickness);
                            //System.out.println("new val=" + newVal);
                        } catch (IndexOutOfBoundsException ex){
                            System.out.println(i + "," + j + "->" + (i+k) + "," +(j+p));
                        }
                    }
                }

                if(sum == 0){
                    //System.out.println("sum == 0");
                    if(newVal > 0)
                        newVal = 255;
                    else
                        newVal = 0;
                    result.setXYByte(i-thickness, j-thickness, newVal);
                }else{
                    //System.out.println("sum != 0");
                    newVal /= sum;
                    if(newVal > 255)
                        newVal = 255;
                    else if(newVal < 0)
                        newVal = 0;
                    result.setXYByte(i-thickness, j-thickness, newVal);
                }

            }
        }
        if (MyDebugger.isEnabled()) {
            System.out.println("Result is ready");

            for(int i = 0; i < 5; ++i) {
                for(int j = 0; j < 5; ++j) {
                    System.out.print(borderedImg.getXYByte(i , j) + " ");
                }
                System.out.println();
            }
            System.out.println();
            System.out.println();
            for(int i = 0; i < 5; ++i) {
                for(int j = 0; j < 5; ++j) {
                    System.out.print(result.getXYByte(i , j) + " ");
                }
                System.out.println();
            }

        }
        return result;
    }

    private int findSymmetric(int i, int j) {

        int originX = kernel.length / 2;
        int originY = kernel.length / 2;

        int newX = originX - i;
        int newY = originY - j;

        newX = originX + newX;
        newY = originY + newY;

        int result = 0;
        try{
            result = kernel[newX][newY];
        } catch(IndexOutOfBoundsException ex) {
            System.out.println(i + ", " + j + "->" + newX + ", " + newY);
        }

        return result;
    }


}
