package com.rajaravi.brightkid

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.rajaravi.brightkid.databinding.ActivityRememberPlaceBinding

class RememberPlace : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var binding: ActivityRememberPlaceBinding
    private val sourceImages = mutableListOf(R.drawable.superman, R.drawable.batman, R.drawable.spiderman, R.drawable.superman, R.drawable.batman, R.drawable.spiderman)
    private var lastSeenImageId: Int? = null
    private var lastSeenImageView: ImageView? = null
    private var gameEnd: Boolean = false
    private var closedPositions: List<String> = listOf()
    private val totalPosition: Int = 6


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_remember_place)
        mediaPlayer = MediaPlayer.create(this, R.raw.the_cutest_bunny)

        if (savedInstanceState == null) {
            fillInitialImage()
        }
    }

    private fun fillInitialImage(loadFresh: Boolean = false) {
        mediaPlayer.start()
        sourceImages.shuffle()
        for (imageViewId in 0..binding.hostLayout.childCount) {
            val imageView = binding.hostLayout.getChildAt(imageViewId) as ImageView?
            imageView?.setTag(sourceImages.get(imageViewId))
            if (loadFresh) {
                imageView?.setImageResource(android.R.drawable.ic_menu_help)
            }
        }
    }

    fun playAgain(view: View) {
        fillInitialImage(loadFresh = true)
        lastSeenImageId = null
        lastSeenImageView = null
        gameEnd = false
        closedPositions = listOf()
        view.visibility = View.GONE
    }

    fun openImage(view: View) {
        val imageView = view as ImageView
        val currentImageId = view.tag?.toString()?.toInt()

        if (currentImageId == null) {
            return
        }

        if (gameEnd) {
            Snackbar.make(view, "Game end.. Play Again", Snackbar.LENGTH_LONG).show()
        }

        if (view.id.toString() in closedPositions) {
            return
        }

        if (lastSeenImageView == imageView) {
            return
        }

        val rotate = RotateAnimation(0f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotate.duration = 75
        rotate.repeatCount = 1
        rotate.setAnimationListener(object: Animation.AnimationListener{
            override fun onAnimationRepeat(p0: Animation?) {
            }
            override fun onAnimationEnd(p0: Animation?) {
                if (lastSeenImageId == currentImageId) {
                    lastSeenImageId?.let {it -> lastSeenImageView?.setImageResource(it)}
                    imageView.setImageResource(currentImageId)
                    closedPositions = closedPositions + listOf(lastSeenImageView?.id.toString(), imageView.id.toString())
                    lastSeenImageId = null
                    lastSeenImageView = null
                    Snackbar.make(view, "Good, Keep it up!", Snackbar.LENGTH_SHORT).show()

                    if (totalPosition == closedPositions.size) {
                        Snackbar.make(view, "You won!", Snackbar.LENGTH_LONG).show()
                        gameEnd = true
                        Snackbar.make(view, "Game end!", Snackbar.LENGTH_LONG).show()
                        binding.apply {
                            replayBtn.visibility = View.VISIBLE
                            invalidateAll()
                        }
                    }
                } else {
                    imageView.setImageResource(android.R.drawable.ic_menu_help)
                }
                lastSeenImageId = currentImageId
                lastSeenImageView = imageView

            }
            override fun onAnimationStart(p0: Animation?) {
                imageView.setImageResource(currentImageId)
            }
        })
        imageView.startAnimation(rotate)
    }
}
