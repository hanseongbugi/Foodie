package com.kbs.foodie

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kbs.foodie.databinding.ActivityMainBinding
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {
    private val ACCESS_FINE_LOCATION = 1000
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        if (checkLocationService()) {
            permissionCheck()
        } else {
            Toast.makeText(this, "GPS를 켜주세요", Toast.LENGTH_SHORT).show()
        }
    //setContentView(R.layout.activity_main)
        //getAppKeyHash()
    }
/*
   fun getAppKeyHash() { //kakao maps api 를 위한 해시키 발생
       try {
           val info =
               packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
           for (signature in info.signatures) {
               var md: MessageDigest
               md = MessageDigest.getInstance("SHA")
               md.update(signature.toByteArray())
               val something = String(Base64.encode(md.digest(), 0))
               Log.e("Hash key", something)
           }
       } catch (e: Exception) {

           Log.e("name not found", e.toString())
       }
   }
    */
private fun permissionCheck() {
    val preference = getPreferences(MODE_PRIVATE)
    val isFirstCheck = preference.getBoolean("isFirstPermissionCheck", true)
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        // 권한이 없는 상태
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // 권한 거절
            val builder = AlertDialog.Builder(this)
            builder.setMessage("현재 위치를 확인하시려면 위치 권한을 허용해주세요.")
            builder.setPositiveButton("확인") { dialog, which ->
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), ACCESS_FINE_LOCATION)
            }
            builder.setNegativeButton("취소") { dialog, which ->

            }
            builder.show()
        } else {
            if (isFirstCheck) {
                // 최초 권한 요청
                preference.edit().putBoolean("isFirstPermissionCheck", false).apply()
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), ACCESS_FINE_LOCATION)
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("현재 위치를 확인하시려면 설정에서 위치 권한을 허용해주세요.")
                builder.setPositiveButton("설정으로 이동") { dialog, which ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
                    startActivity(intent)
                }
                builder.setNegativeButton("취소") { dialog, which ->

                }
                builder.show()
            }
        }
    } else {

    }

}

    // 권한 요청
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "위치 권한이 승인되었습니다", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "위치 권한이 거절되었습니다", Toast.LENGTH_SHORT).show()

            }
        }
    }

    // GPS가 켜져있는지 확인
    private fun checkLocationService(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

}