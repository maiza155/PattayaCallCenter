package com.pattaya.pattayacallcenter.member;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.pattaya.pattayacallcenter.BusProvider;
import com.pattaya.pattayacallcenter.Data.LastMessageData;
import com.pattaya.pattayacallcenter.Data.Messages;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.chat.DatabaseChatHelper;
import com.pattaya.pattayacallcenter.member.Adapter.AdapterListChatFragment;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class ChatListFragment extends Fragment {
    private AdapterListChatFragment adapterListChatFragment; //Adapter List ที่เรากำหนดขึ้นเอง
    private ListView listViewDataChatFragment;
    static ChatListFragment fragment = null;
    DatabaseChatHelper databaseChatHelper = DatabaseChatHelper.init();
    ArrayList<LastMessageData> dataArray = new ArrayList<>();
    int TAG_DELETE_ACTIVITY = 909;

    public static ChatListFragment newInstance() {
        if (fragment == null) {
            fragment = new ChatListFragment();
            BusProvider.getInstance().register(fragment);
        }

        return fragment;
    }

    public ChatListFragment() {
        // Required empty public constructor
    }


    // widget
    ImageButton btn;
    TextView titleTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Subscribe
    public void onBusReciver(Messages messages) {
        new queryTask().execute();

    }

    @Subscribe
    public void onBusReciver(String messages) {
        if (messages.matches("update_last_message")) {
            new queryTask().execute();
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {     // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        listViewDataChatFragment = (ListView) view.findViewById(R.id.listChatItem);
        adapterListChatFragment = new AdapterListChatFragment(getActivity().getBaseContext(), dataArray);
        listViewDataChatFragment.setAdapter(adapterListChatFragment);
        listViewDataChatFragment.setEmptyView(view.findViewById(R.id.txt_empty));

        new queryTask().execute();
        return view;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        setActionBar(getActivity());
        inflater.inflate(R.menu.chat_menu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_delete:
                Intent intent = new Intent(getActivity(), DeleteChatActivity.class);
                startActivityForResult(intent, TAG_DELETE_ACTIVITY);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void setActionBar(Context actionBar) {
        /** Set Title Center Actionbar */
        LayoutInflater inflater = LayoutInflater.from(actionBar);
        View v = inflater.inflate(R.layout.custom_actionbar_none, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);


        titleTextView.setText(getResources().getString(R.string.chat));
        titleTextView.setPadding(60, 0, 0, 0);
        ((MemberMainActivity) actionBar).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((MemberMainActivity) actionBar).getSupportActionBar().setCustomView(v);
        ((MemberMainActivity) actionBar).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MemberMainActivity) actionBar).getSupportActionBar().setDisplayShowHomeEnabled(false);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAG_DELETE_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                new queryTask().execute();

            }

        }
    }


    class queryTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            dataArray = databaseChatHelper.getLastMessage();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            adapterListChatFragment.resetData(dataArray);
        }
    }


}
