package com.dut.note.net.sync;

import com.dut.note.lib.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileCache {
    private static final String mCacheFolder = "cache";
    private ISync mSyncManager;
    private String mFileName;
    private File mFile;
    private boolean mIsPushPending;

    // a real path to this file cache
    public String getPath() {
        if (mFile.exists())
            return mFile.getPath();
        return null;
    }

    // this method must be called first to initialize cache file
    // content so you can use other method from this class
    public boolean init() {
        String newName = FileUtil.getRandomFileName();
        String filePath = FileUtil.combine(FileUtil.mLocalFolder, mCacheFolder);
        filePath = FileUtil.combine(filePath, newName);

        FileOutputStream outputStream = null;
        FileInputStreamSync inputStreamSync = null;

        mFile = new File(filePath);
        try {
            mFile.createNewFile();
            outputStream = new FileOutputStream(mFile);
            inputStreamSync = mSyncManager.getFileForRead(mFileName);
            if (inputStreamSync != null) {
                FileInputStream inputStream = inputStreamSync.getInputStream();
                FileUtil.writeAllBytes(inputStream, outputStream);
            } else {
                outputStream.close();
                mFile.delete();
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (inputStreamSync != null)
                inputStreamSync.close();
        }
        return true;
    }

    // file cache should be deleted when it not use
    public void delete() {
        mFile.delete();
    }

    // call this method when you change file content
    // so it can update to service by call updateToService method
    public void setModified() {
        mIsPushPending = true;
    }

    // when cache file content has been modified, then call this
    // method will update current cache file content to cloud server
    public void updateToService() {
        if (!mIsPushPending || !mFile.exists())
            return;
        mIsPushPending = false;
        mSyncManager.replaceFile(mFileName, mFile.getPath());
    }

    // when the same file in cloud server has a newer version than
    // this cache file, then this method will update current cache file
    // content by the same file in cloud server
    public void updateFromService() {
        if (!mFile.exists())
            return;
        FileInputStreamSync inputStreamSync = mSyncManager.getNewestFileForRead(mFileName);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(mFile);
            FileUtil.writeAllBytes(inputStreamSync.getInputStream(), outputStream);
            mIsPushPending = false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStreamSync.close();
        }
    }

    public FileCache(ISync sync, String fileName) {
        mSyncManager = sync;
        mFileName = fileName;
        mIsPushPending = false;
    }

    static {
        File cacheFolder = new File(FileUtil.combine(FileUtil.mLocalFolder, mCacheFolder));
        if (!cacheFolder.exists())
            cacheFolder.mkdir();
    }
}
