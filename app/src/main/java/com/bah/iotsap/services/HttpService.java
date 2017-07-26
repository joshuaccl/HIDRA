package com.bah.iotsap.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bah.iotsap.util.FileRW;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Random;
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
    public String fileContents;
    private Context mContext;
    private String filename;

    private static final int CONN_TIMEOUT = 3000;
    private static final int READ_TIMEOUT = 3000;


    public HttpService() {
        super("HttpService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");
        mContext = this;
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
        String address = "http://192.168.1.100:8080/upload"; // intent.getStringExtra(ADDRESS);
        filename = intent.getStringExtra(FILENAME);
        String desc = intent.getStringExtra(DESC);
        String boundary = Long.toHexString(System.currentTimeMillis());
        URL url;
        HttpURLConnection conn = null;
        PrintWriter writer = null;
        File file = null;

        // Guard against invalid intnt extras
        if(filename == null || filename.isEmpty() ||
                address  == null || address.isEmpty()) {
            Log.i(TAG, "sendFile(): Entered GUARD, invalid filename or address");
            return success;
        }

        /**
         * Primary block where the following happens:
         * 1. A HttpURLConnection is made to the provided address.
         * 2. Configure the connection so we can write to it, so it timesout instead of blocking,
         *    so we write a POST....
         * 3. A Writer is set up to write strings / files to the output stream of the connection.
         * 4. A HTTP form is written then the provided file
         * 5. Response code is checked for error handling
         */
        try {
            Log.i(TAG, "sendFile(): Entered main try block");
            url  = new URL(address);
            file = new File(getFilesDir(), filename);
            if (file!= null) {
                Scanner scanner = new Scanner(file);
                fileContents = scanner.nextLine();
                scanner.close();
            }
            Log.i(TAG, "sendFile(): File contents are: " + fileContents);

            // HTTP connection / header setup
            Log.i(TAG, "sendFile(): Setting up HTTP connection");
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);                  // Allow inputs on connection
            conn.setDoOutput(true);                 // Allow outputs on connection
            conn.setReadTimeout(READ_TIMEOUT);      // prevent getRequestCode from blocking
            conn.setConnectTimeout(CONN_TIMEOUT);   // Timeout if connection unavailable
            conn.setChunkedStreamingMode(0);        // Body length is unknown
            conn.setRequestMethod("POST");          // Post information to server
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            // Setup stream writer
            Log.i(TAG, "sendFile(): Settings up PrintWriter");
            writer = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8));
            // Send text file
            Log.i(TAG, "sendFile(): About to begin writing body fields");
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=" + StandardCharsets.UTF_8.name()).append(CRLF);
            writer.append(CRLF).flush();
            // copy all bytes of file to output stream
            Log.i(TAG, "sendFile(): About to write file to stream");
            FileRW.copy(file, conn.getOutputStream());
            writer.append(CRLF).flush();
            // END of multipart/form-data
            writer.append("--" + boundary + "--").append(CRLF).flush();

            Log.i(TAG, "sendFile(): Gettings response code");
            int responseCode = conn.getResponseCode();
            Log.i(TAG, "Checking for connection success");

            if(HttpURLConnection.HTTP_ACCEPTED != responseCode &&
               HttpURLConnection.HTTP_OK       != responseCode) {
                Log.i(TAG, "sendFile(): Connection unsuccessful, aborting");
                throw new IOException("Could not connect to address and/or get responseCode");
            }
            Log.i(TAG, "sendFile(): responseCode = " + responseCode);

            success = true;
        } catch(MalformedURLException e) {
            Log.i(TAG, "sendFile(): MalformedURLException", e);
        } catch(IOException e) {
            Log.i(TAG, "sendFile(): IOException", e);
        } catch (NoSuchElementException e) {
            Log.i(TAG, "sendFile(): NoSuchElementException" + e);
        } finally {
            if(conn!= null) {
                conn.disconnect();
                close(writer);
            }
        }
        Log.i(TAG, "sendFile(): success = " + success);

        if (success) {
            FileRW.delete(mContext, filename);
            FileRW.init(mContext, filename);
        }
        return success;
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