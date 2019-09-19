package com.dynashwet.chatmate.Utils;

import android.os.StrictMode;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Upload {

    public static final String UPLOAD_URL= "http://topautocareindia.com/ChatMate/PostMaster/TestVideo";

    private int serverResponseCode;

    public String uploadVideo(String file, String UID, String caption, String scope) throws IOException {

        String fileName = file;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        File sourceFile = new File(file);
        if (!sourceFile.isFile()) {
            Log.e("Huzza", "Source File Does not exist");
            return null;
        }

        try {

            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(UPLOAD_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("file", fileName);
            dos = new DataOutputStream(conn.getOutputStream());

            //Parameters: UID
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"UID\"" + lineEnd);
            //dos.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
            //dos.writeBytes("Content-Length: " + name.length() + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(UID); // UID is String variable
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //Parameters: File
            dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + fileName + "\"" + lineEnd);
//            dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + fileName + "\"" + lineEnd);

            dos.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            Log.i("Huzza", "Initial .available : " + bytesAvailable);

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //Parameters: Caption
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"Dyna-Caption\"" + lineEnd);
            //dos.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
            //dos.writeBytes("Content-Length: " + name.length() + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(caption); // Caption is String variable
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //Parameter: PrivacyScope
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"Dyna-PrivacyScope\"" + lineEnd);
            //dos.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
            //dos.writeBytes("Content-Length: " + name.length() + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(scope); // PrivacyScope is String variable
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);


            serverResponseCode = conn.getResponseCode();

            fileInputStream.close();
            dos.flush();
            dos.close();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (serverResponseCode == 200) {
            String responseMessage = conn.getResponseMessage();
//            Log.e("Huzza", responseMessage);
            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn
                        .getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
            } catch (IOException ioex) {
            }
            return sb.toString();
        } else {
            return "Could not upload";
        }
    }
}
