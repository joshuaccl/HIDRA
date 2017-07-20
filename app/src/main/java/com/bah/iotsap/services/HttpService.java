package com.bah.iotsap.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


/**
 * HttpService allows us to send information over HTTP.
 * You are able to send files or text over HTTP via POST or form/multipart.
 * TODO: Remove test code in primary sendFile method
 * TODO: Set up default address or config to listen to
 * TODO: Get POST working with a request directly to the target address
 */
public class HttpService extends IntentService {

    private static final String TAG  = "HttpService";
    private static final String CRLF = "\r\n";
    public  static final String SEND_FILE = "com.bah.iotsap.services.HttpService.SEND_FILE";
    public  static final String ADDRESS  = "address";
    public  static final String FILENAME = "title";
    public  static final String DESC     = "description";



    public HttpService() {
        super("HttpService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "onHandleIntent(): action = " + action);

        if(SEND_FILE.equals(action)) {
            sendFile(intent);
        } else {
            Log.i(TAG, "onHandleIntent(): Action is NOT SEND_FILE");
        }
        Log.i(TAG, "onHandleIntent(): Ending method");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    }

    /**
     * sendFile(Intent) is the method used for sending a particular file over HTTP
     * to a server.
     * @param intent with StringExtras for address of site and filename to send
     * @return success if file was sent successfully, false otherwise
     */
    private boolean sendFile(Intent intent) {
        Log.i(TAG, "sendFile()");
        boolean success = false;

        // File and address Strings
        String address = "http://192.168.1.100:8080/upload/"; // intent.getStringExtra(ADDRESS);
        String filename = intent.getStringExtra(FILENAME);
        String desc = intent.getStringExtra(DESC);
        String boundary = Long.toHexString(System.currentTimeMillis());
        URL url;
        HttpURLConnection conn = null;
        PrintWriter writer = null;
        File file = null;


        // TEST CODE FOR FILE
        filename = "myTestFile100.txt";
        String contents = "hello world! This is a test file to ensure can upload files to a server";
        try {
            FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(contents.getBytes(StandardCharsets.UTF_8));
            fos.close();

            File myFile = new File(getFilesDir(), filename);
            Scanner scanner = new Scanner(myFile);
            String fileContents = scanner.nextLine();
            scanner.close();
            if(contents.equals(fileContents)) {
                Log.i(TAG, "file contents EQUAL");
            } else {
                Log.i(TAG, "file contents NOT EQUAL!");
                Log.i(TAG, "contents = " + contents);
                Log.i(TAG, "fileContents = " + fileContents);
            }

        } catch(FileNotFoundException e) {
            Log.i(TAG, "sendFile(Intent): FileNotFoundException");
        } catch(IOException e) {
            Log.i(TAG, "sendFile(Intent): IOException");
        }
        // END TEST CODE

        // Guard against invalid
        if(filename == null || filename.isEmpty() ||
                address  == null || address.isEmpty()) {
            Log.i(TAG, "sendFile(): Entered GUARD, invalid filename or address");
            return success;
        }

        try {
            url = new URL(address);
            file = new File(getFilesDir(), filename);

            // HTTP connection / header setup
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true); // Allow inputs on connection
            conn.setDoOutput(true); // Allow outputs on connection
            conn.setChunkedStreamingMode(0); // Body length is unknown
            conn.setRequestMethod("POST"); // Post information to server
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            // Setup stream writer
            writer = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8));
            // Send text file
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=" + StandardCharsets.UTF_8.toString()).append(CRLF);
            writer.append(CRLF).flush();
            // copy all bytes of file to output stream
            copyFile(file, conn.getOutputStream());
            writer.append(CRLF).flush();
            // END of multipart/form-data
            writer.append("--" + boundary + "--").append(CRLF).flush();

            int responseCode = conn.getResponseCode();
            Log.i(TAG, "sendFile(): responseCode = " + responseCode);

            success = true;
        } catch(MalformedURLException e) {
            Log.i(TAG, "sendFile(): MalformedURLException");
        } catch(IOException e) {
            Log.i(TAG, "sendFile(): IOException");
        } finally {
            conn.disconnect();
            close(writer);
        }
        return success;
    }

    /**
     * Copy a all bytes of a file to an output stream
     * @param file nonNull File
     * @param out nonNull OutputStream
     */
    private void copyFile(@NonNull File file, @NonNull OutputStream out) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            int data;
            while((data = in.read()) != -1) out.write(data);
            out.flush();

        } catch(FileNotFoundException e) {
            Log.i(TAG, "copyFile(): FileNotFoundException");
        } catch(IOException e) {
            Log.i(TAG, "copyFile(): IOException");
        } finally {
            close(in);
        }
    }

    /**
     * Helper method used to cleanly close all streams found in the above try block.
     * We simple pass all closeable objects to this method in a finally block.
     * @param c an object that has the .close() function.
     */
    private void close(@Nullable Closeable c) {
        if(c == null) return;
        try {
            c.close();
        } catch(IOException e) {
            Log.i(TAG, "close(Closeable): Caught IOException");
        }
    }
}
