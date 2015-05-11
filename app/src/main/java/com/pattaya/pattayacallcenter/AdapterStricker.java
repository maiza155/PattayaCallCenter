package com.pattaya.pattayacallcenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.pattaya.pattayacallcenter.chat.DatabaseChatHelper;
import com.pattaya.pattayacallcenter.customview.SlideMenuManage;

import java.util.ArrayList;


/**
 * Created by SWF on 2/6/2015.
 */
public class AdapterStricker extends BaseAdapter {
    private LayoutInflater mInflater;
    private int layoutResourceId = R.layout.custom_image_grid;
    Context context;
    ArrayList<byte[]> mArray;

    SlideMenuManage slideMenuManage;
    OnItemClickListener mItemClickListener;


    public SlideMenuManage getSlideMenuManage() {
        return slideMenuManage;
    }

    public interface OnItemClickListener {
        public void onItemClick(String base64);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


    public void setSlideMenuManage(SlideMenuManage slideMenuManage) {
        this.slideMenuManage = slideMenuManage;
    }


    public AdapterStricker(Context context, int resource, ArrayList mArray) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.layoutResourceId = resource;
        this.mArray = mArray;
    }

    public void addItem(byte[] data) {
        mArray.add(data);
        notifyDataSetChanged();
    }

    public void resetAdapter(ArrayList list) {
        mArray = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mArray.size();
    }

    @Override
    public Object getItem(int position) {
        return mArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(layoutResourceId, null);
            holder.imgSticker = (ImageView) convertView.findViewById(R.id.imgSticker);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        byte[] data = mArray.get(position);
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        holder.imgSticker.setImageBitmap(bitmap);
        holder.imgSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPopupWindow(v, pos);
            }


        });
        return convertView;
    }

    private void displayPopupWindow(View anchorView, final int position) {
        final PopupWindow popup = new PopupWindow(context);
        View layout = mInflater.inflate(R.layout.custom_image_grid, null);
        layout.setBackgroundResource(R.color.whiteColor);
        //Setting Image
        ImageView stView = (ImageView) layout.findViewById(R.id.imgSticker);
        stView.getLayoutParams().height = 150;
        stView.getLayoutParams().width = 150;
        Log.d("TAG", "SElect Sticker" + position);
        final byte[] data = mArray.get(position);
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        stView.setImageBitmap(bitmap);

        popup.setContentView(layout);
        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(false);
        //popup.setCanceledOnTouchOutside(false);
        // Show anchored to button
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAsDropDown(anchorView, 0, -anchorView.getHeight());
        stView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    String encodedImage = "<s>" + Base64.encodeToString(data, Base64.DEFAULT);
                    mItemClickListener.onItemClick(encodedImage);
                }
                Log.d("TAG", "SElect Sticker" + position);
                popup.dismiss();
                getSlideMenuManage().stateShowMenu(getSlideMenuManage().SETTING_MENU_HIDE);

            }
        });
    }


    public void querySticker() {
        new queryStrickerTask().execute();
    }


    /**
     * Inner class
     *
     * @author tasol
     */
    class ViewHolder {
        ImageView imgSticker;
    }


    class queryStrickerTask extends AsyncTask<Void, Void, Boolean> {
        ArrayList listStiker = new ArrayList<>();


        @Override
        protected Boolean doInBackground(Void... params) {
            listStiker = DatabaseChatHelper.init().getSticker();
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            resetAdapter(listStiker);
        }
    }


}
