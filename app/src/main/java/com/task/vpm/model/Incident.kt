package com.task.vpm.model

data class Incident(
    val address: String,
    val description: String,
    val id: Int,
    val location_description: Any,
    val location_type: Any,
    val media: Media,
    val occurred_at: Int,
    val source: Source,
    val title: String,
    val type: String,
    val type_properties: Any,
    val updated_at: Int,
    val url: String
)