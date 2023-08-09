package mhha.sample.android_mvvm.models.rest

open class FCommonResult {
    var result: Boolean? = null
    var code: Int? = null
    var message: String? = null
    open fun setSuccess(): FCommonResult {
        result = true
        return this
    }
    open fun setFail(failCode: Int? = -1, failMessage: String? = "not defined error"): FCommonResult {
        result = false
        code = failCode
        message = failMessage
        return this
    }
    open fun setFail(data: FCommonResult): FCommonResult {
        this.result = data.result
        this.code = data.code
        this.message = data.message
        return this
    }
}