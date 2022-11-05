package com.kbs.foodie

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kbs.foodie.databinding.MapFragmentBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



class MapFragment : Fragment(R.layout.map_fragment)  {
    private val listItems = arrayListOf<ListLayout>()   // 리사이클러 뷰 아이템
    private val listAdapter = ListAdapter(listItems)    // 리사이클러 뷰 어댑터
    private var pageNumber = 1      // 검색 페이지 번호
    private var keyword = ""        // 검색 키워드
    private var binding: MapFragmentBinding? = null
    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK b4186fe9f3dc84011569230d000fc096"  // REST API 키
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //searchKeyword("은행")
        Log.w("1","1")
        binding = MapFragmentBinding.inflate(inflater,container ,false)
        Log.w("1","2")

        binding!!.rvList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        Log.w("1","3")

        binding!!.rvList.adapter = listAdapter
        // 리스트 아이템 클릭 시 해당 위치로 이동
        Log.w("1","4")
        listAdapter.setItemClickListener(object: ListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val mapPoint = MapPoint.mapPointWithGeoCoord(listItems[position].y, listItems[position].x)
                binding!!.mapView.setMapCenterPointAndZoomLevel(mapPoint, 1, true)
            }
        })
        Log.w("1","5")
        // 검색 버튼
        binding!!.btnSearch.setOnClickListener {
            keyword = binding!!.etSearchField.text.toString()
            pageNumber = 1
            searchKeyword(keyword, pageNumber)
        }
        Log.w("1","5")
        // 이전 페이지 버튼
        binding!!.btnPrevPage.setOnClickListener {
            pageNumber--
            binding!!.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyword, pageNumber)
        }
        Log.w("1","5")
        // 다음 페이지 버튼
        binding!!.btnNextPage.setOnClickListener {
            pageNumber++
            binding!!.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyword, pageNumber)
        }
        Log.w("1","5")
        return binding!!.root

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

           // binding?.btnNextPage?.isEnabled ?:  = !searchResult.meta.is_end // 페이지가 더 있을 경우 다음 버튼 활성화
          //  binding?.btnPrevPage?.isEnabled ?:  = pageNumber != 1             // 1페이지가 아닐 경우 이전 버튼 활성화

        } else {
            // 검색 결과 없음
            Toast.makeText(context, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

}