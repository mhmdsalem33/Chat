package mhmd.salem.chatkotlin.Activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import mhmd.salem.chatkotlin.Fragments.LoginFragment
import mhmd.salem.chatkotlin.Fragments.SignupFragment
import mhmd.salem.chatkotlin.R
import mhmd.salem.chatkotlin.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {


    private lateinit var binding          : ActivityAuthBinding
    private lateinit var viewPager2       : ViewPager2
    private val titles = arrayListOf("Login" , "Signup")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val tapLayout = binding.tapLayout
            viewPager2 = binding.viewPager2
            viewPager2.adapter = AuthPagerAdapter(this)


        TabLayoutMediator(tapLayout, viewPager2){ tab, position ->
            tab.text = titles[position]
        }.attach()

    }


    class AuthPagerAdapter(fragmentActivity: FragmentActivity):FragmentStateAdapter(fragmentActivity)
    {
        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
                return when(position){
                   0 -> LoginFragment()
                   1 -> SignupFragment()
                    else -> LoginFragment()
                }
        }

    }

}