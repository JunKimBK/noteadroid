package com.dut.note.ui;

import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dut.note.R;
import com.dut.note.bean.Note;
import com.dut.note.lib.SharedObject;
import com.dut.note.ui.view.ContentFragment;
import com.dut.note.ui.view.GridNoteFragment;

import java.util.List;

public class SearchResultActivity extends AppCompatActivity implements GridNoteFragment.OnViewNoteListener {
    public static final String INTENT_NOTES_RESULT = "notes_result";

    private void navigateToFragment(ContentFragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.search_result_content, fragment).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        List<Note> notes = (List<Note>) SharedObject.getInstance().pop(INTENT_NOTES_RESULT);
        GridNoteFragment fragment = new GridNoteFragment();
        fragment.setNotes(notes);
        fragment.setOnViewNoteListener(this);
        navigateToFragment(fragment);
    }

    @Override
    public void onViewNote() {
        finish();
    }
}
