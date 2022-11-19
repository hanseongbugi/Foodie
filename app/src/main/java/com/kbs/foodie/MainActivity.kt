package com.kbs.foodie

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.kbs.foodie.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(),OnLocationSetListener {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val ACCESS_FINE_LOCATION = 1000
    private lateinit var binding: ActivityMainBinding
    lateinit var navController:NavController
    lateinit var user:String
    private var toolBarkey=ToolBarKey.Home
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initToolBar()
        user=intent.getStringExtra("user")?:""
//        if (checkLocationService()) {
//            permissionCheck()
//        } else {
//            Toast.makeText(this, "GPS를 켜주세요", Toast.LENGTH_SHORT).show()
//        }
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentView) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)

        setupActionBarWithNavController(navController, appBarConfiguration)
        val bottomNavigationView = binding.bottomNav
        bottomNavigationView.setupWithNavController(navController)

        //bottom navigation의 fragment를 루트 최상위로
        //bottom navigation 변환에도 업 버튼이 출력되지 않기 위해 사용
        val appBarConfig=AppBarConfiguration.Builder(
            R.id.homeFragment, R.id.mapFragment,R.id.myInfoFragment
        ).build()
        NavigationUI.setupActionBarWithNavController(this,navController,appBarConfig)

    }

    // 업 버튼이 눌릴 때 호출 된다. -> SearchFragment에서 MyInfoFragment로의 이동을 위해 사용
    override fun onSupportNavigateUp(): Boolean {
        val navController=findNavController(R.id.fragmentView)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    //이 메소드를 각 fragment에서 호출하여 app bar를 관리한다
    fun showHomeMenu(){
        toolBarkey=ToolBarKey.Home
        invalidateOptionsMenu()
    }
    fun showSearchMenu(){
        toolBarkey=ToolBarKey.Search
        invalidateOptionsMenu()
    }
    fun hiddenMenu(){
        toolBarkey=ToolBarKey.Hidden
        invalidateOptionsMenu()
    }

    //invaiidateOptionsMenu가 호출되고 호출되는 메소드
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        when(toolBarkey) {
            ToolBarKey.Home-> {
                menu?.findItem(R.id.addMenu)?.isVisible = true
                menu?.findItem(R.id.search_menu)?.isVisible = false
            }
            ToolBarKey.Search->{
                menu?.findItem(R.id.addMenu)?.isVisible = false
                menu?.findItem(R.id.search_menu)?.isVisible = true
            }
            ToolBarKey.Hidden -> {
                menu?.clear()
            }

        }
        println(menu?.findItem(R.id.addMenu)?.isVisible.toString()+menu?.findItem(R.id.search_menu)?.isVisible.toString())
        return true
    }


    // 초기에 app bar를 생성
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.icon_menu,menu)
        return true
    }

    // item이 눌렸을 때
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // + 버튼이 눌리면 addActivity를 intent를 이용해 전환
            R.id.addMenu -> {
                val intent= Intent(this, AddActivity::class.java)
                    .putExtra("user",user)
                startActivity(intent)
            }
            // 돋보기 아이콘이 눌르면 friendaddFragment로 전환
            R.id.search_menu->{
                navController.navigate(R.id.action_myInfoFragment_to_friendAddFragment)
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
        return true
    }


    private fun initToolBar(){
        // 기존의 툴바를 제거
        supportActionBar?.hide()
        // layout에서 만든 toolbar를 사용한다.
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onBackPressed() {
        val fragmentManager: FragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 1) {
            fragmentManager.popBackStackImmediate()
        } else {
            super.onBackPressed()
        }
    }

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
        if (requestCode==Manifest.permission.ACCESS_FINE_LOCATION.toInt()) { //bug
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

    override fun onLocationSet(location: String, name: String, y: Double, x: Double) {
        Log.w("!!!",location)
    }

    enum class ToolBarKey{
        Home,Search,Hidden
    }
}

