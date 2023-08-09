package mhha.sample.android_mvvm.utils

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Animatable
import android.graphics.drawable.AnimatedImageDrawable
import android.net.Uri
import android.support.annotation.DrawableRes
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.updateLayoutParams
import com.facebook.common.util.UriUtil
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.AbstractDraweeController
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.controller.ControllerListener
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.GenericDraweeHierarchy
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.drawee.view.GenericDraweeView
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.fresco.animation.drawable.AnimatedDrawable2
import com.facebook.fresco.animation.drawable.AnimationListener
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.ImageRequestBuilder

class FFrescoSupport {
    companion object {
        fun initialize(application: Application) {
            Fresco.initialize(application)
        }
        fun imageLoad(url: String, imageView: SimpleDraweeView) {
            if (!Fresco.hasBeenInitialized()) {
                return
            }
            val imageUri = Uri.parse(url)
            val controller: DraweeController = Fresco.newDraweeControllerBuilder()
                .setUri(imageUri)
                .setControllerListener(object: BaseControllerListener<ImageInfo>() {
                    override fun onFailure(id: String?, throwable: Throwable?) {
                        // do something
                    }
                })
                .setAutoPlayAnimations(true)
                .build()
            imageView.controller = controller
        }
        fun imageLoad(url: String, imageView: GenericDraweeView) {
            if (!Fresco.hasBeenInitialized()) {
                return
            }
            val imageUri = Uri.parse(url)
            val controller: DraweeController = Fresco.newDraweeControllerBuilder()
                .setUri(imageUri)
                .setControllerListener(object: BaseControllerListener<ImageInfo>() {
                    override fun onFailure(id: String?, throwable: Throwable?) {
                        // do something
                    }
                })
                .setAutoPlayAnimations(true)
                .build()
            imageView.controller = controller
        }
        fun imageResizeLoad(url: String, width: Int, height: Int, imageView: GenericDraweeView) {
            if (!Fresco.hasBeenInitialized()) {
                return
            }
            val imageUri = Uri.parse(url)
            val resizeOptions = ResizeOptions(width, height)
            val imageRequest = ImageRequestBuilder.newBuilderWithSource(imageUri)
                .setResizeOptions(resizeOptions)
                .build()
            val hierarchy: GenericDraweeHierarchy = GenericDraweeHierarchyBuilder.newInstance(imageView.context.resources)
                .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE)
                .build()
            val controllerBuilder = Fresco.newDraweeControllerBuilder()
                .setControllerListener(object: ControllerListener<ImageInfo> {
                    override fun onSubmit(id: String?, callerContext: Any?) {
                        // do something
                    }
                    override fun onIntermediateImageFailed(id: String?, throwable: Throwable?) {
                        // do something
                    }
                    override fun onIntermediateImageSet(id: String?, imageInfo: ImageInfo?) {
                        // do something
                    }
                    override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
                        imageView.updateLayoutParams { this.width = resizeOptions.width }
                        val widthFloat = imageInfo?.width?.toFloat() ?: return
                        val heightFloat = imageInfo.height.toFloat()
                        if (heightFloat > 0F) {
                            imageView.aspectRatio = widthFloat / heightFloat
                        }
                    }
                    override fun onFailure(id: String?, throwable: Throwable?) {
                        // do something
                    }
                    override fun onRelease(id: String?) {
                        // do something
                    }
                })
                .setOldController(imageView.controller)
                .setImageRequest(imageRequest)
            imageView.controller = controllerBuilder.build()
            imageView.hierarchy = hierarchy
        }
        fun gifLoad(url: String, imageView: SimpleDraweeView, replayCount: Int = 5) {
            if (!Fresco.hasBeenInitialized()) {
                return
            }
            var repeatCount = 0
            val imageUri = Uri.parse(url)
            val hierarchy = GenericDraweeHierarchyBuilder.newInstance(imageView.context.resources)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .build()
            val controller = Fresco.newDraweeControllerBuilder()
                .setAutoPlayAnimations(true)
                .setUri(imageUri)
                .setOldController(imageView.controller)
                .setTapToRetryEnabled(true)
                .setControllerListener(object: ControllerListener<ImageInfo> {
                    override fun onSubmit(id: String?, callerContext: Any?) {
                        // do something
                    }
                    override fun onIntermediateImageFailed(id: String?, throwable: Throwable?) {
                        // do something
                    }
                    override fun onFailure(id: String?, throwable: Throwable?) {
                        // do something
                    }
                    override fun onRelease(id: String?) {
                        // do something
                    }
                    override fun onIntermediateImageSet(id: String?, imageInfo: ImageInfo?) {
                        // do something
                    }
                    override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
                        if (animatable is AnimatedDrawable2) {
                            animatable.setAnimationListener(object: AnimationListener {
                                override fun onAnimationStart(drawable: AnimatedDrawable2?) {
                                    repeatCount++
                                }
                                override fun onAnimationStop(drawable: AnimatedDrawable2?) {
                                    // do something
                                }
                                override fun onAnimationReset(drawable: AnimatedDrawable2?) {
                                    // do something
                                }
                                override fun onAnimationRepeat(drawable: AnimatedDrawable2?) {
                                    if (repeatCount >= replayCount) {
                                        animatable.stop()
                                    }
                                    repeatCount++
                                }
                                override fun onAnimationFrame(drawable: AnimatedDrawable2?, frameNumber: Int) {
                                    // do something
                                }
                            })
                            animatable.start()
                        } else {
                            animatable?.start()
                        }
                    }
                }).build()

            imageView.controller = controller
            imageView.hierarchy = hierarchy
        }

        fun imageLoad(uri: Uri, imageView: SimpleDraweeView) {
            if (!Fresco.hasBeenInitialized()) {
                return
            }
            val controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setControllerListener(object: BaseControllerListener<ImageInfo>() {
                    override fun onFailure(id: String?, throwable: Throwable?) {
                        // do something
                    }
                })
                .setAutoPlayAnimations(true)
                .build()
            imageView.controller = controller
        }
        fun imageLoad(uri: Uri, imageView: GenericDraweeView) {
            if (!Fresco.hasBeenInitialized()) {
                return
            }
            val controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setControllerListener(object: BaseControllerListener<ImageInfo>() {
                    override fun onFailure(id: String?, throwable: Throwable?) {
                        // do something
                    }
                })
                .setAutoPlayAnimations(true)
                .build()
            imageView.controller = controller
        }
        fun imageResizeLoad(uri: Uri, width: Int, height: Int, imageView: GenericDraweeView) {
            if (!Fresco.hasBeenInitialized()) {
                return
            }
            val resizeOptions = ResizeOptions(width, height)
            val imageRequest = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(resizeOptions)
                .build()
            val hierarchy: GenericDraweeHierarchy = GenericDraweeHierarchyBuilder.newInstance(imageView.context.resources)
                .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE)
                .build()
            val controllerBuilder = Fresco.newDraweeControllerBuilder()
                .setControllerListener(object: ControllerListener<ImageInfo> {
                    override fun onSubmit(id: String?, callerContext: Any?) {
                        // do something
                    }
                    override fun onIntermediateImageFailed(id: String?, throwable: Throwable?) {
                        // do something
                    }
                    override fun onIntermediateImageSet(id: String?, imageInfo: ImageInfo?) {
                        // do something
                    }
                    override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
                        imageView.updateLayoutParams { this.width = resizeOptions.width }
                        val widthFloat = imageInfo?.width?.toFloat() ?: return
                        val heightFloat = imageInfo.height.toFloat()
                        if (heightFloat > 0F) {
                            imageView.aspectRatio = widthFloat / heightFloat
                        }
                    }
                    override fun onFailure(id: String?, throwable: Throwable?) {
                        // do something
                    }
                    override fun onRelease(id: String?) {
                        // do something
                    }
                })
                .setOldController(imageView.controller)
                .setImageRequest(imageRequest)
            imageView.controller = controllerBuilder.build()
            imageView.hierarchy = hierarchy
        }
        fun gifLoad(uri: Uri, imageView: SimpleDraweeView, replayCount: Int =  5) {
            if (!Fresco.hasBeenInitialized()) {
                return
            }
            var repeatCount = 0
            val hierarchy = GenericDraweeHierarchyBuilder.newInstance(imageView.context.resources)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .build()
            val controller = Fresco.newDraweeControllerBuilder()
                .setAutoPlayAnimations(true)
                .setUri(uri)
                .setOldController(imageView.controller)
                .setTapToRetryEnabled(true)
                .setControllerListener(object: ControllerListener<ImageInfo> {
                    override fun onSubmit(id: String?, callerContext: Any?) {
                        // do something
                    }
                    override fun onIntermediateImageFailed(id: String?, throwable: Throwable?) {
                        // do something
                    }
                    override fun onFailure(id: String?, throwable: Throwable?) {
                        // do something
                    }
                    override fun onRelease(id: String?) {
                        // do something
                    }
                    override fun onIntermediateImageSet(id: String?, imageInfo: ImageInfo?) {
                        // do something
                    }
                    override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
                        if (animatable is AnimatedDrawable2) {
                            animatable.setAnimationListener(object: AnimationListener {
                                override fun onAnimationStart(drawable: AnimatedDrawable2?) {
                                    repeatCount++
                                }
                                override fun onAnimationStop(drawable: AnimatedDrawable2?) {
                                    // do something
                                }
                                override fun onAnimationReset(drawable: AnimatedDrawable2?) {
                                    // do something
                                }
                                override fun onAnimationRepeat(drawable: AnimatedDrawable2?) {
                                    if (repeatCount >= replayCount) {
                                        animatable.stop()
                                    }
                                    repeatCount++
                                }
                                override fun onAnimationFrame(drawable: AnimatedDrawable2?, frameNumber: Int) {
                                    // do something
                                }
                            })
                            animatable.start()
                        } else {
                            animatable?.start()
                        }
                    }
                }).build()

            imageView.controller = controller
            imageView.hierarchy = hierarchy
        }

        fun imageLoad(@DrawableRes resourceId: Int, imageView: SimpleDraweeView) {
            imageLoad(UriUtil.getUriForResourceId(resourceId), imageView)
        }
        fun imageLoad(@DrawableRes resourceId: Int, imageView: GenericDraweeView) {
            imageLoad(resourceId, imageView)
        }
        fun imageResizeLoad(@DrawableRes resourceId: Int, width: Int, height: Int, imageView: GenericDraweeView) {
            imageResizeLoad(UriUtil.getUriForResourceId(resourceId), width, height, imageView)
        }
        fun gifLoad(@DrawableRes resourceId: Int, imageView:SimpleDraweeView, replayCount: Int = 5) {
            gifLoad(UriUtil.getUriForResourceId(resourceId), imageView, replayCount)
        }
    }
}