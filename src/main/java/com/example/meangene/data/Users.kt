package com.example.meangene.data

data class Users(
    val firstName:String,
    val lastName:String,
    val email:String,
    var imagepath:String = ""
){

    constructor(): this("","","","")
}
