package com.pattaya.pattayacallcenter.share;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.pattaya.pattayacallcenter.Application;
import com.pattaya.pattayacallcenter.Data.MasterData;
import com.pattaya.pattayacallcenter.Data.Users;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.chat.DatabaseChatHelper;
import com.pattaya.pattayacallcenter.chat.XMPPService;
import com.pattaya.pattayacallcenter.chat.restadatper.OpenfireQueary;
import com.pattaya.pattayacallcenter.chat.restadatper.RestAdapterOpenFire;
import com.pattaya.pattayacallcenter.chat.xmlobject.Roster.Roster;
import com.pattaya.pattayacallcenter.chat.xmlobject.Roster.RosterItem;
import com.pattaya.pattayacallcenter.chat.xmlobject.User;
import com.pattaya.pattayacallcenter.guest.CaseListActivity;
import com.pattaya.pattayacallcenter.member.MemberMainActivity;
import com.pattaya.pattayacallcenter.webservice.RestFulQueary;
import com.pattaya.pattayacallcenter.webservice.WebserviceConnector;
import com.pattaya.pattayacallcenter.webservice.object.AcessTokenObject;
import com.pattaya.pattayacallcenter.webservice.object.GetTokenObject;
import com.pattaya.pattayacallcenter.webservice.object.LoginObject;
import com.pattaya.pattayacallcenter.webservice.object.SaveFacebookObject;
import com.pattaya.pattayacallcenter.webservice.object.UserDataObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class LoginActivity extends Activity {
    final Context context = this;
    Button btnRegis;
    Button btnLogin;
    Button btnForget;
    TextView userName;
    TextView userPass;
    LoginButton btnFacebook;
    ProfileTracker mProfileTracker;
    RestAdapter webserviceConnector = WebserviceConnector.getInstance();
    // AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    SharedPreferences spConfig;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String token;

    List<String> permissionNeeds = Arrays.asList("user_photos", "email", "user_birthday", "user_friends");
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        btnRegis = (Button) findViewById(R.id.btn_register);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnForget = (Button) findViewById(R.id.btn_forgetPassword);
        btnFacebook = (LoginButton) findViewById(R.id.authButton);

        userName = (TextView) findViewById(R.id.txt_userName);
        userPass = (TextView) findViewById(R.id.txt_password);


        btnFacebook.setReadPermissions(permissionNeeds);
        mCallbackManager = CallbackManager.Factory.create();
        setClickListener();
        init();

        final View activityRootView = findViewById(R.id.activityRoot);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...
                    System.out.println("move");
                    activityRootView.scrollTo(0, btnFacebook.getBottom());
                }
            }
        });
    }

    void init() {
        spConfig = getSharedPreferences(MasterData.SHARED_NAME_CONFIG_FILE, Context.MODE_PRIVATE);
        sp = getSharedPreferences(MasterData.SHARED_NAME_USER_FILE, Context.MODE_PRIVATE);
        String token = spConfig.getString(MasterData.SHARED_CONFIG_TOKEN, null);
        if (token != null) {
            String org = sp.getString(MasterData.SHARED_USER_USER_TYPE, null);
            showIntent(org);
        } else {
            SharedPreferences settings = Application.getContext().getSharedPreferences(MasterData.SHARED_CASE_COUNT, Context.MODE_PRIVATE);
            settings.edit().clear().commit();
        }

    }


    void showIntent(String type) {
        String jid = sp.getString(MasterData.SHARED_USER_JID, null);
        System.out.println("type >>>>>>>>>>>>>>>>>>>>>>" + type);
        System.out.println("jid >>>>>>>>>>>>>>>>>>>>>>" + jid);
        if (jid != null) {
            if (type != null && type.equalsIgnoreCase("Member")) {
                startService(new Intent(this, XMPPService.class));
                Intent intent = new Intent(getApplicationContext(), MemberMainActivity.class);
                startActivity(intent);
                finish();
            } else {
                startService(new Intent(this, XMPPService.class));
                Intent intent = new Intent(getApplicationContext(), CaseListActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "This user cannot login.", Toast.LENGTH_SHORT)
                    .show();
        }


    }

    void setClickListener() {
        btnForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgetPasswordActivity.class);
                startActivity(intent);

            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog ringProgressDialog = ProgressDialog.show(context, null, getResources().getString(R.string.please_wait), true);
                ringProgressDialog.setCancelable(true);

                if (userName.getText().toString().matches("")) {
                    ringProgressDialog.dismiss();
                    YoYo.with(Techniques.Shake).duration(1000).playOn(userName);
                    Toast.makeText(getApplication(),
                            getResources().getString(R.string.please_enter_fill), Toast.LENGTH_SHORT)
                            .show();
                } else if (userPass.getText().toString().matches("")) {
                    ringProgressDialog.dismiss();
                    YoYo.with(Techniques.Shake).duration(1000).playOn(userPass);

                    Toast.makeText(getApplication(),
                            getResources().getString(R.string.please_enter_fill), Toast.LENGTH_SHORT)
                            .show();
                } else {
                    LoginObject loginObject = new LoginObject();
                    loginObject.setUsername(userName.getText().toString());
                    loginObject.setPassword(userPass.getText().toString());
                    final RestFulQueary login = webserviceConnector.create(RestFulQueary.class);

                    /**
                     * การ login เเละ authen ไปที่ server
                     * */
                    login.authenUser(loginObject, new Callback<UserDataObject>() {
                        @Override
                        public void success(final UserDataObject userDataObject, retrofit.client.Response response) {


                            //ไม่มี user ในระบบ
                            if (userDataObject.getUserId() == 0) {
                                ringProgressDialog.dismiss();
                                alertDialogFailtoServer(getResources().getString(R.string.pass_or_user_incorrect));

                            } else {
                                //เมื่อ login สำเร็จ
                                GetTokenObject getTokenObject = new GetTokenObject("android");// android คือ clientId
                                /**
                                 * การ login สำเร็จ เเล้วต้องการขอ TokenId
                                 * */
                                login.getToken(getTokenObject, new Callback<AcessTokenObject>() {
                                    @Override
                                    public void success(AcessTokenObject acessTokenObject, retrofit.client.Response response) {

                                        token = acessTokenObject.getAcessToken();
                                        //ตรวจสอบว่า user เคย login ในระบบ หรือ ไม่ ถ้าไม่ จะ return -10
                                        Integer userID = sp.getInt(MasterData.SHARED_USER_USER_ID, -10);
                                        if (acessTokenObject.getAcessToken() != null) {
                                            //เป็น user ใหม่
                                            if (userID == -10) {
                                                Log.e("Get-NEWTOKEN", acessTokenObject.getAcessToken() + " Token :" + token + "  ID " + userDataObject.getDisplayName());

                                                editor = sp.edit();
                                                editor.putInt(MasterData.SHARED_USER_USER_ID, userDataObject.getUserId());
                                                editor.putString(MasterData.SHARED_USER_USERNAME, userDataObject.getUsername());
                                                editor.putString(MasterData.SHARED_USER_IMAGE, userDataObject.getUserImage());
                                                editor.putString(MasterData.SHARED_USER_DISPLAY_NAME, userDataObject.getDisplayName());
                                                editor.putString(MasterData.SHARED_USER_ORGANIZE, userDataObject.getOrgId());
                                                editor.putString(MasterData.SHARED_USER_USER_TYPE, userDataObject.getUserTypeEN());
                                                editor.putString(MasterData.SHARED_USER_JID, userDataObject.getjId());
                                                editor.putString(MasterData.SHARED_USER_FRIST_NAME, userDataObject.getFirstname());
                                                editor.putString(MasterData.SHARED_USER_LAST_NAME, userDataObject.getLastname());

                                                editor.commit();
                                                Log.e("Tag New Login>>>", " UserId " + sp.getInt("USER_ID", -10));
                                                //ถ้า เป็น member
                                                if (userDataObject.getUserTypeEN().matches("Member")) {
                                                    getRosterFromServer(userDataObject, ringProgressDialog);
                                                } else {//ถ้าเป็นอย่างอื่น เช่น guest
                                                    editor = spConfig.edit();
                                                    editor.putString(MasterData.SHARED_CONFIG_TOKEN, token);
                                                    editor.putString(MasterData.SHARED_CONFIG_CLIENT_ID, "android");
                                                    editor.putBoolean(MasterData.SHARED_CONFIG_IS_FIRST, true);
                                                    editor.commit();
                                                    ringProgressDialog.dismiss();
                                                    showIntent(userDataObject.getUserTypeEN());
                                                }
                                            } else {// มี user เคย login เเล้ว
                                                //เป็น user ที่ค้างอยู่ในระบบ
                                                if (userID == userDataObject.getUserId()) {
                                                    Log.d("Login Check user", "" + userDataObject.getDisplayName() + " Hello Again");
                                                    editor = sp.edit();
                                                    editor.putInt(MasterData.SHARED_USER_USER_ID, userDataObject.getUserId());
                                                    editor.putString(MasterData.SHARED_USER_USERNAME, userDataObject.getUsername());
                                                    editor.putString(MasterData.SHARED_USER_IMAGE, userDataObject.getUserImage());
                                                    editor.putString(MasterData.SHARED_USER_DISPLAY_NAME, userDataObject.getDisplayName());
                                                    editor.putString(MasterData.SHARED_USER_ORGANIZE, userDataObject.getOrgId());
                                                    editor.putString(MasterData.SHARED_USER_USER_TYPE, userDataObject.getUserTypeEN());
                                                    editor.putString(MasterData.SHARED_USER_JID, userDataObject.getjId());
                                                    editor.putString(MasterData.SHARED_USER_FRIST_NAME, userDataObject.getFirstname());
                                                    editor.putString(MasterData.SHARED_USER_LAST_NAME, userDataObject.getLastname());
                                                    editor.commit();


                                                    editor = spConfig.edit();
                                                    editor.putString(MasterData.SHARED_CONFIG_TOKEN, token);
                                                    editor.putString(MasterData.SHARED_CONFIG_CLIENT_ID, "android");
                                                    editor.putBoolean(MasterData.SHARED_CONFIG_IS_FIRST, true);
                                                    editor.commit();
                                                    ringProgressDialog.dismiss();
                                                    showIntent(userDataObject.getUserTypeEN());

                                                } else {//เป็น user ใหม่
                                                    ringProgressDialog.setMessage("Downloading data From ChatServer");
                                                    Log.d("Login Check user", "" + userDataObject.getDisplayName() + " New User");
                                                    // ลบ database ของ user เดิม
                                                    DatabaseChatHelper.init().close();
                                                    Application.getContext().deleteDatabase(DatabaseChatHelper.DB_NAME);

                                                    File database = getApplicationContext().getDatabasePath(DatabaseChatHelper.DB_NAME);
                                                    if (!database.exists()) {
                                                        // Database does not exist so copy it from assets here
                                                        Log.i("Database", "Not Found");
                                                    } else {
                                                        Log.i("Database", "Found");
                                                    }

                                                    if (userDataObject.getUserTypeEN().matches("Member")) {
                                                        getRosterFromServer(userDataObject, ringProgressDialog);
                                                    } else {
                                                        editor = sp.edit();
                                                        editor.putInt(MasterData.SHARED_USER_USER_ID, userDataObject.getUserId());
                                                        editor.putString(MasterData.SHARED_USER_USERNAME, userDataObject.getUsername());
                                                        editor.putString(MasterData.SHARED_USER_IMAGE, userDataObject.getUserImage());
                                                        editor.putString(MasterData.SHARED_USER_DISPLAY_NAME, userDataObject.getDisplayName());
                                                        editor.putString(MasterData.SHARED_USER_ORGANIZE, userDataObject.getOrgId());
                                                        editor.putString(MasterData.SHARED_USER_USER_TYPE, userDataObject.getUserTypeEN());
                                                        editor.putString(MasterData.SHARED_USER_JID, userDataObject.getjId());
                                                        editor.putString(MasterData.SHARED_USER_FRIST_NAME, userDataObject.getFirstname());
                                                        editor.putString(MasterData.SHARED_USER_LAST_NAME, userDataObject.getLastname());
                                                        editor.commit();


                                                        editor = spConfig.edit();
                                                        editor.putString(MasterData.SHARED_CONFIG_TOKEN, token);
                                                        editor.putString(MasterData.SHARED_CONFIG_CLIENT_ID, "android");
                                                        editor.putBoolean(MasterData.SHARED_CONFIG_IS_FIRST, true);
                                                        editor.commit();
                                                        ringProgressDialog.dismiss();
                                                        showIntent(userDataObject.getUserTypeEN());
                                                    }
                                                }
                                            }
                                        } else {
                                            Log.d("Login Check Token", "Error");
                                            ringProgressDialog.dismiss();
                                            alertDialogFailtoServer(getResources().getString(R.string.cant_connect_server));
                                        }

                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        ringProgressDialog.dismiss();
                                        alertDialogFailtoServer(getResources().getString(R.string.cant_connect_server));
                                        Log.e("Error-Token", " " + error);

                                    }
                                });


                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            ringProgressDialog.dismiss();
                            alertDialogFailtoServer(getResources().getString(R.string.cant_connect_server));

                        }

                    });

                }

            }
        });
        btnRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
        btnFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        System.out.println("login success");
                        final SaveFacebookObject facebookData = new SaveFacebookObject();
                        final ProgressDialog ringProgressDialog = ProgressDialog.show(context, null, getResources().getString(R.string.please_wait), true);
                        ringProgressDialog.setCancelable(true);
                        ringProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        ringProgressDialog.setIndeterminate(true);
                        final Boolean[] boolprofile = {false, false};

                        if (Profile.getCurrentProfile() != null && AccessToken.getCurrentAccessToken() != null) {
                            boolprofile[0] = true;
                            facebookData.setFirstName(Profile.getCurrentProfile().getFirstName());
                            facebookData.setLastName(Profile.getCurrentProfile().getLastName());
                            facebookData.setImage(Profile.getCurrentProfile().getProfilePictureUri(100, 100).toString());
                            //  Log.i("TAG", Profile.getCurrentProfile().getName() + ", " + Profile.getCurrentProfile().getId() + ", " + Profile.getCurrentProfile().getLinkUri());
                        } else {
                            mProfileTracker = new ProfileTracker() {
                                @Override
                                protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                                    Log.v("facebook - profile", profile2.getFirstName());
                                    facebookData.setFirstName(profile2.getFirstName());
                                    facebookData.setLastName(profile2.getLastName());
                                    facebookData.setImage(profile2.getProfilePictureUri(100, 100).toString());
                                    boolprofile[0] = true;
                                    if (boolprofile[0] && boolprofile[1]) {
                                        seveFacebook(facebookData);
                                        ringProgressDialog.dismiss();
                                        LoginManager.getInstance().logOut();
                                    }

                                    mProfileTracker.stopTracking();
                                }
                            };
                            mProfileTracker.startTracking();
                        }


                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {


                                    @Override
                                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                                        boolprofile[1] = true;
                                        // Application code
                                        //   Log.e("LoginActivity", graphResponse.toString());
                                        try {
                                            String email = jsonObject.getString("email").toString();
                                            Log.e("email", email);
                                            facebookData.setUsername(email);
                                            token = jsonObject.getString("id").toString();

                                            if (boolprofile[0] && boolprofile[1]) {
                                                seveFacebook(facebookData);
                                                ringProgressDialog.dismiss();
                                                LoginManager.getInstance().logOut();
                                            }

                                            // Log.d("Shreks Fragment onSuccess", "" + profile.getProfilePictureUri(100, 100));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            LoginManager.getInstance().logOut();
                                        }
                                    }
                                }
                        );

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender, birthday");
                        request.setParameters(parameters);
                        request.executeAsync();


                    }

                    @Override
                    public void onCancel() {
                        //    System.out.println("/////////////////////////////////////");
                        // System.out.println(loginResult.);


                    }

                    @Override
                    public void onError(FacebookException e) {
                        //    System.out.println("/////////////////////////////////////");
                        // System.out.println(loginResult.);
                        System.out.println(e);


                    }
                }

        );
    }

    void seveFacebook(final SaveFacebookObject saveFacebookObject) {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(context, null, getResources().getString(R.string.please_wait), true);
        ringProgressDialog.setCancelable(true);
        ringProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        ringProgressDialog.setIndeterminate(true);

        final RestFulQueary login = webserviceConnector.create(RestFulQueary.class);
        login.saveUserFacebook(saveFacebookObject, new Callback<UserDataObject>() {
            @Override
            public void success(UserDataObject userDataObject, Response response) {
                editor = sp.edit();
                Integer userID = sp.getInt(MasterData.SHARED_USER_USER_ID, -10);
                //  Log.e("Facerbook ", "id    " + userID);
                if (userID == userDataObject.getUserId()) {
                    //   Log.e("Facerbook ", "id    " + saveFacebookObject.getFirstName());
                    //   Log.e("Facerbook ", "id    " + saveFacebookObject.getLastName());
                    editor.putInt(MasterData.SHARED_USER_USER_ID, userDataObject.getUserId());
                    editor.putString(MasterData.SHARED_USER_USERNAME, userDataObject.getUsername());

                    if (userDataObject.getUserImage() != null && !userDataObject.getUserImage().isEmpty()) {
                        editor.putString(MasterData.SHARED_USER_IMAGE, saveFacebookObject.getImage());
                    } else {
                        editor.putString(MasterData.SHARED_USER_IMAGE, userDataObject.getUserImage());
                    }

                    editor.putString(MasterData.SHARED_USER_DISPLAY_NAME, userDataObject.getDisplayName());
                    editor.putString(MasterData.SHARED_USER_ORGANIZE, userDataObject.getOrgId());
                    editor.putString(MasterData.SHARED_USER_USER_TYPE, userDataObject.getUserTypeEN());
                    editor.putString(MasterData.SHARED_USER_JID, userDataObject.getjId());

                    // System.out.println(userDataObject.getFirstname());
                    // System.out.println(userDataObject.getLastname());
                    if (userDataObject.getFirstname() == null || userDataObject.getFirstname().isEmpty()) {
                        editor.putString(MasterData.SHARED_USER_FRIST_NAME, saveFacebookObject.getFirstName());
                    } else {
                        editor.putString(MasterData.SHARED_USER_FRIST_NAME, userDataObject.getFirstname());
                    }
                    if (userDataObject.getLastname() == null || userDataObject.getLastname().isEmpty()) {
                        editor.putString(MasterData.SHARED_USER_LAST_NAME, saveFacebookObject.getLastName());
                    } else {
                        editor.putString(MasterData.SHARED_USER_LAST_NAME, userDataObject.getLastname());
                    }



                    editor.putBoolean(MasterData.SHARED_USER_FACEBOOK, true);
                    editor.commit();


                    editor = spConfig.edit();
                    editor.putString(MasterData.SHARED_CONFIG_TOKEN, token);
                    editor.putString(MasterData.SHARED_CONFIG_CLIENT_ID, "android");
                    editor.putBoolean(MasterData.SHARED_CONFIG_IS_FIRST, true);
                    editor.commit();
                    ringProgressDialog.dismiss();
                    showIntent(userDataObject.getUserTypeEN());
                } else {
                    Log.e("Facerbook ", "id    " + userDataObject.getUserId());

                    editor.putInt(MasterData.SHARED_USER_USER_ID, userDataObject.getUserId());
                    editor.putString(MasterData.SHARED_USER_USERNAME, userDataObject.getUsername());

                    if (userDataObject.getUserImage() != null && !userDataObject.getUserImage().isEmpty()) {
                        editor.putString(MasterData.SHARED_USER_IMAGE, saveFacebookObject.getImage());
                    } else {
                        editor.putString(MasterData.SHARED_USER_IMAGE, userDataObject.getUserImage());
                    }

                    editor.putString(MasterData.SHARED_USER_DISPLAY_NAME, userDataObject.getDisplayName());
                    editor.putString(MasterData.SHARED_USER_ORGANIZE, userDataObject.getOrgId());
                    editor.putString(MasterData.SHARED_USER_USER_TYPE, userDataObject.getUserTypeEN());
                    editor.putString(MasterData.SHARED_USER_JID, userDataObject.getjId());

                    if (userDataObject.getFirstname() == null || userDataObject.getFirstname().isEmpty()) {
                        editor.putString(MasterData.SHARED_USER_FRIST_NAME, saveFacebookObject.getFirstName());
                    } else {
                        editor.putString(MasterData.SHARED_USER_FRIST_NAME, userDataObject.getFirstname());
                    }
                    if (userDataObject.getLastname() == null || userDataObject.getLastname().isEmpty()) {
                        editor.putString(MasterData.SHARED_USER_LAST_NAME, saveFacebookObject.getLastName());
                    } else {
                        editor.putString(MasterData.SHARED_USER_LAST_NAME, userDataObject.getLastname());
                    }
                    editor.putBoolean(MasterData.SHARED_USER_FACEBOOK, true);

                    editor.commit();


                    editor = spConfig.edit();
                    editor.putString(MasterData.SHARED_CONFIG_TOKEN, token);
                    editor.putString(MasterData.SHARED_CONFIG_CLIENT_ID, "android");
                    editor.putBoolean(MasterData.SHARED_CONFIG_IS_FIRST, true);
                    editor.commit();


                    // ลบ database ของ user เดิม
                    DatabaseChatHelper.init().close();
                    Application.getContext().deleteDatabase(DatabaseChatHelper.DB_NAME);

                    File database = getApplicationContext().getDatabasePath(DatabaseChatHelper.DB_NAME);
                    if (!database.exists()) {
                        // Database does not exist so copy it from assets here
                        Log.i("Database", "Not Found");
                    } else {
                        Log.i("Database", "Found");
                    }
                    ringProgressDialog.dismiss();
                    showIntent(userDataObject.getUserTypeEN());


                }


            }

            @Override
            public void failure(RetrofitError error) {
                alertDialogFailtoServer(getResources().getString(R.string.cant_connect_server));
                LoginManager.getInstance().logOut();
                ringProgressDialog.dismiss();
            }
        });

    }


    void getRosterFromServer(final UserDataObject userDataObject, final ProgressDialog ringProgressDialog) {

        System.out.println("???????????????????????????Load DataINTO SERVER????????????????????????????????????????");
        System.out.println(userDataObject.toString());
        ringProgressDialog.setMessage("Download chat data");
        final Boolean[] bool = {false, false};

        bool[1] = true;
        final String jid = userDataObject.getjId();
        //final String name = jid.split("@")[0];
        final RestAdapter restAdapterOpenFire = RestAdapterOpenFire.getInstance();
        final OpenfireQueary openfireQueary = restAdapterOpenFire.create(OpenfireQueary.class);
        System.out.println("???????????????????????????Load DataINTO SERVER????????????????????????????????????????");
        openfireQueary.getRoster(jid.split("@")[0], new Callback<Roster>() {
            @Override
            public void success(final Roster roster, retrofit.client.Response response) {
                final ArrayList<String> tempArr = new ArrayList<>();

                if (roster.getRosterItem() != null) {

                    for (final RosterItem e : roster.getRosterItem()) {
                        // มี Group ใน Rooster
                        if (e.getGroups().getGroup() != null) {
                            //Friend
                            if (e.getGroups().getGroup().size() == 1) {

                                final Users mUser = new Users(e.getJid(), e.getNickname(), null, Users.TYPE_FRIEND);
                                openfireQueary.getUser(e.getJid().split("@")[0], new Callback<User>() {
                                    @Override
                                    public void success(User user, retrofit.client.Response response) {

                                        mUser.setName(user.getName());
                                        mUser.setPic(user.getProperty().get("userImage"));

                                        DatabaseChatHelper.init().addUsers(mUser);
                                        tempArr.add(mUser.getJid());
                                        if (tempArr.size() == roster.getRosterItem().size()) {

                                            bool[0] = true;
                                            if (bool[0] && bool[1]) {
                                                editor = sp.edit();
                                                editor.putInt(MasterData.SHARED_USER_USER_ID, userDataObject.getUserId());
                                                editor.putString(MasterData.SHARED_USER_USERNAME, userDataObject.getUsername());
                                                editor.putString(MasterData.SHARED_USER_IMAGE, userDataObject.getUserImage());
                                                editor.putString(MasterData.SHARED_USER_DISPLAY_NAME, userDataObject.getDisplayName());
                                                editor.putString(MasterData.SHARED_USER_ORGANIZE, userDataObject.getOrgId());
                                                editor.putString(MasterData.SHARED_USER_USER_TYPE, userDataObject.getUserTypeEN());
                                                editor.putString(MasterData.SHARED_USER_JID, userDataObject.getjId());
                                                editor.commit();


                                                editor = spConfig.edit();
                                                editor.putString(MasterData.SHARED_CONFIG_TOKEN, token);
                                                editor.putString(MasterData.SHARED_CONFIG_CLIENT_ID, "android");
                                                editor.putBoolean(MasterData.SHARED_CONFIG_IS_FIRST, true);
                                                editor.commit();

                                                showIntent(userDataObject.getUserTypeEN());
                                                ringProgressDialog.dismiss();
                                            }
                                        }
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        ringProgressDialog.dismiss();
                                        System.out.println("error = [" + error + "]");

                                    }
                                });


                            }
                            // Friend & Favorite
                            else if (e.getGroups().getGroup().size() == 2) {
                                System.out.println(e.getGroups().getGroup().get(0).getGroup());
                                System.out.println(e.getGroups().getGroup().get(1).getGroup());
                                final Users mUser = new Users(e.getJid(), e.getNickname(), null, Users.TYPE_FRIEND);
                                mUser.setFavorite(true);
                                openfireQueary.getUser(e.getJid().split("@")[0], new Callback<User>() {
                                    @Override
                                    public void success(User user, retrofit.client.Response response) {

                                        mUser.setName(user.getName());
                                        mUser.setPic(user.getProperty().get("userImage"));
                                        DatabaseChatHelper.init().addUsers(mUser);
                                        tempArr.add(mUser.getJid());
                                        if (tempArr.size() == roster.getRosterItem().size()) {

                                            bool[0] = true;

                                            if (bool[0] && bool[1]) {
                                                editor = sp.edit();
                                                editor.putInt(MasterData.SHARED_USER_USER_ID, userDataObject.getUserId());
                                                editor.putString(MasterData.SHARED_USER_USERNAME, userDataObject.getUsername());
                                                editor.putString(MasterData.SHARED_USER_IMAGE, userDataObject.getUserImage());
                                                editor.putString(MasterData.SHARED_USER_DISPLAY_NAME, userDataObject.getDisplayName());
                                                editor.putString(MasterData.SHARED_USER_ORGANIZE, userDataObject.getOrgId());
                                                editor.putString(MasterData.SHARED_USER_USER_TYPE, userDataObject.getUserTypeEN());
                                                editor.putString(MasterData.SHARED_USER_JID, userDataObject.getjId());
                                                editor.commit();


                                                editor = spConfig.edit();
                                                editor.putString(MasterData.SHARED_CONFIG_TOKEN, token);
                                                editor.putString(MasterData.SHARED_CONFIG_CLIENT_ID, "android");
                                                editor.putBoolean(MasterData.SHARED_CONFIG_IS_FIRST, true);
                                                editor.commit();
                                                showIntent(userDataObject.getUserTypeEN());
                                                ringProgressDialog.dismiss();

                                            }

                                        }
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        ringProgressDialog.dismiss();
                                        //alertDialogFailtoServer("Cannot connect to Server");
                                        System.out.println("error = [" + error + "]");

                                    }
                                });


                            }

                        }// ไม่มี group
                        else {
                            final Users mUser = new Users(e.getJid(), e.getNickname(), null, Users.TYPE_NOT_FRIEND);
                            // System.out.println("No Group >>" + e.getJid());
                            openfireQueary.getUser(e.getJid().split("@")[0], new Callback<User>() {
                                @Override
                                public void success(User user, retrofit.client.Response response) {
                                    mUser.setName(user.getName());
                                    mUser.setPic(user.getProperty().get("userImage"));
                                    if (!e.getJid().matches(userDataObject.getjId())) {
                                        DatabaseChatHelper.init().addUsers(mUser);
                                    }
                                    tempArr.add(mUser.getJid());
                                    if (tempArr.size() == roster.getRosterItem().size()) {

                                        //new queryTask().execute();
                                        bool[0] = true;
                                        if (bool[0] && bool[1]) {
                                            editor = sp.edit();
                                            editor.putInt(MasterData.SHARED_USER_USER_ID, userDataObject.getUserId());
                                            editor.putString(MasterData.SHARED_USER_USERNAME, userDataObject.getUsername());
                                            editor.putString(MasterData.SHARED_USER_IMAGE, userDataObject.getUserImage());
                                            editor.putString(MasterData.SHARED_USER_DISPLAY_NAME, userDataObject.getDisplayName());
                                            editor.putString(MasterData.SHARED_USER_ORGANIZE, userDataObject.getOrgId());
                                            editor.putString(MasterData.SHARED_USER_USER_TYPE, userDataObject.getUserTypeEN());
                                            editor.putString(MasterData.SHARED_USER_JID, userDataObject.getjId());
                                            editor.commit();


                                            editor = spConfig.edit();
                                            editor.putString(MasterData.SHARED_CONFIG_TOKEN, token);
                                            editor.putString(MasterData.SHARED_CONFIG_CLIENT_ID, "android");
                                            editor.putBoolean(MasterData.SHARED_CONFIG_IS_FIRST, true);
                                            editor.commit();
                                            showIntent(userDataObject.getUserTypeEN());
                                            ringProgressDialog.dismiss();

                                        }

                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {

                                    ringProgressDialog.dismiss();
                                    // alertDialogFailtoServer("Cannot connect to Server");
                                    System.out.println("error = [" + error + "]");
                                }
                            });

                        }
                    }
                } else {
                    System.out.println("No roster");
                    bool[0] = true;
                    if (bool[0] && bool[1]) {

                        editor = sp.edit();
                        editor.putInt(MasterData.SHARED_USER_USER_ID, userDataObject.getUserId());
                        editor.putString(MasterData.SHARED_USER_USERNAME, userDataObject.getUsername());
                        editor.putString(MasterData.SHARED_USER_IMAGE, userDataObject.getUserImage());
                        editor.putString(MasterData.SHARED_USER_DISPLAY_NAME, userDataObject.getDisplayName());
                        editor.putString(MasterData.SHARED_USER_ORGANIZE, userDataObject.getOrgId());
                        editor.putString(MasterData.SHARED_USER_USER_TYPE, userDataObject.getUserTypeEN());
                        editor.putString(MasterData.SHARED_USER_JID, userDataObject.getjId());


                        editor.commit();


                        editor = spConfig.edit();
                        editor.putString(MasterData.SHARED_CONFIG_TOKEN, token);
                        editor.putString(MasterData.SHARED_CONFIG_CLIENT_ID, "android");
                        editor.putBoolean(MasterData.SHARED_CONFIG_IS_FIRST, true);
                        editor.commit();
                        showIntent(userDataObject.getUserTypeEN());
                        ringProgressDialog.dismiss();
                    }


                }

            }

            @Override
            public void failure(RetrofitError error) {
                ringProgressDialog.dismiss();
                //alertDialogFailtoServer("Cannot connect to Server");
                System.out.println("error = [" + error + "]");

            }
        });


//        openfireQueary.getChatRoom(new Callback<ChatRooms>() {
//            @Override
//            public void success(ChatRooms chatRooms, retrofit.client.Response response) {
//                if (chatRooms.getChatRoom() != null) {
//                    final ArrayList<Users> arrUser = new ArrayList<>();
//                    for (ChatRoom e : chatRooms.getChatRoom()) {
//                        final String room = e.getRoomName() + "@conference.pattaya-data";
//                        for (Owners owners : e.getOwners()) {
//                            if (owners.getOwner().matches(jid)) {
//                                final Users mUser = new Users(room, e.getNaturalName(), null, Users.TYPE_GROUP);
//                                arrUser.add(mUser);
//                                DatabaseChatHelper.init().addUsers(mUser);
//                            }
//                        }
//                        if (e.getMembers().getListMember() != null) {
//                            for (Member member : e.getMembers().getListMember()) {
//                                //System.out.println(member.getText());
//                                if (member.getText().matches(jid)) {
//                                    final Users mUser = new Users(room, e.getNaturalName(), null, Users.TYPE_GROUP);
//                                    arrUser.add(mUser);
//                                    DatabaseChatHelper.init().addUsers(mUser);
//                                }
//
//                            }
//                        }
//
//                    }
//                }
//
//                System.out.println(bool[0] + "------------------" + bool[1]);
//                bool[1] = true;
//                if (bool[0] && bool[1]) {
//                    editor = sp.edit();
//                    editor.putInt("USER_ID", userDataObject.getUserId());
//                    editor.putString("USERNAME", userDataObject.getUsername());
//                    editor.putString("IMAGE", userDataObject.getUserImage());
//                    editor.putString("NAME", userDataObject.getDisplayName());
//                    editor.putString("ORGANIZE", userDataObject.getOrgId());
//                    editor.putString("USER_TYPE", userDataObject.getUserTypeEN());
//                    editor.putString("JID", userDataObject.getjId());
//                    editor.commit();
//
//                    editor = spConfig.edit();
//                    editor.putString("TOKEN", token);
//                    editor.putString("CLIENT_ID", "android");
//                    editor.putBoolean("IS_FIRST", true);
//                    editor.commit();
//                    showIntent(userDataObject.getUserTypeEN());
//                    ringProgressDialog.dismiss();
//                }
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                System.out.println(error);
//                ringProgressDialog.dismiss();
//                alertDialogFailtoServer("Cannot connect to Server");
//            }
//
//        });


    }

    void alertDialogFailtoServer(String msg) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(getResources().getString(R.string.login_failure));
        alertDialog.setMessage(msg);
        alertDialog.setButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
