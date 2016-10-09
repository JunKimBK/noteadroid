package com.dut.note.net.sync;

import java.io.FileOutputStream;
import java.io.IOException;

public abstract class FileOutputStreamSync extends FileStreamSync{
    protected FileOutputStream mStream;

    public FileOutputStream getOutputStream(){
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
