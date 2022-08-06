package mhmd.salem.chatkotlin.Activites

import android.app.AlertDialog
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import kotlinx.coroutines.*
import mhmd.salem.chatkotlin.Adapters.ChatAdapter
import mhmd.salem.chatkotlin.Models.ChatModel
import mhmd.salem.chatkotlin.Models.UserModel
import mhmd.salem.chatkotlin.Notifications.NotificationData
import mhmd.salem.chatkotlin.Notifications.PushNotification
import mhmd.salem.chatkotlin.Notifications.RetrofitInstance
import mhmd.salem.chatkotlin.R

import mhmd.salem.chatkotlin.appUtil.AppUtil
import mhmd.salem.chatkotlin.databinding.ActivityChatBinding
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatActivity : AppCompatActivity() {

    private lateinit var binding   :ActivityChatBinding
    private var firebaseAuth            :FirebaseAuth      ?  = null
    private var firestore               :FirebaseFirestore ? = null
    private var mDatabase               :DatabaseReference ? = null
    private lateinit var otherUserId    :String
    private lateinit var otherUserName  :String
    private lateinit var otherUserImage :String
    private lateinit var chatAdapter    : ChatAdapter
    private var imgUri : Uri? = null


    val getImage = registerForActivityResult(ActivityResultContracts.GetContent(), ActivityResultCallback {
            uri -> imgUri = uri })

    var chatList   = arrayListOf<ChatModel>()

    var topic = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore    = FirebaseFirestore.getInstance()
        chatAdapter  = ChatAdapter()

            getOtherUserInformationByIntent()
            setOtherInformationInViews()
            onArrowBackClick()
            checkUser()
            checkUserStatus()

            setupMessage()
            readMessage(firebaseAuth!!.uid , otherUserId)

            checkTypingStatus()
            sendImages()

    }


    private fun sendImages() {
        binding.btnSendImage.setOnClickListener {
            getImage.launch("image/")
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Send Image")
            builder.setNegativeButton("No"){dialog ,  _-> dialog.dismiss() }
            builder.setPositiveButton("Yes"){dialog , _->
            if (imgUri != null)
            {
                val imagePath = FirebaseStorage.getInstance().getReference()
                    .child("userImage/"+ firebaseAuth!!.uid + Calendar.getInstance().time + ".jpg")
                imagePath.putFile(imgUri!!)
                    .addOnFailureListener{
                        Toast.makeText(applicationContext, ""+it.message, Toast.LENGTH_SHORT).show()
                    }
                    .addOnCompleteListener {
                        imagePath.downloadUrl.addOnSuccessListener { uri ->
                            val imageMap = HashMap<String ,Any>()
                            imageMap["senderId"]    =  firebaseAuth!!.currentUser!!.uid
                            imageMap["receiverId"]  =  otherUserId
                            imageMap["img"]         =  uri.toString()
                            FirebaseDatabase.getInstance().getReference().child("Chats")
                                .push()
                                .setValue(imageMap)
                                .addOnSuccessListener {

                                    Toast.makeText(applicationContext, "success", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(applicationContext, ""+it.message, Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
            }
            }.setCancelable(false)
            val dialog = builder.create()
            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(Color.rgb(0,128,128))
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(Color.rgb(0,128,128))
            }
            dialog.show()
        }

    }

    private fun checkTypingStatus() {
        binding.edtMessage.addTextChangedListener(object  :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (p0.toString().isEmpty())
                    {
                        typingStatus("false")
                    }
                else
                    {
                        typingStatus(otherUserId)
                    }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }
    private fun typingStatus(typing: String) {

        val typingMap = HashMap<String ,Any>()
            typingMap["typing"] = typing

          firestore!!.collection("Users").document(firebaseAuth!!.uid!!)
              .update(typingMap)

    }
    private fun setupMessage() {
        binding.btnSendMessage.setOnClickListener {
            val message = binding.edtMessage.text.toString()
            if (message.isEmpty())
            {
                Toast.makeText(applicationContext, "Message is empty", Toast.LENGTH_SHORT).show()
            }
            else
            {
                sendMessage(firebaseAuth!!.currentUser!!.uid , otherUserId , message)
                binding.edtMessage.text.clear()

                var readSharedPref = getSharedPreferences("myshared" , 0)
                val userName =  readSharedPref.getString("userName" , "User Name Not Found")
                topic  = "/topics/$otherUserId"
                PushNotification(
                    NotificationData(userName.toString(), message)
                    , topic).also { sendNotification(it) }

            }

        }
    }

    private fun sendMessage(senderId: String, receiverId: String, message: String) {
        val messageMap = HashMap<String , String>()
            messageMap["senderId"]    = senderId
            messageMap["receiverId"]  = receiverId
            messageMap["message"]     = message

            FirebaseDatabase.getInstance().getReference().child("Chats")
                .push()  // generate Unique id
                .setValue(messageMap)
                .addOnSuccessListener {
                    Toast.makeText(applicationContext, "Message Send Success", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, ""+it.message, Toast.LENGTH_SHORT).show()
                }
    }
    private fun readMessage(senderId: String?, receiverId: String) {

        mDatabase = FirebaseDatabase.getInstance().getReference("Chats")
        mDatabase!!.addValueEventListener(object : ValueEventListener{
              override fun onDataChange(snapshot: DataSnapshot) {
                  chatList.clear()
                  for (data in snapshot.children)
                  {
                      val chatModel = data.getValue(ChatModel::class.java)
                      if (chatModel!!.senderId == senderId   && chatModel.receiverId   == receiverId ||
                          chatModel.senderId   == receiverId && chatModel.receiverId == senderId
                      ){
                          chatList.add(chatModel)
                      }
                      chatAdapter.differ.submitList(chatList)
                      binding.chatRec.apply {
                          layoutManager = LinearLayoutManager(context)
                          adapter       = chatAdapter
                          scrollToPosition(chatList.size - 1)
                      }
                  }
              }
              override fun onCancelled(error: DatabaseError) {
                  Log.d("testApp" , error.message.toString())
              }

          })
        mDatabase!!.keepSynced(true)

    }


    private fun sendNotification(notification :PushNotification) = CoroutineScope(Dispatchers.IO).launch {


            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful)
                {
                   // Log.d("testApp" , "Response: ${Gson().toJson(response)}")
                }
                else
                {
                    Log.d("testApp" , response.errorBody().toString())
                }

            }catch (e:Exception)
            {
                Log.d("testApp" , e.toString())
            }




    }

    private fun checkUserStatus() {
        firestore!!.collection("Users").document(otherUserId)
            .addSnapshotListener{ value , error ->

                val userList = arrayListOf<UserModel>()
                val userModel = value?.toObject(UserModel::class.java)
                    userList.add(userModel!!)

                for (i in userList)
                {
                    if (i.status == "online")
                    {
                        binding.icOnline.visibility  = View.VISIBLE
                        binding.icOffline.visibility = View.GONE
                    }
                    else
                    {
                        binding.icOffline.visibility = View.VISIBLE
                        binding.icOnline.visibility  = View.GONE
                    }


                    val typing = i.typing
                    if (typing == firebaseAuth!!.currentUser!!.uid)
                    {
                        binding.animationView.visibility = View.VISIBLE
                        binding.animationView.playAnimation()
                    }
                    else
                    {
                        binding.animationView.cancelAnimation()
                        binding.animationView.visibility = View.INVISIBLE
                    }

                }
            }
    }
    private fun checkUser() {
        if (firebaseAuth!!.uid!! == otherUserId)
        {
            binding.txtMe.text = "It's Me"
        }
    }
    private fun onArrowBackClick() {
        binding.arrowBack.setOnClickListener {
            finish()
        }
    }
    private fun setOtherInformationInViews() {
       if (otherUserImage.isNotEmpty())
       {
           Glide.with(applicationContext)
               .load(otherUserImage)
               .error(R.drawable.person)
               .into(binding.imgUser)
       }
        binding.txtName.text = otherUserName

    }
    private fun getOtherUserInformationByIntent() {
        val intent     = intent
            otherUserId    = intent.getStringExtra( "otherUserId").toString()
            otherUserName  = intent.getStringExtra("otherUserName").toString()
            otherUserImage = intent.getStringExtra("otherUserImage").toString()
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

    override fun onStop() {
        super.onStop()
        typingStatus("false")
    }

}