@file:SuppressLint("StaticFieldLeak")

package com.example.depgen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.depgen.model.Event
import com.example.depgen.model.EventRole
import com.example.depgen.model.Profile
import com.example.depgen.model.Skill
import com.example.depgen.utils.load
import com.example.depgen.utils.safeNavigate
import com.example.depgen.utils.switchProfile
import com.example.depgen.view.components.ConfirmationScreen
import com.example.depgen.view.fragments.EventListPage
import com.example.depgen.view.fragments.EventPage
import com.example.depgen.view.fragments.LoginPage
import com.example.depgen.view.fragments.MasterPage
import com.example.depgen.view.fragments.MemberListPage
import com.example.depgen.view.fragments.NewEventPage
import com.example.depgen.view.fragments.NewRolePage
import com.example.depgen.view.fragments.NewSkillPage
import com.example.depgen.view.fragments.OneTimeDeploymentPage
import com.example.depgen.view.fragments.ProfilePage
import com.example.depgen.view.fragments.RepeatingDeploymentPage
import com.example.depgen.view.fragments.RolesListPage
import com.example.depgen.view.fragments.SettingsPage
import com.example.depgen.view.fragments.SignUpPage
import com.example.depgen.view.fragments.SkillPage
import com.example.depgen.view.fragments.SkillsTrackerPage
import com.example.depgen.view.theme.DepGenTheme
import kotlinx.serialization.Serializable

var active: Toast? = null
var lastBack: Long = 0
lateinit var ctxt : Context
lateinit var navController : NavHostController

@Serializable
object Global {
    var profile: Profile = LOGGED_OUT
    var profileList: ArrayList<Profile> = ArrayList()
    var eventList: ArrayList<Event> = ArrayList()
    var skillsList: ArrayList<Skill> = ArrayList()
    var rolesList: ArrayList<EventRole> = ArrayList()
    var idx: Int = 0

    fun isAdmin() : Boolean {
        return this.profile == ADMIN
    }

    fun getSkillsString(): ArrayList<String> {
        val ans = ArrayList<String>()
        for (skill in skillsList) ans.add(skill.skill)
        return ans
    }

    fun getSkillFromString(string: String) : Skill? {
        for (skill in skillsList) {
            if (skill.skill == string) {
                return skill
            }
        }
        return null
    }
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
                        BackHandler(enabled = true) {
                            if (System.currentTimeMillis() - lastBack < DELTA) {
                                val intent = Intent(Intent.ACTION_MAIN)
                                intent.addCategory(Intent.CATEGORY_HOME)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                ctxt.startActivity(intent)

                                if (active != null) {
                                    active!!.cancel()
                                    active = null
                                }

                            } else {
                                lastBack = System.currentTimeMillis()
                                toast("Press back again to exit!")
                            }
                        }
                    }
                    composable("Master") {
                        var confirming by remember { mutableStateOf(false) }
                        MasterPage()
                        if (confirming) {
                            ConfirmationScreen(
                                onConfirm = {
                                    switchProfile(LOGGED_OUT)
                                    safeNavigate("Login")
                                    confirming = false
                                },
                                onDecline = {
                                    confirming = false
                                },
                                body = "You will be logged out of\nyour account!"
                            )
                        }
                        BackHandler(enabled = true) {
                            confirming = true
                        }
                    }
                    composable("Profile/{idx}/{nxt}") {
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
                    composable("OneTimeDeployment") {
                        OneTimeDeploymentPage()
                    }
                    composable("RepeatingDeployment") {
                        RepeatingDeploymentPage()
                    }
                    composable("SkillsTracker") {
                        SkillsTrackerPage()
                    }
                    composable("NewSkill") {
                        NewSkillPage()
                    }
                    composable("Skill/{idx}") {
                        SkillPage(it.arguments!!.getString("idx")!!.toInt())
                    }
                    composable("RolesList") {
                        RolesListPage()
                    }
                    composable("NewRole") {
                        NewRolePage()
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
