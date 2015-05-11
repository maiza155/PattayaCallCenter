package com.pattaya.pattayacallcenter.member.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.customview.FullscreenActivity;
import com.pattaya.pattayacallcenter.customview.RoundedImageView;
import com.pattaya.pattayacallcenter.member.ShowPostData;
import com.pattaya.pattayacallcenter.webservice.object.PostObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PROSPORT on 3/2/2558.
 */
public class AdapterListViewPost extends BaseAdapter {


    View convertView;
    private LayoutInflater mInflater;
    private Context context; //รับ Context จาก CustomListViewActivity
    private ArrayList<PostObject> listData; //list ในการเก็บข้อมูลของ DataShow

    public AdapterListViewPost(Context context, ArrayList listData) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.listData = listData;
    }

    public void addItem(List<PostObject> data) {
        listData.addAll(data);
        notifyDataSetChanged();


    }

    public void addItemUpdate(ArrayList<PostObject> data) {
        ArrayList tempList = new ArrayList();
        tempList.addAll(data);
        tempList.addAll(listData);
        listData = tempList;
        notifyDataSetChanged();


    }


    public ArrayList<PostObject> getListData() {
        return listData;
    }

    public void setListData(ArrayList<PostObject> listData) {
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

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final Holder holder; //เก็บส่วนประกอบของ List แต่ละอัน

        if (convertView == null) {
            //ใช้ Layout ของ List เราเราสร้างขึ้นเอง (convertView.xml)
            convertView = mInflater.inflate(R.layout.custom_list_post, null);

            //สร้างตัวเก็บส่วนประกอบของ List แต่ละอัน
            holder = new Holder();

            //เชื่อมส่วนประกอบต่างๆ ของ List เข้ากับ View

            holder.view = convertView.findViewById(R.id.dview);
            holder.txtViewMore = (TextView) convertView.findViewById(R.id.txt_image_more);
            holder.image1 = (ImageView) convertView.findViewById(R.id.image1);
            holder.image2 = (ImageView) convertView.findViewById(R.id.image2);
            holder.txtName = (TextView) convertView.findViewById(R.id.txt_name);
            holder.txtMore = (TextView) convertView.findViewById(R.id.txt_more);
            holder.txtDateTime = (TextView) convertView.findViewById(R.id.txt_dateTime);
            holder.txtResultDetail = (TextView) convertView.findViewById(R.id.txt_resultDetail);
            holder.imageView = (RoundedImageView) convertView.findViewById(R.id.pic_logoAdapter);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        //ดึงข้อมูลจาก listData มาแสดงทีละ position
        holder.txtName.setText(listData.get(position).getPostByName());
        holder.txtDateTime.setText(listData.get(position).getPostDateTime());

        // holder.imageView.setImageResource(listData.get(position).getPic());
        final String text = listData.get(position).getDetail();
        //System.out.println("detail>>>>>>>>>>>>>>>>"+detailLine);
        if (text.length() > 100) {
            //holder.txtResultDetail.setText(listData.get(position).getDetail());
            holder.txtResultDetail.setText(text.substring(0, 100));
            holder.txtMore.setVisibility(View.VISIBLE);
            holder.txtMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.txtResultDetail.setText(text);
                    holder.txtMore.setVisibility(View.GONE);
                }
            });

        } else {
            holder.txtMore.setVisibility(View.GONE);
            holder.txtResultDetail.setText(text);

        }


        String pic = (listData.get(position).getPostByUserImage() == null
                || listData.get(position).getPostByUserImage() == "") ? "NO IMAGE" : listData.get(position).getPostByUserImage();
        Glide.with(context).load(pic)
                .error(R.drawable.com_facebook_profile_picture_blank_square)
                .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                .override(200, 200)
                .into(holder.imageView);
        int size = (listData.get(position).getPostImageList() == null) ? 0 : listData.get(position).getPostImageList().size();

        holder.image1.setVisibility(View.GONE);
        holder.image2.setVisibility(View.GONE);
        holder.view.setVisibility(View.GONE);
        holder.txtViewMore.setVisibility(View.GONE);
        if (size == 1 && !listData.get(position).getPostImageList().get(0).isEmpty()) {
            Glide.with(context).load(listData.get(position).getPostImageList().get(0))
                    .error(R.drawable.img_not_found)
                    .placeholder(R.drawable.loading)
                    .override(500, 500)
                    .fitCenter()
                    .into(holder.image1);
            holder.image1.setVisibility(View.VISIBLE);

        } else if (size >= 2) {
            Glide.with(context).load((listData.get(position).getPostImageList().get(0).matches("")) ? "NoImage" : listData.get(position).getPostImageList().get(0))
                    .error(R.drawable.img_not_found)
                    .placeholder(R.drawable.loading)
                    .override(200, 200)
                    .centerCrop()
                    .into(holder.image1);
            Glide.with(context).load((listData.get(position).getPostImageList().get(1).matches("")) ? "NoImage" : listData.get(position).getPostImageList().get(1))
                    .error(R.drawable.img_not_found)
                    .placeholder(R.drawable.loading)
                    .override(200, 200)
                    .centerCrop()
                    .into(holder.image2);
            holder.image1.setVisibility(View.VISIBLE);
            holder.image2.setVisibility(View.VISIBLE);
        }
        if (size > 2) {
            holder.view.setVisibility(View.VISIBLE);
            holder.txtViewMore.setVisibility(View.VISIBLE);
            holder.txtViewMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println(listData.get(position).toString());
                    Intent intent = new Intent(context, ShowPostData.class);
                    intent.putExtra("postdata", listData.get(position));
                    v.getContext().startActivity(intent);
                }
            });
        }


        holder.image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullscreenActivity.class);
                intent.putExtra("pathUrl", listData.get(position).getPostImageList().get(0));
                intent.putExtra("position", position);
                v.getContext().startActivity(intent);

            }
        });
        holder.image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullscreenActivity.class);
                intent.putExtra("pathUrl", listData.get(position).getPostImageList().get(1));
                intent.putExtra("position", position);
                v.getContext().startActivity(intent);

            }
        });

        return convertView;
    }


    class Holder {
        RoundedImageView imageView;
        TextView txtName;
        TextView txtDateTime;
        TextView txtResultDetail;
        TextView txtMore;
        TextView txtViewMore;

        ImageView image1;
        ImageView image2;

        View view;


    }

}
