package com.sample.camera

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sample.camera.databinding.ActivityMainBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private const val REQUEST_CODE_FOR_THUMBNAIL_IMAGE = 101
private const val REQUEST_CODE_FOR_ORIGINAL_IMAGE = 102
private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var photoPath: String
    private lateinit var photoUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.thumbnail.setOnClickListener {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { imageCaptureIntent ->
                imageCaptureIntent.resolveActivity(packageManager)?.also {
                    startActivityForResult(imageCaptureIntent, REQUEST_CODE_FOR_THUMBNAIL_IMAGE);
                }
            }
        }

        /*
            외부파일디렉토리: 타입별로 서브디렉토리가 만들어지며 앱 삭제시 함께 삭제된다.
            외부캐시디렉토리: 앱 종료시 파일이 삭제될 수도 있고, 남아 있을 수도 있다. 시스템이 판단하여 용량 확보를 위해 삭제할 수도 있다.
            안드로이드 설정에서 앱 설정 > 내부저장소
                 > 임시파일 삭제 -> 캐시 디렉토리가 삭제된다.
                 > 데이터 지우기 -> 외부 파일 디렉토리 및 외부 캐시 디렉토리 모두 삭제된다.
            오래된 안드로이드 버전(4.3 이전)에서는 WRITE_EXTERNAL_STORAGE 권한이 필요하다.
         */
        binding.original.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(packageManager) != null) {
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                //val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES) // photos will be deleted when the app is uninstalled
                val dir = externalCacheDir // photo can be deleted when the app is terminated
                val file = File.createTempFile("photo_$timestamp", ".jpg", dir)
                photoPath = file.absolutePath
                photoUri = FileProvider.getUriForFile(this, "$packageName.provider", file)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(intent, REQUEST_CODE_FOR_ORIGINAL_IMAGE)
            }
        }
    }

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    when (requestCode) {
        REQUEST_CODE_FOR_THUMBNAIL_IMAGE -> {
            if (resultCode == RESULT_OK) {
                val thumbnailBitmap = data?.extras?.get("data") as Bitmap
                thumbnailBitmap?.let {
                    Log.i(TAG, "onActivityResult: thumbnail ${it.width} ${it.height}")
                    binding.image.setImageBitmap(it)
                }
            } else {
                Toast.makeText(this, "취소 되었습니다", Toast.LENGTH_LONG).show()
            }
        }
            REQUEST_CODE_FOR_ORIGINAL_IMAGE -> {
                if (resultCode == RESULT_OK) {
//                    BitmapFactory.decodeFile(photoPath)?.let {
//                        Log.i(TAG, "onActivityResult: original ${it.width} ${it.height}")
//                        binding.image.setImageBitmap(it)
//                    }
//                    Glide.with(this).load(photoUri).into(binding.image)
                    Glide.with(this).load(photoUri).apply(RequestOptions.circleCropTransform()).into(binding.image)
                } else {
                    Toast.makeText(this, "취소 되었습니다", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}