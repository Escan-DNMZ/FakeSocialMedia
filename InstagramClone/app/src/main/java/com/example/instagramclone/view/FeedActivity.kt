package com.example.instagramclone.view


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instagramclone.R
import com.example.instagramclone.adapter.FeedRecyclerAdapter
import com.example.instagramclone.databinding.ActivityFeedBinding
import com.example.instagramclone.databinding.ActivityUploadBinding
import com.example.instagramclone.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class FeedActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var postArrayList:ArrayList<Post>
    private lateinit var postAdapter: FeedRecyclerAdapter

    private lateinit var binding: ActivityFeedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        db = Firebase.firestore
        postArrayList = ArrayList<Post>()

        getData()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        postAdapter = FeedRecyclerAdapter(postArrayList)
        binding.recyclerView.adapter = postAdapter
    }

    private fun getData(){
        db.collection("Posts").addSnapshotListener{ value, error ->

            if(error != null){
                Toast.makeText(this,error.localizedMessage,Toast.LENGTH_LONG).show()
            }else{
                if (value != null){
                    if (!value.isEmpty){
                        val documents = value.documents
                        postArrayList.clear()
                        for (document in documents){
                            val comment = document.get("comment") as String //stringe çevir
                            val userMail = document.get("userMail") as String
                            val downloadUrl = document.get("downloadUrl") as String


                            val post = Post(userMail,comment,downloadUrl)
                            postArrayList.add(post)
                        }
                        postAdapter.notifyDataSetChanged()
                    }
                }
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.feed_menu,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.addPost){
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }
        if(item.itemId == R.id.signOut){
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

}