package com.example.opticloudtheempire.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val data: UserDto?
)

data class UserDto(
    val id: String,
    val name: String,
    val email: String
)