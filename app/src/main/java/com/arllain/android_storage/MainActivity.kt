package com.arllain.android_storage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.arllain.android_storage.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val adapter by lazy {
        FileAdapter{file ->
            file.delete()
            updateList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUi()
        updateList()
    }

    private fun initUi() {
        binding.apply {
            btnCreateFile.setOnClickListener() {
                createFile()
            }

            rgStorageType.setOnCheckedChangeListener { radioGroup, i ->
                updateList()
            }

            rvFiles.adapter = adapter
            rvFiles.layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun createFile() {
        val file = File(getDirFromRadioGroup(), binding.edtFileName.text.toString())
        file.writeText(binding.edtFileContent.text.toString())
        updateList()
    }

    private fun updateList(){
        adapter.submitList(getDirFromRadioGroup()?.listFiles()?.toList())
    }

    private fun getDirFromRadioGroup() = when (binding.rgStorageType.checkedRadioButtonId) {
            binding.rbInternal.id -> filesDir
            else -> getExternalFilesDir(null)
    }

}