package com.zhuanche.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class OperateCSVfile {
    public static void main(String[] args){
        String [] str = {"省","市","区","街","路","里","幢","村","室","园","苑","巷","号"};
        File inFile = new File("d://1.csv"); // 读取的CSV文件
        File outFile = new File("d://out.csv");//写出的CSV文件
        String inString = "";
        String tmpString = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
            while((inString = reader.readLine())!= null){
                for(int i = 0;i<str.length;i++){
                    tmpString = inString.replace(str[i], "," + str[i] + ",");
                    inString = tmpString;
                }
               writer.write(inString);
               writer.newLine();
            }
            reader.close();
            writer.close();
        } catch (FileNotFoundException ex) {
            System.out.println("没找到文件！");
        } catch (IOException ex) {
            System.out.println("读写文件出错！");
        }
    }
    
    private static String[] cvsField(String line){
        List<String> fields = new LinkedList<>();
        char[] alpah = line.toCharArray();
        boolean isFieldStart = true;
        int pos = 0; int len = 0; boolean yinhao = false;
        for(char c : alpah){
          if(isFieldStart){
            len = 0;
            isFieldStart = false;
          }
          if(c == '\"'){
            yinhao = !yinhao;
          }
          if(c == ',' && !yinhao){
            fields.add(new String(alpah, pos - len, len));
            isFieldStart = true;
          }
          pos++; len++;
        }
        return fields.toArray(new String[0]);
      }
    
    
} 
