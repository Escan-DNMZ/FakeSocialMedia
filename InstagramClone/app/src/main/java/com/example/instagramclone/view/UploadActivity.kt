package com.example.instagramclone.view

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.instagramclone.R
import com.example.instagramclone.databinding.ActivityUploadBinding

import com.google.android.material.snackbar.Snackbar


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

import java.sql.Timestamp
import java.time.Instant


import java.util.*


class UploadActivity : AppCompatActivity() {

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture: Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private lateinit var binding: ActivityUploadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        registerLauncher()

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
    }





    fun imageUpload(view: View){
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                    //request permission
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }.show()
            }
            else{
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        else{
            val intenToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intenToGallery)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun uploadClick(view:View){

        //universal uniq id
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"

        val reference = storage.reference
        val imageReference = reference.child("images").child(imageName)


        if (selectedPicture != null){
            imageReference.putFile(selectedPicture!!).addOnSuccessListener {
                val uploadPictureReference = storage.reference.child("images").child(imageName)
                    uploadPictureReference.downloadUrl.addOnSuccessListener {
                        val downloadUrl = it.toString()

                        val postMap = hashMapOf<String,Any>()
                        postMap.put("downloadUrl",downloadUrl)
                        postMap.put("userMail",auth.currentUser!!.email!!)
                        postMap.put("comment",binding.commentText.text.toString())
                        postMap.put("date",Timestamp.from(Instant.now()))

                        firestore.collection("Posts").add(postMap).addOnSuccessListener {
                            finish()
                        }.addOnFailureListener{
                            Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
                        }


                }
            }.addOnFailureListener{
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }

    }



    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == RESULT_OK){
                val intentFromResult = result.data

                selectedPicture = intentFromResult!!.data
                selectedPicture.let {
                    binding.imageView.setImageURI(it)
                }

            }

        }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){result ->
            if (result){
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
            else{
                Toast.makeText(this@UploadActivity,"Permission Needed",Toast.LENGTH_LONG).show()
            }
        }
    }
}