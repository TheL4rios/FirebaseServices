package com.thelarios.firebaseservices

import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_action.*
import mx.edu.ittepic.ladm_u3_practica1_arturolarios.Utils.Utils

class ActionActivity : AppCompatActivity() {

    private val requestTakePhoto = 0
    private val requestSelectImageGallery = 1

    private var image : FirebaseVisionImage ?= null
    private var detector : FirebaseVisionImageLabeler ?= null
    private var imageBitmap : Bitmap ?= null
    private var imgObject = ""
    private var confidence = 0f

    private val database = FirebaseDatabase.getInstance().getReference("DB")

    private val mStorageRef = FirebaseStorage.getInstance().reference
    private val remoteConfig = FirebaseRemoteConfig.getInstance()
    private val configSettings = FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(5).build()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_action)

        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.default_values)

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    this.title = remoteConfig.getString("title")
                } else {
                    Toast.makeText(this, "Algo salió mal :/", Toast.LENGTH_SHORT).show()
                }
            }

        detector = FirebaseVision.getInstance().onDeviceImageLabeler
        showProgressBar(false)

        btnAddPhoto.setOnClickListener {
            addImage()
        }

        btnSave.setOnClickListener {
            saveImage()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun saveImage()
    {
        showProgressBar(true)
        imageBitmap?.let {
            val key = (0 until 100000).random()
            val data = mStorageRef.child("Photos").child(imgObject + key)
            data.putBytes(Utils.getByteArray(it))
                .addOnProgressListener { taskSnapshot ->
                    val progress = ((100 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount).toInt()
                    progress_horizontal.setProgress(progress, true)
                }
                .addOnSuccessListener {
                    data.downloadUrl.addOnSuccessListener { url ->
                        saveData(url.toString())
                    }
                }
                .addOnFailureListener {
                    showProgressBar(false)
                    Utils.toastMessageLong("Algo salió mal, Revise su conexión a internet", this)
                }
            return
        }
        showProgressBar(false)
        Utils.toastMessageLong("Para poder hacer el proceso seleccione una imagen", this)
    }

    private fun saveData(url : String)
    {
        val objectId = database.push().key
        val objectData = hashMapOf(
            "Object" to imgObject,
            "Confidence" to confidence,
            "Image" to url
        )

        objectId?.let { id ->
            database.child("Object").child(id).setValue(objectData)
                .addOnSuccessListener {
                    Utils.toastMessageLong("Se registró con éxito", this)
                    txtObject.text = ""
                    img.setImageBitmap(null)
                }
                .addOnFailureListener {
                    Utils.toastMessageLong("Algo salió mal, Revise su conexión a internet", this)
                }
            showProgressBar(false)
            return
        }
        showProgressBar(false)
        Utils.toastMessageLong("Algo salió mal", this)
    }

    private fun showProgressBar(status : Boolean)
    {
        if(status)
        {
            txtSaving.visibility = View.VISIBLE
            progress_horizontal.visibility = View.VISIBLE
            return
        }

        txtSaving.visibility = View.INVISIBLE
        progress_horizontal.visibility = View.INVISIBLE
    }

    private fun analyze()
    {
        image?.let {
            detector?.processImage(it)
                ?.addOnSuccessListener { detectedObjects ->
                    for (obj in detectedObjects) {
                        if (obj.confidence > confidence) {
                            confidence = obj.confidence
                            imgObject = obj.text
                        }
                    }

                    txtObject.text = "$imgObject -> $confidence"
                }
                ?.addOnFailureListener { e ->
                    Utils.toastMessageLong("Algo salió mal, Revise su conexión a internet", this)
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK && requestCode == requestSelectImageGallery)
        {
            val imageUri = data?.data
            imageBitmap = Utils.getBitMap(imageUri, contentResolver)
            imageBitmap?.let {
                image = FirebaseVisionImage.fromBitmap(it)
                img.setImageBitmap(imageBitmap)
                analyze()
            }
        }
        else if (resultCode == RESULT_OK && requestCode == requestTakePhoto)
        {
            imageBitmap = data?.extras?.get("data") as Bitmap
            imageBitmap?.let {
                image = FirebaseVisionImage.fromBitmap(it)
                img.setImageBitmap(imageBitmap)
                analyze()
            }
        }
    }

    /**********************************************************************************************/

    private fun addImage()
    {
        val popup = PopupMenu(this, btnAddPhoto)
        popup.inflate(R.menu.photo_popup)

        popup.setOnMenuItemClickListener {
            when(it.itemId)
            {
                R.id.gallery -> selectImageInAlbum()
                R.id.camera -> takePhoto()
            }
            true
        }

        popup.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        when(item.itemId)
        {
            R.id.signOut -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, MainActivity :: class.java))
                finish()
            }
            R.id.watchAll -> {
                startActivity(Intent(this, ShowActivity :: class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun selectImageInAlbum()
    {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null)
        {
            startActivityForResult(intent, requestSelectImageGallery)
        }
    }

    private fun takePhoto()
    {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, requestTakePhoto)
            }
        }
    }
}
