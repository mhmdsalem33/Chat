package mhmd.salem.chatkotlin.Fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import mhmd.salem.chatkotlin.Activites.UsersActivity
import mhmd.salem.chatkotlin.R
import mhmd.salem.chatkotlin.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {


    private lateinit var binding      :FragmentLoginBinding
    private lateinit var userEmail    :TextInputEditText
    private lateinit var userPassword :TextInputEditText
    private var firebaseAuth          :FirebaseAuth ? = null
    private var firestore             :FirebaseFirestore ? = null
    private lateinit var googleSignInClient : GoogleSignInClient
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
        binding = FragmentLoginBinding.inflate(inflater , container , false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userEmail     = binding.root.findViewById(R.id.edt_email_log)
        userPassword  = binding.root.findViewById(R.id.edt_password_log)


        signInWithNormalButton()
        signInWithGoogleBottom()


    }


    private fun signInWithNormalButton() {
        binding.btnLogin.setOnClickListener {
            val email     =  userEmail.text.toString()
            val password  =  userPassword.text.toString()
            if (email.isEmpty())
            {
                userEmail.error = "Email is required to login"
            }
            else if (password.isEmpty())
            {
                userPassword.error = "Password is required to login"
            }
            else
            {
                binding.progress.visibility = View.VISIBLE
                login(email , password)
            }
        }
    }

    private fun login(email: String, password: String) {

        firebaseAuth!!.signInWithEmailAndPassword(email , password)
            .addOnCompleteListener{ task ->
                task.addOnSuccessListener {
                    binding.progress.visibility = View.GONE
                    val intent = Intent(context , UsersActivity::class.java)
                    startActivity(intent)
                    requireActivity().onBackPressed()
                }
                task.addOnFailureListener {
                    Toast.makeText(context, ""+it.message, Toast.LENGTH_SHORT).show()
                    binding.progress.visibility = View.GONE}
            }
    }



    //Start Login with Google

    private fun signInWithGoogleBottom() {

        val  gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext() , gso)

        binding.btnLoginWithGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)

        }
    }
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if(result.resultCode == Activity.RESULT_OK)
        {
            val data = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResults(task)
        }
    }


    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful)
        {
            val account  : GoogleSignInAccount? = task.result
            if (account != null)
            {
                updateUI(account)
            }
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {

        val credential = GoogleAuthProvider.getCredential(account.idToken , null)
        firebaseAuth!!.signInWithCredential(credential).addOnCompleteListener{ it ->

            it.addOnSuccessListener {
                val intent = Intent(context , UsersActivity::class.java)
                //   intent.putExtra("email" , account.email)
                //   intent.putExtra("name"  , account.displayName)
                startActivity(intent)
                requireActivity().onBackPressed()

                val user = HashMap<String , Any>()
                    user["userEmail"]     = account.email.toString()
                    user["userName"]      = account.displayName.toString()
                    user["userId"]        = firebaseAuth!!.currentUser!!.uid

                firestore!!.collection("Users").document(firebaseAuth!!.currentUser!!.uid)
                    .set(user)
                Toast.makeText(context, "Login Success", Toast.LENGTH_SHORT).show()
            }
            it.addOnFailureListener {
                Toast.makeText(context, ""+it.message, Toast.LENGTH_SHORT).show()
            }

        }

    }




    //End Login with Google


}