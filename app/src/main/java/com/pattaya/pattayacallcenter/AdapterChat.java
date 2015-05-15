package com.pattaya.pattayacallcenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.Data.Messages;
import com.pattaya.pattayacallcenter.Data.Users;
import com.pattaya.pattayacallcenter.chat.DatabaseChatHelper;
import com.pattaya.pattayacallcenter.chat.XMPPManage;
import com.pattaya.pattayacallcenter.chat.XMPPService;
import com.pattaya.pattayacallcenter.customview.FullscreenActivity;
import com.pattaya.pattayacallcenter.customview.RoundedImageView;
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.pattaya.pattayacallcenter.webservice.object.upload.FileListObject;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by SWF on 2/23/2015.
 */
public class AdapterChat extends BaseAdapter {

    final RestAdapter restAdapterUpload = WebserviceConnector.getInstanceUpload();
    final RestFulQueary restFulQuearyUpload = restAdapterUpload.create(RestFulQueary.class);
    XMPPManage xmppManage = XMPPManage.getInstance();
    MultiUserChat multiUserChat;
    ListView view;
    SimpleDateFormat sdf = new SimpleDateFormat("E dd MMM yyyy-HH:mm:ss ");
    //private ArrayList<TimeData> timeDatas = new ArrayList();
    Map<String, Integer> mapTime = new HashMap<>();
    Context context;
    private String mUser;
    private ArrayList<Messages> data;
    private Users dataUser;
    // private Users meData;
    private LayoutInflater mInflater;
    private ProgressListener listener;


    public AdapterChat(Context context, ArrayList data, Users user, String mUser, ListView view) {
        this.context = context;
        this.dataUser = user;
        this.data = data;
        this.mInflater = LayoutInflater.from(context);
        this.mUser = mUser;
        this.view = view;

        // boolean istrue = false;
        for (Messages d : this.data) {
            //Log.d("Time", "Have a data>>>"+d.getTime().split("-")[0]);
            String dateTime = d.getTime().split("-")[0];
            Integer position = mapTime.get(dateTime);
            if (position == null) {
                position = this.data.indexOf(d);
                Log.d("TAG TIME DATE SAVE", position + "    " + dateTime);
                mapTime.put(dateTime, position);
            }

        }
        initChatService();
    }

    private void update(long id) {

        int c = view.getChildCount();
        for (int i = 0; i < c; i++) {
            View viewlist = view.getChildAt(i);
            if ((Long) viewlist.getTag() == id) {
                // update view
            }
        }
    }

    public void resetAdapter(ArrayList<Messages> message) {
        this.data.clear();
        this.data.addAll(message);
        mapTime.clear();
        for (Messages d : this.data) {
            // Log.d("Time", "Have a data>>>"+d.getTime().split("-")[0]);
            String dateTime = d.getTime().split("-")[0];
            Integer position = mapTime.get(dateTime);
            if (position == null) {
                position = this.data.indexOf(d);
                Log.d("TAG TIME DATE SAVE", position + "    " + dateTime);
                mapTime.put(dateTime, position);
            }

        }

        this.notifyDataSetChanged();

    }

    public void add(Messages data) {
        this.data.add(data);
        //this.dataUser.add(users);
        String dataDate = data.getTime().split("/")[0];
        Integer position = mapTime.get(dataDate);
        if (position == null) {
            mapTime.put(dataDate, this.data.size() - 1);
        }

        notifyDataSetChanged();

    }

    public void addImageList(String[] image) {
        for (int i = 0; i < image.length; i++) {
            //mGridAdapter.addItem(imagesPath[i], "" + i);
            System.out.println(image[i]);
            String temp_image_message = "<img>xxx<img>0";
            Messages messages = new Messages();
            messages.setMessage(temp_image_message);
            messages.setSender(mUser);
            Calendar c = Calendar.getInstance();
            messages.setTime(sdf.format(c.getTime()));
            messages.setRoom(dataUser.getJid());
            new TaskResizeUpload(image[i], getCount()).execute();
            data.add(messages);

            notifyDataSetChanged();

        }


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
        Holder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_chat_list, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        //Log.d("TAG", "DATA>>>" + data.get(position).getId()+" -- "+mUserId);
        //Log.d("TAG", "DATA>>>" + data.get(position).getMessage());
        final boolean isMe = (data.get(position).getSender().equalsIgnoreCase(mUser)) ? true : false;
        //boolean time = false;
        holder.txtMessage.setOnClickListener(new mClickListener(position));
        holder.layoutTimeHeader.setVisibility(View.GONE);
        String[] dataDate = data.get(position).getTime().split("-");
        String[] time = dataDate[1].split(":");
        String stTime = time[0] + ":" + time[1];

        if (isMe) {
            holder.pic.setVisibility(View.GONE);
            holder.txtTime.setGravity(Gravity.CENTER | Gravity.RIGHT);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            holder.txtMessage.setLayoutParams(params);
            holder.imageStickerMsg.setLayoutParams(params);
            holder.imageContrainer.setLayoutParams(params);
            holder.txtTime.setText(stTime);
            if (data.get(position).getError() == 1) {
                holder.txtMessage.setBackgroundResource(R.drawable.chatbox_fail);

            } else {
                holder.txtMessage.setBackgroundResource(R.drawable.chatbox_me);
                //holder.imageContrainer.setBackgroundResource(R.drawable.chatbox_me);
            }

        } else {
            holder.pic.setVisibility(View.VISIBLE);
            holder.txtTime.setGravity(Gravity.CENTER | Gravity.LEFT);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL);
            holder.txtMessage.setLayoutParams(params);
            holder.imageStickerMsg.setLayoutParams(params);
            holder.imageContrainer.setLayoutParams(params);
            //holder.imageContrainer.setBackgroundResource(R.drawable.chatbox_fri);
            holder.txtMessage.setBackgroundResource(R.drawable.chatbox_fri);
            holder.txtTime.setText(data.get(position).getSenderName() + "  " + stTime);

            String image = (data.get(position).getSenderImage() == null || data.get(position).getSenderImage().isEmpty()) ? "No" : data.get(position).getSenderImage();
            Glide.with(context)
                    .load(image)
                    .override(100, 100)
                    .fitCenter()
                    .error(R.drawable.com_facebook_profile_picture_blank_square)
                    .into(holder.pic);
        }


        Integer datePosition = mapTime.get(dataDate[0]);
        if (datePosition != null && datePosition == position) {
            holder.layoutTimeHeader.setVisibility(View.VISIBLE);
            holder.txtTimeHeader.setText(dataDate[0]);
        }


        ///////////////////////////Sticker ////////////////////////////////////////////
        final String dataMessage = data.get(position).getMessage();
        String[] image = dataMessage.split("<s>");
        holder.imageStickerMsg.setVisibility(View.GONE);
        holder.txtMessage.setVisibility(View.GONE);
        if (image.length > 1) {
            holder.imageStickerMsg.setVisibility(View.VISIBLE);
            holder.imageStickerMsg.setImageBitmap(Base64DecodeBitmap(image[1]));
        } else {
            holder.txtMessage.setVisibility(View.VISIBLE);
            String strMsg;
            if (data.get(position).getMessage().length() > 1000) {
                strMsg = data.get(position).getMessage().substring(0, 1000);
            } else {
                strMsg = data.get(position).getMessage();
            }


            holder.txtMessage.setText(strMsg);
        }


        ////////////////////////ImageView /////////////////////////////////////////////


        final String[] imagedata = dataMessage.split("<img>");
        holder.imageContrainer.setVisibility(View.GONE);
        if (imagedata.length > 1) {
            holder.imageStickerMsg.setVisibility(View.GONE);
            holder.txtMessage.setVisibility(View.GONE);
            holder.imageContrainer.setVisibility(View.VISIBLE);
            holder.progress.setVisibility(View.VISIBLE);
            holder.imageViewMsg.setImageResource(R.drawable.blue_background);
            if (imagedata[1].matches("xxx")) {
                int progress = Integer.parseInt(imagedata[2]);
                holder.progress.setProgress(progress);
            } else {
                System.out.println(imagedata[1]);
                Glide.with(context).load(imagedata[1])
                        .placeholder(R.drawable.loading)
                        .override(200, 200)
                        .fitCenter()
                        .error(R.drawable.img_not_found)
                        .into(holder.imageViewMsg);
                holder.progress.setVisibility(View.GONE);

            }

        }
        holder.imageContrainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullscreenActivity.class);
                intent.putExtra("pathUrl", imagedata[1]);
                intent.putExtra("position", position);
                v.getContext().startActivity(intent);
            }
        });


        return convertView;
    }

    Bitmap Base64DecodeBitmap(String s) {
        byte[] b = Base64.decode(s, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        bitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, false);
        return bitmap;
    }

    void initChatService() {
        xmppManage = XMPPManage.getInstance();
        if (xmppManage.getmConnection() != null && xmppManage.getmConnection().isConnected()) {
            if (dataUser.getType() == Users.TYPE_FRIEND) {
                xmppManage.initChat(dataUser.getJid());
            } else if (dataUser.getType() == Users.TYPE_GROUP) {
                multiUserChat = xmppManage.initGroupChat(dataUser.getJid());
                xmppManage.setJoinGroupChat(multiUserChat);
            }
            xmppManage.initIQForChatreQuest(dataUser.getJid());
        } else {
            context.startService(new Intent(context, XMPPService.class));
        }

    }

    void enterImage(String message, int position) {
        if (!xmppManage.getmConnection().isConnected()
                || xmppManage.getmConnection().isSocketClosed()
                || (xmppManage.getChat() == null && dataUser.getType() == Users.TYPE_FRIEND)) {
            Log.e("Error", "Connection Fail wait .... for login  " + xmppManage.getChat());
            initChatService();
            Calendar c = Calendar.getInstance();
            Messages messages = new Messages();
            messages.setTime(sdf.format(c.getTime()));
            messages.setSender(mUser);
            messages.setMessage(message);
            messages.setError(1);
            // add(messages);

        } else {
            Log.e("TAG Chat Socket", "" + xmppManage.getmConnection().isSocketClosed());
            if (multiUserChat != null && dataUser.getType() == Users.TYPE_GROUP) {
                xmppManage.setJoinGroupChat(multiUserChat);
                if (multiUserChat.isJoined()) {
                    try {
                        Calendar c = Calendar.getInstance();
                        Messages messages = new Messages();
                        messages.setTime(sdf.format(c.getTime()));
                        messages.setSender(mUser);
                        messages.setMessage(message);
                        add(messages);
                        multiUserChat.sendMessage(message);
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("Smack", "Join ? : " + multiUserChat.isJoined());
                    xmppManage.setJoinGroupChat(multiUserChat);
                    Log.e("Smack", "Join ? : " + multiUserChat.isJoined());
                    Calendar c = Calendar.getInstance();
                    Messages messages = new Messages();
                    messages.setTime(sdf.format(c.getTime()));
                    messages.setSender(mUser);
                    messages.setMessage(message);
                    messages.setError(1);
                    add(messages);
                }
            } else if (dataUser.getType() == Users.TYPE_FRIEND || dataUser.getType() == Users.TYPE_NOT_FRIEND) {
                Messages messages = new Messages();
                Calendar c = Calendar.getInstance();
                messages.setTime(sdf.format(c.getTime()));
                messages.setSender(mUser);
                messages.setMessage(message);
                add(messages);
                xmppManage.chat(message, dataUser.getJid());
            }
        }
    }

    public void enterMsg(String message) {

        if (!xmppManage.getmConnection().isConnected()
                || xmppManage.getmConnection().isSocketClosed()
                || (xmppManage.getChat() == null && dataUser.getType() == Users.TYPE_FRIEND)) {
            Log.e("Error", "Connection Fail wait .... for login  " + xmppManage.getChat());
            initChatService();
            Calendar c = Calendar.getInstance();
            Messages messages = new Messages();
            messages.setTime(sdf.format(c.getTime()));
            messages.setSender(mUser);
            messages.setMessage(message);
            messages.setError(1);
            add(messages);

        } else {
            Log.e("TAG Chat Socket", "" + xmppManage.getmConnection().isSocketClosed());
            if (dataUser.getType() == Users.TYPE_GROUP) {
                if (multiUserChat != null) {
                    xmppManage.setJoinGroupChat(multiUserChat);
                    if (multiUserChat.isJoined()) {
                        try {
                            Calendar c = Calendar.getInstance();
                            Messages messages = new Messages();
                            messages.setTime(sdf.format(c.getTime()));
                            messages.setSender(mUser);
                            messages.setMessage(message);
                            add(messages);
                            System.out.println(mUser);

                            multiUserChat.sendMessage(message);
                        } catch (XMPPException e) {
                            e.printStackTrace();
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }
                    } else {

                        Calendar c = Calendar.getInstance();
                        Messages messages = new Messages();
                        messages.setTime(sdf.format(c.getTime()));
                        messages.setSender(mUser);
                        messages.setMessage(message);
                        messages.setError(1);
                        add(messages);
                    }
                } else {
                    System.out.println("Null MUC");

                    multiUserChat = xmppManage.initGroupChat(dataUser.getJid());
                    xmppManage.setJoinGroupChat(multiUserChat);
                    Calendar c = Calendar.getInstance();
                    Messages messages = new Messages();
                    messages.setTime(sdf.format(c.getTime()));
                    messages.setSender(mUser);
                    messages.setMessage(message);
                    messages.setError(1);
                    add(messages);

                }

            } else if (dataUser.getType() == Users.TYPE_FRIEND || dataUser.getType() == Users.TYPE_NOT_FRIEND) {
                Messages messages = new Messages();
                Calendar c = Calendar.getInstance();
                messages.setTime(sdf.format(c.getTime()));
                messages.setSender(mUser);
                messages.setMessage(message);
                add(messages);
                xmppManage.chat(message, dataUser.getJid());
            }
        }
    }

    public void queryChatLogs(Boolean bool) {
        new queryTask(bool).execute();
    }

    public void queryChatLogsNoReset(Messages messages, Boolean bool) {
        new queryTask(messages, bool).execute();
    }

    class queryTask extends AsyncTask<Void, Void, Boolean> {
        ArrayList<Messages> messages;
        Messages message;
        boolean isReset = false;

        queryTask(boolean isReset) {
            this.isReset = isReset;
        }

        queryTask(Messages message, boolean isReset) {
            this.message = message;
            this.isReset = isReset;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (isReset)
                messages = DatabaseChatHelper.init().getLogs(dataUser.getJid());
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (isReset) {
                resetAdapter(messages);
            } else {
                add(message);
            }
            BusProvider.getInstance().post("fin_updateChat");
            //setListViewInBtm();
        }
    }

    class Holder {
        RoundedImageView pic;
        TextView txtTime;
        TextView txtMessage;
        TextView txtTimeHeader;
        View layoutTimeHeader;
        ImageView imageStickerMsg;
        ImageView imageViewMsg;
        ProgressBar progress;
        RelativeLayout imageContrainer;


        Holder(View v) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            pic = (RoundedImageView) v.findViewById(R.id.pic_profile);
            txtTime = (TextView) v.findViewById(R.id.txt_time);
            txtMessage = (TextView) v.findViewById(R.id.txt_msg);
            txtTimeHeader = (TextView) v.findViewById(R.id.txt_timeheader);
            layoutTimeHeader = v.findViewById(R.id.timeheader);
            imageStickerMsg = (ImageView) v.findViewById(R.id.message_image);
            imageContrainer = (RelativeLayout) v.findViewById(R.id.image_container);
            progress = (ProgressBar) v.findViewById(R.id.progress);
            imageViewMsg = (ImageView) v.findViewById(R.id.image);

            txtMessage.setMaxWidth(width / 2);


        }
    }

    class mClickListener implements View.OnClickListener {

        int position;

        mClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            final TextView tv = (TextView) v;
            final Messages messages = data.get(position);
            Log.d("TAG Click " + position, "" + tv.getText() + "   " + messages.getError());

            if (messages.getError() == 1) {
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Fail Message");
                alertDialog.setMessage(tv.getText());
                alertDialog.setButton("Resend", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Add your code for the button here.
                        Log.d("Recen", "" + tv.getText() + "   " + position);
                        //boolean active = sp.getBoolean("active", false);
                        enterMsg(messages.getMessage());
                        data.remove(position);
                        if (!xmppManage.getmConnection().isConnected()) {
                            Toast.makeText(context.getApplicationContext(), "Pleace check internet your connection", Toast.LENGTH_SHORT).show();
                        }
                        notifyDataSetChanged();

                    }
                });
                alertDialog.setButton2("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Add your code for the button here.
                        Log.d("Delete", "" + data.get(position).getMessage());
                        data.remove(position);
                        notifyDataSetChanged();
                    }
                });
                alertDialog.show();
            }
        }
    }

    class TaskResizeUpload extends AsyncTask<Void,Void,Boolean>{
        String s;
        int position;

        public TaskResizeUpload( String s,int position) {
            this.position = position;
            this.s = s;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            int randomNum = 500 + (int) ((Math.random() * 1204006080) / Math.random() + Math.random());
            int randomNum2 = 500 + (int) ((Math.random() * 1204006080) / Math.random() + Math.random());
            String name = "pattaya-image-chat" + randomNum + "ToiP" + randomNum2;
            File file = new File(context.getCacheDir(), name);
            try {
                file.createNewFile();
            } catch (IOException error) {
                error.printStackTrace();
            }
            Bitmap bitmap = null;
            try {
                bitmap = Glide.with(Application.getContext())
                        .load(s)
                        .asBitmap()
                        .fitCenter()
                        .into(500, 500) // Width and height
                        .get();
            } catch (InterruptedException error) {
                error.printStackTrace();
            } catch (ExecutionException error) {
                error.printStackTrace();
            }

            System.out.println(bitmap);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, MasterData.PERCEN_OF_IMAGE_FILE, bos);
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
            } catch (FileNotFoundException error) {
                error.printStackTrace();
            }
            try {
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            } catch (IOException error) {
                error.printStackTrace();
            }
            Log.e("File upload",""+file.length());
            Log.e("File upload",""+file.getAbsolutePath());

            final long totalSize = file.length();

            listener = new ProgressListener() {
                @Override
                public void transferred(long num) {
                    //publishProgress((int) ((num / (float) totalSize) * 100));
                    final int[] count = {Math.round(((num / (float) totalSize) * 100))};
                    System.out.println("position   " + position + "          >>>>>>>" + count[0]);
                    data.get(position).setMessage("<img>xxx<img>" + count[0]);
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            View mView = view.getChildAt(position - view.getFirstVisiblePosition());
                            getView(position, mView, view);

                        }
                    });
                }
            };

            restFulQuearyUpload.uploadImage(new CountingTypedFile("image/jpeg", file, listener), new Callback<Response>() {
                @Override
                public void success(Response result, Response response2) {
                    BufferedReader reader = null;
                    StringBuilder sb = new StringBuilder();
                    try {

                        reader = new BufferedReader(new InputStreamReader(result.getBody().in()));

                        String line;

                        try {
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String JsonConvertData = "{data:" + sb.toString() + "}";
                    System.out.println(JsonConvertData);
                    FileListObject fileListObject = new Gson().fromJson(JsonConvertData, FileListObject.class);
                    System.out.println(fileListObject.getData().get(0).getUrl());
                    System.out.println("Complete Upload Image !!!!");
                    System.out.println("position : " + position);
                    data.get(position).setMessage("<img>" + fileListObject.getData().get(0).getUrl());
                    enterImage(data.get(position).getMessage(), position);
                    data.remove(position);
                    notifyDataSetChanged();

                }

                @Override
                public void failure(RetrofitError error) {
                    System.out.println("error = [" + error + "]");
                    data.remove(position);
                    Toast.makeText(context.getApplicationContext(), "Fail uploading ,please try again.", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();

                }
            });

            return null;
        }
    }

}
