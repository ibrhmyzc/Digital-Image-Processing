package homework_classes_131044011;

import java.util.ArrayList;

import vpt.ByteImage;
import vpt.Image;
import vpt.algorithms.display.Display2D;
import vpt.algorithms.io.Load;
import vpt.algorithms.io.Save;

public class MathematicalMorphology {
	private Image f;
	private int factor;
	private Image ccImg;
	public MathematicalMorphology(Image f, int factor) {
		this.f = f;
		this.factor = factor;
		
		if(factor < 7)
			factor = 7;
		if(factor > 15)
			factor = 15;
		if(factor % 2 == 0)
			factor += 1;
	}
	
	public Image test() {
		Display2D.invoke(f, "default");
		erosion();
		dilation();
		return f;
	}
	
	private void erosion() {	
		int c = factor / 2;
		Image windowed = new ByteImage(f.getXDim() + factor - 1, f.getYDim() + factor - 1, 1);
		
		for(int i = 0; i < windowed.getXDim(); ++i) {
			for(int j = 0; j < windowed.getYDim(); ++j) {
				if(i < c || i == (f.getXDim() * 2 - 1) || j < c || j == (f.getYDim() * 2 - 1))
					windowed.setXYByte(i, j, 0);
			}
		}
		
		
		for(int i = 0; i < f.getXDim(); ++i) {
			for(int j = 0; j < f.getYDim(); ++j) {
				windowed.setXYByte(i+c, j+c, f.getXYByte(i, j));
			}
		}
		
		Image out = f;

		
		for(int i = c; i < windowed.getXDim() -c -1; ++i) {
			for(int j = c; j < windowed.getYDim() -c - 1; ++j) {
				for(int k = 0; k < c; ++k) {
					if(windowed.getXYByte(i-k, j+k) == 0 || windowed.getXYByte(i, j+k) == 0 || windowed.getXYByte(i+k, j+k) == 0 ||
							windowed.getXYByte(i-k, j) == 0 									|| windowed.getXYByte(i+k, j) == 0 ||
							windowed.getXYByte(i-k, j-k) == 0 || windowed.getXYByte(i, j-k) == 0 || windowed.getXYByte(i+k, j-k) == 0) {
						try{
							out.setXYByte(i-c, j-c, 0);
						} catch (IndexOutOfBoundsException ex) {
							System.out.println((i-1) + " " + (j-1));
						}
					}
				}
			}
		}

		Display2D.invoke(out, "erosion");
		//Save.invoke(out, "erosion.png");

	}
	
	private void dilation() {	
		int c = factor / 2;
		Image windowed = new ByteImage(f.getXDim() + factor - 1, f.getYDim() + factor - 1, 1);
		
		
		for(int i = 0; i < windowed.getXDim(); ++i) {
			for(int j = 0; j < windowed.getYDim(); ++j) {
				if(i < c || i == (f.getXDim() * 2 - 1) || j < c || j == (f.getYDim() * 2 - 1))
					windowed.setXYByte(i, j, 0);
			}
		}
		
		
		for(int i = 0; i < f.getXDim(); ++i) {
			for(int j = 0; j < f.getYDim(); ++j) {
				windowed.setXYByte(i+c, j+c, f.getXYByte(i, j));
			}
		}
		
		Image out = f;

		
		for(int i = c; i < windowed.getXDim() -c -1; ++i) {
			for(int j = c; j < windowed.getYDim() -c - 1; ++j) {
				for(int k = 0; k < c; ++k) {
					if(windowed.getXYByte(i-k, j+k) == 255 || windowed.getXYByte(i, j+k) == 255 || windowed.getXYByte(i+k, j+k) == 255 ||
							windowed.getXYByte(i-k, j) == 255 									|| windowed.getXYByte(i+k, j) == 255 ||
							windowed.getXYByte(i-k, j-k) == 255 || windowed.getXYByte(i, j-k) == 255 || windowed.getXYByte(i+k, j-k) == 255) {
						try{
							out.setXYByte(i-c, j-c, 255);
						} catch (IndexOutOfBoundsException ex) {
							System.out.println((i-1) + " " + (j-1));
						}
					}
				}
			}
		}

		Display2D.invoke(out, "dilation");
		countConnectedCompanents(out);
		//Save.invoke(out, "dilation.png");

	}
	
	private void countConnectedCompanents(Image img) {
			
//		Display2D.invoke(img, "cc");
//		ccImg = img;
//		int count = component(ccImg);
//		System.out.println("Number of connected componenets is " + count);	
	}
	
	private int component(Image img) {
		
		int counter = 0;
		for(int i = 0; i < img.getXDim(); ++i) {
			for(int j = 0 ; j < img.getYDim(); ++j) {
				if(img.getXYByte(i, j) == 255 ) {
					int size = dfsComponent(img, i ,j);
					if(size != 0)
						++counter;
				}
			}
		}
		
		return counter;
	}
	
	private int dfsComponent(Image img, int i, int j) {
	    if (i < 0 || i >= img.getXDim() || j < 0 || j >= img.getYDim()) {
            return 0;
        }
	    if (img.getXYByte(i, j) == 0) {
            return 0;
        }
		int s = 1;
		img.setXYByte(i, j, 0);
		for (int r = i - 1; r <= i + 1; r++) {
	        for (int c = j - 1; c <= j + 1; c++) {
	            if (r!= i || c!= j) {
	                s += dfsComponent(img, r, c);
	            }
	        }
	    }
		return s;
	}
	
	
	
}
