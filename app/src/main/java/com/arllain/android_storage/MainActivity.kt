package com.arllain.android_storage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import com.arllain.android_storage.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnInternalStorage.setOnClickListener {
            createInternalFile("internal_file.txt", "Internal Content")
        }

        binding.btnExternalStorage.setOnClickListener {
            createExternalFile("external_file.txt", "External Content")
        }

    }

    private fun createInternalFile(fileName: String, fileContent: String) {
        //write file
        val file = File(this.filesDir, fileName)
        file.writeText(fileContent);
//        file.outputStream().use { outputStream ->
//            outputStream.write(fileContent.toByteArray())
//        }

        //read file
        val result = file.readText()
        Log.d("Internal File", result )

    }

    private fun createExternalFile(fileName: String, fileContent: String) {
        //write file
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        file.writeText(fileContent)

        //read file
        Log.d("External File", file.readText() )

    }

}