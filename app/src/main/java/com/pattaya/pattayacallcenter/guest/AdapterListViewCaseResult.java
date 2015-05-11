package com.pattaya.pattayacallcenter.guest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.customview.ExpandableHeightGridView;
import com.pattaya.pattayacallcenter.customview.RoundedImageView;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseResultData;
import com.pattaya.pattayacallcenter.webservice.object.casedata.ImageObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.TaskImageObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PROSPORT on 3/2/2558.
 */
public class AdapterListViewCaseResult extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context context; //รับ Context จาก CustomListViewActivity
    private List<CaseResultData> listData = new ArrayList(); //list ในการเก็บข้อมูลของ DataShow
    View convertView;

    public AdapterListViewCaseResult(Context context, List<CaseResultData> listData) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.listData = listData;
    }

    public void resetAdapter(List<CaseResultData> listData) {
        this.listData = listData;
        notifyDataSetChanged();
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

    public class HolderListAdapter {
        RoundedImageView imageView;
        TextView txtName;
        TextView txtDateTime;
        TextView txtResultDetail;
        ExpandableHeightGridView gridView;
        AdapteGridViewShow mGridAdapter;

    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        HolderListAdapter holderListAdapter; //เก็บส่วนประกอบของ List แต่ละอัน
        List<ImageObject> imageData = new ArrayList<>();
        if (convertView == null) {
            //ใช้ Layout ของ List เราเราสร้างขึ้นเอง (convertView.xml)
            convertView = mInflater.inflate(R.layout.list_case_result_custom, null);

            //สร้างตัวเก็บส่วนประกอบของ List แต่ละอัน
            holderListAdapter = new HolderListAdapter();

            //เชื่อมส่วนประกอบต่างๆ ของ List เข้ากับ View
            holderListAdapter.txtName = (TextView) convertView.findViewById(R.id.txt_name);
            holderListAdapter.txtDateTime = (TextView) convertView.findViewById(R.id.txt_dateTime);
            holderListAdapter.txtResultDetail = (TextView) convertView.findViewById(R.id.txt_resultDetail);
            holderListAdapter.imageView = (RoundedImageView) convertView.findViewById(R.id.pic_logoAdapter);
            holderListAdapter.gridView = (ExpandableHeightGridView) convertView.findViewById(R.id.grd_picResult);
            holderListAdapter.gridView.setExpanded(true);
            holderListAdapter.mGridAdapter = new AdapteGridViewShow(context, R.layout.custom_gridview_image, imageData);
            holderListAdapter.gridView.setAdapter(holderListAdapter.mGridAdapter);
            convertView.setTag(holderListAdapter);
        } else {
            holderListAdapter = (HolderListAdapter) convertView.getTag();
        }
        holderListAdapter.txtName.setText(listData.get(position).getUserName());
        holderListAdapter.txtDateTime.setText(listData.get(position).getCompletedDateString());
        holderListAdapter.txtResultDetail.setText(listData.get(position).getResultsOperations());

        String image = (listData.get(position).getUserImageUrl() == null
                || listData.get(position).getUserImageUrl().matches("")) ? "no" : listData.get(position).getUserImageUrl();

        Glide.with(context)
                .load(image)
                .override(200, 200)
                .error(R.drawable.com_facebook_profile_picture_blank_square)
                .fitCenter()
                .into(holderListAdapter.imageView);


        for (TaskImageObject e : listData.get(position).getTaskImageList()) {
            ImageObject imageObject = new ImageObject();
            imageObject.setInfoImage(e.getTaskImage());
            imageData.add(imageObject);

        }
        holderListAdapter.mGridAdapter.resetAdapter(imageData);


        return convertView;
    }

}
