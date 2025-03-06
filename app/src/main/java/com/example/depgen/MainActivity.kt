@file:SuppressLint("StaticFieldLeak")

package com.example.depgen

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
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

var active: Toast? = null
lateinit var ctxt : Context
lateinit var navController : NavHostController

@Serializable
object Global {
    var profile: Profile = LOGGED_OUT
    val profileList: ArrayList<Profile> = ArrayList()
    val eventList: ArrayList<Event> = ArrayList()
    var idx: Int = 0
}

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        profileList.add(LOGGED_OUT)
        profileList.add(ADMIN)

        setContent {
            ctxt = LocalContext.current

            DepGenTheme {
                navController = rememberNavController()

                NavHost(navController = navController, startDestination = "Login") {
                    composable("Login") {
                        LoginPage()
                    }
                    composable("Master") {
                        MasterPage()
                    }
                    composable("Profile/{idx}") {
                        ProfilePage(it.arguments!!.getString("idx")!!.toInt())
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
