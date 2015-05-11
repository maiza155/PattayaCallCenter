package com.pattaya.pattayacallcenter.member.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pattaya.pattayacallcenter.Data.LastMessageData;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.customview.RoundedImageView;
import com.pattaya.pattayacallcenter.member.ChatActivity;
import com.readystatesoftware.viewbadger.BadgeView;

import java.util.ArrayList;


/**
 * Created by PROSPORT on 5/2/2558.
 */
public class AdapterListChatFragment extends BaseAdapter {
    private LayoutInflater mInflater;
    //Dialog dialog;
    private Context context; //รับ Context จาก CustomListViewActivity
    private ArrayList<LastMessageData> listData = new ArrayList<>(); //list ในการเก็บข้อมูลของ DataShow

    View convertView;


    Intent intent;

    public AdapterListChatFragment(Context context, ArrayList<LastMessageData> listData) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.listData = listData;
    }

    @Override
    public int getCount() {
        return listData.size(); //ส่งขนาดของ List ที่เก็บข้อมุลอยู่
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void resetData(ArrayList<LastMessageData> newData) {
        listData = newData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        Holder holder; //เก็บส่วนประกอบของ List แต่ละอัน

        if (convertView == null) {
            //ใช้ Layout ของ List เราเราสร้างขึ้นเอง (convertView.xml)
            convertView = mInflater.inflate(R.layout.list_chat_item_custom, null);
            //สร้างตัวเก็บส่วนประกอบของ List แต่ละอัน

            holder = new Holder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        //System.out.println("sssssssssssssssssssssssssssssssssssssss"+listData.get(position).getUser());


        String name = (listData.get(position).getUser() == null
                || listData.get(position).getUser().getName() == null)
                ? "Unknown" : listData.get(position).getUser().getName();


        if (listData.get(position).getUser() == null) {
            listData.remove(position);
            notifyDataSetChanged();
            return convertView;
        }

        String userImage = (listData.get(position).getUser() == null
                || listData.get(position).getUser().getPic() == null
                || listData.get(position).getUser().getPic().isEmpty()) ? "No"
                : listData.get(position).getUser().getPic();
        //ดึงข้อมูลจาก listData มาแสดงทีละ position
        holder.txtName.setText(name);
        // holder.txtMessage.setText(listData.get(position).getMessage());
        holder.txtTimeSend.setText(listData.get(position).getTime());

        Glide
                .with(context)
                .load(userImage)
                .override(100, 100)
                .fitCenter()
                .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                .error(R.drawable.com_facebook_profile_picture_blank_square)
                .into(holder.imageView);


        String dataMessage = listData.get(position).getMessage();
        String[] sticker = dataMessage.split("<s>");
        String[] image = dataMessage.split("<img>");
        //    holder.imageMsg.setVisibility(View.GONE);
        holder.txtMessage.setVisibility(View.GONE);
        if (sticker.length > 1) {
            holder.txtMessage.setVisibility(View.VISIBLE);
            holder.txtMessage.setText("ส่งสติ๊กเกอร์");
        } else if (image.length > 1) {
            holder.txtMessage.setVisibility(View.VISIBLE);
            holder.txtMessage.setText("Image file ");
        } else {
            if (dataMessage.length() > 100) {
                dataMessage = dataMessage.substring(0, 100) + "...";
            }
            holder.txtMessage.setVisibility(View.VISIBLE);
            holder.txtMessage.setText(dataMessage);


        }

        int count = listData.get(position).getCount();
        if (count > 0) {
            String strCount = (count > 9) ? "N" : String.valueOf(count);

            holder.badge.setText(strCount);
            holder.badge.setBackgroundResource(R.drawable.custom_budget);
            holder.badge.setGravity(Gravity.CENTER);
            holder.badge.setTextSize(13);
            holder.badge.show();
        } else
            holder.badge.hide();


        convertView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (listData.get(position).getUser() != null) {
                    intent = new Intent(v.getContext(), ChatActivity.class);
                    intent.putExtra("user", listData.get(position).getUser());
                    v.getContext().startActivity(intent);
                }


            }
        });

        return convertView;
    }

    Bitmap Base64DecodeBitmap(String s) {
        byte[] b = Base64.decode(s, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        return bitmap;
    }

    public class Holder {
        RoundedImageView imageView;
        TextView txtName;
        TextView txtMessage;
        TextView txtTimeSend;
        ImageView imageMsg;
        View budget;
        BadgeView badge = null;

        public Holder(View convertView) {
            //เชื่อมส่วนประกอบต่างๆ ของ List เข้ากับ View
            txtName = (TextView) convertView.findViewById(R.id.txt_name);
            txtMessage = (TextView) convertView.findViewById(R.id.txt_message);
            txtTimeSend = (TextView) convertView.findViewById(R.id.txt_timesend);
            imageView = (RoundedImageView) convertView.findViewById(R.id.pic_profileAdapter);
            // imageMsg = (ImageView) convertView.findViewById(R.id.message_image);
            budget = convertView.findViewById(R.id.badge);
            badge = new BadgeView(context, budget);
        }
    }


}
