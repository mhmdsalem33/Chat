package mhmd.salem.chatkotlin.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import mhmd.salem.chatkotlin.Activites.UsersActivity
import mhmd.salem.chatkotlin.R
import mhmd.salem.chatkotlin.databinding.FragmentSignupBinding
import java.util.*
import kotlin.collections.HashMap


class SignupFragment : Fragment() {


    private lateinit var binding              : FragmentSignupBinding
    private lateinit var userName             : TextInputEditText
    private lateinit var userEmail            : TextInputEditText
    private lateinit var userPhone            : TextInputEditText
    private lateinit var userPassword         : TextInputEditText
    private lateinit var userConfirmPassword  : TextInputEditText
    private var firebaseAuth : FirebaseAuth      ? = null
    private var firestore    : FirebaseFirestore ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            firebaseAuth = FirebaseAuth.getInstance()
            firestore = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignupBinding.inflate(inflater , container , false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        userName            = binding.root.findViewById(R.id.edt_user_name)
        userEmail           = binding.root.findViewById(R.id.edt_email_regs)
        userPhone           = binding.root.findViewById(R.id.edt_phone)
        userPassword        = binding.root.findViewById(R.id.edt_password_regs)
        userConfirmPassword = binding.root.findViewById(R.id.edt_confirm_password)

        binding.btnSignup.setOnClickListener {

            val name               = userName.text.toString()
            val email              = userEmail.text.toString()
            val phone              = userPhone.text.toString()
            val password           = userPassword.text.toString()
            val confirmPassword    = userConfirmPassword.text.toString()

            if (name.isEmpty())
            {
                userName.error = "Name required to create account"
            }
            else if (email.isEmpty())
            {
                userEmail.error = "Email required to create account"
            }
            else if (phone.isEmpty())
            {
                userPhone.error = "Phone required to create account"
            }
            else if (password.isEmpty())
            {
                userPassword.error = "Password required to create account"
            }
            else if (confirmPassword.isEmpty())
            {
                userConfirmPassword.error = "Confirm Password required to create account"
            }
            else if (password.length  < 8)
            {
                userPassword.error        = " Password must be greater than 7"
            }
            else if (confirmPassword.length  < 8)
            {
                userConfirmPassword.error        = "confirm Password must be greater than 7"
            }
            else if (password != confirmPassword)
            {
                userPassword.error        = "Both Password Not Matched"
                userConfirmPassword.error = "Both Password Not Matched"
            }
            else
            {
                binding.progressSign.visibility = View.VISIBLE
               createAccount(name , email , phone , password)
            }


        }


    }


    private fun createAccount(name: String, email: String, phone: String, password: String) {

        firebaseAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener{ task ->
            task.addOnSuccessListener {

                val userMap = HashMap<String , Any>()
                    userMap["userName"]      = name
                    userMap["lowerName"]     = name.lowercase(Locale.getDefault())
                    userMap["userEmail"]     = email
                    userMap["userPhone"]     = phone
                    userMap["userPassword"]  = password
                    userMap["userId"]        = firebaseAuth!!.currentUser!!.uid

                firestore!!.collection("Users").document(firebaseAuth!!.currentUser!!.uid)
                    .set(userMap)
                    .addOnCompleteListener{
                        it.addOnSuccessListener {
                            Toast.makeText(context, "Congratulation!!", Toast.LENGTH_SHORT).show()
                            binding.progressSign.visibility = View.GONE
                            val intent = Intent(context , UsersActivity::class.java)
                                startActivity(intent)
                                requireActivity().onBackPressed()


                        }
                        it.addOnFailureListener { error ->
                            Toast.makeText(context, ""+error.message, Toast.LENGTH_SHORT).show()
                            binding.progressSign.visibility = View.GONE }
                    }


            }
            task.addOnFailureListener { Toast.makeText(context, ""+it.message, Toast.LENGTH_SHORT).show() }
        }

    }

}