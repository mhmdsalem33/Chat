package mhmd.salem.chatkotlin.Models

data class UserModel (
    val userId    :String = "",
    var userName  :String = "",
    val userEmail :String = "",
    val userPhone :String = "",
    val status    :String = "offline",
    val about     :String = "",
    val typing    :String = "",
    val imageProfile :String = ""
        )
