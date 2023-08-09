package mhha.sample.android_mvvm.models.rest

class FBaseResponse<T>: FCommonResult() {
    val data: T? = null
    override fun setFail(failCode: Int?, failMessage: String?): FBaseResponse<T> {
        this.result = false
        this.code = failCode
        this.message = failMessage
        return this
    }
    override fun setFail(data: FCommonResult): FBaseResponse<T> {
        this.result = data.result
        this.code = data.code
        this.message = data.message
        return this
    }
}