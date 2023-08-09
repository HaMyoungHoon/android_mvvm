package mhha.sample.android_mvvm.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.annotation.DrawableRes
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import com.squareup.picasso.Target
import java.io.File
import java.lang.Exception

class FPicassoSupport {
    companion object {
        fun imageLoad(file: File): RequestCreator {
            return Picasso.get().load(file)
        }
        fun imageLoad(file: File, imageView: ImageView) {
            Picasso.get().load(file)
//                .fit()
//                .centerCrop(Gravity.CENTER)
//                .error(R.drawable.error_resource)
                .into(imageView)
        }
        fun imageResizeLoad(file: File, width: Int, height: Int, imageView: ImageView) {
            Picasso.get().load(file)
                .resize(width, height)
                .into(imageView)
        }
        fun imageLoad(@androidx.annotation.DrawableRes resourceId: Int): RequestCreator {
            return Picasso.get().load(resourceId)
        }
        fun imageLoad(@androidx.annotation.DrawableRes resourceId: Int, imageView: ImageView) {
            Picasso.get().load(resourceId)
//                .fit()
//                .centerCrop(Gravity.CENTER)
//                .error(R.drawable.error_resource)
                .into(imageView)
        }
        fun imageResizeLoad(@androidx.annotation.DrawableRes resourceId: Int, width: Int, height: Int, imageView: ImageView) {
            Picasso.get().load(resourceId)
                .resize(width, height)
                .into(imageView)
        }
        fun imageLoad(url: String): RequestCreator {
            return Picasso.get().load(url)
        }
        fun imageLoad(url: String, imageView: ImageView) {
            Picasso.get().load(url)
//                .fit()
//                .centerCrop(Gravity.CENTER)
//                .error(R.drawable.error_resource)
                .into(imageView)
        }
        fun imageResizeLoad(url: String, width: Int, height: Int, imageView: ImageView) {
            Picasso.get().load(url)
                .resize(width, height)
                .into(imageView)
        }
        fun imageLoad(uri: Uri): RequestCreator {
            return Picasso.get().load(uri)
        }
        fun imageLoad(uri: Uri, imageView: ImageView) {
            Picasso.get().load(uri)
//                .fit()
//                .centerCrop(Gravity.CENTER)
//                .error(R.drawable.error_resource)
                .into(imageView)
        }
        fun imageResizeLoad(uri: Uri, width: Int, height: Int, imageView: ImageView) {
            Picasso.get().load(uri)
                .resize(width, height)
                .into(imageView)
        }
    }
}