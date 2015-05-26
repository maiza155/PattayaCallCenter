package com.pattaya.pattayacallcenter.chat;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.util.Log;

import com.pattaya.pattayacallcenter.Application;
import com.pattaya.pattayacallcenter.BusProvider;
import com.pattaya.pattayacallcenter.Data.LastMessageData;
import com.pattaya.pattayacallcenter.Data.Messages;
import com.pattaya.pattayacallcenter.Data.Users;
import com.pattaya.pattayacallcenter.chat.restadatper.OpenfireQueary;
import com.pattaya.pattayacallcenter.chat.restadatper.RestAdapterOpenFire;
import com.pattaya.pattayacallcenter.chat.xmlobject.User;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseListDataObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit.RestAdapter;

/**
 * Created by SWF on 2/25/2015.
 */
public class DatabaseChatHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "pattaya";
    static final int DB_VERSION = 1;
    static DatabaseChatHelper databaseChatHelper = null;
    final RestAdapter restAdapterOpenFire = RestAdapterOpenFire.getInstance();
    final OpenfireQueary openfireQueary = restAdapterOpenFire.create(OpenfireQueary.class);
    Date date;
    SimpleDateFormat sdf = new SimpleDateFormat("E dd MMM yyyy-HH:mm:ss ");
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public DatabaseChatHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static DatabaseChatHelper init() {
        if (databaseChatHelper == null) {
            databaseChatHelper = new DatabaseChatHelper(Application.getContext());

        }
        return databaseChatHelper;
    }

    public static Calendar DateToCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + RosterEntry.TABLE_NAME + " ("
                + RosterEntry.Column.JID + " TEXT PRIMARY KEY, "
                + RosterEntry.Column.NAME + " TEXT, "
                + RosterEntry.Column.ORGANIZE + " TEXT, "
                + RosterEntry.Column.EMAIL + " TEXT, "
                + RosterEntry.Column.PIC + " TEXT, "
                + RosterEntry.Column.FAVORITE + " INTEGER, "
                + RosterEntry.Column.TYPE + " INTEGER ) ");

        db.execSQL("CREATE TABLE " + LastMessage.TABLE_NAME + " ( "
                + LastMessage.Column.ROOM + " TEXT PRIMARY KEY, "
                + LastMessage.Column.TIME + " DATETIME, "
                + LastMessage.Column.COUNT + " INTEGER DEFAULT 0, "
                + LastMessage.Column.MESSAGE + " TEXT )");

        db.execSQL("CREATE TABLE " + Logs.TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Logs.Column.ROOM + " TEXT, "
                + Logs.Column.MESSAGE + " TEXT, "
                + Logs.Column.SENDER + " TEXT, "
                + Logs.Column.SENDER_NAME + " TEXT, "
                + Logs.Column.SENDER_IMAGE + " TEXT, "
                + Logs.Column.TIME + " DATETIME )");

        db.execSQL("CREATE TABLE " + Case.TABLE_NAME + " ( "
                + Case.Column.NAME + " TEXT, "
                + Case.Column.ID + " INTEGER UNIQUE, "
                + Case.Column.ID_CASE + " INTEGER , "
                + Case.Column.ISNEW + " INTEGER , "
                + Case.Column.DATE + " DATETIME )");

        db.execSQL("CREATE TABLE " + Stickers.TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Stickers.Column.NAME + " TEXT UNIQUE, "
                + Stickers.Column.IMAGE + " BLOB )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RosterEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LastMessage.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Logs.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Stickers.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Case.TABLE_NAME);
        onCreate(db);
    }

    //////////// //////////// CASE /////////// /////////////////////////
    public void clearCaseTable() {
        SQLiteDatabase db = databaseChatHelper.getWritableDatabase();
        db.execSQL("delete from " + Case.TABLE_NAME);
    }

    public void addCase(CaseListDataObject caseData) {
        DateFormat format = new SimpleDateFormat("d/MM/yyyy HH:mm");
        SQLiteDatabase db = databaseChatHelper.getWritableDatabase();
        //System.out.println("date " +caseData.getDate());
        Date date = null;
        try {
            date = format.parse(convertDateTimeToEng(caseData.getDate()));
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        ContentValues values = new ContentValues();
        values.put(Case.Column.ID, caseData.getComplaintId());
        values.put(Case.Column.ID_CASE, caseData.getCasesId());
        values.put(Case.Column.NAME, caseData.getCaseName());
        values.put(Case.Column.DATE, dateFormat.format(date));
        values.put(Case.Column.ISNEW, (caseData.getIsNew()) ? 0 : 1);

        // insert row
        long todo_id = db.replace(Case.TABLE_NAME, null, values);
        //System.out.println("DAtabase Add"+todo_id);
    }

    public List<CaseListDataObject> getCaseList() {
        List<CaseListDataObject> data = new ArrayList<>();
        String selectQuery = " SELECT * FROM " + Case.TABLE_NAME + " ORDER BY " + Case.Column.DATE + " DESC ";
        //Log.e("Database Chat ", "Database query>>>" + selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                Calendar calendar = null;
                try {
                    Date date = dateFormat.parse(c.getString((c.getColumnIndex(Case.Column.DATE))));
                    calendar = DateToCalendar(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String strDate = "" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + "/" + (calendar.get(Calendar.YEAR) + 543) + " " + calendar.getTime().getHours() + ":" + calendar.getTime().getMinutes();
                //System.out.println("Date>>>>>>>>>>>>>"+strDate);
                CaseListDataObject caseData = new CaseListDataObject();
                caseData.setCaseName(c.getString((c.getColumnIndex(Case.Column.NAME))));
                caseData.setCasesId(c.getInt(c.getColumnIndex(Case.Column.ID_CASE)));
                caseData.setComplaintId(c.getInt(c.getColumnIndex(Case.Column.ID)));
                caseData.setDate(strDate);
                caseData.setIsNew((c.getInt(c.getColumnIndex(Case.Column.ISNEW)) == 0) ? true : false);
                data.add(caseData);

            } while (c.moveToNext());
        }

        return data;

    }

    String convertDateTimeToEng(String e) {
        String[] date = e.split("/");
        // System.out.println(date[2]);
        String[] time = date[2].split(" ");
        String year = String.valueOf(Integer.parseInt(time[0]) - 543);
        return date[0] + "/" + date[1] + "/" + year + " " + time[1];
    }

    //////////////////////////////////////////////////////////////
    public void addUsers(Users users) {
        SQLiteDatabase db = databaseChatHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RosterEntry.Column.JID, users.getJid());
        values.put(RosterEntry.Column.FAVORITE, (users.getFavorite()) ? 1 : 0);
        values.put(RosterEntry.Column.NAME, users.getName());
        values.put(RosterEntry.Column.PIC, users.getPic());
        values.put(RosterEntry.Column.TYPE, users.getType());
        long todo_id = db.replace(RosterEntry.TABLE_NAME, null, values);
        //Log.d("DatabaseChat", "Add User " + todo_id);
    }

    public void addStricker(String name, byte[] image) {
        SQLiteDatabase db = databaseChatHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Stickers.Column.NAME, name);
        values.put(Stickers.Column.IMAGE, image);
        long lodo = db.replace(Stickers.TABLE_NAME, null, values);
        //System.out.println(lodo);
    }

    public void clearUserTable() {
        SQLiteDatabase db = databaseChatHelper.getWritableDatabase();
        db.execSQL("delete from " + RosterEntry.TABLE_NAME);
    }

    public ArrayList<byte[]> getSticker() {
        ArrayList data = new ArrayList();
        String selectQuery = " SELECT * FROM " + Stickers.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                // Log.e("Database qry Image id", "" + c.getString((c.getColumnIndex(Stickers.Column.NAME))));
                byte[] image = c.getBlob((c.getColumnIndex(Stickers.Column.IMAGE)));
                data.add(image);
            } while (c.moveToNext());
        }
        return data;
    }


    public void updateFavorUser(String jid, Boolean bool) {
        SQLiteDatabase db = databaseChatHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RosterEntry.Column.FAVORITE, (bool) ? 1 : 0);
        db.update(RosterEntry.TABLE_NAME, values, RosterEntry.Column.JID + " = '" + jid + "'", null);
    }


    public ArrayList<Users> getUsers() {
        ArrayList<Users> arrUsers = new ArrayList<>();
        String selectQuery = " SELECT * FROM " + RosterEntry.TABLE_NAME;
        // Log.e("Database Chat ", "Database query>>>" + selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                Users users = new Users();
                String name = c.getString((c.getColumnIndex(RosterEntry.Column.NAME)));
                String jid = c.getString((c.getColumnIndex(RosterEntry.Column.JID)));
                String pic = c.getString((c.getColumnIndex(RosterEntry.Column.PIC)));
                int type = c.getInt((c.getColumnIndex(RosterEntry.Column.TYPE)));
                Boolean favorite = (c.getInt((c.getColumnIndex(RosterEntry.Column.FAVORITE))) == 1) ? true : false;

                //System.out.println(name);
                // System.out.println(jid);

                users.setName(name);
                users.setJid(jid);
                users.setPic(pic);
                users.setType(type);
                users.setFavorite(favorite);

                arrUsers.add(users);

            } while (c.moveToNext());
        }
        return arrUsers;
    }

    public ArrayList<Users> searchUsers(String search) {
        ArrayList<Users> arrUsers = new ArrayList<>();
        String selectQuery = " SELECT * FROM " + RosterEntry.TABLE_NAME
                + " WHERE "
                + RosterEntry.Column.JID + " like '%" + search + "%' or " + RosterEntry.Column.NAME + " like '%" + search + "%'";
        //Log.e("Database Chat ", "Database query>>>" + selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                Users users = new Users();
                String name = c.getString((c.getColumnIndex(RosterEntry.Column.NAME)));
                String jid = c.getString((c.getColumnIndex(RosterEntry.Column.JID)));
                String pic = c.getString((c.getColumnIndex(RosterEntry.Column.PIC)));
                int type = c.getInt((c.getColumnIndex(RosterEntry.Column.TYPE)));
                Boolean favorite = (c.getInt((c.getColumnIndex(RosterEntry.Column.FAVORITE))) == 1) ? true : false;

                //System.out.println(name);
                //System.out.println(jid);

                users.setName(name);
                users.setJid(jid);
                users.setPic(pic);
                users.setType(type);
                users.setFavorite(favorite);

                arrUsers.add(users);

            } while (c.moveToNext());
        }
        return arrUsers;
    }

    public Users getOneUsers(String nameUser) {
        Users users = null;
        String selectQuery = " SELECT * FROM " + RosterEntry.TABLE_NAME + " WHERE " + RosterEntry.Column.JID + "= '" + nameUser + "'";
        //Log.e("Database Chat ", "Database query>>>" + selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {

            users = new Users();
            String name = c.getString((c.getColumnIndex(RosterEntry.Column.NAME)));
            String jid = c.getString((c.getColumnIndex(RosterEntry.Column.JID)));
            String pic = c.getString((c.getColumnIndex(RosterEntry.Column.PIC)));
            int type = c.getInt((c.getColumnIndex(RosterEntry.Column.TYPE)));
            Boolean favorite = (c.getInt((c.getColumnIndex(RosterEntry.Column.FAVORITE))) == 1) ? true : false;

            // System.out.println(name);
            //System.out.println(jid);

            users.setName(name);
            users.setJid(jid);
            users.setPic(pic);
            users.setType(type);
            users.setFavorite(favorite);

        }
        return users;
    }


    /*
     * Creating a todo
     */
    public long addLogs(Messages messages) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        try {
            date = sdf.parse(messages.getTime());// all done
            //System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Log.d("LOPPPPPPPPPPPPP", "" + messages.getSender());
        values.put(Logs.Column.ROOM, messages.getRoom());
        values.put(Logs.Column.MESSAGE, messages.getMessage());
        values.put(Logs.Column.TIME, dateFormat.format(date));
        values.put(Logs.Column.SENDER, messages.getSender());
        values.put(Logs.Column.SENDER_IMAGE, messages.getSenderImage());
        values.put(Logs.Column.SENDER_NAME, messages.getSenderName());

        // insert row
        long todo_id = db.insert(Logs.TABLE_NAME, null, values);
        System.out.println(todo_id);
        String checkCase = messages.getRoom().split("@")[0];
        checkCase = checkCase.split("-")[0];
        System.out.println(checkCase);
        if (!checkCase.matches("case") && !checkCase.matches("complaint")) {
            saveLastMessage(messages);
        }


        return todo_id;
    }


    public void saveLastMessage(Messages messages) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        try {
            date = sdf.parse(messages.getTime());// all done
            // System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        values.put(LastMessage.Column.ROOM, messages.getRoom());
        values.put(LastMessage.Column.MESSAGE, messages.getMessage());
        values.put(LastMessage.Column.TIME, dateFormat.format(date));
        values.put(LastMessage.Column.COUNT, getCountLastMessage(messages.getRoom()));
        db.replace(LastMessage.TABLE_NAME, null, values);
    }

    public void clearCountLastMessage(String room) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LastMessage.Column.COUNT, 0);
        String condition = LastMessage.Column.ROOM + "='" + room + "'";
        db.update(LastMessage.TABLE_NAME, values, condition, null);
        BusProvider.getInstance().post("update_last_message");
    }

    public int getCountLastMessage(String room) {
        int count = 1;
        String selectQuery = " SELECT " + LastMessage.Column.COUNT + " FROM " + LastMessage.TABLE_NAME +
                " WHERE " + LastMessage.Column.ROOM + "= '" + room + "'";
        //System.out.println(selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            count = c.getInt(c.getColumnIndex(LastMessage.Column.COUNT));
            count++;
            //System.out.println(count);
        }
        return count;
    }

    /*  ใช้ดึง LastMessage จาก Database*/

    public ArrayList<LastMessageData> getLastMessage() {
        ArrayList<LastMessageData> lastmessage = new ArrayList<>();
        //Users user = new Users("telember@pattaya-data", "kittipong bunmuang", "pic", 0);
        String selectQuery = " SELECT * FROM  " + LastMessage.TABLE_NAME +
                " LEFT JOIN " + RosterEntry.TABLE_NAME + " ON " +
                LastMessage.Column.ROOM + " = " + RosterEntry.Column.JID +
                " ORDER BY " + LastMessage.Column.TIME + " DESC ";

        //Log.e("Database Chat", "Database query>>" + selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                try {
                    date = dateFormat.parse(c.getString((c.getColumnIndex(LastMessage.Column.TIME))));
                    // System.out.println(sdf.format(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Users users = new Users();
                String name = c.getString((c.getColumnIndex(RosterEntry.Column.NAME)));
                String jid = c.getString((c.getColumnIndex(RosterEntry.Column.JID)));
                String pic = c.getString((c.getColumnIndex(RosterEntry.Column.PIC)));
                int type = c.getInt((c.getColumnIndex(RosterEntry.Column.TYPE)));
                Boolean favorite = (c.getInt((c.getColumnIndex(RosterEntry.Column.FAVORITE))) == 1) ? true : false;

                //System.out.println(name);
                //System.out.println(jid);

                users.setName(name);
                users.setJid(jid);
                users.setPic(pic);
                users.setType(type);
                users.setFavorite(favorite);

                LastMessageData lastMessageData = new LastMessageData();
                String message = c.getString((c.getColumnIndex(LastMessage.Column.MESSAGE)));
                // System.out.println(message);
                // System.out.println(c.getString((c.getColumnIndex(LastMessage.Column.ROOM))));

                lastMessageData.setTime(sdf.format(date));
                lastMessageData.setUser(users);
                lastMessageData.setMessage(message);
                lastMessageData.setCount(c.getInt(c.getColumnIndex(LastMessage.Column.COUNT)));

                lastmessage.add(lastMessageData);


            } while (c.moveToNext());
        }
        return lastmessage;
    }


    public boolean deleteLastMessage(String room) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.delete(LastMessage.TABLE_NAME, LastMessage.Column.ROOM + "='" + room + "'", null) > 0;
    }


    //  update Logs เมื่อหน้าเเชท ถูกเปิดโดย จะรันเป็น background เมื่ออัพเดทเสดเเล้วจะ ส่ง broadcast ให้ ui refresh
    public long UpdateChatLogs(String room, ArrayList<Messages> messagesArr) {
        new InsertTask(room, messagesArr).execute();
        return 0;
    }

  /*
 * getting all todos
 * */

    public Map<String, Messages> getLastLogs(String room, int count) {
        //ArrayList<Messages> data = new ArrayList();
        Map<String, Messages> map = new HashMap<>();
        String selectQuery = " SELECT * FROM " + Logs.TABLE_NAME + " WHERE "
                + Logs.Column.ROOM + "= '" + room + "'" +
                " ORDER BY " + Logs.Column.TIME + " DESC  Limit " + count;

        // Log.e("Database Chat ", "Database query>>>" + selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                try {
                    date = dateFormat.parse(c.getString((c.getColumnIndex(Logs.Column.TIME))));
                    //System.out.println(sdf.format(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Messages messages = new Messages();
                messages.setMessage(c.getString((c.getColumnIndex(Logs.Column.MESSAGE))));
                messages.setTime(dateFormat.format(date));
                messages.setRoom(c.getString((c.getColumnIndex(Logs.Column.ROOM))));
                messages.setSender(c.getString((c.getColumnIndex(Logs.Column.SENDER))));
                //Log.e("Database  Logss >>", "Last Message " +messages.getMessage());
                map.put(messages.getTime(), messages);
            } while (c.moveToNext());
        }
        return map;
    }

    public ArrayList<Messages> getLogs(String room) {
        ArrayList<Messages> data = new ArrayList();
        String selectQuery = "SELECT  * FROM " + Logs.TABLE_NAME + " WHERE "
                + Logs.Column.ROOM + "= '" + room + "'" +
                " ORDER BY " + Logs.Column.TIME + " ASC";

        // Log.e("Database Chat", "Database query >>>" + selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                try {
                    date = dateFormat.parse(c.getString((c.getColumnIndex(Logs.Column.TIME))));
                    //System.out.println(sdf.format(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                Messages messages = new Messages();
                messages.setMessage(c.getString((c.getColumnIndex(Logs.Column.MESSAGE))));
                messages.setTime(sdf.format(date));
                messages.setRoom(c.getString((c.getColumnIndex(Logs.Column.ROOM))));
                messages.setSender(c.getString((c.getColumnIndex(Logs.Column.SENDER))));
                messages.setSenderImage(c.getString((c.getColumnIndex(Logs.Column.SENDER_IMAGE))));
                messages.setSenderName(c.getString((c.getColumnIndex(Logs.Column.SENDER_NAME))));
                data.add(messages);

            } while (c.moveToNext());
        }

        return data;
    }


    class InsertTask extends AsyncTask<Void, Void, Boolean> {


        int COUNT_TEMP_MESSAGE_DATA = 10;
        String room;
        Date time = null;
        String lastMessage = "";
        String lastMessageInDB = "";
        Map<String, Messages> map = new HashMap<>();
        ArrayList<Messages> messageses;
        ArrayList<String> duplicateMsg = new ArrayList<>();
        SQLiteDatabase db = databaseChatHelper.getWritableDatabase();


        public InsertTask(String room, ArrayList<Messages> messageses) {
            this.messageses = messageses;
            this.room = room;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            Boolean success = false;

            try {
                //place insert code here
                ContentValues values = new ContentValues();
                //ดึงข้อมูลมาจาก database จำนวนหนึ่งเก็บไว้ โดยจะดึงจากข้อความล่าสุด
                map = getLastLogs(room, COUNT_TEMP_MESSAGE_DATA);
                //Log.e("Log map" ,""+map);

                //ทำการ clear database เพื่อทำการเพิ่มค่า ใหม่ลงไป
                if (messageses.size() != 0) {
                    int delete = db.delete(Logs.TABLE_NAME, Logs.Column.ROOM + "= '" + room + "'", null);
                    Log.e("Database Chat", "DELETE TABLE" + delete);
                }


                int count = (messageses.size() >= 120) ? messageses.size() - 60 : 0; //ตรวจข้อมูลจาก server ที่ได้รับมามีมากกกว่าพอไหม
                //Log.e("TAG MAP Message Size",""+messageses.size()+"    "+count);

                //ดึงข้อมูลมาจำนวนหนึง เพื่อเก็บลงใน database
                for (int i = messageses.size() - 1; i >= count; i--) {
                    Messages messages = messageses.get(i);
                    // Log.d("TAG Message " +i+"  COUNT"+count, messageses.get(i).toString());
                    try {
                        date = dateFormat.parse(messages.getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (i > messageses.size() - 6) {
                        duplicateMsg.add(messages.getMessage());
                    }
                    values.put(Logs.Column.ROOM, messages.getRoom());
                    values.put(Logs.Column.MESSAGE, messages.getMessage());
                    values.put(Logs.Column.TIME, dateFormat.format(date));
                    values.put(Logs.Column.SENDER, messages.getSender());


                    User user = openfireQueary.getUserInTask(messages.getSender().split("@")[0]);
                    values.put(Logs.Column.SENDER_NAME, user.getName());
                    values.put(Logs.Column.SENDER_IMAGE, user.getProperty().get("userImage"));


                    db.insert(Logs.TABLE_NAME, null, values);
                    // Date dateTime = dateFormat.parse(messages.getTime());
                   // Log.d("Time >>>>>>>>>>>>>:", messages.getMessage() + ">>>>>>>>>>>>>>>>>>" + date);

                    //ตรวจดูว่าข้อความไหนคือข้อความล่าสุด
                    if (time == null || time.before(date)) {
                        time = date;
                        lastMessage = messages.getMessage();
                        //Log.d("Lastmessag", lastMessage + ">>>>>>>>>>>>>>>>>>" + time);
                    }

                    Messages tempMsg = map.get(messages.getTime()); // map เพื่อดูว่าซึ้ากับ message ที่ดึงมาจา server หรือไม่


                    if (tempMsg == null) {
                        // Log.e("DATA TIME  >>>>", " Null");
                    } else {
                        //Log.e("DATA TIME  HAVE>>>>", "" + tempMsg.getTime());
                       // Log.e("DATA TIME  HAVE REMOVED", "" + tempMsg.getMessage());
                        map.remove(messages.getTime());
                    }

                    //Log.d("InSeterT  ", messages.getTime() + "   " + todo_id);
                }

                success = true;
            } catch (Exception e) {
                if (e.getMessage() != null)
                    e.printStackTrace();
            }
            if (success == true) {
                Log.e("Database Chat", "SUCCESS DATA INSERT>>>>>>>>>>>>>>>>>>>>>>>");
                Boolean isOk = false;
                Date lastMessageTime = null;

                //ขั้นตอนการตรวจสอบ ข้อมูลที่ดึงมาจาก database กับ ทีาจาก server มีค่าตรงกันไหม
                for (Map.Entry<String, Messages> entry : map.entrySet()) {
                    ContentValues values = new ContentValues();
                    Date date = null;
                    Messages messages = entry.getValue();
                    System.out.println("Key : " + entry.getKey() + " Value : " + messages.toString());
                    try {
                        date = dateFormat.parse(messages.getTime());
                    } catch (ParseException e) {
                        //Log.e("TAG ERROR", ""+e);
                        e.printStackTrace();
                    }
                    //เช็ค time ==  null เพราะ ไม่มีข้อมูล History ให้ get
                    if (time != null && date != null && date.after(time)) {
                        // เช็คเวลา ที่ดึงมาจาก DB ว่าอันไหนล่าสุด
                        if (lastMessageTime == null || lastMessageTime.after(date)) {
                            lastMessageTime = date;
                            lastMessageInDB = messages.getMessage();
                        }

                    }
                }
                // ตรวจสอบว่า ข้อความล่าสุด ที่เอามาจา DB กับ server ต้องไม่ตรงกัน จึง ให้บันทึก ลง DB
                if (!lastMessageInDB.matches(lastMessage)) {
                    for (Map.Entry<String, Messages> entry : map.entrySet()) {
                        ContentValues values = new ContentValues();
                        Date date = null;
                        Messages messages = entry.getValue();
                        try {
                            date = dateFormat.parse(messages.getTime());
                        } catch (ParseException e) {
                            //Log.e("TAG ERROR", ""+e);
                            e.printStackTrace();
                        }
                        if (time != null && date != null && date.after(time)) {
                            System.out.println("Key : " + entry.getKey() + " Value : " + messages.toString());
                            values.put(Logs.Column.ROOM, messages.getRoom());
                            values.put(Logs.Column.MESSAGE, messages.getMessage());
                            values.put(Logs.Column.TIME, dateFormat.format(date));
                            values.put(Logs.Column.SENDER, messages.getSender());


                            User user = openfireQueary.getUserInTask(messages.getSender().split("@")[0]);
                            values.put(Logs.Column.SENDER_NAME, user.getName());
                            values.put(Logs.Column.SENDER_IMAGE, user.getProperty().get("userImage"));
                            db.insert(Logs.TABLE_NAME, null, values);

                            System.out.println("Database Chat :: InSeterT  Other " + messages.getMessage() + "   ");

                        }
                    }
                }


            }
            return success;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            // Log.e("room ",room);
            BusProvider.getInstance().post(room);
        }
    }


}


class RosterEntry {

    static final String TABLE_NAME = "roster_entry";

    public class Column {
        static final String JID = "jid";
        static final String NAME = "name";
        static final String TYPE = "type";
        static final String ORGANIZE = "organize";
        static final String EMAIL = "email";
        static final String PIC = "pic";
        static final String FAVORITE = "favorite";
    }

}

class LastMessage {
    static final String TABLE_NAME = "last_message";

    public class Column {
        static final String ROOM = "room";
        static final String MESSAGE = "message";
        static final String TIME = "time";
        static final String COUNT = "count";


    }
}

class Logs {
    static final String TABLE_NAME = "logs";

    public class Column {
        static final String ID = BaseColumns._ID;
        static final String ROOM = "room";
        static final String TIME = "time";
        static final String MESSAGE = "msg";
        static final String SENDER = "sender";
        static final String SENDER_NAME = "name";
        static final String SENDER_IMAGE = "image";
    }
}

class Stickers {
    static final String TABLE_NAME = "sticker";

    public class Column {
        static final String ID = BaseColumns._ID;
        static final String NAME = "name";
        static final String IMAGE = "image";

    }
}

class Case {
    static final String TABLE_NAME = "casedata";

    public class Column {
        static final String ID = "id";
        static final String ID_CASE = "idcase";
        static final String NAME = "name";
        static final String DATE = "date";
        static final String ISNEW = "isnew";

    }
}




