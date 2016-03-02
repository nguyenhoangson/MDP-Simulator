/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Map;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import static Map.MapData.COLS;
import static Map.MapData.ROWS;

/**
 *
 * @author Tan Quang Ngo
 */


public class FileLoader {

    public static String[] readTextFile(String fileName) throws IOException {
        FileReader fileReader = new FileReader(fileName);

        List<String> lines;
        try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            lines = new ArrayList<>();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
        }

        return lines.toArray(new String[lines.size()]);
    }

    public static void saveTextFile(String fileName, Cell c[][]) throws IOException {

        // save Map
        File file = new File(fileName);

        // if file doesn't exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());

        BufferedWriter bw = new BufferedWriter(fw);

        for (int i = 0; i < ROWS; i++) {

            for (int j = 0; j < COLS; j++) {
                bw.write(c[i][j].getContent());
            }
            bw.write("\n");
        }

        bw.close();

    }

    //
    public static String getFileName(boolean isForSave) {
        final JFileChooser fc = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("Text file", new String[]{"txt"});
        fc.setFileFilter(filter);
        fc.addChoosableFileFilter(filter);

        fc.setCurrentDirectory(new File("."));
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int returnVal;
        if (isForSave) {
            returnVal = fc.showSaveDialog(null);
        } else {
            returnVal = fc.showOpenDialog(null);
        }
        File file = fc.getSelectedFile();
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println(file.getPath());
            return file.getPath();

        }
        System.out.println("Save/Load unsuccessful!");
        return null;
    }


    public static void saveExploreTextFile(String fileName, int explore[][], boolean obj[][]) throws FileNotFoundException, IOException {
        
        
        String str = "11";
        // save Map
        File file = new File(fileName);
        // if file doesnt exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(str + "\n");
        
        for (int i = 0; i < ROWS - 2; i++) {
            for (int j = 0; j < COLS - 2; j++) {
                if (explore[i][j] == 1) {
                    bw.write("1");
                } else {
                    bw.write("0");
                }
            }
            bw.write("\n");
        }
               
        for (int i = ROWS - 3; i >=0; i--) {
            for (int j = 0; j < COLS - 2; j++) {
                if (explore[i][j] == 1) {
                    str += "1";
                } else {
                    str += "0";
                }
            }
        }
        str += "11";
        bw.write("11\n\nExplored string 1: \n");
        for (int i = 0; i < str.length(); i += 4) {
            String hex = str.substring(i, i + 4);
            int k = Integer.parseInt(hex, 2);
            String hexString = Integer.toHexString(k);
            bw.write(hexString);
        }
        bw.write("\n\nExplored part 2:\n");

        str = "";
        
         for (int i = 0; i <ROWS - 2; i++) {
            for (int j = 0; j < COLS - 2; j++) {
                if (explore[i][j] == 1) {

                    if (obj[i][j]) {
                        bw.write("0");
                    } else {
                        bw.write("1");
                    }
                }
            }
            bw.write("\n");
        }
   
                
       for (int i = ROWS - 3; i >=0; i--) {
            for (int j = 0; j < COLS - 2; j++) {
                if (explore[i][j] == 1) {
                    if (obj[i][j]) {
                        str += "0";
                    } else {
                        str += "1";
                    }
                }
            }
        }
       
        int r = str.length()%4;
        while(r != 0){
            str+="1";
            r = str.length()%4;
        }
        bw.write("\n\nExplored string 2: \n");
        for (int i = 0; i < str.length(); i += 4) {
            String hex = str.substring(i, i + 4);
            int k = Integer.parseInt(hex, 2);
            String hexString = Integer.toHexString(k);
            bw.write(hexString);
        }
        bw.write("\n");
        bw.close();

    }
}
