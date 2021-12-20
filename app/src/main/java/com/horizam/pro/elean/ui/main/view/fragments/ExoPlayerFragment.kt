package com.horizam.pro.elean.ui.main.view.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.requests.LoginRequest
import com.horizam.pro.elean.data.model.requests.RegisterRequest
import com.horizam.pro.elean.data.model.response.LoginResponse
import com.horizam.pro.elean.data.model.response.RegisterResponse
import com.horizam.pro.elean.databinding.FragmentExoPlayerBinding
import com.horizam.pro.elean.databinding.FragmentLoginBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.view.activities.HomeActivity
import com.horizam.pro.elean.ui.main.viewmodel.LoginViewModel
import com.horizam.pro.elean.ui.main.viewmodel.RegisterViewModel
import com.horizam.pro.elean.utils.BaseUtils.Companion.hideKeyboard
import com.horizam.pro.elean.utils.PrefManager
import com.horizam.pro.elean.utils.Status
import com.horizam.pro.elean.utils.Validator


class ExoPlayerFragment : Fragment() {

    private lateinit var binding: FragmentExoPlayerBinding
    private lateinit var proEleanExoplayer: SimpleExoPlayer
    private var playbackPosition: Long = 0
    private var mediaUrl: String? = ""
    private lateinit var genericHandler: GenericHandler
    private val args: ExoPlayerFragmentArgs by navArgs()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExoPlayerBinding.inflate(layoutInflater, container, false)
        initViews()
        return binding.root
    }

    private fun initViews() {
        mediaUrl = args.url
        if (mediaUrl.isNullOrEmpty()) {
            genericHandler.showMessage(getString(R.string.str_something_went_wrong))
            findNavController().popBackStack()
        }
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun initializePlayer() {
        proEleanExoplayer = SimpleExoPlayer.Builder(requireContext()).build()
        val mediaItem = createMediaItem()
        proEleanExoplayer.addMediaItem(mediaItem)
        proEleanExoplayer.prepare()
        binding.exoplayerView.player = proEleanExoplayer
        proEleanExoplayer.seekTo(playbackPosition)
        proEleanExoplayer.playWhenReady = true
        proEleanExoplayer.addListener(exoListener)
    }

    private fun createMediaItem(): MediaItem {
        val mediaUri = Uri.parse(mediaUrl)
        return MediaItem.fromUri(mediaUri)
    }

    private fun releasePlayer() {
        playbackPosition = proEleanExoplayer.currentPosition
        proEleanExoplayer.release()
    }

    private val exoListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) { // check player play back state
                Player.STATE_READY -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    //binding.aspectRationLayout.setAspectRatio()
                }
                Player.STATE_ENDED -> {
                    binding.progressBar.visibility = View.INVISIBLE
                }
                Player.STATE_BUFFERING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                Player.STATE_IDLE -> {
                    binding.progressBar.visibility = View.INVISIBLE
                }
                else -> {
                    binding.exoplayerView.hideController()
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            genericHandler.showMessage(error.message.toString())
        }
    }

}