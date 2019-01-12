package homework_classes_131044011;

import vpt.ByteImage;
import vpt.Image;
import vpt.algorithms.display.Display2D;
import vpt.algorithms.io.Load;

public class IbrahimYaziciMain {

	public static void main(String[] args) {
		
		// Test for part 1
		Image rotate = Load.invoke("valve.png");
		double sx = 2, sy = 2, theta = 1.57;
		Image ourRotate = scale(rotate, sx, sy, theta);
		
		// Test for part 2
		Image histogram = Load.invoke("valve.png");
		Image ourHistogram = equalize(histogram);

		// Test for part 3
		Image mm = Load.invoke("abdomen.png");
		int factor = 11;
		Image ed = erosionDilation(mm, factor);
	}
	
	public static Image erosionDilation (Image f, int factor) {
		MathematicalMorphology part3 = new MathematicalMorphology(f, factor);
		Image out = part3.test();
		return out;
	}
	
	public static Image equalize(Image f) {
		HistogramEqualization part2 = new HistogramEqualization(f, true);
		Image out = part2.test();
		return out;
	}
	
	public static Image scale(Image f, double sx, double sy, double theta) {
		RotateInterpolation part1 = new RotateInterpolation(f, sx, sy, theta);
		Image out = part1.test();
		return out;
	}
}


