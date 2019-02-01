package com.example.renai.instagram.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

var auth: FirebaseAuth = FirebaseAuth.getInstance()
var database: DatabaseReference = FirebaseDatabase.getInstance().reference
var storage: StorageReference = FirebaseStorage.getInstance().reference

fun currentUid():String? = auth.currentUser?.uid
fun currentUserReference(): DatabaseReference = database.child("users").child(currentUid()!!)
