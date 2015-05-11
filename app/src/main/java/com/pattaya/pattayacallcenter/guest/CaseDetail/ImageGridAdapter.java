package com.pattaya.pattayacallcenter.guest.CaseDetail;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.customview.FullscreenActivity;


import java.io.File;
import java.util.List;

/**
 * Created by SWF on 1/29/2015.
 */
public class ImageGridAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private List<String> data;

    int image = 0;

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }


    public List getData() {
        return data;
    }

    public ImageGridAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);

        this.data = objects;
        this.context = context;
        this.layoutResourceId = resource;


    }


    public void addItem(String image) {
        data.add(image);
        notifyDataSetChanged();
    }


    public void removeAt(int position) {
        data.remove(position);
        notifyDataSetChanged();


    }


    public View getView(final int position, View convertView, ViewGroup parent) {

        File file;
        View row = convertView;
        ViewHolder holder = null;
        if (row == null) {
            row = LayoutInflater.from(context)
                    .inflate(layoutResourceId, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        try {
            if (image == 0) {
                System.out.println("pic :" + 0);
                file = new File((String) data.get(position));
                Glide.
                        with(context).
                        load(file).
                        placeholder(R.drawable.loading).
                        error(R.drawable.img_not_found).
                        override(150, 150).
                        centerCrop().
                        into(holder.image);

            }
            holder.btnCancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeAt(position);
                }
            });

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, FullscreenActivity.class);
                    intent.putExtra("path", (String) data.get(position));
                    intent.putExtra("position", position);
                    context.startActivity(intent);


                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return row;
    }


    class ViewHolder {
        ImageView image;
        ImageButton btnCancle;

        public ViewHolder(View view) {
            image = (ImageView) view.findViewById(R.id.image);
            btnCancle = (ImageButton) view.findViewById(R.id.btn_delete);

        }
    }

}
