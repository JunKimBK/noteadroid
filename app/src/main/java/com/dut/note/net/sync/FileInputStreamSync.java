package com.dut.note.net.sync;

import java.io.FileInputStream;
import java.io.IOException;

public abstract class FileInputStreamSync extends FileStreamSync{
    protected FileInputStream mStream;

    public FileInputStream getInputStream(){
        return mStream;
    }

    public void close(){
        if(mStream != null) {
            try {
                mStream.close();
                mStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
