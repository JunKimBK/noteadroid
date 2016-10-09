package com.dut.note.sample;

import com.dut.note.net.sync.FileInputStreamSync;
import com.dut.note.net.sync.FileOutputStreamSync;
import com.dut.note.net.sync.ISync;
import com.dut.note.net.sync.OnLoginOKListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by noem on 10/10/2015.
 */
public class SyncMgrSample implements ISync {
    @Override
    public void setInitFolder(String initFolder) {

    }

    @Override
    public boolean sync() {
        return false;
    }

    @Override
    public FileInputStreamSync getFileForRead(String fileName) {
        final FileInputStreamSync inputStreamSync = new FileInputStreamSync() {
            private FileInputStream inputStream;

            @Override
            public void close() {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public FileInputStream getInputStream() {
                try {
                    inputStream = new FileInputStream(new File("/sdcard/Download/me.jpg"));
                } catch (FileNotFoundException e) {
                    inputStream = null;
                }
                return inputStream;
            }
        };
        return inputStreamSync;
    }

    @Override
    public FileOutputStreamSync getFileForWrite(String fileName) {
        return null;
    }

    @Override
    public FileInputStreamSync getNewestFileForRead(String fileName) {
        return null;
    }

    @Override
    public FileOutputStreamSync getNewestFileForWrite(String fileName) {
        return null;
    }

    @Override
    public void replaceFile(String srcFile, String desFile) {

    }

    @Override
    public void createFile(String fileName, byte[] data) {

    }

    @Override
    public void createFile(String fileName, String cacheFile) {

    }

    @Override
    public void removeFile(String fileName) {

    }

    @Override
    public String getAccountName() {
        return null;
    }

    @Override
    public void logout() {

    }

    @Override
    public boolean isLogin() {
        return false;
    }

    @Override
    public void requestLogin() {

    }

    @Override
    public void setOnLoginOKListener(OnLoginOKListener listener) {

    }
}
