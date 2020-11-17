package com.sample.camera

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import com.bumptech.glide.Glide
import com.sample.camera.databinding.ActivityMainBinding
import java.io.File

private const val REQUEST_CODE_FOR_IMAGE_CAPTURE = 100
private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var photoFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.camera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(packageManager) != null) {
                //val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES) // photos will be deleted when the app is uninstalled
                val dir = externalCacheDir // photo can be deleted when the app is terminated
                val file = File.createTempFile("photo_", ".jpg", dir)
                val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                startActivityForResult(intent, REQUEST_CODE_FOR_IMAGE_CAPTURE)
                photoFile = file
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_FOR_IMAGE_CAPTURE -> {
                if (resultCode == RESULT_OK) {
//                    BitmapFactory.decodeFile(photoFile.absolutePath)?.let {
//                        binding.image.setImageBitmap(it)
//                    }
                    Glide.with(this).load(photoFile).into(binding.image) // call .centerCrop() .circleCrop() before .into()
                } else {
                    Toast.makeText(this, "취소 되었습니다", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}