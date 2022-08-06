package mhmd.salem.chatkotlin.Fragments

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import mhmd.salem.chatkotlin.Models.UserModel
import mhmd.salem.chatkotlin.R
import mhmd.salem.chatkotlin.appUtil.AppUtil
import mhmd.salem.chatkotlin.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {

    private lateinit var binding  :FragmentProfileBinding
    private var firebaseAuth      :FirebaseAuth      ?   = null
    private var firestore         :FirebaseFirestore ?   = null
    private var imageView         :ImageView? = null
    private var imageUri          :Uri? = null



    val getImage = registerForActivityResult(
        ActivityResultContracts.GetContent(),
        ActivityResultCallback { uri ->
            imageView?.setImageURI(uri)
            imageUri = uri
        })


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth  = FirebaseAuth.getInstance()
        firestore     = FirebaseFirestore.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater , container ,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


            onEdtNameClick()
            onEdtStatusClick()
            getUserInformation()
            setupImageProfile()




    }

    private fun setupImageProfile() {
            imageView = binding.profileImage
            binding.btmUploadImage.setOnClickListener{
                    getImage.launch("image/")
                    val builder = AlertDialog.Builder(context)
                        builder.setTitle("Change Avatar")
                        builder.setMessage("Are you sure want to change avatar?")
                        builder.setNegativeButton("Cancel") {dialog ,_-> dialog.dismiss()}
                        builder.setPositiveButton("Change") {dialog , _->
                            if(imageUri != null)
                            {
                              val imagePath = FirebaseStorage.getInstance().getReference()
                                  .child("avatar/"+firebaseAuth!!.currentUser!!.uid)
                                    imagePath.putFile(imageUri!!)
                                        .addOnFailureListener{error ->
                                            Toast.makeText(context, ""+error.message, Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnSuccessListener {
                                            imagePath.downloadUrl.addOnSuccessListener { uri ->
                                                val avatarMap = HashMap<String , Any>()
                                                    avatarMap["imageProfile"] = uri.toString()
                                                firestore!!.collection("Users")
                                                    .document(firebaseAuth!!.currentUser!!.uid)
                                                    .update(avatarMap)
                                                    .addOnSuccessListener {
                                                            dialog.dismiss()
                                                    }
                                                    .addOnFailureListener {
                                                            dialog.dismiss()
                                                    }
                                            }
                                            dialog.dismiss()

                                        }
                            }
                        }.setCancelable(false)

                val dialog = builder.create()
                    dialog.show()

            }


    }

    private fun getUserInformation() {

        firestore!!.collection("Users").document(firebaseAuth!!.currentUser!!.uid)
            .addSnapshotListener{ value , error ->
                if (error != null)
                {
                    Log.d("testApp" , error.message.toString())
                }
                else
                {
                    val userList = arrayListOf<UserModel>()
                    val userModel = value?.toObject(UserModel::class.java)
                        userList.add(userModel!!)

                    for (i in userList)
                    {
                        binding.Phone.text       = i.userPhone
                        binding.status.text      = i.about
                        binding.nameProfile.text = i.userName


                        if (userModel.imageProfile.isNotEmpty() &&  i.imageProfile.isNotEmpty())
                        {

                            if (context == null)
                            {
                                Log.d("testApp" , "Context Profile fragment empty")
                            }
                            else{
                                Glide.with(binding.root)
                                    .load(i.imageProfile)
                                    .into(binding.profileImage)
                            }

                        }

                    }



                   

                }

            }

    }
    private fun onEdtStatusClick() {
        binding.LinearAbout.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_edtAboutFragment)
        }

    }
    private fun onEdtNameClick() {

        binding.linearName.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_edtNameFragment)
        }
    }


}