package sample;

import vpt.ByteImage;
import vpt.Image;

public class DCT2D {
    private Image block;
    private int xDim;
    private int yDim;
    private double PI = 3.142857;

    public DCT2D(Image block) {
        this.block = block;
        this.xDim = block.getXDim();
        this.yDim = block.getYDim();
    }

    public double[][] dct2d(int colour) {
        double[][] result = new double[xDim][yDim];
        double tmpSum = 0.0;
        double p1 = 0.0;
        double p2 = 0.0;
        for(int i = 0; i < xDim; ++i) {
            for(int j = 0; j < yDim; ++j) {
                tmpSum = 0.0;
                for(int k1 = 0; k1 < xDim; ++k1) {
                    for(int k2 = 0; k2 < yDim; ++k2) {
                        p1 = Math.cos((2 * k1 + 1) * i * PI / (2 * xDim));
                        p2 =  Math.cos((2 * k2 + 1) * j * PI / (2 * yDim));
                        try{
                            double localResult = block.getXYCByte(k1, k2, colour) * p1 * p2;
                            tmpSum += localResult;
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println(k1 + "," + k2);
                        }

;                    }
                }
                double resultVal = 0.0;
                if(i == 0 && j == 0) {
                    resultVal = tmpSum * (1 / Math.sqrt(xDim)) * (1 / Math.sqrt(yDim));
                } else if(i == 0){
                    resultVal = tmpSum * (1 / Math.sqrt(xDim)) * (Math.sqrt(2) / Math.sqrt(yDim));
                } else if(j == 0){
                    resultVal = tmpSum * (1 / Math.sqrt(yDim)) * (Math.sqrt(2) / Math.sqrt(xDim));
                } else {
                    resultVal = tmpSum * (Math.sqrt(2) / Math.sqrt(xDim)) * (Math.sqrt(2) / Math.sqrt(yDim));
                }
                result[i][j] = resultVal;
            }
        }
        return result;
    }

    public Image inverseDct(double[][] newDct) {
        Image result = new ByteImage(xDim, yDim);
        double tmpSum = 0.0;
        double p1 = 0.0;
        double p2 = 0.0;
        double iDctVal = 0.0;
        for(int i = 0; i < xDim; ++i) {
            for (int j = 0; j < yDim; ++j) {
                for(int k1 = 0; k1 < 8; ++k1) {
                    for(int k2 = 0; k2 < 8; ++k2){

                        if(k1 == 0 && k2 == 0) {
                            iDctVal = (1 / Math.sqrt(xDim)) * (1 / Math.sqrt(yDim));
                        } else if(k1 == 0){
                            iDctVal = (1 / Math.sqrt(xDim)) * (Math.sqrt(2) / Math.sqrt(yDim));
                        } else if(k2 == 0){
                            iDctVal = (1 / Math.sqrt(yDim)) * (Math.sqrt(2) / Math.sqrt(xDim));
                        } else {
                            iDctVal = (Math.sqrt(2) / Math.sqrt(xDim)) * (Math.sqrt(2) / Math.sqrt(yDim));
                        }

                        p1 = Math.cos((2 * i + 1) * k1 * PI / (2 * xDim));
                        p2 =  Math.cos((2 * j + 1) * k2 * PI / (2 * yDim));
                        tmpSum += newDct[k1][k2] * p1 * p2 * iDctVal;

                    }
                }
                result.setXYByte(i, j, (int)Math.rint(tmpSum));
                tmpSum = 0.0;
            }
        }
        return result;
    }
}
