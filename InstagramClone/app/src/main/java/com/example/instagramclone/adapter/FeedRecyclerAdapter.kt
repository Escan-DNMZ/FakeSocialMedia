package com.example.instagramclone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.databinding.RecycleRowBinding
import com.example.instagramclone.model.Post
import com.squareup.picasso.Picasso

class FeedRecyclerAdapter(val postList: ArrayList<Post>):RecyclerView.Adapter<FeedRecyclerAdapter.PostHolder>() {
    class PostHolder(val binding: RecycleRowBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = RecycleRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostHolder(binding)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.binding.textView.text = postList.get(position).usermail
        holder.binding.recyclerCommentText.text = postList.get(position).comment
        Picasso.get().load(postList.get(position).downloadUrl).into(holder.binding.recyclerImageView)
    }

    override fun getItemCount(): Int {
        return  postList.size
    }
}