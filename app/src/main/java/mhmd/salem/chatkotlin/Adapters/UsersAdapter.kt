package mhmd.salem.chatkotlin.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mhmd.salem.chatkotlin.Models.UserModel
import mhmd.salem.chatkotlin.R
import mhmd.salem.chatkotlin.databinding.UsersRowBinding

class UsersAdapter():RecyclerView.Adapter<UsersAdapter.ViewHolder>() {


    private val diffUtilCallback = object  :DiffUtil.ItemCallback<UserModel>(){
        override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
                return  oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
          return  oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this , diffUtilCallback)

    lateinit var onUserItemClick : ((UserModel) -> Unit)

    class ViewHolder (val binding : UsersRowBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       return  ViewHolder(UsersRowBinding.inflate(LayoutInflater.from(parent.context) , parent , false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = differ.currentList[position]

        Glide.with(holder.itemView)
            .load(data.imageProfile)
            .error(R.drawable.person)
            .into(holder.binding.imgUser)

        holder.binding.txtUser.text = data.userName

        holder.itemView.setOnClickListener {
            onUserItemClick.invoke(data)
        }


    }

    override fun getItemCount() = differ.currentList.size

}