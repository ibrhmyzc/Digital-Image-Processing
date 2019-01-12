package sample;

import vpt.ByteImage;
import vpt.Image;

public class ImageCorrector {
    private Image src;
    private Image result;

    public ImageCorrector(Image src) {
        this.src = src;
    }

    public Image runCorrector() {
        int tmpX = 0;
        int tmpY = 0;
        if(src.getXDim() % 8 != 0) {
            tmpX = src.getXDim() / 8;
            tmpX = (tmpX + 1) * 8;
        } else {
            tmpX = src.getXDim();
        }

        if(src.getYDim() % 8 != 0) {
            tmpY = src.getYDim() / 8;
            tmpY = (tmpY + 1) * 8;
        } else {
            tmpY = src.getYDim();
        }

        if(tmpX > tmpY) {
            tmpY = tmpX;
        } else {
            tmpX = tmpY;
        }

//        System.out.println("Original size " + src.getXDim() + "x" + src.getYDim());
//        System.out.println("Resized to " + tmpX + "x" + tmpY);

        Image result = new ByteImage(tmpX, tmpY, 3);

        for(int i = 0; i < src.getXDim(); ++i) {
            for(int j = 0; j < src.getYDim(); ++j) {
                for(int c = 0; c < 3; ++c) {
                    result.setXYCByte(i, j, c, src.getXYCByte(i, j, c));
                }
            }
        }
        return result;
    }

    public Image runPostResizer(Image img) {
        Image result = new ByteImage(src.getXDim(), src.getYDim(), 3);
        for(int i = 0; i < result.getXDim(); ++i) {
            for(int j = 0; j < result.getYDim(); ++j) {
                for(int c = 0; c < 3; ++c) {
                    result.setXYCByte(i, j, c, img.getXYCByte(i, j, c));
                }
            }
        }
        return result;
    }

    public Image getResult() {
        return result;
    }
}
