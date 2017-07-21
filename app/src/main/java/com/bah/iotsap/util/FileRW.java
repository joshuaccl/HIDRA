package com.bah.iotsap.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;


/**
 * FileRW is used to read and write files to internal storage.
 *
 */
public final class FileRW {

    private static final String TAG = "FileRW";
    public  static final String FOLDER = "iotsap/";

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
    public static boolean write(Context context, String fileName, String contents) {
        FileOutputStream fileOutputStream;
        String newLine = "\n";

        try{
            fileOutputStream = context.openFileOutput(fileName, Context.MODE_APPEND);
            fileOutputStream.write(contents.getBytes());
            fileOutputStream.write(newLine.getBytes());
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            Log.i(TAG, "write(Context, String, String): Caught Exception");
            e.printStackTrace();
            return false;
        }
    }

    //Read the file with fileName
    public static String read(Context context, String filename) {
        String line, contents = "";
        try{
            InputStream inputStream = context.openFileInput(filename);
            if(inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                try {
                    while((line = bufferedReader.readLine()) != null)
                        contents += line;
                } catch(Exception e) {
                    Log.i(TAG, "read(Context, String): Caught Exception");
                    e.printStackTrace();
                }
            }
        } catch(Exception e) {
            Log.i(TAG, "read(Context, String): Caught Exception");
        }
        return contents;
    }

    // Delete the file with fileName
    public static void delete(Context context, String fileName) {
        context.deleteFile(fileName);
    }

    /**
     * Copy all bytes of a file to an OutputStream.
     * @param file NonNull file
     * @param out NonNull OutputStream
     */
    public static void copy(@NonNull File file, @NonNull OutputStream out) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            int data;
            while((data = in.read()) != -1) out.write(data);
            out.flush();
        } catch(FileNotFoundException e) {
            Log.i(TAG, "copy(File, OutputStream): Caught FileNotFoundException");
        } catch(Exception e) {
            Log.i(TAG, "copy(File, OutputStream): Caught Exception");
        } finally {
            try {
                in.close();
            } catch(Exception e) {
                Log.i(TAG, "copy(File, OutputStream): Caught Exception while closing FIS");
            }
        }
    }
}