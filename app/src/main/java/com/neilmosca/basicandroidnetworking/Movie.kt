package com.neilmosca.basicandroidnetworking

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    @SerialName("_id")
    val id: String = "",
    val title: String,
    val genre: String,
    val year: Int
)