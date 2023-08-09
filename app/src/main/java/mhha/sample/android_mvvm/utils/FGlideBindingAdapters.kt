package mhha.sample.android_mvvm.utils

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

object FGlideBindingAdapters {
    @JvmStatic
    @BindingAdapter(value = ["glide_src", "glide_src_null_remove", "glide_src_center"], requireAll = false)
    fun setGlideSrc(imageView: AppCompatImageView, glide_src: String?, glide_src_null_remove: Boolean?, glide_src_center: Boolean?) {
        if (glide_src.isNullOrEmpty()) {
            if (glide_src_null_remove == true) {
                imageView.setImageDrawable(null)
            }
            return
        }
        if (glide_src_center == true) {
            imageView.scaleType = ImageView.ScaleType.CENTER
        }
        glideLoad(imageView, glide_src)
            .apply(defaultImageRequestOptions())
            .skipMemoryCache(false)
            .optionalCenterCrop()
            .into(imageView)
        return
    }
    @JvmStatic
    @BindingAdapter(value = ["glide_res_src", "glide_res_src_null_remove", "glide_res_src_center"], requireAll = false)
    fun setGlideResSrc(imageView: AppCompatImageView, glide_res_src: Int?, glide_res_src_null_remove: Boolean?, glide_res_src_center: Boolean?) {
        if (glide_res_src == null || glide_res_src == 0) {
            if (glide_res_src_null_remove == true) {
                imageView.setImageDrawable(null)
            }
            return
        }
        if (glide_res_src_center == true) {
            imageView.scaleType = ImageView.ScaleType.CENTER
        }
        glideLoad(imageView, glide_res_src)
            .apply(defaultImageRequestOptions())
            .skipMemoryCache(false)
            .optionalCenterCrop()
            .into(imageView)
        return
    }
    @JvmStatic
    @BindingAdapter("glide_circle_src")
    fun setGlideCircleSrc(imageView: AppCompatImageView, url: String?) {
        if (url.isNullOrEmpty()) {
            imageView.setImageDrawable(null)
            return
        }
        try {
            glideLoad(imageView, url)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView)
        } catch (_: Exception) {
            Log.d("test mhha setGlideCircleSrc", "catch")
        }
    }
    @JvmStatic
    @BindingAdapter(value = ["glide_round_src", "glide_corners"], requireAll = false)
    fun setGlideRoundSrc(imageView: AppCompatImageView, glide_round_src: String?, glide_corners: Int? = 20) {
        if (glide_round_src.isNullOrEmpty()) {
            imageView.setImageDrawable(null)
            return
        }

        if (glide_round_src.endsWith(".svg", ignoreCase = true)) {
            FSVGUtils().fetchSVG(imageView.context.applicationContext, glide_round_src, imageView) { call, e ->
            }
        } else {
            glideBuilder(imageView, glide_round_src)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(glide_corners ?: 0)))
                .into(imageView)
        }
    }
    @JvmStatic
    @BindingAdapter("glide_src_uri")
    fun setGlideSrcUri(imageView: AppCompatImageView, uri: Uri?) {
        if (uri == null) {
            imageView.setImageDrawable(null)
            return
        }
        glideUriLoad(imageView, uri)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop())
            .skipMemoryCache(false)
            .optionalCenterCrop()
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter(value = ["glide_src_uri", "glide_uri_src_null_remove", "glide_uri_src_fit_center"], requireAll = false)
    fun setGlideSrcUri(imageView: androidx.appcompat.widget.AppCompatImageView, glide_src_uri: Uri?, glide_uri_src_null_remove: Boolean?, glide_uri_src_fit_center: Boolean?) {
        if (glide_src_uri == null) {
            if (glide_uri_src_null_remove == true) {
                imageView.setImageDrawable(null)
            }
            return
        }
        val ret = glideUriLoad(imageView, glide_src_uri)
        if (glide_uri_src_fit_center == true) {
            ret.apply(fitCenterImageRequestOptions()
                .override(imageView.width, imageView.height)
            ).into(imageView)
        } else {
            ret.apply(defaultImageRequestOptions()
                .override(imageView.width, imageView.height)
            ).into(imageView)
        }
    }
    @JvmStatic
    @BindingAdapter(value = ["glide_src_uri", "glide_src_uri_width", "glide_src_uri_height"], requireAll = false)
    fun setGlideSrcUri(imageView: AppCompatImageView, glide_src_uri: Uri?, glide_src_uri_width: Int?, glide_src_uri_height: Int?) {
        if (glide_src_uri == null) {
            imageView.setImageDrawable(null)
            return
        }

        glideUriLoad(imageView, glide_src_uri)
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .override(glide_src_uri_width ?: Target.SIZE_ORIGINAL,glide_src_uri_height ?: Target.SIZE_ORIGINAL)
            .optionalCenterCrop()
            .priority(Priority.IMMEDIATE)
//                .encodeFormat(Bitmap.CompressFormat.PNG)
            .format(DecodeFormat.DEFAULT)
            .into(imageView)
    }
    @JvmStatic
    @BindingAdapter(value = ["glide_thumbnail_src_uri", "glide_thumbnail_corners_uri", "glide_thumbnail_src_uri_width", "glide_thumbnail_src_uri_height"], requireAll = false)
    fun setGlideThumbnailUri(imageView: AppCompatImageView, glide_thumbnail_src_uri: Uri?, glide_thumbnail_corners_uri: Int? = 20, glide_thumbnail_src_uri_width: Int?, glide_thumbnail_src_uri_height: Int?) {
        if (glide_thumbnail_src_uri == null) {
            imageView.setImageDrawable(null)
            return
        }
        Glide.with(imageView.context)
            .setDefaultRequestOptions(
                RequestOptions()
                    .frame(1000 * 1000)
                    .centerCrop()
                    .transform(CenterCrop(), RoundedCorners(glide_thumbnail_corners_uri ?: 20))
                    .override(glide_thumbnail_src_uri_width ?: Target.SIZE_ORIGINAL,glide_thumbnail_src_uri_height ?: Target.SIZE_ORIGINAL))
            .load(glide_thumbnail_src_uri)
            .into(imageView)
    }
    @JvmStatic
    @BindingAdapter(value = ["glide_thumbnail_src_url", "glide_thumbnail_corners_url", "glide_thumbnail_src_url_width", "glide_thumbnail_src_url_height"], requireAll = false)
    fun setGlideThumbnailUrl(imageView: AppCompatImageView, glide_thumbnail_src_url: String?, glide_thumbnail_corners_url: Int? = 20, glide_thumbnail_src_url_width: Int?, glide_thumbnail_src_url_height: Int?) {
        if (glide_thumbnail_src_url.isNullOrEmpty()) {
            imageView.setImageDrawable(null)
            return
        }
        Glide.with(imageView.context)
            .setDefaultRequestOptions(
                RequestOptions()
                    .frame(1000 * 1000)
                    .centerCrop()
                    .transform(CenterCrop(), RoundedCorners(glide_thumbnail_corners_url ?: 20))
                    .override(glide_thumbnail_src_url_width ?: Target.SIZE_ORIGINAL,glide_thumbnail_src_url_height ?: Target.SIZE_ORIGINAL))
            .load(glide_thumbnail_src_url)
            .into(imageView)
    }
    @JvmStatic
    @BindingAdapter(value = ["glide_round_src_uri", "glide_round_src_url", "glide_corners_uri", "glide_src_uri_width", "glide_src_uri_height"], requireAll = false)
    fun setGlideRoundSrcUri(imageView: AppCompatImageView, glide_round_src_uri: Uri?, glide_round_src_url: String? = null, glide_corners_uri: Int? = 20, glide_src_uri_width: Int?, glide_src_uri_height: Int?) {
        if (glide_round_src_uri != null) {
            glideUriLoad(imageView, glide_round_src_uri)
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(glide_src_uri_width ?: Target.SIZE_ORIGINAL, glide_src_uri_height ?: Target.SIZE_ORIGINAL)
                .transform(CenterCrop(), RoundedCorners(glide_corners_uri ?: 20))
                .priority(Priority.IMMEDIATE)
//                    .encodeFormat(Bitmap.CompressFormat.PNG)
                .format(DecodeFormat.DEFAULT)
                .into(imageView)
            return
        }
        if (glide_round_src_url != null) {
            glideLoad(imageView, glide_round_src_url)
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(glide_src_uri_width ?: Target.SIZE_ORIGINAL, glide_src_uri_height ?: Target.SIZE_ORIGINAL)
                .transform(CenterCrop(), RoundedCorners(glide_corners_uri ?: 20))
                .priority(Priority.IMMEDIATE)
                //                    .encodeFormat(Bitmap.CompressFormat.PNG)
                .format(DecodeFormat.DEFAULT)
                .into(imageView)
            return
        }
        try {
            imageView.setImageDrawable(null)
        } catch (_: Exception) {
            Log.d("test mhha setGlideRoundSrcUri", "catch setImageDrawable null")
        }
    }
    @JvmStatic
    @BindingAdapter(value = ["gif_asset_src", "gif_asset_loop_count"])
    fun setGifSrcAsset(imageView: AppCompatImageView, gif_asset_src: String, gif_asset_loop_count: Int) {
        if (gif_asset_src.isEmpty()) {
            return
        }

        try {
            Glide.with(imageView.context).asGif().load(gif_asset_src)
                .listener(object: RequestListener<GifDrawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<GifDrawable>?, isFirstResource: Boolean ): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: GifDrawable?, model: Any?, target: Target<GifDrawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        resource?.setLoopCount(gif_asset_loop_count)
                        return false
                    }
                }).into(imageView)
        } catch (e: Exception) {
            Log.d("test mhha setGifSrcAsset", e.message ?: "")
        }
    }
    private fun glideLoad(imageView: AppCompatImageView, url: String) = Glide.with(imageView.context).load(url)
    private fun glideLoad(imageView: AppCompatImageView, resId: Int) = Glide.with(imageView.context).load(resId)
    private fun glideBuilder(imageView: AppCompatImageView, url: String) = Glide.with(imageView.context).asDrawable().load(url)
    private fun glideUriLoad(imageView: AppCompatImageView, uri: Uri) = Glide.with(imageView.context).load(uri)
    private fun defaultImageRequestOptions() = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().priority(Priority.IMMEDIATE).format(DecodeFormat.PREFER_ARGB_8888)
    private fun fitCenterImageRequestOptions() = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().priority(Priority.IMMEDIATE).format(DecodeFormat.PREFER_ARGB_8888)
}