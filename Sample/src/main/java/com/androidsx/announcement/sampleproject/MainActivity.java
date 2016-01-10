package com.androidsx.announcement.sampleproject;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.androidsx.announcement.AnnouncementManager;
import com.androidsx.announcement.model.Announcement;
import com.androidsx.announcements.sampleproject.R;

public class MainActivity extends ActionBarActivity {

    private EditText pushIdEditText;
    private EditText dialogIdEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pushIdEditText = (EditText) findViewById(R.id.push_id_text);
        dialogIdEditText = (EditText) findViewById(R.id.dialog_id_text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void launchPushAnnouncement(View v) {
        String pushId = pushIdEditText.getText().toString();
        if (!"".equals(pushId)) {
            AnnouncementManager.with(this).fetch().launchPushAnnouncement(pushId);
        } else {
            Toast.makeText(this, "Please, introduce a Push Id!", Toast.LENGTH_SHORT).show();
        }
    }

    public void launchDialogAnnouncement(View v) {
        String dialogId = dialogIdEditText.getText().toString();
        if (!"".equals(dialogId)) {
            Announcement dialog = AnnouncementManager.with(this).fetch().getDialogAnnouncementFromId(dialogId);
            if (dialog != null) {
                AnnouncementManager.with(this).fetch().launchDialogAnnouncementIfApply(this, dialogId, getFragmentManager());
            } else {
                Toast.makeText(this, "Dialog '" + dialogId + "' not exists!\nCheck it!", Toast.LENGTH_SHORT).show();
            }
        } else {
            AnnouncementManager.with(this).fetch().launchDialogAnnouncementIfApply(this, getFragmentManager());
        }
    }
}
