package io.agnamc.phonecallui

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MotionEventCompat
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.RelativeLayout
import de.hdodenhof.circleimageview.CircleImageView


class MainActivity : AppCompatActivity() {

    private val BUBBLES_PER_SIDE = 3

    private lateinit var inputCallAnimation: Animation
    private lateinit var inputCallBackgroundAnimation: Animation

    private lateinit var hangUpBackgroundRevealAnimation: Animation
    private lateinit var pickUpBackgroundRevealAnimation: Animation


    private var lastTouchX: Float = 0f
    private var lastTouchY: Float = 0f

    // Initial coordinates before call input drag
    private var initInputCallX: Float = 0f
    private var initInputCallY: Float = 0f

    private var screenWidth: Int = 0

    private var leftBubbles = mutableListOf<CircleImageView>()
    private var rightBubbles = mutableListOf<CircleImageView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get width of screen
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        screenWidth = size.x

        // Initialize animations and listeners
        inputCallAnimation = AnimationUtils.loadAnimation(this, R.anim.call_input_anim)
        imageInputCall.startAnimation(inputCallAnimation)

        inputCallBackgroundAnimation = AnimationUtils.loadAnimation(this, R.anim.call_input_background_anim)
        circleImageInputCall.startAnimation(inputCallBackgroundAnimation)

        hangUpBackgroundRevealAnimation = AnimationUtils.loadAnimation(this, R.anim.call_reveal_anim)
        hangUpBackgroundRevealAnimation.setAnimationListener(hangUpAnimListener)

        pickUpBackgroundRevealAnimation = AnimationUtils.loadAnimation(this, R.anim.call_reveal_anim)
        pickUpBackgroundRevealAnimation.setAnimationListener(pickUpAnimListener)

        createLeftBubbles()
        createRightBubbles()

        setupLeftBubblesAnimations()
        setupRightBubblesAnimations()

        circleImageInputCall.setOnTouchListener(touchListener)

    }

    private fun setupRightBubblesAnimations() {

        for (i in 1..BUBBLES_PER_SIDE) {

            val rightBubble = rightBubbles[i - 1]

            val rightTranslation = ObjectAnimator.ofFloat(rightBubble, "translationX", 85f).apply {
                duration = 1000
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {}
                    override fun onAnimationEnd(animation: Animator?) {}
                    override fun onAnimationCancel(animation: Animator?) {}
                    override fun onAnimationStart(animation: Animator?) {
                        rightBubble.visibility = View.VISIBLE
                    }
                })
            }

            val translationAlpha = ObjectAnimator.ofFloat(rightBubble, "alpha", 0.5f).apply {
                duration = 1000
            }

            val scaleAlpha = ObjectAnimator.ofFloat(rightBubble, "alpha", 0f).apply {
                duration = 1000
                startDelay = 250
            }

            val translationScaleX = ObjectAnimator.ofFloat(rightBubble, "scaleX", 1.5f).apply {
                duration = 1000
            }

            val alphaScaleX = ObjectAnimator.ofFloat(rightBubble, "scaleX", 3f).apply {
                duration = 1000
                startDelay = 1000
            }

            val translationScaleY = ObjectAnimator.ofFloat(rightBubble, "scaleY", 1.5f).apply {
                duration = 1000
            }

            val alphaScaleY = ObjectAnimator.ofFloat(rightBubble, "scaleY", 3f).apply {
                duration = 1000
                startDelay = 1000
            }

            var animatorSet = AnimatorSet().apply {
                play(rightTranslation).with(translationScaleX).with(translationScaleY).with(translationAlpha)
                    .before(scaleAlpha).with(alphaScaleX)
                    .with(alphaScaleY)
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {}
                    override fun onAnimationCancel(animation: Animator?) {}
                    override fun onAnimationStart(animation: Animator?) {}
                    override fun onAnimationEnd(animation: Animator?) {
                        start()
                    }
                })
            }

            Handler().postDelayed({
                animatorSet.start()
            }, 400L * (i + 1))

        }
    }

    private fun setupRightBubbles() {
        createRightBubbles()
        setupRightBubblesAnimations()
    }

    private fun createRightBubbles() {
        for (i in 1..BUBBLES_PER_SIDE) {
            val bubble = CircleImageView(this)

            val layoutParams = RelativeLayout.LayoutParams(15, 15)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL)

            bubble.layoutParams = layoutParams
            bubble.setImageResource(R.color.green_2)
            bubble.visibility = View.GONE

            layoutRightBubbles.addView(bubble)

            rightBubbles.add(bubble)
        }
    }

    private fun setupLeftBubbles() {

    }

    private fun setupLeftBubblesAnimations() {

        for (i in 1..BUBBLES_PER_SIDE) {

            val leftBubble = leftBubbles[i - 1]
            val leftTranslation = ObjectAnimator.ofFloat(leftBubble, "translationX", -85f).apply {
                duration = 1000
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {}
                    override fun onAnimationEnd(animation: Animator?) {}
                    override fun onAnimationCancel(animation: Animator?) {}
                    override fun onAnimationStart(animation: Animator?) {
                        leftBubble.visibility = View.VISIBLE
                    }
                })
            }

            val translationAlpha = ObjectAnimator.ofFloat(leftBubble, "alpha", 0.5f).apply { duration = 1000 }

            val scaleAlpha = ObjectAnimator.ofFloat(leftBubble, "alpha", 0f).apply {
                duration = 1000
                startDelay = 250
            }

            val translationScaleX = ObjectAnimator.ofFloat(leftBubble, "scaleX", 1.5f).apply {
                duration = 1000
            }

            val alphaScaleX = ObjectAnimator.ofFloat(leftBubble, "scaleX", 3f).apply {
                duration = 1000
                startDelay = 1000
            }

            val translationScaleY = ObjectAnimator.ofFloat(leftBubble, "scaleY", 1.5f).apply {
                duration = 1000
            }

            val alphaScaleY = ObjectAnimator.ofFloat(leftBubble, "scaleY", 3f).apply {
                duration = 1000
                startDelay = 1000
            }

            var animatorSet = AnimatorSet().apply {
                play(leftTranslation).with(translationScaleX).with(translationScaleY).with(translationAlpha)
                    .before(scaleAlpha).with(alphaScaleX)
                    .with(alphaScaleY)
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {}
                    override fun onAnimationCancel(animation: Animator?) {}
                    override fun onAnimationStart(animation: Animator?) {}
                    override fun onAnimationEnd(animation: Animator?) {
                        start()
                    }
                })
            }

            // pan
            Handler().postDelayed({
                animatorSet.start()
            }, 400L * (i + 1))

        }
    }

    private fun createLeftBubbles() {
        for (i in 1..BUBBLES_PER_SIDE) {
            val bubble = CircleImageView(this)
            val layoutParams = RelativeLayout.LayoutParams(15, 15)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL)

            bubble.layoutParams = layoutParams
            bubble.setImageResource(R.color.red)
            bubble.visibility = View.GONE

            layoutLeftBubbles.addView(bubble)

            leftBubbles.add(bubble)
        }
    }

    private var hangUpAnimListener = object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {
        }

        override fun onAnimationEnd(animation: Animation?) {
        }

        override fun onAnimationStart(animation: Animation?) {
            civBgHangUp.visibility = View.VISIBLE
        }
    }

    private var pickUpAnimListener = object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {
        }

        override fun onAnimationEnd(animation: Animation?) {
        }

        override fun onAnimationStart(animation: Animation?) {
            civBgPickUp.visibility = View.VISIBLE
        }
    }

    private var touchListener = object : View.OnTouchListener {

        override fun onTouch(v: View?, ev: MotionEvent?): Boolean {
            val action = MotionEventCompat.getActionMasked(ev)

            var posX = ev!!.rawX
            var posY = ev!!.rawY

            when (action) {

                MotionEvent.ACTION_DOWN -> {

                    // Retrieve input call position
                    initInputCallX = v!!.x
                    initInputCallY = v!!.y

                    lastTouchX = posX
                    lastTouchY = posY

                    circleImageInputCall.clearAnimation()
                    imageInputCall.clearAnimation()
                    imageInputCall.visibility = View.GONE

                    imageHangUp.drawable.setTint(resources.getColor(android.R.color.white))
                    imagePickUp.drawable.setTint(resources.getColor(android.R.color.white))

                    civBgHangUp.startAnimation(hangUpBackgroundRevealAnimation)
                    civBgPickUp.startAnimation(pickUpBackgroundRevealAnimation)

                    layoutLeftBubbles.visibility = View.GONE
                    layoutRightBubbles.visibility = View.GONE

                }

                MotionEvent.ACTION_MOVE -> {
                    // Move only on x axis
                    v!!.x += posX - lastTouchX

                    when {
                        v!!.x < screenWidth / 2 - v!!.width -> (v!! as CircleImageView).setImageResource(R.color.red)
                        v!!.x > screenWidth / 2 -> (v!! as CircleImageView).setImageResource(R.color.green)
                        else -> (v!! as CircleImageView).setImageResource(android.R.color.white)
                    }

                    lastTouchX = posX
                    lastTouchY = posY
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {

                    imageInputCall.visibility = View.VISIBLE

                    circleImageInputCall.startAnimation(inputCallBackgroundAnimation)
                    imageInputCall.startAnimation(inputCallAnimation)

                    civBgHangUp.clearAnimation()
                    civBgPickUp.clearAnimation()

                    civBgHangUp.visibility = View.INVISIBLE
                    civBgPickUp.visibility = View.INVISIBLE

                    imageHangUp.drawable.setTint(resources.getColor(R.color.red))
                    imagePickUp.drawable.setTint(resources.getColor(R.color.green_2))

                    // Reset input call position
                    v!!.x = initInputCallX
                    v!!.y = initInputCallY

                    (v!! as CircleImageView).setImageResource(android.R.color.white)

                    layoutLeftBubbles.visibility = View.VISIBLE
                    layoutRightBubbles.visibility = View.VISIBLE
                }

            }

            return true
        }
    }
}
