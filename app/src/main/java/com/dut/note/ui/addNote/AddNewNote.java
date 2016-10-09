package com.dut.note.ui.addNote;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.blogspot.sontx.libex.DateTime;
import com.dut.note.App;
import com.dut.note.Config;
import com.dut.note.R;
import com.dut.note.bean.Alarm;
import com.dut.note.bean.Check;
import com.dut.note.bean.Note;
import com.dut.note.lib.SharedObject;
import com.dut.note.net.sync.FileInputStreamSync;
import com.dut.note.ui.MainActivity;
import com.dut.note.ui.view.ImageStreamView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;

public class AddNewNote extends AppCompatActivity {
    public static final String SHARED_KEY_NEW_NOTE = "new_note_obj";
    public static final String SHARED_KEY_EDIT_NOTE = "edit_note_obj";
    public static final String SHARED_KEY_UPDATE_NOTE = "update_note_obj";
    public static final String SHARED_KEY_ORIGIN_NOTE = "origin_note_obj";
    private static int RESULT_LOAD_IMG = 1;
    private ListView mListView;
    private NewNoteListAdapter mListAdapter;
    private List<Check> mItemData;
    private EditText mTitleEditText;
    private TextView mDateTextView;
    private TextView mTimeTextView;
    private EditText mContentText;
    private MultiAutoCompleteTextView mTagText;
    private DateTime mDateTime = new DateTime();
    private String mImagePath = null;
    private ImageStreamView mImageStreamView;
    private Note mEditNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_note);
        setIconColor();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mListView = (ListView) findViewById(R.id.new_node_list);
        mTitleEditText = (EditText) findViewById(R.id.add_title_text);
        mDateTextView = (TextView) findViewById(R.id.add_note_set_calendar_text);
        mTimeTextView = (TextView) findViewById(R.id.add_note_set_time_text);
        mContentText = (EditText) findViewById(R.id.add_content_text);
        mImageStreamView = (ImageStreamView) findViewById(R.id.imageview_add_note);

        mTagText = (MultiAutoCompleteTextView) findViewById(R.id.add_note_set_tag_text);
        List<String> tags = (List<String>) SharedObject.getInstance().get(Config.SHARED_ALL_TAGS);
        if (tags == null)
            tags = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line,
                tags);
        mTagText.setAdapter(adapter);
        mTagText.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        Note note = (Note) SharedObject.getInstance().pop(SHARED_KEY_EDIT_NOTE);
        if (note == null) {// create new note
            mItemData = new ArrayList<>();
        } else {// edit current note
            mTitleEditText.setText(note.getTitle());

            mContentText.setText(note.getText());

            if (note.getChecks() != null)
                mItemData = note.getChecks();
            else
                mItemData = new ArrayList<>();

            if (note.getImages() != null) {
                mImagePath = note.getImages().get(0);
                //FileInputStreamSync input = App.getSyncManager().getFileForRead(mImagePath);
                mImageStreamView.setSyncImageResource(mImagePath, App.getSyncManager());
                mImageStreamView.setVisibility(View.VISIBLE);
            }

            if (note.getAlarm() != null) {
                Alarm alarm = note.getAlarm();
                DateTime dt = alarm.getDateTime();
                if (alarm.isAlarm())
                    setTimeTextView(dt.getHours(), dt.getMinutes());
                if (alarm.isCalendar())
                    setDateTextView(dt.getYear(), dt.getMonth(), dt.getDay());
            }

            if (note.getTags() != null) {
                StringBuilder builder = new StringBuilder();
                List<String> _tags = note.getTags();
                for (int i = 0; i < _tags.size(); i++) {
                    if (i < _tags.size() - 1)
                        builder.append(_tags.get(i) + ",");
                    else
                        builder.append(_tags.get(i));
                }
                mTagText.setText(builder.toString());
            }
        }
        mEditNote = note;

        mListAdapter = new NewNoteListAdapter(this, R.layout.new_note_list_custom, mItemData);
        mListView.setAdapter(mListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_new_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection Simplifiabl.0eIfStatement
        switch (id) {
            case android.R.id.home:
                AddNewNote.this.finish();
                return true;
            case R.id.action_image:
                loadImageFromGallery();
                return true;
            case R.id.action_save_new_node:
                shareData();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Load image from gallery
     */
    public void loadImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    private void shareData() {
//        ArrayList<String> jobList = new ArrayList<>();
//        for (int i = 0; i < mItemData.size(); i++) {
//            jobList.add(mItemData.get(i).string);
//        }
//        DataUi data = new DataUi(mTitleEditText.getText().toString(), jobList,
//                mDateTextView.getText().toString(), mTimeTextView.getText().toString());
//        SharedObject.getInstance().set(mTitleEditText.getText().toString()
//                + mDateTextView.getText().toString(), data);
        Note note = new Note();
        note.setTitle(mTitleEditText.getText().toString());
        note.setText(mContentText.getText().toString());

        List<Check> checks = new ArrayList<>(mItemData.size());
        for (int i = 0; i < mItemData.size(); i++) {
            Check check = new Check();
            NewNoteListAdapter.ListItemHolder holder = (NewNoteListAdapter.ListItemHolder) mListView.getChildAt(i).getTag();
            check.setText(holder.text.getText().toString());
            check.setChecked(false);
            checks.add(check);
        }
        note.setChecks(checks);

        if (mImagePath != null) {
            List<String> images = new ArrayList<>(1);
            images.add(mImagePath);
            note.setImages(images);
        }

        Alarm alarm = new Alarm();
        alarm.setDateTime(mDateTime);
        alarm.setLoop(false);
        if (alarm.isAlarm() || alarm.isCalendar())
            note.setAlarm(alarm);

        String st_tags = mTagText.getText().toString();
        if (st_tags.trim().length() > 0) {
            StringTokenizer tokenizer = new StringTokenizer(st_tags, ",");
            List<String> tags = new ArrayList<>();
            while (tokenizer.hasMoreElements()) {
                tags.add(tokenizer.nextToken());
            }
            note.setTags(tags);
        }

        if (note.isValid()) {
            if (mEditNote != null) {
                note.set_id(mEditNote.get_id());
                note.setCreated(mEditNote.getCreated());
                SharedObject.getInstance().set(SHARED_KEY_ORIGIN_NOTE, mEditNote);
                SharedObject.getInstance().set(SHARED_KEY_UPDATE_NOTE, note);
            } else {
                SharedObject.getInstance().set(SHARED_KEY_NEW_NOTE, note);
            }
            Toast.makeText(AddNewNote.this, "Saved!", Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(AddNewNote.this, "You miss some information!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
            // Get the Image from data

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            // Get the cursor
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();

            mImagePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));

            // Set the Image in ImageView after decoding the String
            mImageStreamView.setImageURI(Uri.fromFile(new File(mImagePath)));
            mImageStreamView.setVisibility(View.VISIBLE);

            cursor.close();
        }
    }

    public void createNewCheckbox(View view) {
        Check item = new Check();
        item.setText("");
        item.setChecked(false);
        mItemData.add(item);
        mListAdapter.notifyDataSetChanged();
    }

    public void deleteCheckbox() {
        /*int[] index = NewNoteListAdapter.deleteItemIndex;
        if(index[0] == 1) {
            mItemData.remove(index[1]);
            mListAdapter.notifyDataSetChanged();
            NewNoteListAdapter.deleteItemIndex[0] = 0;
        }*/
    }

    public void setIconColor() {
        int tint = Color.parseColor("#1ab750");
        PorterDuff.Mode mode = PorterDuff.Mode.SRC_ATOP;
        int[] img = {R.drawable.ic_add_alert_black_36dp, R.drawable.ic_add_black_36dp, R.drawable.ic_close_black_24dp};
        for (int i : img) {
            Drawable icon = getResources().getDrawable(i);
            icon.setColorFilter(tint, mode);
        }
    }

    public void setDateTextView(View view) {
        Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int day = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker;
        mDatePicker = new DatePickerDialog(AddNewNote.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                setDateTextView(selectedyear, selectedmonth, selectedday);
            }
        }, year, month, day);
        mDatePicker.setTitle("Select Date");
        mDatePicker.show();
    }

    private void setDateTextView(int selectedyear, int selectedmonth, int selectedday) {
        GregorianCalendar calendar = new GregorianCalendar(selectedyear, selectedmonth, selectedday);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String strDayOfWeek = null;
        switch (dayOfWeek) {
            case 1:
                strDayOfWeek = "Sunday";
                break;
            case 2:
                strDayOfWeek = "Monday";
                break;
            case 3:
                strDayOfWeek = "Tuesday";
                break;
            case 4:
                strDayOfWeek = "Wednesday";
                break;
            case 5:
                strDayOfWeek = "Thursday";
                break;
            case 6:
                strDayOfWeek = "Friday";
                break;
            case 7:
                strDayOfWeek = "Saturday";
                break;
        }
        selectedmonth = selectedmonth + 1;
        mDateTextView.setText(strDayOfWeek + ", " + selectedday + "/" + selectedmonth + "/" + selectedyear);
        mDateTime.setDay(selectedday);
        mDateTime.setMonth(selectedmonth);
        mDateTime.setYear(selectedyear);
        mDateTime.setSeconds(mDateTime.getSeconds() | 0x2);
    }

    public void setTimeTextView(View view) {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(AddNewNote.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                setTimeTextView(selectedHour, selectedMinute);
            }
        }, hour, minute, true);//True 24h, false 12h
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();

    }

    private void setTimeTextView(int selectedHour, int selectedMinute) {
        mTimeTextView.setText(selectedHour + ":" + selectedMinute);
        mDateTime.setHours(selectedHour);
        mDateTime.setMinutes(selectedMinute);
        mDateTime.setSeconds(mDateTime.getSeconds() | 0x1);
    }
}