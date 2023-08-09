package mhha.sample.android_mvvm.bases

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import mhha.sample.android_mvvm.models.retrofit.interfaces.repo.IRetrofitLoginRepository
import mhha.sample.android_mvvm.models.retrofit.interfaces.service.IRetrofitLoginService
import mhha.sample.android_mvvm.models.retrofit.repo.RetrofitLoginRepository
import mhha.sample.android_mvvm.services.ForcedTerminationService
import mhha.sample.android_mvvm.services.RetrofitService
import mhha.sample.android_mvvm.utils.FAmhohwa
import mhha.sample.android_mvvm.utils.FThemeUtil
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.bindings.Singleton
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import kotlin.math.sign

class FMainApplication: MultiDexApplication(), LifecycleEventObserver, KodeinAware {
    companion object {
        var isForeground = false
        private var _ins: FMainApplication? = null
        val ins: FMainApplication
            get() {
                if (_ins == null) {
                    _ins = FMainApplication()
                }
                return _ins!!
            }
        private lateinit var firebaseAnalytics: FirebaseAnalytics
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
    override fun onCreate() {
        super.onCreate()
        _ins = this
        firebaseAnalytics = Firebase.analytics
        FThemeUtil.applyTheme()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
//        KakaoSdk.init(this, "appKey")
        try {
            startService(Intent(this, ForcedTerminationService::class.java))
        } catch (_: Exception) { }
    }
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_START) {
            isForeground = true
        } else if (event == Lifecycle.Event.ON_STOP) {
            isForeground = false
        }
    }
    override val kodein by Kodein.lazy {
        import(androidXModule(this@FMainApplication))
        bind<IRetrofitLoginRepository>(IRetrofitLoginRepository::class.java) with singleton { RetrofitLoginRepository(RetrofitService.create(IRetrofitLoginService::class.java)) }
        bind<FNotificationService>(FNotificationService::class) with singleton { FNotificationService(instance()) }
    }

    fun isDebug() = 0 != applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE
    fun getVersionCode(flags: Int = 0): Long {
        val pInfo = getPackageInfo()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            pInfo?.longVersionCode ?: 0L
        } else {
            @Suppress("DEPRECATION") (pInfo?.versionCode ?: 0).toLong()
        }
    }
    fun getVersionName(flags: Int = 0): Long {
        val versionName = getVersionNameString(flags)
        return if (versionName.isEmpty()) {
            0L
        } else {
            versionName.replace(".", "").toLong()
        }
    }
    fun getVersionNameString(flags: Int = 0): String {
        val pInfo = getPackageInfo()
        return pInfo?.versionName ?: ""
    }

    fun getApplicationID() = applicationInfo.processName.toString()
    fun getPackageInfo(flags: Int = 0): PackageInfo? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
            } else {
                @Suppress("DEPRECATION") packageManager.getPackageInfo(packageName, flags)
            }
        } catch (e: Exception) {
            return null
        }
    }

    fun iHateRoot() {
        if (detectTestKeys()) {
            //
        }
        if (checkSu()) {

        }
        if (checkBusybox()) {

        }
        if (checkPossibleCallSu()) {

        }
        if (iWantHashString() != "") {

        }
        if (iWantPublicKeyHashString() != "") {

        }
    }
    fun detectTestKeys(): Boolean {
        return getBuildTags().contains("test-keys", true)
    }
    fun getBuildTags(): String {
        return Build.TAGS
    }

    val BINARY_PATHS = arrayListOf("/data/local/", "/data/local/bin/", "/data/local/xbin/", "/sbin/", "/su/bin/",
        "/system/bin/", "/system/bin/.ext/", "/system/bin/failsafe/", "/system/sd/xbin/", "/system/usr/we-need-root/",
        "/system/xbin/", "/system/app/Superuser.apk", "/cache", "/data", "/dev")
    fun checkBinary(fileName: String): Boolean {
        BINARY_PATHS.forEach {
            val file = File(it, fileName)
            if (file.exists()) {
                return true
            }
        }
        return false
    }
    fun checkSu() = checkBinary("su")
    fun checkBusybox() = checkBinary("busybox")
    fun checkPossibleCallSu(): Boolean {
        try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "which"))
            val ret = BufferedReader(InputStreamReader(process.inputStream)).readLine()
            process?.destroy()
            if (ret.isNullOrEmpty()) {
                return true
            }
        } finally {
        }
        return false
    }

    fun iWantHashString(): String {
        val signatureArray = getSignatureArray() ?: return ""
        if (signatureArray.isEmpty()) {
            return ""
        }

        val msgDigest = FAmhohwa.toMessageDigest(signatureArray[0].toByteArray())
        return FAmhohwa.toBase64(msgDigest) ?: ""
    }
    fun iWantPublicKeyHashString(): String {
        val signatureArray = getSignatureArray() ?: return ""
        if (signatureArray.isEmpty()) {
            return ""
        }
        val publicKey = FAmhohwa.getPublicKeyFromCertificate(signatureArray[0])
        return FAmhohwa.calculatePublicKeyHash(publicKey)
    }
    fun getSignatureArray(): Array<Signature>? {
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            PackageManager.GET_SIGNING_CERTIFICATES
        } else {
            @Suppress("DEPRECATION") PackageManager.GET_SIGNATURES
        }
        val pInfo = getPackageInfo(flag) ?: return null
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pInfo.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION") pInfo.signatures
            }
        } catch (_: Exception) {
            return null
        }
    }
}