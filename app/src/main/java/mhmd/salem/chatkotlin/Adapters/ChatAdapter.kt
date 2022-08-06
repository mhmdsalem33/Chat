package mhmd.salem.chatkotlin.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.makeramen.roundedimageview.RoundedImageView
import mhmd.salem.chatkotlin.Models.ChatModel
import mhmd.salem.chatkotlin.R
import org.w3c.dom.Text


class ChatAdapter():RecyclerView.Adapter<ChatAdapter.ViewHolder>() {


    private val messageTypeLeft  = 0
    private val messageTypeRight = 1
    private var firebaseAuth     : FirebaseAuth ? = null

    private val diffUtilCallback = object  :DiffUtil.ItemCallback<ChatModel>(){
        override fun areItemsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
            return  oldItem.senderId == newItem.senderId
        }

        override fun areContentsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
            return  oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this ,diffUtilCallback)


    override fun getItemViewType(position: Int): Int {
        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth!!.uid == differ.currentList[position].senderId)
        {
            return messageTypeRight
        }
        else{
            return messageTypeLeft
        }
    }
    class ViewHolder(view :View):RecyclerView.ViewHolder(view) {

        val message = view.findViewById<TextView>(R.id.tv_message)
        val img     = view.findViewById<RoundedImageView>(R.id.img_send)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == messageTypeRight)
        {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.right_row , parent , false))
        }
        else
        {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.left_row , parent, false))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = differ.currentList[position]
            holder.message.text = data.message


            if (data.img.isNotEmpty())
            {
                Glide.with(holder.itemView)
                    .load(data.img)
                    .into(holder.img)
                holder.message.visibility = View.GONE
            }
        else
            {
                holder.img.visibility = View.GONE
            }

    }

    override fun getItemCount() = differ.currentList.size







}