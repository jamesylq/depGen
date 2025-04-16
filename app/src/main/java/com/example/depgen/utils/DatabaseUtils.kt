package com.example.depgen.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
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


fun saveLocally() {
    Log.d("LocalFileIO", "Initiated Save ${Global.profileList}")
    val saveFile = File(ctxt.filesDir, "save.json")
    val wrapper = Wrapper(Global.profileList, Global.eventList, Global.skillsList, Global.rolesList)
    saveFile.writeText(json.encodeToString(wrapper))

    val timeFile = File(ctxt.filesDir, "lastUpdate.json")
    timeFile.writeText(json.encodeToString(LocalDateTime.now().toString()))
}


fun saveLuxuries(profile: Profile? = null, writeToDB: Boolean = true) {
    Log.d("LocalFileIO", "Initiated Luxury Save.")
    val saveFile = File(ctxt.filesDir, "luxuries.json")
    saveFile.writeText(json.encodeToString(luxuryManager))

    if (profile != null) {
        if (writeToDB) {
            val now = LocalDateTime.now()
            luxuryProfiles[profile.username] = now
            val dbRef = FirebaseDatabase.getInstance(FIREBASE_URL).reference

            dbRef
                .child("profilePicture")
                .child(profile.username).setValue(
                    luxuryManager.getLuxury(profile).profilePicture
                )
                .addOnFailureListener {
                    Log.wtf("FirebaseIO", "Saving Failed: ", it)
                }
                .addOnCompleteListener {
                    dbRef
                        .child("luxuryProfiles")
                        .child(profile.username)
                        .child("lastUpdate")
                        .setValue(now.toString())
                }
        }
    }
}


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

    dbRef.child("luxuryProfiles")
        .get()
        .addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                for (userSnapshot in snapshot.children) {
                    val username = userSnapshot.key
                    val lastUpdateStr = userSnapshot.child("lastUpdate").getValue(String::class.java)

                    if (username != null && lastUpdateStr != null) {
                        try {
                            val dateTime = LocalDateTime.parse(lastUpdateStr)
                            luxuryProfiles[username] = dateTime
                        } catch (_: Exception) { }
                    }
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
                    Global.sortEventList()
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
                            Global.sortEventList()
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

fun deletePFP(profile: Profile) {
    luxuryManager.getLuxury(profile).updateProfilePicture("")
    saveLuxuries(profile)
}

fun isConnected(context: Context = ctxt): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}

//fun addUser(newProfile: Profile, password: String) {
//    val dbRef = FirebaseDatabase.getInstance(FIREBASE_URL).reference
//    dbRef
//        .child("emails")
//        .child(newProfile.username)
//        .setValue(newProfile.email)
//        .addOnCompleteListener {
//            auth.createUserWithEmailAndPassword(newProfile.email, password)
//                .addOnCompleteListener {
//                    Global.profileList.add(newProfile)
//                    save()
//                    toast("Signed up account ${newProfile.username}!")
//                    safeNavigate("Login")
//                }
//        }
//}
//
//fun validate(profile: Profile, password: String) {
//    val dbRef = FirebaseDatabase.getInstance(FIREBASE_URL).reference
//
//    dbRef
//        .child("emails")
//        .child(profile.username)
//        .get()
//        .addOnSuccessListener {
//            if (it.exists()) {
//                val email = it.getValue(String::class.java)!!
//                auth.signInWithEmailAndPassword(email, password)
//                    .addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            switchProfile(profile)
//                            safeNavigate("Master")
//                        } else {
//                            throw PasswordValidationException("")
//                        }
//                    }
//
//            }
//        }
//
//}
