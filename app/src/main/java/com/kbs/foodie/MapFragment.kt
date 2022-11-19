package com.kbs.foodie

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.protobuf.LazyStringArrayList
import com.kbs.foodie.databinding.MapFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.daum.mf.map.api.CalloutBalloonAdapter
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MapFragment : Fragment(R.layout.map_fragment)  {
    private val listItems = arrayListOf<ListLayout>()   // 리사이클러 뷰 아이템
    //private val markedList = arrayListOf<ListLayout>()
    private val listAdapter = ListAdapter(listItems)    // 리사이클러 뷰 어댑터
    //private val markedListAdapter = ListAdapter(markedList)
    private var pageNumber = 1      // 검색 페이지 번호
    private var keyword = ""        // 검색 키워드
    private var binding: MapFragmentBinding? = null
    lateinit var locationDataListener: OnLocationSetListener
    private lateinit var mapView : MapView              // 카카오 지도 뷰
    private lateinit var user:String //나
    private var users = ArrayList<String>(); //친구들

    data class ContentData(val review:String, val index:MapPoint, val score : Float)
    //    private val eventListener = context?.let { MarkerEventListener(it) }
    private var pointList=ArrayList<ContentData>()

    private lateinit var eventListener:MarkerEventListener // 마커 클릭 이벤트 리스너
    val db: FirebaseFirestore = Firebase.firestore
    private lateinit var contentCollectionRef: CollectionReference
    private lateinit var FriendCollectionRef : CollectionReference
    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK b4186fe9f3dc84011569230d000fc096"  // REST API 키
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        user = if(activity is MainActivity) {
            val main = activity as MainActivity
            main.hiddenMenu()
            main.user
        } else{
            val add = activity as AddActivity
            add.user
        }

        val firebaseAuth = FirebaseAuth.getInstance()
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val friendCollectionRef = db.collection("user").document(user).collection("friend")


        friendCollectionRef.get().addOnSuccessListener { it ->
            for(doc in it){
                val friend = doc["friendEmail"].toString()
                contentCollectionRef= db.collection("user").document(friend)
                    .collection("content")
                contentCollectionRef.get()
                    .addOnSuccessListener { // it: DocumentSnapshot
                        for (doc in it) {
                            val point = MapPOIItem()
                            var checkOnly = true
                            var points = binding?.mapView?.poiItems

                            point.apply {
                                if (doc["y"] != null) {
                                    var review = doc["review"].toString()
                                    var score = doc["score"].toString()
                                    itemName = doc["name"].toString()
                                    //Log.w("in MapFragment",itemName)
                                    val y = doc["y"].toString().toDouble()
                                    //Log.w("in MapFragment","${y}")
                                    val x = doc["x"].toString().toDouble()
                                    mapPoint = MapPoint.mapPointWithGeoCoord(
                                        y,
                                        x
                                    )
                                    markerType = MapPOIItem.MarkerType.RedPin
                                    isCustomImageAutoscale = false
                                    setCustomImageAnchor(0.5f, 1.0f)

                                    for(pt in pointList){
                                        if(pt.index.mapPointGeoCoord.latitude == mapPoint.mapPointGeoCoord.latitude &&
                                            pt.index.mapPointGeoCoord.longitude == mapPoint.mapPointGeoCoord.longitude    )
                                            checkOnly =false
                                    }
                                    //Log.w("in MapFragment", pointListReview.get(0))

                                    //이미 마커가 찍힌경우 => 하나만 찍히도록

                                    pointList.add(ContentData("$review \n",mapPoint, score.toFloat()))

                                }

                                if(checkOnly)
                                    binding?.mapView?.addPOIItem(point)

                            }
                        }
                    }
                    .addOnFailureListener {
                    }

            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            contentCollectionRef= db.collection("user").document(user)
                .collection("content")
            contentCollectionRef.get()
                .addOnSuccessListener { // it: DocumentSnapshot
                    for (doc in it) {
                        val point = MapPOIItem()
                        var points = binding?.mapView?.poiItems

                        var checkOnly = true
                        point.apply {
                            if (doc["y"] != null) {
                                var review = doc["review"].toString()
                                var score = doc["score"].toString()
                                itemName = doc["name"].toString()
                                //Log.w("in MapFragment",itemName)
                                val y = doc["y"].toString().toDouble()
                                //Log.w("in MapFragment","${y}")
                                val x = doc["x"].toString().toDouble()
                                mapPoint = MapPoint.mapPointWithGeoCoord(
                                    y,
                                    x
                                )
                                markerType = MapPOIItem.MarkerType.RedPin
                                isCustomImageAutoscale = false
                                setCustomImageAnchor(0.5f, 1.0f)

                                for(pt in pointList){
                                    if(pt.index.mapPointGeoCoord.latitude == mapPoint.mapPointGeoCoord.latitude &&
                                        pt.index.mapPointGeoCoord.longitude == mapPoint.mapPointGeoCoord.longitude    )
                                        checkOnly =false
                                }
                                //Log.w("in MapFragment", pointListReview.get(0))

                                //이미 마커가 찍힌경우 => 하나만 찍히도록

                                pointList.add(ContentData("$review \n",mapPoint, score.toFloat()))

                            }

                            if(checkOnly)
                                binding?.mapView?.addPOIItem(point)

                        }
                    }
                }
                .addOnFailureListener {
                }

        }



        //searchKeyword("은행")
        binding = MapFragmentBinding.inflate(inflater,container ,false)

        binding!!.rvList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        eventListener = context?.let { MarkerEventListener(it) }!!;
        binding!!.rvList.adapter = listAdapter
        // 리스트 아이템 클릭 시 해당 위치로 이동
        listAdapter.setItemClickListener(object: ListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val mapPoint = MapPoint.mapPointWithGeoCoord(listItems[position].y, listItems[position].x)

                binding!!.mapView.setMapCenterPointAndZoomLevel(mapPoint, 1, true)
                locationDataListener.onLocationSet(listItems[position].address, listItems[position].name, listItems[position].y, listItems[position].x)
            }
        })

        // 검색 버튼
        binding!!.btnSearch.setOnClickListener {
            keyword = binding!!.etSearchField.text.toString()
            pageNumber = 1
            searchKeyword(keyword, pageNumber)
        }
        // 이전 페이지 버튼
        binding!!.btnPrevPage.setOnClickListener {
            pageNumber--
            binding!!.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyword, pageNumber)
        }
        // 다음 페이지 버튼
        binding!!.btnNextPage.setOnClickListener {
            pageNumber++
            binding!!.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyword, pageNumber)
        }
        mapView = binding!!.mapView   // 카카오 지도 뷰





        mapView.setCalloutBalloonAdapter(CustomBalloonAdapter(layoutInflater))  // 커스텀 말풍선 등록
        mapView.setPOIItemEventListener(eventListener)
        return binding!!.root

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        locationDataListener= context as OnLocationSetListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //locationDataListener.onLocationSet("광진구 군자동")
    }

    private fun searchKeyword(keyword: String, page: Int) {
        val retrofit = Retrofit.Builder()          // Retrofit 구성
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)            // 통신 인터페이스를 객체로 생성
        val call = api.getSearchKeyword(API_KEY, keyword, page)    // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(object: Callback<ResultSearchKeyword> {
            override fun onResponse(call: Call<ResultSearchKeyword>, response: Response<ResultSearchKeyword>) {
                // 통신 성공
                addItemsAndMarkers(response.body())
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.w("LocalSearch", "통신 실패: ${t.message}")
            }
        })
    }
    // 검색 결과 처리 함수
    private fun addItemsAndMarkers(searchResult: ResultSearchKeyword?) {
        if (!searchResult?.documents.isNullOrEmpty()) {
            // 검색 결과 있음
            listItems.clear()                   // 리스트 초기화
            binding?.mapView?.removeAllPOIItems() // 지도의 마커 모두 제거
            for (document in searchResult!!.documents) {
                // 결과를 리사이클러 뷰에 추가
                val item = ListLayout(document.place_name,
                    document.road_address_name,
                    document.address_name,
                    document.x.toDouble(),
                    document.y.toDouble())
                listItems.add(item)

                // 지도에 마커 추가
                val point = MapPOIItem()
                point.apply {
                    itemName = document.place_name
                    mapPoint = MapPoint.mapPointWithGeoCoord(document.y.toDouble(),
                        document.x.toDouble())
                    markerType = MapPOIItem.MarkerType.BluePin
                    selectedMarkerType = MapPOIItem.MarkerType.RedPin
                }
                binding?.mapView?.addPOIItem(point)


            }
            listAdapter.notifyDataSetChanged()

            binding?.btnNextPage?.isEnabled ?:  !searchResult.meta.is_end // 페이지가 더 있을 경우 다음 버튼 활성화
            (binding?.btnPrevPage?.isEnabled
                ?: pageNumber) != 1             // 1페이지가 아닐 경우 이전 버튼 활성화

        } else {
            // 검색 결과 없음
            Toast.makeText(context, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }
    private fun queryItem(itemID: String) {
            }

    // 커스텀 말풍선 클래스
    inner class CustomBalloonAdapter(inflater: LayoutInflater): CalloutBalloonAdapter {
        private val mCalloutBalloon: View = inflater.inflate(R.layout.balloon_layout, null)
        val name: TextView = mCalloutBalloon.findViewById(R.id.ball_tv_name)
        private val address: TextView = mCalloutBalloon.findViewById(R.id.ball_tv_address)


        override fun getCalloutBalloon(poiItem: MapPOIItem?): View {
            // 마커 클릭 시 나오는 말풍선


            name.text = poiItem?.itemName   // 해당 마커의 정보 이용 가능
            var i=0;
            var sum =0.0;
            var count =0;
            for(p in pointList){
                if(p.index.mapPointGeoCoord.latitude==poiItem?.mapPoint?.mapPointGeoCoord?.latitude&&
                    p.index.mapPointGeoCoord.longitude==poiItem?.mapPoint?.mapPointGeoCoord?.longitude){
                    sum += p.score;
                    count++;

                }
            }
            Log.w("sum", "$sum")
            address.text = (sum/count).toString();
            return mCalloutBalloon

        }

        override fun getPressedCalloutBalloon(poiItem: MapPOIItem?): View {
            // 말풍선 클릭 시
            var i=0;
//            lateinit var review:String

            var sum =0.0;
            var reviews = mutableListOf<String>();
            for(p in pointList){
                if(p.index.mapPointGeoCoord.latitude==poiItem?.mapPoint?.mapPointGeoCoord?.latitude&&
                    p.index.mapPointGeoCoord.longitude==poiItem?.mapPoint?.mapPointGeoCoord?.longitude){

                    reviews.add(p.review)



                }
            }

//            var k=0;
            var unireviews = reviews.distinct().toMutableList()
            var TEXT:String =""
            for(review in unireviews)
                TEXT += review

            for (i in unireviews) {
                Log.w("review", i)
            }
            address.text=TEXT


            return mCalloutBalloon
        }

    }
    inner class MarkerEventListener(val context: Context): MapView.POIItemEventListener {
        override fun onPOIItemSelected(mapView: MapView?, poiItem: MapPOIItem?) {
            // 마커 클릭 시
        }

        @Deprecated("Deprecated in Java")
        override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?) {

        }

        override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?, buttonType: MapPOIItem.CalloutBalloonButtonType?) {
            // 말풍선 클릭 시
            val builder = AlertDialog.Builder(context)
            val itemList = arrayOf("마커 삭제", "취소")
            builder.setTitle("${poiItem?.itemName}")
            builder.setItems(itemList) { dialog, which ->
                when(which) {
//                    0 -> Toast.makeText(context, "토스트", Toast.LENGTH_SHORT).show()  // 토스트
                    0 -> mapView?.removePOIItem(poiItem)    // 마커 삭제
                    1 -> dialog.dismiss()   // 대화상자 닫기
                }
            }
            builder.show()
        }

        override fun onDraggablePOIItemMoved(mapView: MapView?, poiItem: MapPOIItem?, mapPoint: MapPoint?) {
            // 마커의 속성 중 isDraggable = true 일 때 마커를 이동시켰을 경우
        }
    }
}







