package sample;

import com.sun.javafx.image.IntPixelGetter;

import java.util.*;

public class IbrahimsHuffman {
    private int[] inp;
    private int[] freqs;
    private HuffmanTree huffmanTree;
    private HuffmanTree.HuffData[] builtByFreqs;
    private Character[] lut;
    private int numberOfUniqueValues = 0;
    private Map<Character, Integer> codingDictionary;
    private Map<Integer, Character> codingReverse;
    private String encodedMessage;
    private int quality;
    public IbrahimsHuffman(int[] inp, int quality) {
        this.inp = inp;
        this.quality = quality;
        huffmanTree = new HuffmanTree();
        codingDictionary = new HashMap<>();
        codingReverse = new HashMap<>();
        String alp = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdef";
        lut = new Character[alp.length()-(20 - quality)];
        for(int i = 0; i < alp.length()-(20 - quality); ++i) {
            lut[i] = alp.charAt(i);
        }
    }

    public String runEncode() {
        Map<Integer, Integer> freqMap= new HashMap<>();

        // calculating frequencies
        for(int i = 0; i < lut.length; ++i) {
            if(!freqMap.containsKey(inp[i])) {
                freqMap.put(inp[i], 1);
            } else {
                freqMap.put(inp[i], 1 + freqMap.get(inp[i]));
            }
        }

        // calculating the minimum number of elements that are to be used in huffman tree
        if(freqMap.size() > lut.length) {
            numberOfUniqueValues = lut.length;
        } else {
            numberOfUniqueValues = freqMap.size() - 1;
        }
        builtByFreqs = new HuffmanTree.HuffData[lut.length];
        freqs = new int[lut.length];

        // building the frequencies
        for(int i = 0; i < lut.length; ++i){
            freqs[i] = freqMap.get(inp[i]);
        }

        if(MyDebugger.isEnabled()) {
            System.out.println("Before encoding: " + Arrays.toString(freqs));
        }

        // Set up huffman tree
        StringBuilder sb = new StringBuilder();
        for(int i = 0 ; i < lut.length; ++i) {
            builtByFreqs[i] = new HuffmanTree.HuffData(freqs[i], lut[i]);
            codingDictionary.put(lut[i], inp[i]);
        }

        for(int i = 0; i < lut.length; ++i) {
            codingReverse.put(inp[i], lut[i]);
        }

        for(int i = 0; i < lut.length; ++i) {
            sb.append(codingReverse.get(inp[i]));
        }

        huffmanTree.buildTree(builtByFreqs);
        encodedMessage = huffmanTree.encode(sb.toString());
        if(MyDebugger.isEnabled()) {
            System.out.println(sb.toString());
            System.out.println("Encoded: " + encodedMessage);
        }

        return encodedMessage;
    }

    public int[] runDecode(String message) {
        int[] result = new int[64];
        String decodedMessage = huffmanTree.decode(message);
        for(int i = 0 ; i < decodedMessage.length(); ++i) {
            result[i] = codingDictionary.get(decodedMessage.charAt(i));
        }
        if(MyDebugger.isEnabled()) {
            System.out.print("Decoded: ");
            System.out.println(Arrays.toString(result));
            System.out.println();
        }
        return result;
    }
}
