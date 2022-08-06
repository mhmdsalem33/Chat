package mhmd.salem.chatkotlin.Activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mhmd.salem.chatkotlin.R
import mhmd.salem.chatkotlin.appUtil.AppUtil
import mhmd.salem.chatkotlin.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySplashBinding
    private var firebaseAuth : FirebaseAuth ? = null
    private lateinit var topAnimation :Animation


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()


        topAnimation =AnimationUtils.loadAnimation(this , R.anim.top_anim)
        binding.imgWhats.animation = topAnimation



        if (firebaseAuth!!.currentUser != null)
        {
            val intent = Intent(this , UsersActivity::class.java)
                startActivity(intent)
                finish()
        }
        else
        {
            GlobalScope.launch {
                delay(2000)
                val intent = Intent(this@SplashActivity , AuthActivity::class.java)
                startActivity(intent)
                finish()
            }

        }


    }





}