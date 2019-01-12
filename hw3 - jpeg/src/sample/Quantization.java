package sample;

public class Quantization {
    private int[] quantizationMatrix;
    private double[][] srcMatrix;
    public Quantization(double[][] srcMatrix) {
        quantizationMatrix = new int[64];
        this.srcMatrix = srcMatrix;

        quantizationMatrix[0]=16;
        quantizationMatrix[1]=11;
        quantizationMatrix[2]=10;
        quantizationMatrix[3]=16;
        quantizationMatrix[4]=24;
        quantizationMatrix[5]=40;
        quantizationMatrix[6]=51;
        quantizationMatrix[7]=61;
        quantizationMatrix[8]=12;
        quantizationMatrix[9]=12;
        quantizationMatrix[10]=14;
        quantizationMatrix[11]=19;
        quantizationMatrix[12]=26;
        quantizationMatrix[13]=58;
        quantizationMatrix[14]=60;
        quantizationMatrix[15]=55;
        quantizationMatrix[16]=14;
        quantizationMatrix[17]=13;
        quantizationMatrix[18]=16;
        quantizationMatrix[19]=24;
        quantizationMatrix[20]=40;
        quantizationMatrix[21]=57;
        quantizationMatrix[22]=69;
        quantizationMatrix[23]=56;
        quantizationMatrix[24]=14;
        quantizationMatrix[25]=17;
        quantizationMatrix[26]=22;
        quantizationMatrix[27]=29;
        quantizationMatrix[28]=51;
        quantizationMatrix[29]=87;
        quantizationMatrix[30]=80;
        quantizationMatrix[31]=62;
        quantizationMatrix[32]=18;
        quantizationMatrix[33]=22;
        quantizationMatrix[34]=37;
        quantizationMatrix[35]=56;
        quantizationMatrix[36]=68;
        quantizationMatrix[37]=109;
        quantizationMatrix[38]=103;
        quantizationMatrix[39]=77;
        quantizationMatrix[40]=24;
        quantizationMatrix[41]=35;
        quantizationMatrix[42]=55;
        quantizationMatrix[43]=64;
        quantizationMatrix[44]=81;
        quantizationMatrix[45]=104;
        quantizationMatrix[46]=113;
        quantizationMatrix[47]=92;
        quantizationMatrix[48]=49;
        quantizationMatrix[49]=64;
        quantizationMatrix[50]=78;
        quantizationMatrix[51]=87;
        quantizationMatrix[52]=103;
        quantizationMatrix[53]=121;
        quantizationMatrix[54]=120;
        quantizationMatrix[55]=101;
        quantizationMatrix[56]=72;
        quantizationMatrix[57]=92;
        quantizationMatrix[58]=95;
        quantizationMatrix[59]=98;
        quantizationMatrix[60]=112;
        quantizationMatrix[61]=100;
        quantizationMatrix[62]=103;
        quantizationMatrix[63]=99;

    }

    public int[][] quantize() {
        int[][] result = new int[8][8];
        int counter = 0;

        for(int i = 0; i < 8; ++i){
            for(int j = 0; j < 8; ++j) {
                result[i][j] = (int) Math.rint(srcMatrix[i][j] / quantizationMatrix[counter]);
                counter++;
            }
        }
        return result;
    }

    public double[][] deQuantize(int[][] newQuantize) {
        double[][] result = new double[8][8];
        int counter = 0;
        for(int i = 0; i < 8; ++i) {
            for(int j = 0; j < 8; ++j) {
                result[i][j] =  Math.rint(newQuantize[i][j] * quantizationMatrix[counter]);
                counter++;
            }
        }

        return result;
    }

}
