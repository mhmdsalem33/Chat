package mhmd.salem.chatkotlin.NetWork

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class ChatNetWork  :Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}