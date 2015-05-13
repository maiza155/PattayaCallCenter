package com.pattaya.pattayacallcenter.guest;

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
import com.pattaya.pattayacallcenter.webservice.object.casedata.ImageObject;

import java.util.List;

/**
 * Created by SWF on 4/2/2015.
 */
public class AdapteGridViewShow extends BaseAdapter {
    private Context context;
    private int layoutResourceId;
    private List<ImageObject> data;

    public AdapteGridViewShow(Context context, int resource, List<ImageObject> objects) {
        this.data = objects;
        this.context = context;
        this.layoutResourceId = resource;


    }

    public void resetAdapter(List data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void add(ImageObject data) {
        this.data.add(data);
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

        View row = convertView;
        ViewHolder holder;
        if (row == null) {
            row = LayoutInflater.from(context)
                    .inflate(layoutResourceId, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }


        holder.btnCancle.setVisibility(View.GONE);
        // System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + data.get(position).getInfoImage());
        Glide.
                with(context).
                load(data.get(position).getInfoImage()).
                placeholder(R.drawable.loading).
                error(R.drawable.img_not_found).
                // nskipMemoryCache().
                        override(150, 150).
                centerCrop().
                into(holder.image);


        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullscreenActivity.class);
                intent.putExtra("pathUrl", data.get(position).getInfoImage());
                intent.putExtra("position", position);
                v.getContext().startActivity(intent);
            }
        });

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
