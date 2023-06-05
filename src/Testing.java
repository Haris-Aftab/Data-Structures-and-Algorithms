import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Scanner;

/**
 * This class is used to enter the files to compress and decompress.
 */
public class Testing {
    public static Huffman huffman = new Huffman();
    public static long originalFileSize;

    /**
     * File compression by creating and using its own tree.
     */
    public static void option1() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter location of file to compress (.txt): ");
        File originalFile = new File(scanner.next());

        System.out.print("Enter location of file to store compressed file (.bin): ");
        File compressedFile = new File(scanner.next());

        System.out.print("Enter location of file to store tree (.ser): ");
        File huffmanTree = new File(scanner.next());

        if (originalFile.length() != 0) {
            huffman.huffmanCode = new StringBuilder();
            huffman.code = new HashMap<>();
            huffman.freq = new HashMap<>();

            //Reads a given file and stores a hashmap containing a character and its frequency in the text.
            String text = huffman.createFreq(originalFile);

            // The createTree function creates the huffman tree using the hashmap containing a character and frequency pair.
            // It then returns the root of the huffman tree.
            huffman.root = huffman.createTree();

            // Given the root, this function traverses down the tree storing the huffman codes for each character.
            huffman.encode(huffman.root, "");

            // Converts the whole original text into its huffman code.
            huffman.compressText(text);

            // Converts the huffman code to binary and stores it in the output file.
            huffman.huffmanCodeToFile(compressedFile);

            // Stores the hashmap of the character and its frequency to a file.
            huffman.treeToFile(huffmanTree);
        } else System.out.println("File is empty!");
        try {
            originalFileSize = Files.size(originalFile.toPath());
            long compressed = Files.size(compressedFile.toPath()) + Files.size(huffmanTree.toPath());
            System.out.println("Original file size: " + originalFileSize);
            System.out.println("Compressed file size: " + compressed);
            System.out.printf("Compressed file %.2f%%", 100.00 - (((double)compressed/originalFileSize) * 100));
            System.out.println();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * File compression using another files tree.
     */
    public static void option2() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter location of file to compress (.txt): ");
        File originalFile = new File(scanner.next());

        System.out.print("Enter location of file to store compressed file (.bin): ");
        File compressedFile = new File(scanner.next());

        System.out.print("Enter location of tree file (.ser): ");
        File huffmanTree = new File(scanner.next());

        if (originalFile.length() != 0) {
            huffman.huffmanCode = new StringBuilder();
            huffman.code = new HashMap<>();
            huffman.freq = new HashMap<>();

            String originalText = huffman.readFile(originalFile);

            // Retrieves the hashmap from the file and returns and stores the root (the huffman tree).
            huffman.root = huffman.treeFromFile(huffmanTree);

            // Given the root, this function traverses down the tree storing the huffman codes for each character.
            huffman.encode(huffman.root, "");

            // Converts the whole original text into its huffman code.
            huffman.compressText(originalText);

            // Converts the huffman code to binary and stores it in the output file.
            huffman.huffmanCodeToFile(compressedFile);
        } else System.out.println("File is empty!");
        try {
            originalFileSize = Files.size(originalFile.toPath());
            long compressed = Files.size(compressedFile.toPath()) + Files.size(huffmanTree.toPath());
            System.out.println("Original file size: " + originalFileSize);
            System.out.println("Compressed file size: " + compressed);
            System.out.printf("Compressed file %.2f%%", 100.00 - (((double)compressed/originalFileSize) * 100));
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Decompresses a file
     */
    public static void option3() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter location of file to decompress (.bin): ");
        File compressedFile = new File(scanner.next());

        System.out.print("Enter location of tree file to use (.ser): ");
        File huffmanTree = new File(scanner.next());

        System.out.print("Enter location of file to store decompressed file (.txt): ");
        File decompressedFile = new File(scanner.next());

        // Retrieves the hashmap from the file and returns and stores the root (the huffman tree).
        huffman.root = huffman.treeFromFile(huffmanTree);

        // Gets the huffman code from the output file and decodes the huffman code back to the original text.
        huffman.huffmanCodeFromFile(compressedFile);

        // decodes the huffman code and stores the decoded text in a string
        String decodedText = huffman.decode(huffman.root);

        // This stores the decompressed text into a file.
        huffman.writeDecompressedFile(decodedText, decompressedFile);
        try {
            long decompressed = Files.size(decompressedFile.toPath());
            System.out.println("Original file size: " + originalFileSize);
            System.out.println("Decompressed file size: " + decompressed);
            System.out.printf("File size difference: %.2f%%", ((double)Math.abs(originalFileSize-decompressed)/originalFileSize) * 100);
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * User interface to decide what tasks to preform
     */
    public static void menu() {
        while (true) {
            System.out.print("""
                    
                    0. Exit
                    1. Compress file (creating its own tree)
                    2. Compress file (using another file's tree)
                    3. Decompress file
                    Input: """);
            Scanner scanner = new Scanner(System.in);
            String input = scanner.next();
            if (("0123").contains(input)) {
                System.out.println();
                switch (input) {
                    case "0" -> System.exit(0);
                    case "1" -> option1();
                    case "2" -> option2();
                    case "3" -> option3();
                }
            } else System.out.println("Enter a valid input!");
        }
    }

    public static void main(String[] args) {
        menu();
    }
}
