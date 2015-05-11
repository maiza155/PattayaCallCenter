package com.pattaya.pattayacallcenter.member;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.customview.NonScrollListView;
import com.pattaya.pattayacallcenter.customview.RoundedImageView;
import com.pattaya.pattayacallcenter.member.Adapter.ImageAdapterList;
import com.pattaya.pattayacallcenter.webservice.object.PostObject;

import java.util.ArrayList;
import java.util.List;

public class ShowPostData extends ActionBarActivity {
    ImageButton btn;
    TextView titleTextView;
    PostObject postObject;
    List<String> imageList = new ArrayList();
    NonScrollListView lvImage;
    ImageAdapterList imageAdapterList;
    RoundedImageView circularImageView;

    TextView txtName, txtTime, txtMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_post_data);
        Bundle data = getIntent().getExtras();
        postObject = data.getParcelable("postdata");
        System.out.println(postObject.toString());
        imageList = postObject.getPostImageList();


        /** Set Title Center Actionbar*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_close, null);
        titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        btn = (ImageButton) v.findViewById(R.id.add_left_menu);


        titleTextView.setText(getResources().getString(R.string.post));
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);


        circularImageView = (RoundedImageView) findViewById(R.id.pic_user);
        txtName = (TextView) findViewById(R.id.txt_name);
        txtTime = (TextView) findViewById(R.id.txt_dateTime);
        txtMsg = (TextView) findViewById(R.id.txt_resultDetail);
        lvImage = (NonScrollListView) findViewById(R.id.lv_image);
        imageAdapterList = new ImageAdapterList(this, imageList);
        lvImage.setAdapter(imageAdapterList);
        String pic = (postObject.getPostByUserImage() == null || postObject.getPostByUserImage().matches("")) ? "NO IMAGE" : postObject.getPostByUserImage();
        System.out.println(pic);
        Glide.with(this).load(pic)
                .error(R.drawable.com_facebook_profile_picture_blank_square)
                .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                .override(200, 200)
                .into(circularImageView);

        txtName.setText(postObject.getPostByName());
        txtTime.setText(postObject.getPostDateTime());
        txtMsg.setText(postObject.getDetail());


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


}
