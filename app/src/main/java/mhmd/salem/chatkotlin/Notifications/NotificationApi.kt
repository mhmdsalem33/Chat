package mhmd.salem.chatkotlin.Notifications

import mhmd.salem.chatkotlin.Notifications.Constants.Companion.CONTENT_TYPE
import mhmd.salem.chatkotlin.Notifications.Constants.Companion.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {

    @Headers("Authorization: Key= $SERVER_KEY" , "Content_Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification : PushNotification
    ):Response<ResponseBody>
}