import java.io.*;
import java.util.*;
import java.nio.file.*;

/**
 * This class creates node
 */
class Node {
    Character ch;
    Integer freq;
    Node left = null, right = null;

    /**
     * Constructor used to create node.
     * @param ch The character in the node
     * @param freq The frequency the character occurs in the text
     */
    Node(Character ch, Integer freq)
    {
        this.ch = ch;
        this.freq = freq;
    }

    /**
     * Constructor used to create a node with child nodes.
     * @param ch The character in the node
     * @param freq The frequency the character occurs in the text
     * @param left The left child node
     * @param right The right child node
     */
    public Node(Character ch, Integer freq, Node left, Node right)
    {
        this.ch = ch;
        this.freq = freq;
        this.left = left;
        this.right = right;
    }
}

/**
 * This class compresses files using huffman coding.
 */
public class Huffman {
    public StringBuilder huffmanCode;   // string containing the huffman code version of a file
    public HashMap<Character, String> code; // stores the character and its corresponding huffman code
    public HashMap<Character, Integer> freq;   // stores the character and frequency pair
    public Node root;   // the huffman tree

    public String readFile(File inFile) {
        StringBuilder originalText = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inFile));
            String line = reader.readLine();
            while(line != null) {
                originalText.append(line).append("\n");
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return originalText.toString();
    }

    /**
     * Reads a given file and stores a hashmap containing a character and its frequency in the text.
     * @param inFile The file to be encoded
     * @return Returns the given file as a string
     */
    public String createFreq(File inFile) {
        FileReader fileReader = null;
        // the fileReader makes it possible to read the contents of a file as a stream of characters
        try {
            fileReader = new FileReader(inFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert fileReader != null;
        BufferedReader br = new BufferedReader(fileReader);   // used to read the text from the input stream
        StringBuilder text = new StringBuilder();   // stores the original text
        int c = 0;   // stores the ascii values of a character
        while(true) {
            try {
                if ((c = br.read()) == -1) break;   // runs until the end of the file
            } catch (IOException e) {
                e.printStackTrace();
            }
            char character = (char) c;   // converts from ascii to character
            text.append(character);   // adds each character to the text
            // adds the character to the HashMap along with its frequency in the file
            freq.put(character, freq.getOrDefault(character, 0) + 1);
        }
        try {
            fileReader.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }

    /**
     * Checks if a node is a leaf node.
     * @param n The node to be checked
     * @return returns true if the node is a leaf node and false otherwise
     */
    static boolean isLeaf(Node n) {
        if (n != null) return n.left == null && n.right == null;
        return false;
    }

    /**
     * Converts the whole original text into its huffman code.
     * @param text The original text
     */
    public void compressText(String text) {
        // iterates through each character in the text
        for (char charInText : text.toCharArray()) {
            if (code.containsKey(charInText)) {
                huffmanCode.append(code.get(charInText));
            } else {
                if (code.containsKey(' ')) huffmanCode.append(code.get(' '));
                else huffmanCode.append(code.get(code.keySet().toArray()[0]));
            }
        }
    }

    /**
     * Creates the huffman tree using the hashmap containing a character and frequency pair.
     * @return Returns the root node of the tree
     */
    public Node createTree() {
        // a priority queue which orders the priority based on the frequency
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(f -> f.freq));
        // runs through each character and frequency pair
        for (var set : freq.entrySet()) {
            // creates a new Node with the character and freq, and adds it to the priority queue
            priorityQueue.add(new Node(set.getKey(), set.getValue()));
        }
        // runs until there is one root node
        while (priorityQueue.size() > 1) {
            // removes the two least frequent nodes
            Node left = priorityQueue.poll();
            Node right = priorityQueue.poll();

            // creates a new node with the two nodes as children
            // the freq is the sum of the children's freq
            int sum = left.freq + right.freq;
            // the new node is added to the priority queue
            priorityQueue.add(new Node(null, sum, left, right));
        }
        // this returns the root of the huffman tree
        return priorityQueue.peek();
    }

    /**
     * Converts the huffman code to binary and stores it in the output file.
     * @param outFile The file containing the compressed text
     */
    public void huffmanCodeToFile(File outFile) {
        // stores the binary version of the huffman code in an array of bytes
        byte[] binCode = getBinary(huffmanCode.toString());
        try {
            // writes the binary code to the output file
            OutputStream outputStream = new FileOutputStream(outFile);
            outputStream.write(binCode);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the huffman code from the output file and stores the code.
     * @param outFile The file containing the compressed text in binary
     */
    public void huffmanCodeFromFile(File outFile) {
        // gets the contents of the output file
        byte[] allBytes = new byte[0];
        try {
            allBytes = Files.readAllBytes(outFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        int len = huffmanCode.length();
        huffmanCode.delete(0, len);    // empties the huffman code string
        // converts the binary to a string and stores the huffman code
        huffmanCode.append(getString(allBytes).substring(0, len));
    }

    /**
     * This stores the decompressed text into a file.
     * @param decodedText The decompressed text from the compressed file
     * @param decompressedFile The file containing the decompressed text
     */
    public void writeDecompressedFile(String decodedText, File decompressedFile) {
        try {
            // writes string to a file
            BufferedWriter writer = new BufferedWriter(new FileWriter(decompressedFile));
            writer.write(decodedText);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stores the hashmap of the character and its frequency to a file.
     * This hashmap is used to create the tree.
     * @param huffmanTree The file containing the hashmap of the character and frequency pair
     */
    public void treeToFile(File huffmanTree) {
        try {
            // writes hashmap object to file - serialization
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(huffmanTree));
            outputStream.writeObject(freq);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the hashmap from the file
     * @param huffmanTree The file containing the hashmap of the character and frequency pair
     * @return Returns the huffman tree (the root)
     */
    public Node treeFromFile(File huffmanTree) {
        try {
            // retrieves the hashmap from the file - deserialization
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(huffmanTree));
            this.freq = (HashMap<Character, Integer>) objectInputStream.readObject();
            objectInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        // creates and returns a huffman tree using the freq hashmap
        return createTree();
    }

    /**
     * Given the root, this function traverses down the tree storing the huffman codes for each character.
     * @param root The root of the tree
     * @param charCode The huffman code that represents a character
     */
    public void encode(Node root, String charCode) {
        // exits this recursive function
        if (root == null) return;

        // checks if the current node is a leaf node (a character)
        if (isLeaf(root))
            code.put(root.ch, charCode.length() > 0 ? charCode : "1");

        // recursion with the next node and adds another digit to the characters huffman code
        encode(root.left, charCode + "0");
        encode(root.right, charCode + "1");
    }


    /**
     * Given the root and the huffman code, this function runs through the huffman code and respectively traverses
     * the huffman tree to reach each character.
     * @param root The root of the tree
     * @return Returns the index to let the function know when all the huffman code has been decoded
     */
    public String decode(Node root) {
        StringBuilder decodedText = new StringBuilder();

        // runs when the text only consists of one character
        if (isLeaf(root))
            while (root.freq-- > 0)
                decodedText.append(root.ch);
        else {
            // starts traversing from the root node
            Node temp = root;
            for (int i = 0; i < huffmanCode.length(); i++) {
                // checks if the current character in the code is a '0' or '1' and traversed the tree accordingly
                temp = (huffmanCode.charAt(i) == '0') ? temp.left : temp.right;
                // if we reach a leaf node, we add the character to the decoded string
                if (temp.left == null) {
                    decodedText.append(temp.ch);
                    temp = root;    // start traversing from the root node again
                }
            }
        }
        return decodedText.toString();
    }

    /**
     * Converts a string to binary.
     * @param s A string
     * @return Returns the binary version of the string
     */
     public byte[] getBinary(String s) {
        StringBuilder sBuilder = new StringBuilder(s);
        while (sBuilder.length() % 8 != 0) {
            sBuilder.append('0');
        }
        s = sBuilder.toString();

        byte[] data = new byte[s.length() / 8];

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '1') {
                data[i >> 3] |= 0x80 >> (i & 0x7);
            }
        }
        return data;
    }

    /**
     * Converts the array of bytes containing binary to a string.
     * @param bytes Binary bytes
     * @return Returns the converted from binary string
     */
    public StringBuilder getString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
        for (int i = 0; i < Byte.SIZE * bytes.length; i++)
            sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        return sb;
    }

}
