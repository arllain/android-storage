package com.arllain.android_storage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import com.arllain.android_storage.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream
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

        if (binding.cbJeckPackSecurity.isChecked) {
           createSafeFile(file, binding.edtFileContent.text.toString())
        }else {
            createFile(file, binding.edtFileContent.text.toString() )
        }

        updateList()
    }

    private fun updateList(){
        adapter.submitList(getDirFromRadioGroup()?.listFiles()?.toList())
    }

    private fun getDirFromRadioGroup() = when (binding.rgStorageType.checkedRadioButtonId) {
            binding.rbInternal.id -> filesDir
            else -> getExternalFilesDir(null)
    }

    private fun createFile(file: File, fileContent: String) {
        file.writeText(fileContent)
    }

    private fun createSafeFile(file: File, fileContent: String) {
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        if (file.exists()) {
            file.delete()
        }

        val encryptedFile = EncryptedFile.Builder(
            file,
            applicationContext,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        encryptedFile.openFileOutput().use { writer ->
            writer.write(fileContent.toByteArray())
        }

        Log.d("Read Safa File", readSafeFile(file))
    }

    private fun readSafeFile(file: File): String {
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        var result = ""
        if (file.exists()) {
            val encryptedFile = EncryptedFile.Builder(
                file,
                applicationContext,
                masterKeyAlias,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            encryptedFile.openFileInput().use { inputStream ->
                result = inputStream.readBytes().decodeToString()
            }
        }
        return result
    }

}