package com.pattaya.pattayacallcenter.member.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pattaya.pattayacallcenter.Application;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.guest.CaseDetailActivity;
import com.pattaya.pattayacallcenter.member.CaseChatMemberActivity;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseListMemberData;
import com.readystatesoftware.viewbadger.BadgeView;

import java.util.List;

import static com.pattaya.pattayacallcenter.R.drawable.asset_icon_status_flag;
import static com.pattaya.pattayacallcenter.R.drawable.asset_icon_status_gray;
import static com.pattaya.pattayacallcenter.R.drawable.asset_icon_status_green;
import static com.pattaya.pattayacallcenter.R.drawable.custom_date_bg_green;
import static com.pattaya.pattayacallcenter.R.drawable.custom_date_bg_red;
import static com.pattaya.pattayacallcenter.R.drawable.custom_date_bg_yellow;

/**
 * Created by SWF on 4/23/2015.
 */
public class AdpterListCase extends BaseAdapter {
    List<CaseListMemberData> data;
    SharedPreferences settings;
    private LayoutInflater mInflater;
    private Context context;
    public AdpterListCase(List<CaseListMemberData> caseListMemberDataList, Context context) {
        this.data = caseListMemberDataList;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    public void resetAdpter(List<CaseListMemberData> caseListMemberDataList) {
        this.data = caseListMemberDataList;
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_case_list_member, null);
            //สร้างตัวเก็บส่วนประกอบของ List แต่ละอัน
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        String name = (data.get(position).getCaseName() == null) ? "Unknown" : data.get(position).getCaseName();

        holder.txtName.setText(name);
        holder.txtDate.setText(data.get(position).getDate());
        holder.layout.removeAllViews();
        settings = Application.getContext().getSharedPreferences(MasterData.SHARED_CASE_COUNT, Context.MODE_PRIVATE);
        final String caseStr = "id_" + data.get(position).getComplaintId();
        int count = settings.getInt(caseStr, 0);

        if (count > 0) {
            String strCount = (count > 9) ? "N" : String.valueOf(count);
            holder.badge.setText(strCount);
            holder.badge.setBackgroundResource(R.drawable.custom_budget);
            holder.badge.setGravity(Gravity.CENTER);
            holder.badge.setTextSize(13);
            holder.badge.show();
        } else
            holder.badge.hide();


        if (data.get(position).getCasesId() > 0) {
            if (data.get(position).getIsAction()) {
                ImageView image = new ImageView(context);
                Drawable drawable = context.getResources().getDrawable(asset_icon_status_green);
                image.setImageDrawable(drawable);
                holder.layout.addView(image);
            } else {
                ImageView image = new ImageView(context);
                Drawable drawable = context.getResources().getDrawable(asset_icon_status_gray);
                image.setImageDrawable(drawable);
                holder.layout.addView(image);
            }

            if (data.get(position).getIsFollowUp()) {
                ImageView image = new ImageView(context);
                Drawable drawable = context.getResources().getDrawable(asset_icon_status_flag);
                image.setImageDrawable(drawable);
                holder.layout.addView(image);
            }
        }


        if (data.get(position).getCountDate() != null && !data.get(position).getCountDate().isEmpty()) {
            holder.txtProgress.setVisibility(View.VISIBLE);
            holder.txtProgress.setText(data.get(position).getCountDate());
            if (data.get(position).getPriority() == null || data.get(position).getPriority() == 0) {
                holder.txtProgress.setBackgroundResource(custom_date_bg_green);
            } else if (data.get(position).getPriority() == 1) {
                holder.txtProgress.setBackgroundResource(custom_date_bg_yellow);
            } else if (data.get(position).getPriority() == 2) {
                holder.txtProgress.setBackgroundResource(custom_date_bg_red);
            }
        } else {
            holder.txtProgress.setVisibility(View.GONE);

        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings.edit().putInt(caseStr, 0).commit();
                notifyDataSetChanged();
                if (data.get(position).getCasesId() != 0) {
                    System.out.println("case" + data.get(position).getCasesId());
                    Intent intent = new Intent(context, CaseChatMemberActivity.class);
                    intent.putExtra("type", data.get(position).getCasesType());
                    intent.putExtra("id", data.get(position).getCasesId());
                    intent.putExtra("casename", data.get(position).getCaseName());
                    intent.putExtra("complainid", data.get(position).getComplaintId());
                    context.startActivity(intent);
                } else {
                    System.out.println(data.get(position).getComplaintId());
                    Intent intent = new Intent(context, CaseDetailActivity.class);
                    intent.putExtra("id", data.get(position).getComplaintId());
                    context.startActivity(intent);

                }

            }
        });


        return convertView;
    }

    class Holder {
        TextView txtName, txtDate, txtProgress;
        LinearLayout layout;
        View budget;
        BadgeView badge = null;

        Holder(View v) {
            layout = (LinearLayout) v.findViewById(R.id.imageLayout);
            txtName = (TextView) v.findViewById(R.id.name);
            txtDate = (TextView) v.findViewById(R.id.date);
            txtProgress = (TextView) v.findViewById(R.id.progress_time);

            budget = v.findViewById(R.id.badge);
            badge = new BadgeView(context, budget);
        }
    }
}
