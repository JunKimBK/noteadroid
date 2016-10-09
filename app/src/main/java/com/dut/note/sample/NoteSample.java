package com.dut.note.sample;

import com.dut.note.bean.Check;
import com.dut.note.bean.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteSample {
    public static List<Note> getNotes() {
        List<Note> notes = new ArrayList<>();

        // text only
        Note note1 = new Note();
        note1.setTitle("note 1");
        note1.setText("If you're looking for one box to watch TV and streaming video -- including ultra high-def content -- all over your h.");

        // text and image
        Note note2 = new Note();
        note2.setTitle("note 2");
        note2.setText("If you're looking for one box to watch TV and streaming video -- including ultra high-def content -- all over your home (and soon away from it) the Bolt is here. But, as always, the costs of ownership will keep some buyers away.");
        ArrayList<String> img1 = new ArrayList<>();
        img1.add("sample1.png");
        note2.setImages(img1);

        // text and check
        Note note3 = new Note();
        note3.setTitle("note 3");
        note3.setText("There are a ton of new movies and TV shows hitting Netflix in October. Also, the beginning of October sees lots of titles going offline.");
        Check check1 = new Check();
        check1.setText("task 1");
        Check check2 = new Check();
        check2.setText("task 2");
        Check check3 = new Check();
        check3.setText("task 3");
        check1.setChecked(true);
        Check check4 = new Check();
        check4.setText("task 4");
        List<Check> checks = new ArrayList<>();
        checks.add(check1);
        checks.add(check2);
        checks.add(check3);
        checks.add(check4);
        note3.setChecks(checks);

        // text, check and image
        Note note4 = new Note();
        note4.setTitle("note 4");
        note4.setText("The streaming service will also pick up some awesome comic-book content with \"Batman Begins,\" the first season of \"The Flash\" and the the third season of \"Arrow.\" On the flip side");
        note4.setChecks(checks);
        ArrayList<String> img2 = new ArrayList<>();
        img2.add("sample2.png");
        note4.setChecks(checks);
        note4.setImages(img2);

        notes.add(note1);
        notes.add(note2);
        notes.add(note3);
        notes.add(note4);
        return notes;
    }
}
