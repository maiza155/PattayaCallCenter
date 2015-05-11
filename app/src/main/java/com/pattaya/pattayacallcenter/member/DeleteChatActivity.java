package com.pattaya.pattayacallcenter.member;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.pattaya.pattayacallcenter.Data.LastMessageData;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.chat.DatabaseChatHelper;
import com.pattaya.pattayacallcenter.member.Adapter.AdapterListDeleteChat;

import java.util.ArrayList;

public class DeleteChatActivity extends ActionBarActivity {
    private Intent intent;
    private AdapterListDeleteChat adapterListDeleteChat; //Adapter List ที่เรากำหนดขึ้นเอง
    private ArrayList<LastMessageData> dataArray = new ArrayList<>(); //list ในการเก็บข้อมูลของ DataShow
    private ListView listViewDataDeleteChat;
    private ImageButton btn;
    private TextView titleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_chat);
        setAvtionBar();

        listViewDataDeleteChat = (ListView) findViewById(R.id.listChatItem);

        adapterListDeleteChat = new AdapterListDeleteChat(getBaseContext(), dataArray);
        listViewDataDeleteChat.setAdapter(adapterListDeleteChat);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        new queryTask().execute();
    }


    void setAvtionBar() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_back, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);
        titleTextView.setText(getResources().getString(R.string.chat));
        titleTextView.setPadding(60, 0, 0, 0);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            ArrayList<LastMessageData> list = adapterListDeleteChat.selectedItem();
            if (list.size() > 0) {
                for (LastMessageData e : list) {
                    DatabaseChatHelper.init().deleteLastMessage(e.getUser().getJid());

                }
            }
            Intent i = new Intent();
            setResult(Activity.RESULT_OK, i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DataShowChatFragment {
    }

    class queryTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            dataArray = DatabaseChatHelper.init().getLastMessage();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            adapterListDeleteChat.reseAdapter(dataArray);

        }
    }


}
