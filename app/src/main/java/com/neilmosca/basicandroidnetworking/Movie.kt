package com.neilmosca.basicandroidnetworking

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    @SerialName("_id") val id: String = "",
    @SerialName("title") val title: String,
    @SerialName("genre") val genre: String,
    @SerialName("year") val year: Int
)