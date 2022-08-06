package mhmd.salem.chatkotlin.Fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import mhmd.salem.chatkotlin.Activites.ChatActivity
import mhmd.salem.chatkotlin.Adapters.SearchUserAdapter
import mhmd.salem.chatkotlin.Models.UserModel
import mhmd.salem.chatkotlin.R
import mhmd.salem.chatkotlin.databinding.FragmentSearchBinding
import java.util.*
import kotlin.collections.ArrayList


class SearchFragment : Fragment() {


    private lateinit var binding : FragmentSearchBinding

    private  var searchList      :List<UserModel> = ArrayList()
    private  val searchAdapter   = SearchUserAdapter(searchList)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onSearchBox()
        prepareRecView()
        onSearchClick()

    }

    private fun onSearchClick() {
        searchAdapter.onItemClick = { data ->
            val intent = Intent(context , ChatActivity::class.java)
            intent.putExtra("otherUserId"     , data.userId)
            intent.putExtra("otherUserName"   , data.userName)
            intent.putExtra("otherUserImage"  , data.imageProfile)
            startActivity(intent)

        }
    }

    private fun prepareRecView() {
        binding.searchRec.hasFixedSize()
        binding.searchRec.layoutManager =  LinearLayoutManager(context)
        binding.searchRec.adapter       = searchAdapter

    }
    private fun onSearchBox() {
        binding.edtSearch.addTextChangedListener(object  :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searchText  :String =  binding.edtSearch.text.toString()


                searchInFirestore(searchText.lowercase(Locale.getDefault()))
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    private fun searchInFirestore(searchText: String) {
        FirebaseFirestore.getInstance().collection("Users")
            .orderBy("lowerName")
            .startAt(searchText.lowercase(Locale.getDefault()))
            .endAt("$searchText\uf8ff")
            .limit(100)
            .get()
            .addOnCompleteListener {

                searchList  = it.result.toObjects(UserModel::class.java)
                searchAdapter.searchList = searchList
                searchAdapter.notifyDataSetChanged()

            }
            .addOnFailureListener{
                Toast.makeText(context, ""+it.message, Toast.LENGTH_SHORT).show()
            }

    }

}