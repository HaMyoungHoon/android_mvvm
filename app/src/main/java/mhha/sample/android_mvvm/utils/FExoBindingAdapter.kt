package mhha.sample.android_mvvm.utils

import android.net.Uri
import androidx.databinding.BindingAdapter
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView

class FExoBindingAdapter {
    companion object {
        @JvmStatic
        @BindingAdapter(value = ["exo_player_uri", "exo_player_uri_width", "exo_player_uri_height", "exo_player_auto_play"], requireAll = false)
        fun setExoPlayerUriSource(exoPlayer: StyledPlayerView, exo_player_uri: Uri?, exo_player_uri_width: Int?, exo_player_uri_height: Int?, exo_player_auto_play: Boolean?) {
            val player = exoPlayer.player as? ExoPlayer ?: return
            player.clearMediaItems()
            if (exo_player_uri == null) {
                return
            }
            if (exoPlayer.player == null) {
                return
            }
            val width = if (exo_player_uri_width == null) 0 else FExtensionUtils.dpToPx(exoPlayer.context, exo_player_uri_width)
            val height = if (exo_player_uri_height == null) 0 else FExtensionUtils.dpToPx(exoPlayer.context, exo_player_uri_height)
            player.addMediaItem(MediaItem.fromUri(exo_player_uri))
            player.volume = 0F
            player.repeatMode = Player.REPEAT_MODE_ALL
            player.playWhenReady = false
            player.trackSelectionParameters = player.trackSelectionParameters
                .buildUpon()
                .setMaxVideoSize(width, height)
                .build()
            player.addListener(object: Player.Listener {
            })
            if (exo_player_auto_play == true) {
                player.prepare()
                player.play()
            }
        }
        @JvmStatic
        @BindingAdapter(value = ["exo_player_url", "exo_player_url_width", "exo_player_url_height", "exo_player_auto_play"], requireAll = false)
        fun setExoPlayerUrlSource(exoPlayer: StyledPlayerView, exo_player_url: String?, exo_player_url_width: Int?, exo_player_url_height: Int?, exo_player_auto_play: Boolean?) {
            val player = exoPlayer.player as? ExoPlayer ?: return
            player.clearMediaItems()
            if (exo_player_url.isNullOrEmpty()) {
                return
            }
            if (exoPlayer.player == null) {
                return
            }
            val width = if (exo_player_url_width == null) 0 else FExtensionUtils.dpToPx(exoPlayer.context, exo_player_url_width)
            val height = if (exo_player_url_height == null) 0 else FExtensionUtils.dpToPx(exoPlayer.context, exo_player_url_height)
            player.addMediaItem(MediaItem.fromUri(exo_player_url))
            player.volume = 0F
            player.repeatMode = Player.REPEAT_MODE_ALL
            player.playWhenReady = false
            player.trackSelectionParameters = player.trackSelectionParameters
                .buildUpon()
                .setMaxVideoSize(width, height)
                .build()
            player.addListener(object: Player.Listener {
            })
            if (exo_player_auto_play == true) {
                player.prepare()
                player.play()
            }
        }
    }
}