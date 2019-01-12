package homework_classes_131044011;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import vpt.ByteImage;
import vpt.Image;
import vpt.algorithms.display.Display2D;
import vpt.algorithms.io.Load;
import vpt.algorithms.io.Save;


public class HistogramEqualization {
	// member variables
	private Image f;
	private int[] lookUpTable;
	private double[] probilities;
	private double[] cuf;
	private int[] cuFreq;
	private Map<Integer, Integer> myMap;
	private boolean adaptive;
	
	public HistogramEqualization(Image f, boolean adaptive) {
		
		this.f = f;
		this.adaptive = adaptive;
		
		lookUpTable = new int[256];
		Arrays.fill(lookUpTable, 0);
		
		probilities = new double[256];
		Arrays.fill(probilities, 0.0);
		
		cuf = new double[256];
		Arrays.fill(cuf, 0.0);
		
		cuFreq = new int[256];
		Arrays.fill(cuFreq, 0);
		
		myMap = new HashMap<Integer, Integer>();
		
	}
		
	
	public Image test() {
		fillLUTArray();
		findProb();
		findCumulative();
		findCuFreq();
		designTheMap();
		
		if(adaptive) {
			System.out.println("adoptive");
			adoptiveHistogram();
		}
		//Display2D.invoke(f);
		return createOutputimage();
	}
	
	
	private void fillLUTArray() {
		for(int i = 0; i < f.getXDim(); ++i)
			for(int j = 0; j < f.getYDim(); ++j)
				lookUpTable[f.getXYByte(i, j)] += 1;
		//System.out.println("i"+Arrays.toString(lookUpTable));
	}
	
	private void findProb() {
		int numberOfPixels = f.getXDim() * f.getYDim();
		for(int i = 0; i < lookUpTable.length; ++i) {
			double pixel_prob = (double)lookUpTable[i] / numberOfPixels;
			probilities[i] = pixel_prob;
		}
		//System.out.println("i"+Arrays.toString(probilities));
	}
	
	private void findCumulative() {
		cuf[0] = probilities[0];
		for(int i = 1; i < probilities.length; ++i) {
			double new_prob = cuf[i-1] + probilities[i];
			cuf[i] = new_prob;
		}
		//System.out.println("i"+Arrays.toString(cuf));
	}

	private void findCuFreq() {
		int size = f.getXDim() * f.getYDim();
		int ideal_freq = (int) Math.floor(size / 256.0);
		for(int i = 0; i < cuf.length; ++i)
			cuFreq[i] =  ideal_freq * (i+1);
		//System.out.println("i"+Arrays.toString(cuFreq));
	}
	
	private void designTheMap() {
		boolean[] isUsed = new boolean[256];
		Arrays.fill(isUsed, false);
		for(int i = 0; i < cuf.length; ++i) {
			double current_pixel_intensity = cuf[i];
			if(!isUsed[i]) {
				myMap.put(i, (int)Math.round(current_pixel_intensity * 255));
				isUsed[i] = true;
			}
			
		}
	}
	
	private Image createOutputimage() {
		Image out = new ByteImage(f.getXDim(), f.getYDim());
		for(int i= 0; i < out.getXDim(); ++i)
			for(int j = 0 ; j < out.getYDim(); ++j)
				out.setXYByte(i, j, myMap.get(f.getXYByte(i, j)));
		//Save.invoke(out, "histogramImg.png");
		//Display2D.invoke(out);
		return out;
	}
	
	private Image adoptiveHistogram() {
		int factor = 2; //2x2
			
		HistogramEqualization[] myHistogram = new HistogramEqualization[factor*factor];
		Image[] inp = new ByteImage[factor*factor];
		Image[] outp = new ByteImage[factor*factor];
		
		int small_width = f.getXDim() / factor;
		int small_height = f.getYDim() / factor;
		
		for(int i = 0; i < factor*factor; ++i) {
			inp[i] = new ByteImage(small_width, small_height);
			
			for(int k = 0; k < small_width; ++k) {
				for(int j = 0; j < small_height; ++j) {
					if(i == 0) {
						inp[i].setXYByte(k, j, f.getXYByte(k, j));
					} else if (i == 1) {
						inp[i].setXYByte(k, j, f.getXYByte(k + small_width, j));
					} else if (i == 2) {
						inp[i].setXYByte(k, j, f.getXYByte(k, j + small_height));
					} else {
						inp[i].setXYByte(k, j, f.getXYByte(k + small_width, j + small_height));
					}
				}
			}
			
			outp[i] = new ByteImage(small_width, small_height);
			myHistogram[i] = new HistogramEqualization(inp[i], false);
			outp[i] = myHistogram[i].test();
		}
		
		Image result = new ByteImage(f.getXDim(), f.getYDim());
		
		for(int i = 0; i < factor*factor; ++i)
			for(int k = 0; k < small_width; ++k) {
				for(int j = 0; j < small_height; ++j) {
					if(i == 0) {
						result.setXYByte(k, j, outp[i].getXYByte(k, j));
					} else if (i == 1) {
						result.setXYByte(k + small_width, j, outp[i].getXYByte(k, j));
					} else if (i == 2) {
						result.setXYByte(k, j + small_height, outp[i].getXYByte(k, j));
					} else {
						result.setXYByte(k + small_width, j + small_height, outp[i].getXYByte(k, j));
					}
				}
			}
		Display2D.invoke(result, "result");	
		//Save.invoke(result, "adaptiveHistogram.png");
		return result;
	}
	
}
