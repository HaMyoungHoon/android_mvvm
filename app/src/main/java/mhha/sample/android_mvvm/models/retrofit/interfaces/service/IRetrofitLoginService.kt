package mhha.sample.android_mvvm.models.retrofit.interfaces.service

import mhha.sample.android_mvvm.models.rest.FBaseResponse
import mhha.sample.android_mvvm.models.rest.request.GetTokenRequest
import mhha.sample.android_mvvm.models.rest.response.GetTokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface IRetrofitLoginService {
    @POST("login")
    suspend fun postLogin(@Body data: GetTokenRequest): FBaseResponse<GetTokenResponse>
}