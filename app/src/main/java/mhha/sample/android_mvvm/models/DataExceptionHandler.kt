package mhha.sample.android_mvvm.models

import mhha.sample.android_mvvm.models.rest.FCommonResult
import retrofit2.HttpException
import java.io.IOException

class DataExceptionHandler {
    companion object {
        fun handelException(exception: Exception): FCommonResult {
            return when (exception) {
                is IOException -> FCommonResult().setFail(failMessage = exception.localizedMessage)
                is HttpException -> {
                    val code = exception.code()
                    val errorResponse = exception.response()?.errorBody()?.string()
                    FCommonResult().setFail(code, errorResponse)
                }
                else -> FCommonResult().setFail(failMessage = exception.message)
            }
        }
        fun handelThrowable(throwable: Throwable?): FCommonResult {
            return when (throwable) {
                null -> FCommonResult().setFail()
                else -> {
                    FCommonResult().setFail(failMessage = throwable.localizedMessage)
                }
            }
        }
    }
}