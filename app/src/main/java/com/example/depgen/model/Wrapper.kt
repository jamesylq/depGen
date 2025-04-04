package com.example.depgen.model

import kotlinx.serialization.Serializable


@Serializable
data class Wrapper(
    var profiles: ArrayList<Profile>,
    var events: ArrayList<Event>,
    var skills: ArrayList<Skill>
)
