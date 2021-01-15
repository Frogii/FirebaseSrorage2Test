package com.example.firebasesrorage2

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


private const val REQUEST_CODE_IMAGE_PICK = 0
private const val REQUEST_CODE_PHOTO_PICK = 1
private const val M_ACTIVITY = "MainActivity"

class MainActivity : AppCompatActivity() {

    private val imageRef = Firebase.storage.reference
    private val imageCollectionRef = Firebase.firestore.collection("MapImages")
    var imageFile: Uri? = null
    var imageName: String = ""
    lateinit var recAdapter: RecAdapter
    var mapImageList = mutableListOf<MapImage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRecycler()

        buttonChooseImage.setOnClickListener {
            Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            ).also {
                startActivityForResult(it, REQUEST_CODE_IMAGE_PICK)
            }
        }

        imageViewChosenImage.setOnClickListener {
            ChoosePictureDialog(this, object : IDialog {
                override fun chooseImage() {
                    Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    ).also {
                        startActivityForResult(it, REQUEST_CODE_IMAGE_PICK)
                    }
                }

                override fun takePhoto() {
                    Intent(
                        Intent("android.media.action.IMAGE_CAPTURE")
                    ).also {
                        startActivityForResult(it, REQUEST_CODE_PHOTO_PICK)
                    }
                }
            }).show()
        }

        buttonUploadImage.setOnClickListener {
            uploadImage(imageName)
        }

        buttonDownloadAll.setOnClickListener {
            mapImageList.clear()
            downloadAllImages()
        }
    }

    private fun downloadAllImages() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val querySnapshot = imageCollectionRef.get().await()
            var mapImage: MapImage
            for (document in querySnapshot.documents) {
                mapImage = document.toObject(MapImage::class.java)!!
                Log.d(M_ACTIVITY, mapImage.toString())
                mapImageList.add(mapImage)
            }
            withContext(Dispatchers.Main) {
                recAdapter.setList(mapImageList)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    private fun uploadImage(imageName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                imageFile?.let {
                    imageRef.child("images/$imageName").putFile(it).await()
                    val imageUrl =
                        imageRef.child("images/$imageName").downloadUrl.await().toString()
                    imageCollectionRef.add(
                        MapImage(
                            imageName,
                            imageUrl,
                            "Лучше фото всех времен и народов. Сделано в январе 2021 года"
                        )
                    ).await()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Image uploaded", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun initRecycler() {
        recAdapter = RecAdapter()
        recyclerViewImages.adapter = recAdapter
        recyclerViewImages.layoutManager = LinearLayoutManager(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PHOTO_PICK && resultCode == Activity.RESULT_OK) {
            Log.d(M_ACTIVITY, "taking photo from camera")
            Log.d(M_ACTIVITY, data.toString())
            data?.let {
                val file = it.extras?.get("data") as Bitmap
                Log.d(M_ACTIVITY, file.toString())
                Log.d(M_ACTIVITY, "${file.width}  ${file.height}")
                Log.d(M_ACTIVITY, "taking photo from camera")
                imageViewChosenImage.setImageBitmap(file)
            }
        } else if (requestCode == REQUEST_CODE_IMAGE_PICK) {
            data?.data?.let {
                imageFile = it
                Log.d(M_ACTIVITY, "taking photo from gallery")
                Log.d(M_ACTIVITY, data.toString())
                imageViewChosenImage.setImageURI(it)
                imageName = it.pathSegments.last()
            }
        }
    }
}