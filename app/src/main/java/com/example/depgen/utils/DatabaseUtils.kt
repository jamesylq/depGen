package com.example.depgen.utils

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.depgen.ADMIN
import com.example.depgen.FIREBASE_URL
import com.example.depgen.Global
import com.example.depgen.LOGGED_OUT
import com.example.depgen.ctxt
import com.example.depgen.json
import com.example.depgen.luxuryManager
import com.example.depgen.luxuryProfiles
import com.example.depgen.model.LuxuryManager
import com.example.depgen.model.Profile
import com.example.depgen.model.Wrapper
import com.google.firebase.database.FirebaseDatabase
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeParseException


@RequiresApi(Build.VERSION_CODES.O)
fun save() {
    val dbRef = FirebaseDatabase.getInstance(FIREBASE_URL).reference

    dbRef.child("lastUpdate").setValue(LocalDateTime.now().toString())

    val wrapper = Wrapper(Global.profileList, Global.eventList, Global.skillsList, Global.rolesList)
    dbRef.child("save").setValue(json.encodeToString(wrapper))
        .addOnSuccessListener {
            Log.d("FirebaseIO", "Save File $wrapper saved to Firebase!")
        }
        .addOnFailureListener {
            Log.wtf("FirebaseIO", "Saving Failed: ", it)
        }

    saveLocally()
}

fun clear() {
    val dbRef = FirebaseDatabase.getInstance(FIREBASE_URL).reference

    dbRef.child("save").setValue("")
        .addOnSuccessListener {
            Log.d("FirebaseIO", "Firebase Save File cleared!")
        }
        .addOnFailureListener {
            Log.wtf("FirebaseIO", "Clearing Failed: ", it)
        }
}

@RequiresApi(Build.VERSION_CODES.O)
fun saveLocally() {
    Log.d("LocalFileIO", "Initiated Save ${Global.profileList}")
    val saveFile = File(ctxt.filesDir, "save.json")
    val wrapper = Wrapper(Global.profileList, Global.eventList, Global.skillsList, Global.rolesList)
    saveFile.writeText(json.encodeToString(wrapper))

    val timeFile = File(ctxt.filesDir, "lastUpdate.json")
    timeFile.writeText(json.encodeToString(LocalDateTime.now().toString()))
}

@RequiresApi(Build.VERSION_CODES.O)
fun saveLuxuries(profile: Profile? = null, isNew: Boolean = false) {
    Log.d("LocalFileIO", "Initiated Luxury Save.")
    val saveFile = File(ctxt.filesDir, "luxuries.json")
    saveFile.writeText(json.encodeToString(luxuryManager))

    if (profile != null) {
        val dbRef = FirebaseDatabase.getInstance(FIREBASE_URL).reference

        if (isNew) {
            val now = LocalDateTime.now()
            val ref = dbRef.child("luxuryProfiles").push()
            ref.child("username").setValue(profile.username)
            ref.child("lastUpdate").setValue(now.toString())
            luxuryProfiles[profile.username] = now
        }

        dbRef.child("profilePicture").child(profile.username).setValue(
            luxuryManager.getLuxury(profile).profilePicture!!
        )
            .addOnFailureListener {
                Log.wtf("FirebaseIO", "Saving Failed: ", it)
            }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun loadLuxuries() {
    val dbRef = FirebaseDatabase.getInstance(FIREBASE_URL).reference

    try {
        val timeFile = File(ctxt.filesDir, "luxuries.json")
        if (!timeFile.exists()) throw RuntimeException()
        luxuryManager = json.decodeFromString(timeFile.readText())!!

    } catch (e: DateTimeParseException) {
        Log.w("depGen", "Warning (Local Luxury Save Not Found): ", e)
    } catch (e: RuntimeException) {
        Log.w("depGen", "Warning (Local Luxury Save Not Found): ", e)
    }

    dbRef.child("luxuryProfiles").get()
        .addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                luxuryProfiles.clear()
                snapshot.children.forEach {
                    luxuryProfiles[it.child("username").getValue(String::class.java)!!] =
                        LocalDateTime.parse(it.child("lastUpdate").getValue(String::class.java)!!)
                }
            }
        }
        .addOnCompleteListener {
            for (profile in Global.profileList) {
                if (luxuryProfiles.contains(profile.username)) {
                    val profileLuxury = luxuryManager.getLuxury(profile)

                    if (luxuryProfiles[profile.username]!!.isAfter(profileLuxury.getLastUpdate())) {
                        Log.d("depGen", "Using Database Data, Local Luxury Save Outdated for Profile ${profile.username}")
                        dbRef.child("profilePicture").child(profile.username).get()
                            .addOnSuccessListener {
                                profileLuxury.updateProfilePicture(
                                    it.getValue(String::class.java)!!
                                )
                            }
                            .addOnCompleteListener{
                                saveLuxuries()
                            }
                    } else {
                        Log.d("depGen", "Using Local Luxury Save for Profile ${profile.username}")
                    }
                }
            }
        }
}

@RequiresApi(Build.VERSION_CODES.O)
fun load() {
    val dbRef = FirebaseDatabase.getInstance(FIREBASE_URL).reference

    var lastLocalUpdate = NO_DATE_OBJ
    var lastDBUpdate = NO_DATE_OBJ.plusDays(1L)

    try {
        val timeFile = File(ctxt.filesDir, "lastUpdate.json")
        if (!timeFile.exists()) throw RuntimeException()
        lastLocalUpdate = LocalDateTime.parse(json.decodeFromString<String>(timeFile.readText()))

    } catch (e: DateTimeParseException) {
        Log.w("depGen", "Warning (Local Save Not Found): ", e)
    } catch (e: RuntimeException) {
        Log.w("depGen", "Warning (Local Save Not Found): ", e)
    }

    dbRef.child("lastUpdate").get()
        .addOnSuccessListener {
            try {
                lastDBUpdate = LocalDateTime.parse(it.getValue(String::class.java)!!)
                Log.d("FirebaseIO", "Last Database Update: $lastDBUpdate")
            } catch (e: NullPointerException) {
                Log.w("depGen", "Warning (Database Save Time Not Found): ", e)
            }
        }
        .addOnCompleteListener {
            var retrieved = false
            if (!lastLocalUpdate.isBefore(lastDBUpdate)) {
                try {
                    val file = File(ctxt.filesDir, "save.json")
                    if (!file.exists()) {
                        file.createNewFile()
                        throw RuntimeException()
                    }

                    val wrapper: Wrapper = json.decodeFromString(file.readText())

                    Global.eventList.clear()
                    Global.profileList.clear()
                    Global.skillsList.clear()
                    Global.rolesList.clear()
                    Global.eventList.addAll(wrapper.events)
                    Global.profileList.addAll(wrapper.profiles)
                    Global.skillsList.addAll(wrapper.skills)
                    Global.rolesList.addAll(wrapper.roles)
                    LOGGED_OUT = Global.profileList[0]
                    ADMIN = Global.profileList[1]

                    for (i in 1 ..< Global.profileList.size) {
                        for (skill in Global.skillsList) {
                            if (!Global.profileList[i].skills.containsKey(skill)) {
                                Global.profileList[i].skills[skill] = skill.defaultLevel
                            }
                        }
                    }

                    retrieved = true

                } catch (e: RuntimeException) {
                    Log.wtf("depGen", "Critical Error With Local Save File, Falling Back on Firebase: ", e)

                } finally {
                    luxuryManager = LuxuryManager()
                    loadLuxuries()
                }
            }

            if (!retrieved) {
                Log.d("FirebaseIO", "Local Save File outdated, retrieving Data from Firebase.")
                dbRef.child("save").get()
                    .addOnSuccessListener {
                        try {
                            val data = it.getValue(String::class.java)!!
                            val wrapper: Wrapper = json.decodeFromString(data)

                            Global.eventList.clear()
                            Global.profileList.clear()
                            Global.skillsList.clear()
                            Global.rolesList.clear()
                            Global.eventList.addAll(wrapper.events)
                            Global.profileList.addAll(wrapper.profiles)
                            Global.skillsList.addAll(wrapper.skills)
                            Global.rolesList.addAll(wrapper.roles)
                            LOGGED_OUT = Global.profileList[0]
                            ADMIN = Global.profileList[1]

                            for (i in 1..<Global.profileList.size) {
                                for (skill in Global.skillsList) {
                                    if (!Global.profileList[i].skills.containsKey(skill)) {
                                        Global.profileList[i].skills[skill] = skill.defaultLevel
                                    }
                                }
                            }
                            saveLocally()

                        } catch (_: RuntimeException) {
                            Log.d("depGen", "Save file not found on database. Data has been reset.")
                            Global.profileList.clear()
                            Global.eventList.clear()
                            Global.skillsList.clear()
                            Global.rolesList.clear()
                            Global.profileList.add(LOGGED_OUT)
                            Global.profileList.add(ADMIN)

                        } finally {
                            luxuryManager = LuxuryManager()
                            loadLuxuries()
                        }
                    }
                    .addOnFailureListener {
                        Log.wtf("FirebaseIO", "Loading Failed: ", it)
                    }

            } else {
                Log.d("LocalFileIO", "Operating on Local Save File. (DB: $lastDBUpdate, Local: $lastLocalUpdate)")
            }
        }
}
