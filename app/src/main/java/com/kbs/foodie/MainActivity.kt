package com.kbs.foodie

import android.Manifest
import android.app.Activity
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
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kbs.foodie.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity(),OnLocationSetListener {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val ACCESS_FINE_LOCATION = 1000
    private lateinit var binding: ActivityMainBinding
    lateinit var navController:NavController
    lateinit var user:String
    private var toolBarkey=ToolBarKey.Home
    var friendName:String?=null
    var friendEmail:String?=null
    var backMainMenu=true //bottom navigation을 위한 flag변수
    var myInfoPos=0
    var ImageTrueFalse :Boolean = true
    var FoodImageTrueFalse :Boolean = true
    //이미지 관련
    var userPhoto : Uri? = null
    var foodPhoto : Uri? = null
    private var userFileName : String?=null
    val storage = Firebase.storage
    lateinit var resultImage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initToolBar()
        user=intent.getStringExtra("user")?:""
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
        if(backMainMenu)
            binding.bottomNav.isVisible=true
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
                removeBottomNavigation()
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
        return true
    }

    fun removeBottomNavigation(){
        binding.bottomNav.isVisible=false
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
    override fun onLocationSet(location: String, name: String, y: Double, x: Double) {
        Log.w("!!!",location)
    }
    fun rememberContent(pos:Int){
        myInfoPos = pos
    }
    enum class ToolBarKey{
        Home,Search,Hidden
    }
}

