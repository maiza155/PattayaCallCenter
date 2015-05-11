package com.pattaya.pattayacallcenter.guest;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.pattaya.pattayacallcenter.R;

import java.util.ArrayList;


public class OrganizeMemberListActivity extends ActionBarActivity {
    private AdapterListViewMemberData adapterListViewData; //Adapter List ที่เรากำหนดขึ้นเอง
    private ArrayList<DataShow> listData = new ArrayList<DataShow>(); //list ในการเก็บข้อมูลของ DataShow
    private ListView listViewData;


    ImageButton btn;
    TextView titleTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organize_member_list);
        listViewData = (ListView) findViewById(R.id.listViewData);

        adapterListViewData = new AdapterListViewMemberData(getBaseContext(), listData);
        listViewData.setAdapter(adapterListViewData);
        listViewData.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                view.setSelected(true);
                Log.e("Click_", "" + view.getBackground());
            }
        });

        /** Set Title Center Actionbar*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_close, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);

        titleTextView.setText(getResources().getString(R.string.org));
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        setClickListener();

    }

    private void setClickListener() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    //เก็บของมูลของ List แต่ละอัน
    public class DataShow {
        private String name;
        private int pic;

        public DataShow(String name, int pic) {
            // TODO Auto-generated constructor stub
            this.name = name;
            this.pic = pic;
        }

        //ใช้ส่งค่ากลับไป setText ของ TextView
        public String getName() {
            return this.name;
        }

        public int getPic() {
            return this.pic;
        }


    }


}
