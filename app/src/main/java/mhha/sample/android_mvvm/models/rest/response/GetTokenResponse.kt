package mhha.sample.android_mvvm.models.rest.response

import mhha.sample.android_mvvm.utils.FAmhohwa

/**
 * GetTokenResponse
 * - pair of GetTokenRequest
 *
 * @property accessKey
 * @property secretKey in general encrypt
 * @property timeStamp pair of secretKey
 * @constructor Create empty Get token response
 */
data class GetTokenResponse (
    var accessKey: String? = null,
    var secretKey: String? = null,
    var timeStamp: Long? = null,
) {
    fun getTimedSecretKey(): String? {
        if (secretKey == null) {
            return ""
        }
        timeStamp = System.currentTimeMillis()
        val message = "${timeStamp}\n${accessKey}"
        return FAmhohwa.hMacSha256EncryptBase64(message, secretKey!!)
    }
    fun getTimeStamp(): String {
        if (timeStamp == null) {
            return ""
        }
        return timeStamp.toString()
    }
}