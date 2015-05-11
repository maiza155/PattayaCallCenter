package com.pattaya.pattayacallcenter.guest.CaseList;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.guest.CaseDetailActivity;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseListDataObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SWF on 1/27/2015.
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private Context mContext;
    private List<CaseListDataObject> mList;


    public ListAdapter(ArrayList list, Context mContext) {
        this.mContext = mContext;
        this.mList = list;
    }

    public void resetAdapter(List<CaseListDataObject> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.custom_case_list, parent, false);
        ListAdapter.ViewHolder viewHolder = new ListAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ListAdapter.ViewHolder holder, int position) {
        final CaseListDataObject data = mList.get(position);
        holder.textTime.setText(data.getDate());
        holder.textTitle.setText(data.getCaseName());

        holder.frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, CaseDetailActivity.class);
                intent.putExtra("id", data.getComplaintId());
                intent.putExtra("id_case", data.getCasesId());
                mContext.startActivity(intent);
            }
        });

        // BadgeView badge = new BadgeView(mContext, holder.badge);
        // badge.setTextSize(15);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textTitle;
        TextView textTime;
        View frame;

        public ViewHolder(View v) {
            super(v);
            textTitle = (TextView) v.findViewById(R.id.text_title);
            textTime = (TextView) v.findViewById(R.id.text_time);
            frame = v.findViewById(R.id.frame);
        }


    }


}
