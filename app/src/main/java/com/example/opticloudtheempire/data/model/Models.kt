package com.example.opticloudtheempire.data.model

data class InventoryAlert(
    val id: String,
    val itemName: String,
    val remainingQuantity: Int
)

data class Patient(
    val _id: String,
    val name: String,
    val email: String,
    val phone: String,
    val address: String?
)

data class Exam(
    val _id: String,
    val patientId: String,
    val od: EyeGraduation,
    val os: EyeGraduation,
    val notes: String?
)

data class EyeGraduation(
    val sphere: Double,
    val cylinder: Double,
    val axis: Double
)