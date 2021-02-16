package com.arllain.android_storage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arllain.android_storage.databinding.ItemFileBinding
import java.io.File

class FileAdapter(
    private val onDeleteClick: (File) -> Unit
) : ListAdapter<File, FileAdapter.MyViewHolder>(MyDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemFileBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)

        return MyViewHolder(binding);
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val file = getItem(position)
        holder.bind(file)
    }

    inner class MyViewHolder(private val binding: ItemFileBinding ): RecyclerView.ViewHolder(binding.root){
        fun bind(file: File) {
            binding.apply {
                tvFileName.text = file.name
                btnDeleteFile.setOnClickListener{
                    onDeleteClick(file)
                }
            }
        }
    }

    class MyDiff: DiffUtil.ItemCallback<File>() {
        override fun areItemsTheSame(oldItem: File, newItem: File) = oldItem == newItem
        override fun areContentsTheSame(oldItem: File, newItem: File) = oldItem.name == newItem.name
    }
}