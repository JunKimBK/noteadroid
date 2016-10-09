package com.dut.note.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.dut.note.App;
import com.dut.note.R;
import com.dut.note.bean.Note;
import com.dut.note.dao.IDatabase;
import com.dut.note.dao.SQLiteDB;
import com.dut.note.lib.FileUtil;
import com.dut.note.lib.SharedObject;
import com.dut.note.lib.alarm.AlarmMaker;
import com.dut.note.net.sync.DropboxSync;
import com.dut.note.net.sync.FileCache;
import com.dut.note.net.sync.FileInputStreamSync;
import com.dut.note.net.sync.ISync;
import com.dut.note.ui.addNote.AddNewNote;
import com.dut.note.ui.navigation.DrawerListAdapter;
import com.dut.note.ui.view.AboutFragment;
import com.dut.note.ui.view.AccountFragment;
import com.dut.note.ui.view.ContentFragment;
import com.dut.note.ui.view.GridNoteFragment;
import com.dut.note.ui.view.HelpFragment;
import com.dut.note.ui.view.ImageStreamView;
import com.dut.note.ui.view.SettingFragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends DropboxSync implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {
    private static MainActivity mInstance = null;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerListener;
    private int mTaskId = -1;
    private ContentFragment currentFragment;
    private SearchView searchView;
    private boolean stayHome = true;

    public static MainActivity getInstance() {
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);
        mInstance = this;
        App.setSyncMgr(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerListener = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_menu_white_48dp, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                timeToLoad();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerListener);

        mDrawerList = (ListView) findViewById(R.id.drawerList);
        mDrawerList.setAdapter(new DrawerListAdapter(this));
        mDrawerList.setOnItemClickListener(this);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        loadNotes();
    }

    private boolean needReload = false;
    private boolean chooseTask = false;
    private boolean firstLoad = false;

    @Override
    protected void onStop() {
//        if (!chooseTask && App.getDbManager() != null)
//            needReload = true;
        super.onStop();
    }

    private void loadNotes() {
        mTaskId = 0;
        timeToLoad();
        stayHome = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        chooseTask = false;

//        if (!App.getSyncManager().isLogin()) {
//            mTaskId = 0;
//            timeToLoad();
//            return;
//        }
        if (firstLoad) {
            firstLoad = false;
            loadNotes();
            return;
        }

        if (needReload) {
            needReload = false;
            loadNotes();
            return;
        }

        AlarmMaker maker = new AlarmMaker(this);

        Object newNote = SharedObject.getInstance().pop(AddNewNote.SHARED_KEY_NEW_NOTE);
        if (newNote != null) {
            Note note = (Note) newNote;
            // upload image if necessary
            if (note.getImages() != null && note.getImages().size() > 0) {
                String imageName = note.getImages().get(0);
                String uploadName = FileUtil.getRandomFileName();
                App.getSyncManager().createFile(uploadName, imageName);
                note.getImages().set(0, uploadName);
            }
            // save to database
            App.getDbManager().insertNote(note);

            // reload
            if (currentFragment instanceof GridNoteFragment) {
                ((GridNoteFragment) currentFragment).addNote(note);
            } else {
                navigateToFragment(new GridNoteFragment());
            }

            navigateToFragment(new GridNoteFragment());

            if (note.getAlarm() != null && note.getAlarm().isAlarm())
                maker.setAlarm(note);
        }

        Object deleteNote = SharedObject.getInstance().pop(ViewNote.SHARED_KEY_DELETED);
        if (deleteNote != null) {
            Note note = (Note) deleteNote;
            // delete from cloud if necessary
            if (note.getImages() != null && note.getImages().size() > 0) {
                String uploadName = note.getImages().get(0);
                App.getSyncManager().removeFile(uploadName);
            }
            // delete from database
            App.getDbManager().removeNote(note.get_id());

            // reload
            if (currentFragment instanceof GridNoteFragment) {
                ((GridNoteFragment) currentFragment).removeNote(note.get_id());
            } else {
                navigateToFragment(new GridNoteFragment());
            }

            if (note.getAlarm() != null && note.getAlarm().isAlarm())
                maker.setAlarm(note);
        }

        Object updateCheckNote = SharedObject.getInstance().pop(ViewNote.SHARED_KEY_UPDATED);
        if (updateCheckNote != null) {
            Note note = (Note) updateCheckNote;
            // update to database
            App.getDbManager().updateNote(note);
            // reload
            if (currentFragment instanceof GridNoteFragment) {
                ((GridNoteFragment) currentFragment).refreshNotesContent(note);
            } else {
                navigateToFragment(new GridNoteFragment());
            }
        }

        Object updateNote = SharedObject.getInstance().pop(AddNewNote.SHARED_KEY_UPDATE_NOTE);
        if (updateNote != null) {
            Note note = (Note) updateNote;
            Note origin = (Note) SharedObject.getInstance().pop(AddNewNote.SHARED_KEY_ORIGIN_NOTE);
            note.set_id(origin.get_id());
            boolean reloadImage = false;
            // user change image
            if (note.getImages() != null && origin.getImages() != null) {
                reloadImage = true;
                String current = note.getImages().get(0);
                String last = origin.getImages().get(0);
                String uploadName = FileUtil.getRandomFileName();
                note.getImages().set(0, uploadName);
                if (!current.equals(last)) {
                    // update image
                    App.getSyncManager().removeFile(last);
                    App.getSyncManager().createFile(uploadName, current);
                }
            // user add new image
            } else if (note.getImages() != null && (origin.getImages() == null || origin.getImages().size() == 0)) {
                reloadImage = true;
                String newImage = note.getImages().get(0);
                String uploadName = FileUtil.getRandomFileName();
                App.getSyncManager().createFile(uploadName, newImage);
                note.getImages().set(0, uploadName);
            // user delete image(not implement yet)
            } else if (note.getImages() == null && (origin.getImages() != null && origin.getImages().size() > 0)) {
                reloadImage = true;
                String lastName = origin.getImages().get(0);
                note.setImages(null);
                App.getSyncManager().removeFile(lastName);
            }
            // update to database
            App.getDbManager().updateNote(note);
            // reload
            if (!reloadImage && (currentFragment instanceof GridNoteFragment)) {
                ((GridNoteFragment) currentFragment).refreshNotesContent(note);
            } else {
                ImageStreamView.disposeStaticResources();
                navigateToFragment(new GridNoteFragment());
            }

            if (note.getAlarm() != null && note.getAlarm().isAlarm())
                maker.setAlarm(note);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerListener.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerListener.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean value = super.onOptionsItemSelected(item);
        if (mDrawerListener.onOptionsItemSelected(item)) {
            value = true;
        }
        switch (item.getItemId()) {
            case R.id.action_add:
                chooseTask = true;
                startAddNewNote();
                value = true;
                break;
        }

        return value;
    }

    /**
     * Start "Add New Note" acivity
     */
    public void startAddNewNote() {
        Intent activityIntent = new Intent(this, AddNewNote.class);
        startActivity(activityIntent);
    }

    private void timeToLoad() {
        ContentFragment fragment = null;
        stayHome = mTaskId == 0;
        switch (mTaskId) {
            case 0:
                fragment = new GridNoteFragment();
                break;
            case 1:
                fragment = new AccountFragment();
                break;
            case 2:
                fragment = new SettingFragment();
                break;
            case 3:
                fragment = new HelpFragment();
                break;
            case 4:
                fragment = new AboutFragment();
                break;
            case 5:
                confirmExit();
                break;
        }
        if(fragment != null)
            navigateToFragment(fragment);
        mTaskId = -1;
    }

    private void confirmExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dlg = builder.create();
        dlg.setTitle("Exit?");
        dlg.setMessage("Exit note app, see you again!");
        dlg.setButton(AlertDialog.BUTTON_POSITIVE, "Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.finish();
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
    public void onBackPressed() {
        if (!stayHome) {
            loadNotes();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
        mDrawerLayout.closeDrawers();
        mTaskId = position;
    }

    public void navigateToFragment(ContentFragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.drawerFrame, fragment).commit();
        currentFragment = fragment;
    }

    public void selectItem(int position) {
        mDrawerList.setItemChecked(position, true);
    }

    // Ham thay doi title khi bam vao selectItem ListView cua Drawer
    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private void setupAlarm() {
        List<Note> notes = App.getDbManager().getListNotes(null);
        if (notes != null) {
            AlarmMaker maker = new AlarmMaker(this);
            for (Note note : notes) {
                if (note.getAlarm() != null && note.getAlarm().isAlarm())
                    maker.setAlarm(note);
            }
        }
    }

    @Override
    protected void onLinked() {
        if (App.getDbManager() != null)
            return;
        //final ProcessDialog dialog = new ProcessDialog(this);
        //dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
                    }
                });
                final String TAG = "LOGIN";
                Log.d(TAG, "first sync....");
                sync();
                Log.d(TAG, "finish first sync");

                IDatabase db;
                ISync sync = MainActivity.this;

                Log.d(TAG, "init database...");
                // init db file
                FileInputStreamSync dbFile = sync.getFileForRead(App.DB_NAME);
                if (dbFile == null) {
                    // create db file in cloud
                    try {
                        Log.d(TAG, "database not exists! export empty db file and upload...");
                        InputStream in =  MainActivity.this.getAssets().open("note.db");
                        sync.createFile(App.DB_NAME, FileUtil.readAllBytes(in));
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    dbFile.close();
                }
                Log.d(TAG, "open database");
                // create db file cache
                FileCache fileCache = new FileCache(sync, App.DB_NAME);
                db = new SQLiteDB(fileCache);

                Log.d(TAG, "finish init database.");

                App.setDbMgr(db);

                while (WelcomeActivity.instance == null) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        break;
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupAlarm();
                        firstLoad = true;
                        if (WelcomeActivity.instance != null) {
                            Activity activity = WelcomeActivity.instance.get();
                            if (activity != null)
                                activity.finish();
                            WelcomeActivity.instance = null;
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        query = query.trim();
        if (query.length() > 0) {
            List<Note> notes = App.getDbManager().getListNotes(query);
            if (notes == null || notes.size() == 0) {
                Toast.makeText(MainActivity.this, "Not found!", Toast.LENGTH_SHORT).show();
            } else {
                SharedObject.getInstance().set(SearchResultActivity.INTENT_NOTES_RESULT, notes);
                startActivity(new Intent(this, SearchResultActivity.class));
            }
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
