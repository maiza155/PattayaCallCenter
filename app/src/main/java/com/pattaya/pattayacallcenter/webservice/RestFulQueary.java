package com.pattaya.pattayacallcenter.webservice;

import com.pattaya.pattayacallcenter.webservice.object.AccessUserObject;
import com.pattaya.pattayacallcenter.webservice.object.AcessTokenObject;
import com.pattaya.pattayacallcenter.webservice.object.ChangePassObject;
import com.pattaya.pattayacallcenter.webservice.object.DeletePostObject;
import com.pattaya.pattayacallcenter.webservice.object.GetInviteUserObject;
import com.pattaya.pattayacallcenter.webservice.object.GetTokenObject;
import com.pattaya.pattayacallcenter.webservice.object.GetUserObject;
import com.pattaya.pattayacallcenter.webservice.object.LoginObject;
import com.pattaya.pattayacallcenter.webservice.object.OfficialObject;
import com.pattaya.pattayacallcenter.webservice.object.PersonalObject;
import com.pattaya.pattayacallcenter.webservice.object.RegistObject;
import com.pattaya.pattayacallcenter.webservice.object.SaveFacebookObject;
import com.pattaya.pattayacallcenter.webservice.object.SavePostObject;
import com.pattaya.pattayacallcenter.webservice.object.UpdateResult;
import com.pattaya.pattayacallcenter.webservice.object.UpdateTaskObject;
import com.pattaya.pattayacallcenter.webservice.object.UserDataObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseDataMemberObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CaseMainObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.CloseCaseObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.GetCaseDate;
import com.pattaya.pattayacallcenter.webservice.object.casedata.GetCaseListData;
import com.pattaya.pattayacallcenter.webservice.object.casedata.GetComplainObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.OpenCaseAssignObject;
import com.pattaya.pattayacallcenter.webservice.object.casedata.SendReassignTaskObject;
import com.pattaya.pattayacallcenter.webservice.object.friend.GetListInviteFriendObject;
import com.pattaya.pattayacallcenter.webservice.object.organize.AcceptInviteObject;
import com.pattaya.pattayacallcenter.webservice.object.organize.GetInviteOrgObject;
import com.pattaya.pattayacallcenter.webservice.object.organize.GetOrgObject;
import com.pattaya.pattayacallcenter.webservice.object.organize.OrgData;
import com.pattaya.pattayacallcenter.webservice.object.organize.SendInviteObject;
import com.pattaya.pattayacallcenter.webservice.object.sticker.StickerListObject;
import com.pattaya.pattayacallcenter.webservice.object.upload.GetPostObject;

import org.json.JSONObject;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedFile;

/**
 * Created by SWF on 3/13/2015.
 */
public interface RestFulQueary {

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("/authen/authenUser")
    void authenUser(@Body LoginObject object, Callback<UserDataObject> json);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("/authen/getTokenByClientId")
    void getToken(@Body GetTokenObject object, Callback<AcessTokenObject> json);


    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("/user/getUserData")
    void getUser(@Body GetUserObject object, Callback<AccessUserObject> json);


    @Headers("Content-Type:application/json;charset=UTF-8")
    @PUT("/user/updateUser")
    void postUser(@Body AccessUserObject object, Callback<UpdateResult> json);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @PUT("/user/changePassword")
    void resetPass(@Body ChangePassObject object, Callback<UpdateResult> json);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("/user/getUserListByOrgId")
    void getUserOrg(@Body GetOrgObject object, Callback<Response> json);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("/user/getUserListForInvite")
    void getInviteUser(@Body GetInviteUserObject object, Callback<Response> json);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("/user/inviteMember")
    void sendInvite(@Body SendInviteObject object, Callback<UpdateResult> json);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("/user/getInviteFriend")
    void getInviteFriend(@Body GetListInviteFriendObject getListInviteFriendObject, Callback<Response> json);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("/user/saveUserFacebook")
    void saveUserFacebook(@Body SaveFacebookObject Object, Callback<UserDataObject> json);


    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("/organize/getOrganizeData")
    void getOrganizeData(@Body GetOrgObject object, Callback<OrgData> json);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @GET("/user/getUserOfficialData/{id}")
    void getUserOfficialData(@Path("id") int id, Callback<OfficialObject> json);


    /**
     * case forward
     */


    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("/user/getUserList")
    void getUserList(@Body com.pattaya.pattayacallcenter.webservice.object.casedata.listforward.GetUserObject object, Callback<Response> json);


    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("/organize/getOrganizeList")
    void getOrganizeList(@Body com.pattaya.pattayacallcenter.webservice.object.casedata.listforward.GetOrgObject object, Callback<Response> json);


    /**
     * Guest
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @GET("/user/getInviteMember/{id}")
    void getInviteOrg(@Path("id") int id, Callback<GetInviteOrgObject> json);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @PUT("/user/callBackInviteMember")
    void acceptInvite(@Body AcceptInviteObject object, Callback<UpdateResult> json);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("/user/saveUserGuest")
    void sendRegist(@Body RegistObject object, Callback<UpdateResult> json);


    @Headers("Content-Type:application/json;charset=UTF-8")
    @GET("/user/forgetPassword/{email}/true")
    void forgetPass(@Path("email") String email, Callback<UpdateResult> json);


    /**
     * UploadImage
     */
    @Multipart
    @POST("/upload/people")
    void uploadImage(@Part("uploadedFile") TypedFile file, Callback<Response> callback);

    @Multipart
    @POST("/upload/caseImage")
    void uploadImageCase(@Part("uploadedFile") TypedFile file, Callback<Response> callback);


    @POST("/upload/people")
    void uploadManyImage(@Body MultipartTypedOutput attachments, Callback<Response> callback);


    /**
     * Post
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("/post/savePost")
    void savePost(@Body SavePostObject object, Callback<UpdateResult> json);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("/post/getPostList")
    void getPost(@Body GetPostObject object, Callback<Response> json);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("/post/deletePost")
    void deletePost(@Body DeletePostObject object, Callback<Response> json);


    /**
     * Stickers
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @PUT("/stickerSetting/getStickerSetting")
    void getStricket(@Body JSONObject jsonO, Callback<StickerListObject> json);


    /**
     * Case
     */

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("/cases/saveComplaint")
    void saveCase(@Body CaseMainObject caseDataObject, Callback<UpdateResult> json);


    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("/cases/getCasesListForMobile")
    void getCaseList(@Body GetCaseListData getCaseListData, Callback<Response> json);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("/cases/getComplaintDataForMobile")
    void getComplaintData(@Body GetComplainObject getComplainObject, Callback<CaseMainObject> json);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @PUT("/cases/deleteComplaintForMobile")
    void deleteComplaintData(@Body GetComplainObject getComplainObject, Callback<UpdateResult> json);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @GET("/serviceType/getServiceTypeDrpList/{id}")
    void getTypeCaseList(@Path("id") int id, Callback<Response> json);


    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("/cases/openCases")
    void openCases(@Body OpenCaseAssignObject object, Callback<UpdateResult> json);


    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("/cases/saveCases")
    void saveCases(@Body OpenCaseAssignObject object, Callback<UpdateResult> json);


    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("/cases/getCasesDataForMobile")
    void getCaseData(@Body GetComplainObject getComplainObject, Callback<CaseDataMemberObject> json);


    @Headers("Content-Type:application/json;charset=UTF-8")
    @PUT("/cases/updateTaskDate")
    void updateTaskDate(@Body UpdateTaskObject Object, Callback<UpdateResult> json);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("/cases/getTaskList")
    void getTaskList(@Body GetComplainObject getComplainObject, Callback<Response> json);


    @Headers("Content-Type:application/json;charset=UTF-8")
    @PUT("/cases/updateReassignTask")
    void updateReassignTask(@Body SendReassignTaskObject Object, Callback<UpdateResult> json);


    @Headers("Content-Type:application/json;charset=UTF-8")
    @PUT("/cases/updateTask")
    void updateTask(@Body CloseCaseObject Object, Callback<UpdateResult> json);


    @Headers("Content-Type:application/json;charset=UTF-8")
    @PUT("/cases/deleteCases")
    void deleteCases(@Body GetComplainObject getComplainObject, Callback<UpdateResult> json);


    @Headers("Content-Type:application/json;charset=UTF-8")
    @GET("/cases2/getUserRelateByComplaintId/{id}")
    void getUserListJid(@Path("id") int id, Callback<Response> json);


    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("/cases/getTaskData")
    void getTaskData(@Body GetCaseDate object, Callback<UpdateResult> json);


    /**
     * Personal
     */

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("/personal/savePersonalAndChkMobile")
    void savePersonalAndChkMobile(@Body PersonalObject object, Callback<UpdateResult> json);



}
