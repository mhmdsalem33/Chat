package mhmd.salem.chatkotlin.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import mhmd.salem.chatkotlin.Models.UserModel
import mhmd.salem.chatkotlin.R
import mhmd.salem.chatkotlin.databinding.FragmentEdtNameBinding
import org.w3c.dom.Text


class EdtNameFragment : BottomSheetDialogFragment() {

    private lateinit var binding : FragmentEdtNameBinding
    private var firestore    : FirebaseFirestore ? = null
    private var firebaseAuth : FirebaseAuth ? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore     = FirebaseFirestore.getInstance()
        firebaseAuth  = FirebaseAuth.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEdtNameBinding.inflate(inflater , container , false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onCancelClick()
        onSaveNameClick()
        getUserName()
    }

    private fun getUserName() {

        val edtName = binding.root.findViewById<TextView>(R.id.edt_name_profile)
        firestore!!.collection("Users").document(firebaseAuth!!.currentUser!!.uid).addSnapshotListener{
            values  , error ->
            val userList  = arrayListOf<UserModel>()
            val userModel = values?.toObject(UserModel::class.java)
                userList.add(userModel!!)
            for (i in userList)
            {
                edtName.text = i.userName
            }
        }
    }

    private fun onCancelClick() {
        binding.txtCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun onSaveNameClick(){
        binding.txtSave.setOnClickListener {
            val userName = binding.edtNameProfile.text.toString()
            if (userName.isEmpty())
            {
                Toast.makeText(context, "Name must not be empty", Toast.LENGTH_SHORT).show()
            }
            else
            {

               updateName(userName)
            }
        }
    }

    private fun updateName(userName: String) {

        val nameMap = HashMap<String , Any>()
            nameMap["userName"]   = userName

        firestore!!.collection("Users").document(firebaseAuth!!.currentUser!!.uid)
            .update(nameMap)
            .addOnCompleteListener{ task ->
                task.addOnSuccessListener { Toast.makeText(context, "Name Updated ", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp() }
                task.addOnFailureListener { Toast.makeText(context, ""+it.message, Toast.LENGTH_SHORT).show() }
            }



    }

}