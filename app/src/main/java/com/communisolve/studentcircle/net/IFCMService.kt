package com.pointcourierapp.point.Retrofit

import com.communisolve.studentcircle.Model.FCMResponse
import com.communisolve.studentcircle.Model.FCMSendData
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface IFCMService {
    @Headers(
        "Content-Type:application/json",
        "Authorization:key=AAAA4YVqiFc:APA91bEr1l5FmLH6393XoflPSNQMTu-gWPsDj8iHgk7SGKhpsAPfIv6nq3jS7c1d5WC2XTFuQ335vIYDx9uearJc-UdSqBp5QzHNxSjmH5ArncwrgaFfz7or4o6Z8JIH1V2JkuP9PBAX"
    )

    @POST("fcm/send")
    fun sendNotification(@Body body: FCMSendData?): Observable<FCMResponse?>?
}