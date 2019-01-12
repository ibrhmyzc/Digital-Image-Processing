package sample;

import vpt.ByteImage;
import vpt.Image;
import vpt.algorithms.display.Display2D;


public class ImageReconstructor {
    private int xDim;
    private int yDim;
    private Image result;
    public ImageReconstructor(int xDim, int yDim) {
        this.xDim = xDim;
        this.yDim = yDim;
        result = new ByteImage(xDim, yDim, 3);
    }

    public void reConstruct(int blockNumber, Image block, int colour) {
        int horizontalBlocks = xDim / 8;
        int verticalBlocks = yDim / 8;
        int tmpI = blockNumber / horizontalBlocks;
        int tmpJ = blockNumber % verticalBlocks;
        //System.out.print(colour + "->");
        for(int i = tmpI*8; i < tmpI*8+8; ++i) {
            for(int j = tmpJ*8; j < tmpJ*8+8; ++j) {
                result.setXYCByte(j, i, colour, block.getXYByte(j-tmpJ*8, i-tmpI*8));
                //System.out.print(block.getXYByte(i-tmpI*8, j-tmpJ*8) +", ");
            }
        }
        //System.out.println();
    }

    public Image getResult() {
        return result;
    }
}
