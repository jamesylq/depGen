@file:SuppressLint("StaticFieldLeak")

package com.example.depgen

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.depgen.Global.profileList
import com.example.depgen.ui.theme.DepGenTheme
import kotlinx.serialization.Serializable
import java.io.IOException

var active: Toast? = null
lateinit var ctxt : Context
lateinit var navController : NavHostController

@Serializable
object Global {
    var profile: Profile = LOGGED_OUT
    var profileList: ArrayList<Profile> = ArrayList()
    var eventList: ArrayList<Event> = ArrayList()
    var idx: Int = 0
}

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ctxt = LocalContext.current

            load()

            DepGenTheme {
                navController = rememberNavController()

                NavHost(navController = navController, startDestination = "Login") {
                    composable("Login") {
                        LoginPage()
                    }
                    composable("Master") {
                        MasterPage()
                    }
                    composable("Profile/{idx}/{nxt}") {
                        Log.d("DEBUG", it.arguments!!.getString("nxt")!!)
                        ProfilePage(
                            it.arguments!!.getString("idx")!!.toInt(),
                            it.arguments!!.getString("nxt")!!.toInt()
                        )
                    }
                    composable("Event/{idx}") {
                        EventPage(it.arguments!!.getString("idx")!!.toInt())
                    }
                    composable("MemberList") {
                        MemberListPage()
                    }
                    composable("EventList") {
                        EventListPage()
                    }
                    composable("SignUp") {
                        SignUpPage()
                    }
                    composable("NewEvent") {
                        NewEventPage()
                    }
                    composable("Settings") {
                        SettingsPage()
                    }
                }
            }
        }
    }
}

fun toast(msg: String) {
    active?.cancel()
    active = Toast.makeText(ctxt, msg, Toast.LENGTH_LONG)
    active!!.show()
}
