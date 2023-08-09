package mhha.sample.android_mvvm.models.retrofit.interfaces.repo

import mhha.sample.android_mvvm.models.rest.FBaseResponse
import mhha.sample.android_mvvm.models.rest.request.GetTokenRequest
import mhha.sample.android_mvvm.models.rest.response.GetTokenResponse

interface IRetrofitLoginRepository {
    suspend fun postLogin(data: GetTokenRequest): FBaseResponse<GetTokenResponse>
}