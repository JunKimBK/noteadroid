package com.dut.note.net.sync;

import android.os.DropBoxManager;
import android.util.Log;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileStatus;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * this class provide methods to work with dropbox service, you can read,
 * write, create, delete and sync all files you want from your app folder
 * in dropbox service
 */
public abstract class DropboxSync extends DropboxLinker implements ISync {
    private OnLoginOKListener mOnLoginOKListener = null;

    protected class DropboxFileInputStream extends FileInputStreamSync {
        private DbxFile mDbxFile;

        @Override
        public boolean isLatest() {
            try {
                return mDbxFile.getSyncStatus().isLatest;
            } catch (DbxException e) {
                e.printStackTrace();
                return super.isLatest();
            }
        }

        private DropboxFileInputStream(DbxFile dbxFile){
            mDbxFile = dbxFile;
            try {
                mStream = dbxFile.getReadStream();
                Log.d(TAG, String.format("Open %s for read", dbxFile.getPath().getName()));
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Can't get read stream from dropbox file");
            }
        }
        @Override
        public void close() {
            super.close();
            Log.d(TAG, String.format("Close %s after read", mDbxFile.getPath().getName()));
            mDbxFile.close();
        }
    }

    protected class DropboxFileOutputStream extends FileOutputStreamSync {
        private DbxFile mDbxFile;

        @Override
        public boolean isLatest() {
            try {
                return mDbxFile.getSyncStatus().isLatest;
            } catch (DbxException e) {
                e.printStackTrace();
                return super.isLatest();
            }
        }

        private DropboxFileOutputStream(DbxFile dbxFile){
            mDbxFile = dbxFile;
            try {
                mStream = dbxFile.getWriteStream();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Can't get write stream from dropbox file");
            }
        }
        @Override
        public void close() {
            super.close();
            mDbxFile.close();
        }
    }

    private DbxPath getDbxPath(String fileName){
        Log.d("Dropbox", "get dbxpath of " + fileName);
        try {
            return new DbxPath(DbxPath.ROOT, fileName);
        } catch (DbxPath.InvalidPathException ex) {
            return null;
        }
    }

    private DbxFileSystem getDbxFileSystem() {
        DbxAccount account = mDbxAcctMgr.getLinkedAccount();
        if (account != null) {
            try {
                return DbxFileSystem.forAccount(account);
            } catch (DbxException.Unauthorized e) {
                Log.d(TAG, "Account was unlinked asynchronously from server.");
                return null;
            }
        }
        return null;
    }

    @Override
    public void setInitFolder(String initFolder) {
        //mInitFolder = initFolder;
    }

    /**
     * this method must be called in background thread unless
     * it'll crash in some case
     * @return true if sync is successful
     */
    @Override
    public boolean sync(){
        try {
            DbxFileSystem dbxFileSystem = getDbxFileSystem();
            if(dbxFileSystem != null){
                if(dbxFileSystem.hasSynced()) {
                    List<DbxFileInfo> infos = dbxFileSystem.listFolder(DbxPath.ROOT);
                    for (DbxFileInfo info : infos) {
                        DbxFile file = dbxFileSystem.open(info.path);
                        InputStream in = file.getReadStream();
                        in.close();
                        file.close();
                    }
                }
                else
                    dbxFileSystem.awaitFirstSync();
            }
            return true;
        } catch (DbxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Error when sync data from dropbox service.");
        return false;
    }

    /**
     * get a instance of FileInputStreamSync to read data from
     * given file
     * @param fileName file in dropbox which you want to get FileInputStreamSync
     *                 instance
     * @return FileInputStreamSync instance to read data
     */
    @Override
    public FileInputStreamSync getFileForRead(String fileName){
        DbxPath path = getDbxPath(fileName);
        if (path == null)
            return null;
        try {
            DbxFileSystem dbxFileSystem = getDbxFileSystem();
            if(dbxFileSystem != null) {
                if (dbxFileSystem.exists(path))
                    return new DropboxFileInputStream(dbxFileSystem.open(path));
                else
                    return null;
            }
            return null;
        } catch (DbxException e) {
            e.printStackTrace();
            Log.d(TAG, "File not found or can't access to dropbox service");
            return null;
        }
    }

    /**
     * get a instance of FileOutputStreamSync to write new data
     * to file in dropbox
     * @param fileName file in dropbox which you want to get FileOutputStreamSync
     *                 instance
     * @return FileOutputStreamSync instance to write new data
     */
    @Override
    public FileOutputStreamSync getFileForWrite(String fileName){
        DbxPath path = getDbxPath(fileName);
        if (path == null)
            return null;
        try {
            DbxFileSystem dbxFileSystem = getDbxFileSystem();
            if(dbxFileSystem != null) {
                if (dbxFileSystem.exists(path))
                    return new DropboxFileOutputStream(dbxFileSystem.open(path));
                else
                    return null;
            }
            return null;
        } catch (DbxException e) {
            e.printStackTrace();
            Log.d(TAG, "File not found or can't access to dropbox service");
            return null;
        }
    }

    /**
     * try get newest version of @fileName from dropbox then return
     * a FileInputStreamSync instance
     * @param fileName a file in dropbox
     * @return FileInputStreamSync instance which readable
     */
    @Override
    public FileInputStreamSync getNewestFileForRead(String fileName) {
        DbxPath path = getDbxPath(fileName);
        DbxFileSystem dbxFileSystem = getDbxFileSystem();
        if(dbxFileSystem == null)
            return null;
        try {
            DbxFile file = dbxFileSystem.open(path);
            DbxFileStatus fileStatus = file.getNewerStatus();
            if(fileStatus != null && !fileStatus.isLatest) {
                if(fileStatus.isCached) {
                    do {
                        file.update();
                        fileStatus = file.getNewerStatus();
                        while (fileStatus.pending == DbxFileStatus.PendingOperation.DOWNLOAD) {
                            Thread.sleep(500);
                        }
                    } while(fileStatus.isLatest);
                }
            }
            return new DropboxFileInputStream(file);
        } catch (DbxException e) {
            e.printStackTrace();
            Log.d(TAG, "File not found or can't access to dropbox service");
            return null;
        } catch(InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * try get newest version of @fileName from dropbox then return
     * a FileOutputStreamSync instance
     * @param fileName a file in dropbox
     * @return FileOutputStreamSync instance which writable
     */
    @Override
    public FileOutputStreamSync getNewestFileForWrite(String fileName) {
        DbxPath path = getDbxPath(fileName);
        DbxFileSystem dbxFileSystem = getDbxFileSystem();
        if(dbxFileSystem == null)
            return null;
        try {
            DbxFile file = dbxFileSystem.open(path);
            DbxFileStatus fileStatus = file.getNewerStatus();
            if(fileStatus != null && !fileStatus.isLatest) {
                if(fileStatus.isCached) {
                    do {
                        file.update();
                        fileStatus = file.getNewerStatus();
                        while (fileStatus.pending == DbxFileStatus.PendingOperation.DOWNLOAD) {
                            Thread.sleep(500);
                        }
                    } while(fileStatus.isLatest);
                }
            }
            return new DropboxFileOutputStream(file);
        } catch (DbxException e) {
            e.printStackTrace();
            Log.d(TAG, "File not found or can't access to dropbox service");
            return null;
        } catch(InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * replace a file in dropbox by another file
     * @param srcFile a file in dropbox
     * @param desFile a file in local
     */
    @Override
    public void replaceFile(String srcFile, String desFile){
        DbxPath path = getDbxPath(srcFile);
        if (path == null)
            return;
        try {
            DbxFileSystem dbxFileSystem = getDbxFileSystem();
            if(dbxFileSystem != null) {
                if (dbxFileSystem.exists(path)) {
                    File _desFile = new File(desFile);
                    DbxFile _srcFile = dbxFileSystem.open(path);
                    try {
                        _srcFile.writeFromExistingFile(_desFile, false);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "Can't replace dropbox file content by a local file");
                    } finally {
                        _srcFile.close();
                    }
                }
            }
        } catch (DbxException e) {
            e.printStackTrace();
            Log.d(TAG, "File not found or can't access to dropbox service");
        }
    }

    /**
     * create a new file in dropbox if necessary, then set @data
     * to this file
     * @param fileName a file in dropbox which will be created
     *                 or modified
     * @param data a bytes data will be set for file
     */
    @Override
    public void createFile(String fileName, byte[] data){
        DbxPath path = getDbxPath(fileName);
        try {
            DbxFile file;
            OutputStream stream;
            DbxFileSystem dbxFileSystem = getDbxFileSystem();
            if(dbxFileSystem != null) {
                if (dbxFileSystem.exists(path))
                    file = dbxFileSystem.open(path);
                else
                    file = dbxFileSystem.create(path);
                stream = file.getWriteStream();
                stream.write(data);
                stream.close();
                file.close();
            }
        } catch (DbxException e) {
            e.printStackTrace();
            Log.d(TAG, "Error while access to dropbox service");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Error when access to stream");
        }
    }

    /**
     * create a new file in dropbox if necessary, then set new data
     * from @cacheFile to this file which in dropbox
     * @param fileName a file in dropbox which will be created
     *                 or modified
     * @param cacheFile a file in local which will be get data
     *                  and set for @fileName
     */
    @Override
    public void createFile(String fileName, String cacheFile){
        DbxPath path = getDbxPath(fileName);
        try {
            DbxFile file;
            DbxFileSystem dbxFileSystem = getDbxFileSystem();
            if(dbxFileSystem != null) {
                if (dbxFileSystem.exists(path))
                    file = dbxFileSystem.open(path);
                else
                    file = dbxFileSystem.create(path);
                file.writeFromExistingFile(new File(cacheFile), false);
                file.close();
            }
        } catch (DbxException e) {
            e.printStackTrace();
            Log.d(TAG, "Error while access to dropbox service");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Error when access to stream");
        }
    }

    /**
     * delete @fileName from dropbox
     * @param fileName a file name which will be deleted
     */
    @Override
    public void removeFile(String fileName){
        DbxPath path = getDbxPath(fileName);
        if (path == null)
            return;
        try {
            DbxFileSystem dbxFileSystem = getDbxFileSystem();
            if(dbxFileSystem != null) {
                if (dbxFileSystem.exists(path))
                    dbxFileSystem.delete(path);
            }
        } catch (DbxException e) {
            e.printStackTrace();
            Log.d(TAG, "Error when delete file from dropbox");
        }
    }

    @Override
    public String getAccountName() {
        if(!mDbxAcctMgr.hasLinkedAccount())
            return "Unknown";
        return mDbxAcctMgr.getLinkedAccount().getAccountInfo().userName;
    }

    @Override
    public void logout() {
        if(mDbxAcctMgr.hasLinkedAccount())
            mDbxAcctMgr.unlink();
    }

    @Override
    public boolean isLogin() {
        return mDbxAcctMgr.hasLinkedAccount();
    }

    @Override
    public void requestLogin() {
        startLink();
    }

    @Override
    public void setOnLoginOKListener(OnLoginOKListener listener) {
        mOnLoginOKListener = listener;
    }

    @Override
    protected void onLinked() {
        if(mOnLoginOKListener != null)
            mOnLoginOKListener.onLoginOK();
    }
}
