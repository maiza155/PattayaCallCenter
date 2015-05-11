package com.pattaya.pattayacallcenter.guest.CaseDetail;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
public class ImageUpdateGridAdapter extends BaseAdapter {
    private Context context;
    private int layoutResourceId;
    private List<ImageData> data;


    public ImageUpdateGridAdapter(Context context, int resource, List objects) {
        this.data = objects;
        this.context = context;
        this.layoutResourceId = resource;
    }
    public List<ImageData> getData() {
        return data;
    }
    public void setData(List<ImageData> data) {
        this.data = data;
    }

    public void addItem(String s){
        ImageData obj = new ImageData();
        obj.setPath(s);
        obj.setTag(1);
        data.add(obj);
        notifyDataSetChanged();
    }

    public void addItemUpdate(String s){
        ImageData obj = new ImageData();
        obj.setPath(s);
        obj.setTag(0);
        data.add(obj);
        notifyDataSetChanged();
    }

    public void removeAt(int position) {
        data.remove(position);
        notifyDataSetChanged();
    }

    public void resetAdapter(List<ImageData> data){
        this.data = data;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        File file;
        View row = convertView;
        ViewHolder holder = null;
        if (row == null) {
            row = LayoutInflater.from(context)
                    .inflate(layoutResourceId, parent, false);
            holder = new ViewHolder(row);
            //holder.image.setOnClickListener(new OnImageClickListener());
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final int image = data.get(position).getTag();

        try {
            if (image == 1) {
                System.out.println("pic :" + 0);
                file = new File(data.get(position).getPath());
                Glide.
                        with(context).
                        load(file).
                        placeholder(R.drawable.loading).
                        error(R.drawable.img_not_found).
                        // nskipMemoryCache().
                                override(150, 150).
                        centerCrop().
                        into(holder.image);

            } else if (image == 0) {
                //System.out.println("pic :"+ position);
                //  int imgId = (int) dataInt.get(position);
                // System.out.println("picId :"+imgId);
                Glide.
                        with(context).
                        load(data.get(position).getPath()).
                        placeholder(R.drawable.loading).
                        error(R.drawable.img_not_found).
                        // nskipMemoryCache().
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
                    if (image == 1) {
                        Intent intent = new Intent(context, FullscreenActivity.class);
                        intent.putExtra("path", data.get(position).getPath());
                        intent.putExtra("position", position);
                        context.startActivity(intent);
                    } else if (image == 0) {
                        Intent intent = new Intent(context, FullscreenActivity.class);
                        intent.putExtra("pathUrl", data.get(position).getPath());
                        intent.putExtra("position", position);
                        context.startActivity(intent);
                    }
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
