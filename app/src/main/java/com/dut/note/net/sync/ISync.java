package com.dut.note.net.sync;

public interface ISync {
    void setInitFolder(String initFolder);
    boolean sync();
    FileInputStreamSync getFileForRead(String fileName);
    FileOutputStreamSync getFileForWrite(String fileName);
    FileInputStreamSync getNewestFileForRead(String fileName);
    FileOutputStreamSync getNewestFileForWrite(String fileName);
    void replaceFile(String srcFile, String desFile);
    void createFile(String fileName, byte[] data);
    void createFile(String fileName, String cacheFile);
    void removeFile(String fileName);
    String getAccountName();
    void logout();
    boolean isLogin();
    void requestLogin();
    void setOnLoginOKListener(OnLoginOKListener listener);
}
