package mhmd.salem.chatkotlin.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import mhmd.salem.chatkotlin.R
import mhmd.salem.chatkotlin.databinding.FragmentEdtAboutBinding
import mhmd.salem.chatkotlin.databinding.FragmentEdtNameBinding

class EdtAboutFragment : BottomSheetDialogFragment() {

    private lateinit var binding : FragmentEdtAboutBinding
    private var firebaseAuth     : FirebaseAuth       ? = null
    private var firestore        : FirebaseFirestore  ? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        firestore    = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEdtAboutBinding.inflate(inflater , container ,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        onCancelClick()
        updateAbout()


    }

    private fun updateAbout() {

        binding.txtSave.setOnClickListener {
            val about = binding.edtUpdate.text.toString()
            if (about.isEmpty())
            {
                Toast.makeText(context, "Your Status must not be empty", Toast.LENGTH_SHORT).show()
            }
            else
            {
                val aboutMap  = HashMap<String , Any>()
                    aboutMap["about"]  = about
                firestore!!.collection("Users").document(firebaseAuth!!.currentUser!!.uid)
                    .update(aboutMap)
                    .addOnCompleteListener{ task ->
                        task.addOnSuccessListener {
                            Toast.makeText(context, "Your status updated success", Toast.LENGTH_SHORT).show()
                            findNavController().navigateUp()
                        }
                        task.addOnFailureListener {
                            Toast.makeText(context, ""+it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    private fun onCancelClick() {
        binding.txtCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }


}