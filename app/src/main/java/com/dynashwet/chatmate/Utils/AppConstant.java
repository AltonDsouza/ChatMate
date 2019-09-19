package com.dynashwet.chatmate.Utils;

import android.widget.BaseAdapter;

import com.dynashwet.chatmate.Models.Public;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.POST;

public interface AppConstant
{

    String BASE_URL = "http://topautocareindia.com/ChatMate/";
    String LOGIN = BASE_URL+"Auth/Login";
    String REGISTER = BASE_URL+"Register/UserRegistration";
    String PUBLIC_DATA = BASE_URL+"PostMaster/GetPublicPost";
    String UPDATE_PROFILE_IMAGE = BASE_URL+"Profile/UpdateProfilePic";
    String GET_POST_DATA = BASE_URL+"Profile/GetPostData";
    String UPDATE_PROFILE_DETAILS = BASE_URL+"Basic/UpdateUserProfile";
    String REPORT_POST = BASE_URL+"UserReport/UserReportInsert";
    String BLOCK_USER = BASE_URL+"UserReport/BlockUser";
    String UNBLOCK_USER = BASE_URL+"UserReport/UnblockUser";
    String FETCH_PROFILE_DETAILS = BASE_URL+"Basic/GetUserInfo";
    String FRIENDS_DATA = BASE_URL+"Profile/GetFriendPost";
    String INSERT_COMMENT = BASE_URL+"PostMaster/InsertComment";
    String GET_COMMENTS = BASE_URL+"PostMaster/PostComments";
    String DelComments = BASE_URL+"PostMaster/DeleteComment";
    String LikeDis = BASE_URL+"LikeDisCon/LikDis";
    String SendFriendRequest = BASE_URL+"Friends/FriendsRequest";
    String UPLOAD =BASE_URL+"PostMaster/UploadPost";
    String GetMyPosts = BASE_URL+"PostMaster/GetMyPost";
    String SharePost = BASE_URL+"PostMaster/SharePost";
    String PrivacyTitles = BASE_URL+"PostMaster/GetPostPrivacy";
    String BlockedList = BASE_URL+"UserReport/GetBlockUsers";
    String IsUserExist = BASE_URL+"Register/IsUserExist";
    String ForgotPassword = BASE_URL+"FargotPassword/UserForgotPassword";
    String DeletePost = BASE_URL+"PostMaster/DeletePost";
    String getFriendRequests = BASE_URL+"FriendRequest/UserReportInsert";
    String RequestResponse = BASE_URL+"Friends/RequestResponse";
    String Search = BASE_URL+"Users/GetUserByName";
    String GetNoti = BASE_URL+"Basic/GetNotifications";
    String ClearChat = "http://topautocareindia.com/ChatModule/ManageChat/ClearChat";
    String DeleteChat = "http://topautocareindia.com/ChatMate/Chat/DeleteChat";
    String getRefer = BASE_URL+"Users/GetInfo";
    String GetNotiPost = BASE_URL+"PostMaster/GetSinglePost";
}
