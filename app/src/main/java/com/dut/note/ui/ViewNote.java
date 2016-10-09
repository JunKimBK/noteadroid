package com.dut.note.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.dut.note.App;
import com.dut.note.R;
import com.dut.note.Share.ShareIntent;
import com.dut.note.bean.Alarm;
import com.dut.note.bean.Check;
import com.dut.note.bean.Note;
import com.dut.note.lib.SharedObject;
import com.dut.note.net.sync.FileInputStreamSync;
import com.dut.note.ui.addNote.AddNewNote;
import com.dut.note.ui.view.ImageStreamView;

import java.util.List;

public class ViewNote extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    public static final String SHARED_KEY_NOTE = "shared_note";
    public static final String SHARED_KEY_DELETED = "deleted_note";
    public static final String SHARED_KEY_EDITED = "edited_note";
    public static final String SHARED_KEY_UPDATED = "updated_note";

    private ListView mListView;
    private TextView mTitleTextView;
    private TextView mDateTextView;
    private TextView mTimeTextView;
    private CheckAdapter mListAdapter;
    private TextView mTagText;
    private EditText mContentText;
    private ImageStreamView mImageView;
    private FloatingActionButton mShareButton;
    private Note note;
    private boolean[] mLastChecksState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        note = (Note) SharedObject.getInstance().pop(SHARED_KEY_NOTE);

        mListView = (ListView) findViewById(R.id.view_note_list);
        mTitleTextView = (TextView) findViewById(R.id.view_note_title);
        mDateTextView = (TextView) findViewById(R.id.view_note_date);
        mTimeTextView = (TextView) findViewById(R.id.view_note_time);


        mTagText = (TextView) findViewById(R.id.view_note_set_tag_text);
        mContentText = (EditText) findViewById(R.id.view_content_text);
        mImageView = (ImageStreamView) findViewById(R.id.imageview_view_note);

        mTitleTextView.setText(note.getTitle());

        mShareButton = (FloatingActionButton) findViewById(R.id.share_note);
        mShareButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                createShareIntent();
            }
        });

        if (note.getText() == null || note.getText().length() == 0) {
            mContentText.setVisibility(View.GONE);
        } else {
            mContentText.setText(note.getText());
            mContentText.setVisibility(View.VISIBLE);
        }

        if (note.getChecks() != null) {
            List<Check> checks = note.getChecks();
            mLastChecksState = new boolean[checks.size()];
            for (int i = 0; i < mLastChecksState.length; i++) {
                mLastChecksState[i] = checks.get(i).isChecked();
            }
            mListAdapter = new CheckAdapter(this.getApplicationContext(), checks);
            mListAdapter.setOnCheckedChangeListener(this);
            mListView.setVisibility(View.VISIBLE);
            mListView.setAdapter(mListAdapter);
        } else {
            mListView.setVisibility(View.GONE);
        }

        if (note.getImages() != null) {
            String imageName = note.getImages().get(0);
            //FileInputStreamSync input = App.getSyncManager().getFileForRead(imageName);
            mImageView.setSyncImageResource(imageName, App.getSyncManager());
            mImageView.setVisibility(View.VISIBLE);
        } else {
            mImageView.setVisibility(View.GONE);
        }

        if (note.getAlarm() != null) {
            Alarm alarm = note.getAlarm();
            if (alarm.isAlarm()) {
                mTimeTextView.setText(alarm.getDateTime().toTimeString());
            } else {
                View timeLayout = findViewById(R.id.view_note_layout_time);
                timeLayout.setVisibility(View.GONE);
            }
            if (alarm.isCalendar()) {
                mDateTextView.setText(alarm.getDateTime().toLongDateFriendly());
            } else {
                View dateLayout = findViewById(R.id.view_note_date_layout);
                dateLayout.setVisibility(View.GONE);
            }
        } else {
            View timeLayout = findViewById(R.id.view_note_layout_time);
            View dateLayout = findViewById(R.id.view_note_date_layout);
            dateLayout.setVisibility(View.GONE);
            timeLayout.setVisibility(View.GONE);
            mDateTextView.setVisibility(View.GONE);
        }

        if (note.getTags() != null) {
            StringBuilder builder = new StringBuilder();
            List<String> tags = note.getTags();
            for (int i = 0; i < tags.size(); i++) {
                if (i < tags.size() - 1)
                    builder.append(tags.get(i) + ",");
                else
                    builder.append(tags.get(i));
            }
            mTagText.setText(builder.toString());
        } else {
            View tagLayout = findViewById(R.id.view_note_layout_tag);
            tagLayout.setVisibility(View.GONE);
        }

        if (note.getAlarm() == null && note.getTags() == null) {
            View divider2 = findViewById(R.id.view_note_divider2);
            divider2.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_note, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mLastChecksState != null) {
            List<Check> newChecks = note.getChecks();
            boolean changed = false;
            for (int i = 0; i < newChecks.size(); i++) {
                if (mLastChecksState[i] != newChecks.get(i).isChecked()) {
                    changed = true;
                    break;
                }
            }
            if (changed) {
                SharedObject.getInstance().set(SHARED_KEY_UPDATED, note);
            }
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                ViewNote.this.finish();
                break;
            case R.id.action_delete:
                confirmDelete();
                break;
            case R.id.action_edit:
                SharedObject.getInstance().set(AddNewNote.SHARED_KEY_EDIT_NOTE, note);
                startActivity(new Intent(this, AddNewNote.class));
                mLastChecksState = null;
                finish();
                break;
        }
        return true;
    }

    private void confirmDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dlg = builder.create();
        dlg.setTitle("Delete?");
        dlg.setMessage("Delete this note forever!");
        dlg.setButton(AlertDialog.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedObject.getInstance().set(SHARED_KEY_DELETED, note);
                mLastChecksState = null;
                ViewNote.this.finish();
            }
        });
        dlg.setButton(AlertDialog.BUTTON_NEGATIVE, "No no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dlg.dismiss();
            }
        });
        dlg.show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        for (int i = 0; i < mListView.getChildCount(); i++) {
            if (mListView.getChildAt(i).equals(buttonView.getParent())) {
                note.getChecks().get(i).setChecked(isChecked);
                break;
            }
        }
    }

    private class CheckAdapter extends BaseAdapter {
        private List<Check> checks;
        private LayoutInflater inflater;
        private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = null;

        public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener) {
            mOnCheckedChangeListener = listener;
        }

        public CheckAdapter(Context context, List<Check> checks) {
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            this.checks = checks;
        }

        @Override
        public int getCount() {
            return checks.size();
        }

        @Override
        public Object getItem(int position) {
            return checks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.layout_check_list_item, null);
                CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.check_list_item_checkbox);
                convertView.setTag(checkbox);
            }
            final Check check = checks.get(position);
            CheckBox checkbox = (CheckBox) convertView.getTag();
            checkbox.setOnCheckedChangeListener(mOnCheckedChangeListener);
            checkbox.setText(check.getText());
            checkbox.setChecked(check.isChecked());
            return convertView;
        }
    }

    public void createShareIntent(){
        ShareIntent shareIntent = new ShareIntent();
        shareIntent.share(note);
        startActivity(Intent.createChooser(shareIntent.getShare(), "Send to"));
    }
}
