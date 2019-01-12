package homework_classes_131044011;

import vpt.ByteImage;
import vpt.Image;
import vpt.algorithms.display.Display2D;
import vpt.algorithms.io.Load;
import vpt.algorithms.io.Save;

public class RotateInterpolation {
	private Image f;
	private double sx;
	private double sy;
	private double theta;
	
	public RotateInterpolation(Image f, double sx, double sy, double theta) {
		this.f = f;
		this.sx = sx;
		this.sy = sy;
		this.theta = theta;
	}
	
	public Image test() {
		Image out = scaleBilinear();
		Display2D.invoke(out);
		rotate(out);
		return out;
	}
	
	private Image scaleBilinear() {
		
		int width = f.getXDim();
		int heigth = f.getYDim();
		
		// may not be able to scale them 
		// so double variables are casted to integer in order to
		// get a guaranteed scaling
		int xFactor = (int) sx;
		int yFactor = (int) sy;
		
		// creating our new output image in which we insert appropriate pixels
		Image scaledOutput = new ByteImage(width * xFactor, heigth * yFactor);
		
		// scale this vertically in order to achieve a full scaled
		// image before we can do the rotation on it
		for(int i = 0; i < width; ++i) {
			for(int j = 0; j < heigth; ++j) {
				for(int p = 0; p < yFactor; ++p) {
					int tmp = f.getXYByte(i, j);
					scaledOutput.setXYByte(i, j * yFactor + p, tmp);
				}
			}
		}
		
		// traverse every pixel in the image
		for(int i = 0; i < width; ++i) {
			for(int j = 0; j < heigth * yFactor; ++j) {
				
				int tmp = f.getXYByte(i,  j / yFactor);
				for(int k = 0 ; k < xFactor; ++k) {
					scaledOutput.setXYByte(i * xFactor + k, j, tmp);
				}
			}		
		}	
		
		// now we are ready to do rotate our new scaled image
		
		//Save.invoke(scaledOutput, "scaledImg.png");
		return scaledOutput;
	}
	
	private void rotate(Image p) {
			
		int degree = (int) (theta * 180 / 3.14);
		
		if(degree < 0)
			degree += 360;
		if(degree > 90 && degree != 180)
			degree %= 90;
		theta = degree * 3.14159 / 180;
		System.out.println("degree = " + degree);
		int width = p.getXDim();
		int height = p.getYDim();
		
		
		int tmp = (int) Math.round(Math.sqrt(width*width+height*height));
		//System.out.println(tmp);
		Image out = new ByteImage(tmp , tmp );
		
		int new_x = 0;
		int new_y = 0;
		//System.out.println(width +", " + height );
		for(int i = 0; i < width; ++i) {
			for(int j = 0; j < height ; ++j) {
				int a = 0,b = 0;
				try {					
					a = i - (height / 2);
					b = j - (width / 2);
//					new_x = (int) Math.round((a * Math.cos(theta) - b * Math.sin(theta))) + (height / 2);
//					new_y = (int) Math.round((a * Math.sin(theta) + b * Math.cos(theta))) + (width / 2);

					new_x =  (int) (a * Math.round(Math.cos(theta)) - b * Math.round(Math.sin(theta)) +(height / 2));
					new_y =  (int) (a * Math.round(Math.sin(theta)) + b * Math.round(Math.cos(theta)) +(height / 2));
					
					out.setXYByte(new_x, new_y, p.getXYByte(i, j));
				}catch(IndexOutOfBoundsException ex) {
					//System.out.println(i + ", " + j + " => " + new_x + "," + new_y + " => a and b " + a + ", " + b);
					break;
				}
			}
		}
		//Save.invoke(out, "rotatedImage.png");
		Display2D.invoke(out);
	}
}
