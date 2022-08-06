package mhmd.salem.chatkotlin.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mhmd.salem.chatkotlin.Models.UserModel
import mhmd.salem.chatkotlin.R
import mhmd.salem.chatkotlin.databinding.UsersRowBinding

class SearchUserAdapter(var searchList :List<UserModel>):RecyclerView.Adapter<SearchUserAdapter.ViewHolder>() {



    lateinit var onItemClick :((UserModel) -> Unit)

    class ViewHolder(val binding : UsersRowBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      return  ViewHolder(UsersRowBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       Glide.with(holder.itemView)
           .load(searchList[position].imageProfile)
           .error(R.drawable.person)
           .into(holder.binding.imgUser)

        holder.binding.txtUser.text = searchList[position].userName


        holder.itemView.setOnClickListener {
                    onItemClick.invoke(searchList[position])
        }

    }

    override fun getItemCount() = searchList.size

}