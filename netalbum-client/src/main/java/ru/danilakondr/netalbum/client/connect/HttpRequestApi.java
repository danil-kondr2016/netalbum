/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.danilakondr.netalbum.client.connect;

import okhttp3.ResponseBody;
import retrofit2.*;
import retrofit2.http.*;

/**
 *
 * @author danko
 */
public interface HttpRequestApi {
    @GET("archive/{id}")
    Call<ResponseBody> getThumbnails(@Path("id") String id);
}
