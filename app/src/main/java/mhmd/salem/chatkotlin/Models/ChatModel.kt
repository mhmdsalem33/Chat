package mhmd.salem.chatkotlin.Models

import androidx.room.Entity
import androidx.room.PrimaryKey


data class ChatModel (
        val senderId   : String  = "",
        val receiverId : String  = "",
        val message    : String  = "",
        val img        : String  = "")
