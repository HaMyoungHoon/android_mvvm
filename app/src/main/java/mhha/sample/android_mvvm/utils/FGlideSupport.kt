package mhha.sample.android_mvvm.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
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
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import mhha.sample.android_mvvm.interfaces.command.IEventListener
import java.io.File

class FGlideSupport {
    companion object {
        fun imageLoad(context: Context, file: File): RequestBuilder<Drawable> {
            return Glide.with(context).load(file)
        }
        fun imageLoad(context: Context, file: File, imageView: ImageView) {
            Glide.with(context).load(file)
                .into(imageView)
        }
        fun imageLoad(file: File, imageView: ImageView) {
            Glide.with(imageView.context).load(file)
                .into(imageView)
        }
        fun imageLoad(@androidx.annotation.DrawableRes resourceId: Int, imageView: ImageView) {
            Glide.with(imageView.context).load(resourceId)
                .into(imageView)
        }
        fun imageLoad(url: String, imageView: ImageView, loadFailedListener: IEventListener? = null, resourceReadyListener: IEventListener? = null) {
            Glide.with(imageView.context).load(url)
                .listener(object: RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        loadFailedListener?.onEvent(arrayListOf(e, model, target, isFirstResource))
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        resourceReadyListener?.onEvent(arrayListOf(resource, model, target, dataSource, isFirstResource))
                        return false
                    }
                })
                .into(imageView)
        }
        fun imageLoad(uri: Uri, imageView: AppCompatImageView, loadFailedListener: IEventListener? = null, resourceReadyListener: IEventListener? = null) {
            Glide.with(imageView.context).load(uri)
                .listener(object: RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        loadFailedListener?.onEvent(arrayListOf(e, model, target, isFirstResource))
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        resourceReadyListener?.onEvent(arrayListOf(resource, model, target, dataSource, isFirstResource))
                        return false
                    }
                })
                .into(imageView)
        }
        fun imageLoad(context: Context, imageView: FHexagonMaskView, glideSrc: String?, hexagonMaskNullResourceId: Int?, onResourceReady: (Drawable) -> Unit) {
            if (glideSrc.isNullOrEmpty()) {
                try {
                    hexagonMaskNullResourceId?.let {
                        if (it == 0x00000000) {
                            return@let
                        }
                        imageView.setImageDrawable(AppCompatResources.getDrawable(imageView.context, it))
                    }
                } catch (_: Exception) {
                    imageView.setImageDrawable(null)
                }
                return
            }
            try {
                if (glideSrc.endsWith(".svg", ignoreCase = true)) {
                    FSVGUtils().fetchSVG(context.applicationContext, glideSrc, imageView) { _, _ ->
//                    FCoroutineScope.coroutineScope({
//                        Glide.with(imageView.context).load(glide_src)
//                            .skipMemoryCache(true)
//                            .override(glide_src_uri_width ?: Target.SIZE_ORIGINAL,glide_src_uri_height ?: Target.SIZE_ORIGINAL)
//                            .optionalCenterCrop()
//                            .priority(Priority.IMMEDIATE)
//                            .into(imageView)
//                    })
                    }
                } else {
                    Glide.with(context).load(glideSrc)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .optionalCenterCrop()
                        .priority(Priority.IMMEDIATE)
                        .format(DecodeFormat.PREFER_ARGB_8888)
                        .into(object: CustomTarget<Drawable>() {
                            override fun onLoadCleared(placeholder: Drawable?) {
                            }
                            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                                onResourceReady(resource)
                            }
                        })
                }
            } catch (e: Exception) {
                Log.d("FHexagonMaskView imageLoad", e.message ?: "")
            }
        }
        fun imageResizedLoad(url: String, imageView: ImageView, width: Int?, height: Int?) {
            Glide.with(imageView.context).load(url)
                .override(width ?: Target.SIZE_ORIGINAL, height ?: Target.SIZE_ORIGINAL)
                .into(imageView)
        }
        fun circleImageLoad(url: String, imageView: ImageView) {
            Glide.with(imageView.context).load(url)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView)
        }
        fun radiusImageLoad(url: String, imageView: ImageView, radius: Int) {
            Glide.with(imageView.context).load(url)
                .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(radius)))
                .into(imageView)
        }
        fun assetGifImageLoad(assetGifSrc: String, imageView: ImageView, loopCount: Int) {
            try {
                Glide.with(imageView.context).asGif().load(assetGifSrc)
                    .listener(object: RequestListener<GifDrawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<GifDrawable>?, isFirstResource: Boolean): Boolean {
                            return false
                        }

                        override fun onResourceReady(resource: GifDrawable?, model: Any?, target: Target<GifDrawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            resource?.setLoopCount(loopCount)
                            return false
                        }
                    })
                    .into(imageView)
            } catch (_: Exception) {
            }
        }
        fun imageLoad(imageView: FHexagonMaskView, src: String?, width: Int?, height: Int?) {
            if (src.isNullOrEmpty()) {
                // do something
                return
            }
            if (src.endsWith(".svg", ignoreCase = true)) {
                FSVGUtils().fetchSVG(imageView.context.applicationContext, src, imageView) { call, e ->
                }
            } else {
                Glide.with(imageView.context).load(src)
                    .skipMemoryCache(true)
                    .override(width ?: Target.SIZE_ORIGINAL,height ?: Target.SIZE_ORIGINAL)
                    .optionalCenterCrop()
                    .priority(Priority.IMMEDIATE)
                    .into(imageView)
            }
        }
    }
}