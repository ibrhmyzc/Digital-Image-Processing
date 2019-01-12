package sample;

import java.io.*;
import java.util.ArrayList;

public class ReadKernelFromText {
    private int[][] kernel;
    public ReadKernelFromText(String filename) throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(new File(filename)));
        ArrayList<Integer> numbers = new ArrayList<>();
        String line;
        while ((line = input.readLine()) != null){
            String[] tmp = line.split(" ");
            for(int i = 0; i < tmp.length; ++i){
                numbers.add(Integer.parseInt(tmp[i]));
            }
        }

        input.close();
        int size = (int) Math.sqrt(numbers.size());
        kernel = new int[size][size];

        for(int i = 0; i < kernel.length; ++i){
            for(int j = 0; j  < kernel[0].length; ++j) {
                kernel[i][j] = numbers.remove(0);
            }
        }

    }

    public int[][] getKernel() {
        return kernel;
    }
}
