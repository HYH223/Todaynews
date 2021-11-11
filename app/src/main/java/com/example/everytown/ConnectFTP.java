package com.example.everytown;


import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ConnectFTP {
    private final String TAG = "Connect FTP";
    public FTPClient mFTPClient = null;

    public ConnectFTP() {
        mFTPClient = new FTPClient();
    }

    public boolean ftpConnect(String host, String username, String password, int port) {
        boolean result = false;
        try {
            mFTPClient.connect(host, port);

            if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
                result = mFTPClient.login(username, password);
                mFTPClient.enterLocalPassiveMode();
            }
        } catch (Exception e) {
            Log.d(TAG, "Couldn't connect to host");
        }
        return result;
    }

    public boolean ftpDisconnect() {
        boolean result = false;
        try {
            mFTPClient.logout();
            mFTPClient.disconnect();
            result = true;
        } catch (Exception e) {
            Log.d(TAG, "Failed to disconnect with server");
        }
        return result;
    }

    public boolean ftpUploadFile(String srcFilePath, String desFileName) {
        boolean result = false;
        try {
            mFTPClient.enterLocalPassiveMode();
            mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);

            FileInputStream fis = new FileInputStream(srcFilePath);
            result = mFTPClient.storeFile(desFileName, fis);

            fis.close();
        } catch (Exception e) {
            Log.d(TAG, "Couldn't upload the file");
        }
        return result;
    }

    public InputStream retrieveFileStream(String s) {
        try {

            mFTPClient.enterLocalPassiveMode();
            mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
            mFTPClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
            return mFTPClient.retrieveFileStream(s);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
