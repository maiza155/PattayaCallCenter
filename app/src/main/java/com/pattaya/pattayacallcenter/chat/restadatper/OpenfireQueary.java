package com.pattaya.pattayacallcenter.chat.restadatper;


import com.pattaya.pattayacallcenter.chat.jsonobject.ChatRoomObject;
import com.pattaya.pattayacallcenter.chat.jsonobject.UserProperty;
import com.pattaya.pattayacallcenter.chat.xmlobject.Chatroom.ChatRoom;
import com.pattaya.pattayacallcenter.chat.xmlobject.Chatroom.ChatRooms;
import com.pattaya.pattayacallcenter.chat.xmlobject.Roster.Roster;
import com.pattaya.pattayacallcenter.chat.xmlobject.User;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 * Created by SWF on 3/11/2015.
 */
public interface OpenfireQueary {
    @Headers({
            "Authorization : hello",
            "Content-Type : application/json"
    })
    @GET("/users/{id}/roster")
    void getRoster(@Path("id") String id, Callback<Roster> callback);


    @Headers("Authorization : hello")
    @GET("/users/{id}")
    void getUser(@Path("id") String id, Callback<User> callback);


    @Headers("Authorization : hello")
    @GET("/users/{id}")
    User  getUserInTask(@Path("id") String id);



    @Headers("Authorization : hello")
    @GET("/chatrooms?type=all")
    void getChatRoom(Callback<ChatRooms> callback);

    @Headers("Authorization : hello")
    @GET("/chatrooms?type=all")
    ChatRooms  getChatRoomInThread();


    @Headers("Authorization : hello")
    @GET("/chatrooms/{room}")
    void getChatRoomDetail(@Path("room") String room,Callback<ChatRoom> callback);


    @Headers({
            "Authorization : hello",
            "Content-Type : application/json"
    })
    @PUT("/users/{id}")
    void updateProperty(@Path("id") String id, @Body UserProperty userProperty, Callback<String> callback);



    @Headers("Authorization : hello")
    @POST("/chatrooms")
    void createChatRoom(@Body ChatRoomObject json,Callback<Response> callback);

    @Headers("Authorization : hello")
    @POST("/chatrooms/{roomName}/{roles}/{name}")
    void updateChatRoom(@Path("roomName") String roomName,@Path("roles") String roles,@Path("name") String name,Callback<Response> callback);


    @Headers("Authorization : hello")
    @PUT("/chatrooms/{roomName}")
    void updateChatRoomData(@Path("roomName") String roomName,@Body ChatRoomObject json,Callback<Response> callback);



    @Headers("Authorization : hello")
    @DELETE("/chatrooms/{roomName}/{roles}/{name}")
    void deleteUserChatRoom(@Path("roomName") String roomName,@Path("roles") String roles,@Path("name") String name,Callback<Response> callback);

    @Headers("Authorization : hello")
    @DELETE("/chatrooms/{roomName}")
    void deleteChatRoom(@Path("roomName") String roomName, Callback<Response> callback);

}
