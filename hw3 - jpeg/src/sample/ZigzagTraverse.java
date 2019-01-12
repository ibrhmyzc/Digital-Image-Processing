package sample;

public class ZigzagTraverse {
    private int[] zigzagIndexSequence = {1,2,6,7,15,16,28,29
            ,3,5,8,14,17,27,30,43
            ,4,9,13,18,26,31,42,44
            ,10,12,19,25,32,41,45,54
            ,11,20,24,33,40,46,53,55
            ,21,23,34,39,47,52,56,61
            ,22,35,38,48,51,57,60,62
            ,36,37,49,50,58,59,63,64};
    private int[][] quantizedMatrix;
    public ZigzagTraverse(int[][] quantizedMatrix) {
        this.quantizedMatrix = quantizedMatrix;
    }

    public int[] zigzag() {
        int[] result = new int[64];
        int pos = 0;
        for(int i = 0; i < 8; ++i) {
            for(int j = 0 ; j < 8; ++j) {
                result[zigzagIndexSequence[pos]-1] = quantizedMatrix[i][j];
                pos++;
            }
        }
        return result;
    }

    public int[][] inverseZigzag(int[] newZigzag) {
        int[][] result = new int[8][8];
        int pos = 0;
        for(int i = 0; i < 8; ++i) {
            for(int j = 0; j < 8; ++j) {
                result[i][j] = newZigzag[zigzagIndexSequence[pos]-1];
                pos++;
            }
        }
        return result;
    }
}
