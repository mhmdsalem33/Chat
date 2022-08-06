package mhmd.salem.chatkotlin.appUtil

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mhmd.salem.chatkotlin.Models.UserModel

class AppUtil {


   // private var firebaseAuth : FirebaseAuth ? = null
   // private var firestore    : FirebaseFirestore ? = null

    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var firestore    : FirebaseFirestore


     fun updateOnlineStatus(online :String){
            firebaseAuth  = FirebaseAuth.getInstance()
            firestore     = FirebaseFirestore.getInstance()

            val statusMap = HashMap<String , Any>()
                statusMap["status"]  = online
            FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().currentUser!!.uid)
                .update(statusMap)
                .addOnCompleteListener{
                    it.addOnFailureListener { error ->
                        Log.d("testApp" , ""+error.message.toString())
                    }
                }


    }




}