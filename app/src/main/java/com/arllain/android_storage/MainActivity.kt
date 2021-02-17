package com.arllain.android_storage

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.arllain.android_storage.databinding.ActivityMainBinding

const val REQUEST_PERMISSION_CODE = 1

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUi()
        requestPermission()
    }

    private fun initUi() {
        binding.btnRead.setOnClickListener{
            loadImages()
        }

        binding.btnWrite.setOnClickListener{
            writeImage()
        }

    }

    private fun writeImage() {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "avatar")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MinhasImagens/")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val image_uri = contentResolver.insert(collection, contentValues)

        image_uri?.let {
            contentResolver.openOutputStream(image_uri).use { outputStream ->
                val bmp = BitmapFactory.decodeResource(resources, R.drawable.avatar)
                bmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            }

            contentValues.apply {
                clear()
                put(MediaStore.Images.Media.IS_PENDING, 0)
            }

            contentResolver.update(image_uri, contentValues, null, null)

        }

    }

    private fun loadImages() {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME)

        val selection = "${MediaStore.Images.Media.DISPLAY_NAME} like %?%"
        val selectionArgs = arrayOf(".jpg")
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

            var firstImageUri: Uri? = null

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val displayName = cursor.getString(displayNameColumn)

                if (firstImageUri == null){
                    firstImageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                }

                Log.d("TAG", "Media ID: $id - displayName: $displayName")
            }

            binding.imgMedia.setImageURI(firstImageUri)
        }
    }

    private fun requestPermission() {

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_PERMISSION_CODE,
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION_CODE &&
                grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }else {
            Toast.makeText(this, "Permission allowed", Toast.LENGTH_SHORT).show()

        }

    }
}