package mhmd.salem.chatkotlin.Activites

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mhmd.salem.chatkotlin.Adapters.UsersAdapter
import mhmd.salem.chatkotlin.Fragments.SearchFragment
import mhmd.salem.chatkotlin.Models.UserModel
import mhmd.salem.chatkotlin.R
import mhmd.salem.chatkotlin.appUtil.AppUtil
import mhmd.salem.chatkotlin.databinding.ActivityMainBinding

class UsersActivity : AppCompatActivity() {


    private lateinit var binding     : ActivityMainBinding
    private var firebaseAuth         : FirebaseAuth      ? = null
    private var firestore            : FirebaseFirestore ? = null
    private lateinit var usersAdapter: UsersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore    = FirebaseFirestore.getInstance()
        usersAdapter = UsersAdapter()

        onMenuToolBarClick()
        getCurrentUserInformation()
        getUsersInformation()

        onImageUserClick()
        setupUsersRecView()
        onUserItemClick()
        onBoxSearchClick()


        val userID = firebaseAuth!!.currentUser!!.uid
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$userID")

    }

    private fun onBoxSearchClick() {
        binding.txtSearch.setOnClickListener {
           supportFragmentManager.beginTransaction()
               .replace(R.id.user_activity , SearchFragment())
               .addToBackStack(null)
               .commit()
        }
    }

    private fun onUserItemClick() {
        usersAdapter.onUserItemClick = { data ->
            val intent = Intent(this@UsersActivity , ChatActivity::class.java)
                intent.putExtra("otherUserId"     , data.userId)
                intent.putExtra("otherUserName"   , data.userName)
                intent.putExtra("otherUserImage"  , data.imageProfile)
            startActivity(intent)
        }
    }

    private fun getUsersInformation() {
        firestore!!.collection("Users")
            .addSnapshotListener{ values , error ->
                val  userList  =  arrayListOf<UserModel>()
                val  userModel =  values?.toObjects(UserModel::class.java)
                     userList.addAll(userModel!!)
                usersAdapter.differ.submitList(userList)
            }
    }
    private fun setupUsersRecView() {
        binding.usersRec.apply {
            layoutManager = LinearLayoutManager(context)
            adapter       = usersAdapter
        }
    }
    private fun onImageUserClick() {
        binding.imgUser.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
                intent.putExtra("menuName" , "Profile")
                startActivity(intent)
        }
    }
    private fun getCurrentUserInformation() {
        firestore!!.collection("Users").document(firebaseAuth!!.uid!!)
            .addSnapshotListener{ value , error ->
                if (error != null)
                {
                    Log.d("testApp" , error.message.toString())
                }
                else
                {
                    val  userList  = arrayListOf<UserModel>()
                    val  userModel = value?.toObject(UserModel::class.java)
                         userList.add(userModel!!)

                    var sharedPref  : SharedPreferences? = null
                        sharedPref     = getSharedPreferences("myshared" , 0)  // file name and mode private
                    var editor         = sharedPref.edit()
                        editor.putString("userName" , userModel.userName)  //inset data
                        editor.commit()
                  //      var readSharedPref = getSharedPreferences("myshared" , 0)
                     //   Log.d("test" , readSharedPref.getString("userName" , "No data fount").toString())

                    for (i in userList)
                    {
                            if (userModel.imageProfile.isNotEmpty() && i.imageProfile.isNotEmpty())
                            {

                                if (applicationContext == null)
                                {
                                    Log.d("testApp" , "User Context Empty")
                                }
                                else
                                {
                                    Glide.with(applicationContext)
                                        .load(i.imageProfile)
                                        .error(R.drawable.person)
                                        .into(binding.imgUser)
                                }
                            }
                    }
                }
            }
    }
    private fun onMenuToolBarClick() {
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId){
                R.id.profile -> {
                    val intent = Intent(this, MenuActivity::class.java)
                        intent.putExtra("menuName" , "Profile")
                    startActivity(intent)
                }

                R.id.logout  -> {
                    lifecycleScope.launch (Dispatchers.Default) {
                        val appUtil = AppUtil()
                            appUtil.updateOnlineStatus("offline")
                        delay(500)
                        FirebaseAuth.getInstance().signOut()
                        val  intent = Intent(this@UsersActivity , SplashActivity::class.java)
                             intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                             delay(2000)
                        startActivity(intent)
                    }
                }
            }
            true
        }
    }

    override fun onPause() {
        super.onPause()
           val appUtil = AppUtil()
               appUtil.updateOnlineStatus("offline")
    }

    override fun onResume() {
        super.onResume()
       val appUtil = AppUtil()
           appUtil.updateOnlineStatus("online")
    }
}