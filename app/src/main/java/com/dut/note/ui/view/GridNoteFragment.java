package com.dut.note.ui.view;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.dut.note.App;
import com.dut.note.R;
import com.dut.note.bean.Note;
import com.dut.note.lib.SharedObject;
import com.dut.note.ui.ViewNote;
import com.dut.note.ui.adapter.TileAdapter;

import java.util.ArrayList;
import java.util.List;

public class GridNoteFragment extends ContentFragment implements View.OnClickListener {
    private List<Note> mNotes;
    private TileAdapter mAdapter;
    private GridView mGridView;
    private OnViewNoteListener mOnViewNoteListener;

    public void setOnViewNoteListener(OnViewNoteListener listener) {
        mOnViewNoteListener = listener;
    }

    public void addNote(Note note) {
        if (mNotes == null) {
            mNotes = new ArrayList<>();
            mNotes.add(note);
            mAdapter = new TileAdapter(mActivity, mNotes);
            mAdapter.setOnClickListener(this);
            mGridView.setAdapter(mAdapter);
        } else {
            mNotes.add(0, note);
            mAdapter.notifyDataSetChanged();
        }
    }

    public void removeNote(int id) {
        if (mAdapter != null) {
            for (Note note : mNotes) {
                if (note.get_id() == id) {
                    mNotes.remove(note);
                    break;
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    public void refreshNotesContent(Note updateNote) {
        for (int i = 0; i < mNotes.size(); i++) {
            if (mNotes.get(i).get_id() == updateNote.get_id()) {
                mNotes.set(i, updateNote);
                break;
            }
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
            mGridView.invalidateViews();
        }
    }

    @Override
    protected void runItOnBackground() {
        if (mNotes == null)
            mNotes = App.getDbManager().getListNotes(null);
    }

    @Override
    protected void runItWhenDone() {
        if (mNotes != null) {
            mAdapter = new TileAdapter(mActivity, mNotes);
            mAdapter.setOnClickListener(this);
            mGridView.setAdapter(mAdapter);
        }
    }

    @Override
    protected void onCreatedView(View root) {
        super.onCreatedView(root);
        mGridView = (GridView) root.findViewById(R.id.fragment_notes_gv_list);
        if (App.getDbManager() != null)
            startWorker();
    }

    public GridNoteFragment() {
        super(R.layout.layout_fragment_notes, App.getAppName());
    }

    public void setNotes(List<Note> notes) {
        this.mNotes = notes;
    }

    @Override
    public void onClick(View v) {
        TileAdapter.ViewHolder holder = (TileAdapter.ViewHolder) v.getTag();
        int position = holder.position;
        Note note = mNotes.get(position);
        SharedObject.getInstance().set(ViewNote.SHARED_KEY_NOTE, note);
        getActivity().startActivity(new Intent(getActivity(), ViewNote.class));
        if (mOnViewNoteListener != null)
            mOnViewNoteListener.onViewNote();
    }

    public interface OnViewNoteListener {
        void onViewNote();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ImageStreamView.disposeStaticResources();
    }
}
