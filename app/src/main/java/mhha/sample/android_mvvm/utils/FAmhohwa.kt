package mhha.sample.android_mvvm.utils

import android.content.pm.Signature
import android.os.Build
import java.io.ByteArrayInputStream
import java.io.File
import java.security.MessageDigest
import java.security.PublicKey
import java.security.cert.CertificateFactory
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object FAmhohwa {
    const val H_MAC_SHA_256 = "HmacSHA256"
    fun hMacSha256EncryptBase64(message: String, secretKey: String): String? {
        val byteArray = hMacSha256Encrypt(message, secretKey) ?: return ""
        return toBase64(byteArray)
    }
    fun hMacSha256Encrypt(message: String, secretKey: String) = try {
        val hMacSha256 = Mac.getInstance(H_MAC_SHA_256)
        val secretKeySpec = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), H_MAC_SHA_256)
        hMacSha256.init(secretKeySpec)
        hMacSha256.doFinal(message.toByteArray(Charsets.UTF_8))
    } catch (_: Exception) {
        null
    }
    fun toBase64(data: ByteArray) = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Base64.getEncoder().encodeToString(data)
        } else {
            String(android.util.Base64.encode(data, android.util.Base64.DEFAULT))
        }
    } catch (_: Exception) {
        null
    }
    fun toMessageDigest(data: ByteArray): ByteArray {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(data)
        return md.digest()
    }
    fun getPublicKeyFromCertificate(signature: Signature): PublicKey {
        val cert = CertificateFactory.getInstance("X.509").generateCertificate(ByteArrayInputStream(signature.toByteArray()))
        return cert.publicKey
    }
    fun calculatePublicKeyHash(publicKey: PublicKey): String {
        val msgDigest = toMessageDigest(publicKey.encoded)
        return bytesToHexString(msgDigest)
    }
    fun bytesToHexString(bytes: ByteArray): String {
        val hexChars = "0123456789ABCDEF".toCharArray()
        val hexBuilder = StringBuilder(bytes.size * 2)

        for (byte in bytes) {
            val value = byte.toInt() and 0xFF
            hexBuilder.append(hexChars[value shr 4])
            hexBuilder.append(hexChars[value and 0x0F])
        }

        return hexBuilder.toString()
    }
}