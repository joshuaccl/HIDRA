package com.bah.iotsap.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.spec.ECField;

/**
 * Created by 591263 on 7/17/2017.
 */

//This file is written assuming implementation of scheduled device discovery services

public final class FileRW {

    //Make empty file with name fileName
    public static boolean init(Context context, String fileName) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //Write new object to file with fileName
    public static boolean write(Context context, String fileName, String object) {
        FileOutputStream fileOutputStream;
        String newLine = "\n";

        try{
            fileOutputStream = context.openFileOutput(fileName, Context.MODE_APPEND);
            fileOutputStream.write(object.getBytes());
            fileOutputStream.write(newLine.getBytes());
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    //Read the file with fileName
    public static String read(Context context, String filename) {
        String line, line1 = "";
        try{
            InputStream inputStream = context.openFileInput(filename);
            if(inputStream!= null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                try {
                    while ((line = bufferedReader.readLine()) != null)
                        line1 += line;
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            String error = "";
            error = e.getMessage();
        }

        return line1;
    }

    //Delete the file with fileName
    public static void delete(Context context, String fileName) {
        context.deleteFile(fileName);
    }

}