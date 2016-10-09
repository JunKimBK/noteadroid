package com.dut.note.ui.navigation;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


/**
 * Created by VietHoang on 30/09/2015.
 */
public class DrawerItemClickListener implements ListView.OnItemClickListener {

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
    /*private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private FragmentManager fragmentManager;

    public void DrawerItemClickListener(){
        mNavigationDrawerItemTitles = getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        fragmentManager = getFragmentManager();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
    }

    private void selectItem(int position) {

        Fragment fragment = null;
        switch (position) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            getActionBar().setTitle(mNavigationDrawerItemTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);

        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }
*/

}