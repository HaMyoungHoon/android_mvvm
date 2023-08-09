package mhha.sample.android_mvvm.models.retrofit.repo

import mhha.sample.android_mvvm.models.DataExceptionHandler
import mhha.sample.android_mvvm.models.rest.FBaseResponse
import mhha.sample.android_mvvm.models.rest.request.GetTokenRequest
import mhha.sample.android_mvvm.models.rest.response.GetTokenResponse
import mhha.sample.android_mvvm.models.retrofit.interfaces.repo.IRetrofitLoginRepository
import mhha.sample.android_mvvm.models.retrofit.interfaces.service.IRetrofitLoginService

class RetrofitLoginRepository(private val service: IRetrofitLoginService): IRetrofitLoginRepository {
    override suspend fun postLogin(data: GetTokenRequest): FBaseResponse<GetTokenResponse> {
        return try {
            service.postLogin(data)
        } catch (e: Exception) {
            FBaseResponse<GetTokenResponse>().setFail(DataExceptionHandler.handelException(e))
        }
    }
}