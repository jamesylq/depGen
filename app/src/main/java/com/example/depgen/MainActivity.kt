@file:SuppressLint("StaticFieldLeak")

package com.example.depgen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.depgen.model.Event
import com.example.depgen.model.EventRole
import com.example.depgen.model.LuxuryManager
import com.example.depgen.model.Profile
import com.example.depgen.model.Skill
import com.example.depgen.utils.isConnected
import com.example.depgen.utils.load
import com.example.depgen.utils.loadLuxuries
import com.example.depgen.utils.safeNavigate
import com.example.depgen.utils.switchProfile
import com.example.depgen.view.components.ConfirmationScreen
import com.example.depgen.view.fragments.AvailabilitiesPage
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
import com.example.depgen.view.fragments.SchedulePage
import com.example.depgen.view.fragments.SettingsPage
import com.example.depgen.view.fragments.SignUpPage
import com.example.depgen.view.fragments.SkillPage
import com.example.depgen.view.fragments.SkillsTrackerPage
import com.example.depgen.view.theme.DepGenTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

var active: Toast? = null
var lastBack: Long = 0
val luxuryProfiles = HashMap<String, LocalDateTime>()

lateinit var ctxt : Context
lateinit var auth: FirebaseAuth
lateinit var luxuryManager: LuxuryManager
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

    fun sortEventList() {
        eventList.apply {
            sortBy { it.getLatest() }
            sortBy { it.hasCompleted() }
        }
    }
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        enableEdgeToEdge()

        setContent {
            ctxt = LocalContext.current
            auth = Firebase.auth

            val permissions = buildList {
                add(Manifest.permission.CAMERA)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    add(Manifest.permission.READ_MEDIA_IMAGES)
                } else {
                    add(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }.toTypedArray()

            var permissionsRequested by remember { mutableStateOf(false) }
            var connected by remember { mutableStateOf(true) }

            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions(),
                onResult = {}
            )

            LaunchedEffect(Unit) {
                if (!permissionsRequested) {
                    permissionLauncher.launch(permissions)
                    permissionsRequested = true
                }
                val dbRef = FirebaseDatabase.getInstance(FIREBASE_URL).reference

                dbRef.child("lastUpdate").addValueEventListener(
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            load()
                            loadLuxuries()
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    }
                )

                dbRef.child("luxuryProfiles").addValueEventListener(
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            load()
                            loadLuxuries()
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    }
                )
            }

            LaunchedEffect (Unit) {
                while (true) {
                    connected = isConnected()
                    delay(100)
                }
            }

            DepGenTheme {
                navController = rememberNavController()

                if (!connected) {
                    BasicAlertDialog(
                        onDismissRequest = {
                            connected = isConnected()
                        }
                    ) {
                        ElevatedCard (
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(500.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onBackground
                            )
                        ) {
                            Column (
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(modifier = Modifier.weight(0.2f))
                                Image(
                                    painter = painterResource(R.drawable.icon_512),
                                    contentDescription = "",
                                    modifier = Modifier.clip(CircleShape)
                                )
                                Spacer(Modifier.weight(0.2f))
                                Text(
                                    text = "No Internet Connection!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 21.sp
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    text = "depGen requires an active internet connection. Check back later!",
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

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
                    composable("Availabilities/{idx}") {
                        AvailabilitiesPage(it.arguments!!.getString("idx")!!.toInt())
                    }
                    composable("Schedule/{idx}") {
                        SchedulePage(it.arguments!!.getString("idx")!!.toInt())
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
                    composable("NewRole/{idx}") {
                        NewRolePage(it.arguments!!.getString("idx")!!.toInt())
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
