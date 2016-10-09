package com.dut.note.dao;

import com.dut.note.bean.Note;

import java.util.List;

public interface IDatabase {
    void insertNote(Note note);
    void updateNote(Note note);
    void removeNote(int noteId);
    void removeNotes(List<Integer> noteIds);
    List<Note> getListNotes(String keyword);
    List<String> getAllTags();
    void addAccount(String userName, String password);
    boolean checkLogin(String userName, String password);
    void changePassword(String userName, String oldPassword, String newPassword);
    /* for color */
    void setDoneColor(String color);
    String getDoneColor();
    void setRemainColor(String color);
    String getRemainColor();
    void addHistory(String newHistory);
    List<String> getHistory();
}
