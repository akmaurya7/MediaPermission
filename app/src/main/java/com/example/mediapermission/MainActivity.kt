package com.example.mediapermission

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mediapermission.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val readExternal = READ_EXTERNAL_STORAGE
    private val readImage = READ_MEDIA_IMAGES
    private  val readVideo = READ_MEDIA_VIDEO
    //private  val readMediaVisual = READ_MEDIA_VISUAL_USER_SELECTED

    private val permissions = arrayOf(readImage,readVideo)

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnMediaPermission.setOnClickListener {
            requestPermissions()
        }
    }


    private fun requestPermissions(){
        //check the API level
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            //filter permissions array in order to get permissions that have not been granted
            val notGrantedPermissions=permissions.filterNot { permission->
                ContextCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_GRANTED
            }
            if (notGrantedPermissions.isNotEmpty()){
                //check if permission was previously denied and return a boolean value
                val showRationale=notGrantedPermissions.any { permission->
                    shouldShowRequestPermissionRationale(permission)
                }
                //if true, explain to user why granting this permission is important
                if (showRationale){
                    AlertDialog.Builder(this)
                        .setTitle("Storage Permission")
                        .setMessage("Storage permission is needed in order to show images and videos")
                        .setNegativeButton("Cancel"){dialog,_->
                            Toast.makeText(this, "Read media storage permission denied!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                        .setPositiveButton("OK"){_,_->
                            videoImagesPermission.launch(notGrantedPermissions.toTypedArray())
                        }
                        .show()
                }else{
                    //launch the videoPermission ActivityResultContract
                    videoImagesPermission.launch(notGrantedPermissions.toTypedArray())
                }
            }else{
                Toast.makeText(this, "Read media storage permission granted", Toast.LENGTH_SHORT).show()
            }
        }else{
            //check if permission is granted
            if (ContextCompat.checkSelfPermission(this,readExternal) == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Read external storage permission granted", Toast.LENGTH_SHORT).show()
            }else{
                if (shouldShowRequestPermissionRationale(readExternal)){
                    AlertDialog.Builder(this)
                        .setTitle("Storage Permission")
                        .setMessage("Storage permission is needed in order to show images and video")
                        .setNegativeButton("Cancel"){dialog,_->
                            Toast.makeText(this, "Read external storage permission denied!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                        .setPositiveButton("OK"){_,_->
                            readExternalPermission.launch(readExternal)
                        }
                        .show()
                }else{
                    readExternalPermission.launch(readExternal)
                }
            }
        }
    }


    private val videoImagesPermission=registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissionMap->
        if (permissionMap.all { it.value }){
            Toast.makeText(this, "Media permissions granted", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Media permissions not granted!", Toast.LENGTH_SHORT).show()
        }
    }
    //register a permissions activity launcher for a single permission
    private val readExternalPermission=registerForActivityResult(ActivityResultContracts.RequestPermission()){isGranted->
        if (isGranted){
            Toast.makeText(this, "Read external storage permission granted", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Read external storage permission denied!", Toast.LENGTH_SHORT).show()
        }
    }

}