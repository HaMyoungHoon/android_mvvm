package mhha.sample.android_mvvm.models.rest.request

/**
 * GetTokenRequest
 * - pair of GetTokenResponse
 *
 * @property id
 * @property pw
 * @constructor Create empty Get token request
 */
data class GetTokenRequest (
    var id: String = "",
    var pw: String = ""
) {
}