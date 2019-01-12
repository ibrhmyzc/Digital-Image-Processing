package sample;

import vpt.ByteImage;
import vpt.Image;

public class ImageBlock {
    private int BLOCK_SIZE;
    private Image[] blocks;
    private Image src;
    private Image reconstructedImage;
    int numberOBlocks;

    public ImageBlock(int BLOCK_SIZE, Image src) {
        this.BLOCK_SIZE = BLOCK_SIZE;
        this.src = src;
        numberOBlocks = (src.getXDim() / BLOCK_SIZE) * (src.getYDim() / BLOCK_SIZE);
        this.blocks = new Image[numberOBlocks];
        for(int i = 0; i < numberOBlocks; ++i) {
            this.blocks[i] = new ByteImage(BLOCK_SIZE, BLOCK_SIZE, 3);
        }
        splitIntoBlocks();
    }

    private void splitIntoBlocks() {
        int blockNumber = 0;
        int numberOfColourChannels = 3;
        System.out.println("Size " + src.getXDim() + "x" + src.getYDim());
        for(int j = 0 ; j < src.getXDim(); j += BLOCK_SIZE) {
            for(int i = 0; i < src.getYDim(); i += BLOCK_SIZE) {
                for(int x = 0; x < BLOCK_SIZE; ++x) {
                    for(int y = 0; y < BLOCK_SIZE; ++y) {
                        for(int colour = 0; colour < numberOfColourChannels; ++colour) {
                            try{
                                blocks[blockNumber].setXYCByte(x, y, colour, src.getXYCByte(i+x, j+y, colour));
                            } catch( IndexOutOfBoundsException e) {
                               //System.out.println("Blocking -> " + x + ", " + y + ", " + colour  + ", " + (i+x)  + ", " + (j+y));
                                System.out.println("Block number is " + blockNumber);
                            }
                        }
                    }
                }
                blockNumber += 1;
            }
        }
    }

    public int getBLOCK_SIZE() {
        return BLOCK_SIZE;
    }

    public Image[] getBlocks() {
        return blocks;
    }

    public Image getSrc() {
        return src;
    }

    public int getNumberOBlocks() {
        return numberOBlocks;
    }
}
