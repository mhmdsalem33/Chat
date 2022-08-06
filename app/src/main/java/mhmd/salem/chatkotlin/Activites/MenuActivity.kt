package mhmd.salem.chatkotlin.Activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import mhmd.salem.chatkotlin.Fragments.ProfileFragment
import mhmd.salem.chatkotlin.R
import mhmd.salem.chatkotlin.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var binding  :ActivityMenuBinding
    private lateinit var menuName :String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)


        getInformationMenuByIntent()
        onArrowBackClick()
    }

    private fun onArrowBackClick() {
        binding.arrowBack.setOnClickListener {
            finish()
        }
    }

    private fun getInformationMenuByIntent() {
        val intent = intent
            menuName = intent.getStringExtra("menuName").toString()
            binding.toolbar.title = menuName
            if (menuName == "Profile")
            {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.hostFragment , ProfileFragment())
                    .commit()
            }
    }


}