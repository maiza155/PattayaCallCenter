package com.pattaya.pattayacallcenter.customview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pattaya.pattayacallcenter.R;

import java.io.File;
import java.util.ArrayList;


public class CustomGalleryActivity extends ActionBarActivity {

    private GridView grdImages;


    private ImageAdapter imageAdapter;
    private ArrayList arrPath;
    private boolean[] thumbnailsselection;
    private int ids[];
    private int count;

    private ArrayList<Integer> fail;


    /**
     * Overrides methods
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_gallery);
        grdImages = (GridView) findViewById(R.id.grdImages);
        //btnSelect= (Button) findViewById(R.id.btnSelect);
        fail = new ArrayList<>();
        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_TAKEN};
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        @SuppressWarnings("deprecation")
        Cursor imagecursor = managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy + " DESC");


        int image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
        this.count = imagecursor.getCount();
        this.arrPath = new ArrayList();
        ids = new int[count];
        this.thumbnailsselection = new boolean[this.count];
        for (int i = 0; i < this.count; i++) {
            imagecursor.moveToPosition(i);
            ids[i] = imagecursor.getInt(image_column_index);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
            arrPath.add(imagecursor.getString(dataColumnIndex));
        }


        imageAdapter = new ImageAdapter(this, arrPath);
        grdImages.setAdapter(imageAdapter);
        imagecursor.close();


        /** Set Title Center Actionbar*/
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.custom_actionbar_close, null);
        TextView titleTextView = (TextView) v.findViewById(R.id.actionbar_textview);
        ImageButton btn = (ImageButton) v.findViewById(R.id.add_left_menu);
        titleTextView.setText("เลือกรูปภาพ");
        titleTextView.setPadding(40, 0, 0, 0);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_custom_gallery, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        int id = item.getItemId();
        if (id == R.id.action_add) {

            final int len = thumbnailsselection.length;
            int cnt = 0;
            String selectImages = "";
            for (int i = 0; i < len; i++) {
                if (thumbnailsselection[i]) {
                    cnt++;
                    selectImages = selectImages + arrPath.get(i) + "|";
                }
            }
            if (cnt == 0) {
                Toast.makeText(getApplicationContext(), "Please select at least one image", Toast.LENGTH_LONG).show();
            } else {


                Intent i = new Intent();
                i.putExtra("data", selectImages);
                setResult(Activity.RESULT_OK, i);
                finish();
            }

        }

        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }


    /**
     * List adapter
     *
     * @author tasol
     */

    public class ImageAdapter extends BaseAdapter {
        Context context;
        ArrayList mArray;
        private LayoutInflater mInflater;

        public ImageAdapter(Context context, ArrayList mArray) {
            mInflater = LayoutInflater.from(context);
            this.context = context;
            this.mArray = mArray;
        }


        @Override
        public int getCount() {
            return mArray.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //Log.e("TAG","GETVIEW");

            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.custom_gallery_item, null);
                holder.imgThumb = (ImageView) convertView.findViewById(R.id.imgThumb);
                holder.chkImage = (CheckBox) convertView.findViewById(R.id.chkImage);
                holder.bgImage = convertView.findViewById(R.id.bg_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.chkImage.setId(position);
            holder.imgThumb.setId(position);
            holder.bgImage.setId(position);

            //Log.e("TAG","=====> Array path => " + holder.imgThumb.getId());
            File file = new File((String) mArray.get(position));
            if(!file.exists()){
                fail.add(position);
            }

            Glide.with(context)
                    .load(file)
                    .override(500, 500)
                    .centerCrop()
                    .error(R.drawable.img_not_found)
                    .into(holder.imgThumb);



            if (thumbnailsselection[position]) {
                holder.chkImage.setVisibility(View.VISIBLE);
                holder.bgImage.setBackgroundResource(R.color.bluedark);
            } else {
                holder.chkImage.setVisibility(View.GONE);
                holder.bgImage.setBackgroundResource(R.color.black);
            }

            holder.imgThumb.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    int id = holder.chkImage.getId();
                    if (!fail.contains(id)) {
                        if (thumbnailsselection[id]) {
                            holder.chkImage.setChecked(false);
                            thumbnailsselection[id] = false;
                            holder.chkImage.setVisibility(View.GONE);
                            holder.bgImage.setBackgroundResource(R.color.black);


                        } else {
                            holder.chkImage.setChecked(true);
                            thumbnailsselection[id] = true;
                            holder.chkImage.setVisibility(View.VISIBLE);
                            holder.bgImage.setBackgroundResource(R.color.bluedark);

                        }
                    }

                }
            });


            //setBitmap(holder.imgThumb, ids[position]);

            holder.chkImage.setChecked(thumbnailsselection[position]);
            holder.id = position;
            return convertView;

        }


    }


    /**
     * Inner class
     *
     * @author tasol
     */
    class ViewHolder {
        View bgImage;
        ImageView imgThumb;
        CheckBox chkImage;
        int id;
    }

}
