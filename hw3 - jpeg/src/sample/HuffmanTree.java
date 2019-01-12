package sample;

import java.io.*;
import java.util.*;

public class HuffmanTree implements Serializable {

    public static class HuffData implements Serializable
    {
        private double weight;
        private Character symbol;

        public HuffData(double weight, Character symbol)
        {
            this.weight = weight;
            this.symbol = symbol;
        }
    }
    //Data field
    private BinaryTree<HuffData> huffTree;

    /** A Comparator for Huffman trees; nested class. */
    private static class CompareHuffmanTrees
            implements Comparator < BinaryTree < HuffData >>
    {
        public int compare(BinaryTree < HuffData > treeLeft,
                           BinaryTree < HuffData > treeRight)
        {
            double wLeft = treeLeft.getData().weight;
            double wRight = treeRight.getData().weight;
            return Double.compare(wLeft, wRight);
        }
    }

    /**
     * Builds a binary tree from an array of huffdata
     * @param symbols frequencies of the letters
     */
    public void buildTree(HuffData[] symbols)
    {
        Queue < BinaryTree < HuffData >> theQueue
                = new PriorityQueue < BinaryTree < HuffData >>
                (symbols.length, new CompareHuffmanTrees());
        // Load the queue with the leaves.
        for (HuffData nextSymbol : symbols)
        {
            BinaryTree < HuffData > aBinaryTree =
                    new BinaryTree < HuffData > (nextSymbol, null, null);
            theQueue.offer(aBinaryTree);
        }

        // Build the tree.
        while (theQueue.size() > 1)
        {
            BinaryTree < HuffData > left = theQueue.poll();
            BinaryTree < HuffData > right = theQueue.poll();
            double wl = left.getData().weight;
            double wr = right.getData().weight;
            HuffData sum = new HuffData(wl + wr, null);
            BinaryTree < HuffData > newTree =
                    new BinaryTree < HuffData > (sum, left, right);
            theQueue.offer(newTree);
        }

        // The queue should now contain only one item.
        huffTree = theQueue.poll();
    }

    /**
     * pritns code
     * @param out pritnfstream
     * @param code code to be coded
     * @param tree initial binary tree
     */
    private void printCode(PrintStream out, String code,
                           BinaryTree < HuffData > tree)
    {
        HuffData theData = tree.getData();
        if (theData.symbol != null)
        {
            if (theData.symbol.equals(" "))
            {
                out.println("space: " + code);
            }
            else
            {
                out.println(theData.symbol + ": " + code);
            }
        }
        else
        {
            printCode(out, code + "0", tree.getLeftSubtree());
            printCode(out, code + "1", tree.getRightSubtree());
        }
    }

    /**
     * decodes a coded message
     * @param codedMessage codedmessage
     * @return proginal message
     */
    public String decode(String codedMessage)
    {
        StringBuilder result = new StringBuilder();
        BinaryTree<HuffData> currentTree = huffTree;

        for(int i = 0; i < codedMessage.length(); ++i)
        {
            if(codedMessage.charAt(i) == '1')
            {
                currentTree = currentTree.getRightSubtree();
            }
            else
            {
                currentTree = currentTree.getLeftSubtree();
            }
            if(currentTree.isLeaf())
            {
                HuffData theData = currentTree.getData();
                result.append(theData.symbol);
                currentTree = huffTree;
            }
        }
        return result.toString();
    }

    /**
     * Encodes a message
     * @param message message to be encoded
     * @return encoded message
     */
    public String encode(String message)
    {
        StringBuilder result = new StringBuilder();
        BinaryTree<HuffData> currentTree = huffTree;
        String temp = "";
        //message = message.toUpperCase();
        if(!isValid(message))
            throw new NullPointerException();

        for(int i = 0; i < message.length(); ++i)
        {
            /*Looking for its code in a recursive function*/
            temp = getCode(message.charAt(i), currentTree);
            result.append(temp);
        }
        return result.toString();
    }

    /**
     * Recursively looks for the character and build its code
     * @param target target to be found
     * @param source the tree we will look in
     * @return code of the searched target
     */
    private String getCode(char target, BinaryTree<HuffData> source)
    {
        String temp = "";
        /*if target is found*/
        if(source.isLeaf() && source.getData().symbol == target)
            return "";
            /*if searching is finished and target is not found*/
        else if(source.isLeaf() && source.getData().symbol != target)
            return null;

        /*0 for left*/
        if( (temp = getCode(target, source.getLeftSubtree())) != null)
        {
            return "0" + temp;
        }
        /*1 for right*/
        else if((temp = getCode(target, source.getRightSubtree())) != null)
        {
            return "1" + temp;
        }
        /*returns itself*/
        return temp;
    }

    /**
     * Checks the message if it only contains english letters
     * @param message message to be checked
     * @return if there is non english letters, returns false
     */
    private boolean isValid(String message)
    {
        char temp;
        for(int i = 0; i < message.length(); ++i)
        {
            temp = message.charAt(i);
            if((temp >= 'A' && temp <='Z') || (temp >= 'a' && temp <= 'z'))
            {
                return true;
            }
        }
        return false;
    }
}
