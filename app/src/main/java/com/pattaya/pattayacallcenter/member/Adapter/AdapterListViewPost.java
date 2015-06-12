package com.pattaya.pattayacallcenter.member.Adapter;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pattaya.pattayacallcenter.Application;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.customview.FullscreenActivity;
import com.pattaya.pattayacallcenter.customview.RoundedImageView;
import com.pattaya.pattayacallcenter.member.PostActivity;
import com.pattaya.pattayacallcenter.member.ShowPostData;
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.pattaya.pattayacallcenter.webservice.object.DeletePostObject;
import com.pattaya.pattayacallcenter.webservice.object.PostObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by PROSPORT on 3/2/2558.
 */
public class AdapterListViewPost extends BaseAdapter {


    public static int TAG_EDITOR = 634;
    final RestAdapter restAdapterPost = WebserviceConnector.getInstanceCartdUI();
    final RestFulQueary restFulQuearyPost = restAdapterPost.create(RestFulQueary.class);
    View convertView;
    Map<Integer, Integer> mapPostId = new HashMap<>();
    SharedPreferences sp;
    int userId;
    Fragment fragment;
    private LayoutInflater mInflater;
    private Context context; //รับ Context จาก CustomListViewActivity
    private ArrayList<PostObject> listData; //list ในการเก็บข้อมูลของ DataShow

    public AdapterListViewPost(Context context, ArrayList listData) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.listData = listData;

        sp = Application.getContext().getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
        userId = sp.getInt(MasterData.SHARED_USER_USER_ID, -10);
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public void addItem(List<PostObject> data) {
        listData.addAll(data);
        notifyDataSetChanged();
        for (PostObject e : listData) {
            mapPostId.put(e.getPostId(), listData.indexOf(e));
        }
    }

    public void addItemUpdate(ArrayList<PostObject> data) {
        ArrayList tempList = new ArrayList();
        tempList.addAll(data);
        tempList.addAll(listData);
        listData = tempList;
        notifyDataSetChanged();
        for (PostObject e : listData) {
            mapPostId.put(e.getPostId(), listData.indexOf(e));
        }


    }

    public void updateItem(int id, String detail, ArrayList<String> image) {
        System.out.println("?" + mapPostId.get(id));
        if (mapPostId.get(id) != null) {
            PostObject tempPost = listData.get(mapPostId.get(id));
            tempPost.setDetail(detail);
            tempPost.setPostImageList(image);

            listData.set(mapPostId.get(id), tempPost);
            notifyDataSetChanged();
        }


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
            holder.btnEdit = (ImageView) convertView.findViewById(R.id.btn_edit);
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
                    .override(600, 600)
                    .fitCenter()
                    .into(holder.image1);
            holder.image1.setVisibility(View.VISIBLE);

        } else if (size >= 2) {
            Glide.with(context).load((listData.get(position).getPostImageList().get(0).matches("")) ? "NoImage" : listData.get(position).getPostImageList().get(0))
                    .error(R.drawable.img_not_found)
                    .placeholder(R.drawable.loading)
                    .override(300, 300)
                    .fitCenter()
                    .into(holder.image1);
            Glide.with(context).load((listData.get(position).getPostImageList().get(1).matches("")) ? "NoImage" : listData.get(position).getPostImageList().get(1))
                    .error(R.drawable.img_not_found)
                    .placeholder(R.drawable.loading)
                    .override(300, 300)
                    .fitCenter()
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

        holder.image1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDialog(v.getContext(), listData.get(position).getPostImageList().get(0));
                return false;
            }
        });

        holder.image2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDialog(v.getContext(), listData.get(position).getPostImageList().get(0));
                return false;
            }
        });
        Log.e("TAG-Post", "UserId " + userId + "     Create BY = " + listData.get(position).getPostById());
        if (userId != listData.get(position).getPostById()) {

            holder.btnEdit.setVisibility(View.GONE);
        } else {

        }
        holder.btnEdit.setVisibility(View.VISIBLE);
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final CharSequence[] items = {
                        "เเก้ไข", "ลบ"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            Intent intent = new Intent(context, PostActivity.class);
                            intent.putExtra("postdata", listData.get(position));
                            fragment.startActivityForResult(intent, TAG_EDITOR);
                        } else if (item == 1) {
                            new TaskDelete(position, listData.get(position).getPostId(), v.getContext()).execute();

                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();


            }
        });

        return convertView;
    }

    public void showDialog(final Context context, final String file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Download File Image");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadImage(context, file);
            }
        });

        builder.show();
    }

    void downloadImage(Context context, String url) {

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //request.setDescription("Some descrition");
        request.setTitle("Download new image");
        // in order for this if to run, you must use the android 3.2 to compile your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "pattaya");

        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }



    class TaskDelete extends AsyncTask<Void, Void, Boolean> {
        int postId;
        int position;
        Context context;
        ProgressDialog progressDialog;

        public TaskDelete(int position, int postId, Context context) {
            this.position = position;
            this.postId = postId;
            this.context = context;
        }

        public TaskDelete(int postId) {
            this.postId = postId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, null, context.getResources().getString(R.string.please_wait), true);
            progressDialog.setCancelable(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            restFulQuearyPost.deletePost(new DeletePostObject(postId), new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    System.out.println("response = [" + response + "], response2 = [" + response2 + "]");
                    listData.remove(position);
                    notifyDataSetChanged();
                    progressDialog.dismiss();
                }

                @Override
                public void failure(RetrofitError error) {
                    System.out.println("error = [" + error + "]");
                    Toast.makeText(Application.getContext(), "Unable connect server \n Please try again", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
            });

            return null;
        }

    }


    class Holder {
        RoundedImageView imageView;
        TextView txtName;
        TextView txtDateTime;
        TextView txtResultDetail;
        TextView txtMore;
        TextView txtViewMore;
        ImageView btnEdit;

        ImageView image1;
        ImageView image2;

        View view;


    }

}
