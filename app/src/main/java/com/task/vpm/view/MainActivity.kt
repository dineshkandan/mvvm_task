package com.task.vpm.view

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.task.vpm.R
import com.task.vpm.adapter.MyRecyclerVideoAdapter
import com.task.vpm.adapter.MyRecyclerViewAdapter
import com.task.vpm.databinding.ActivityMainBinding
import com.task.vpm.db.Feeds
import com.task.vpm.db.FeedsDatabase
import com.task.vpm.db.FeedsRepository
import com.task.vpm.model.VideoListResponse
import com.task.vpm.retrofit.RetrofitService
import com.task.vpm.retrofit.RetrofitInstance
import com.task.vpm.utils.PaginationScrollListener
import com.task.vpm.viewmodel.FeedsViewModel
import com.task.vpm.viewmodel.FeedsViewModelFactory
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var feedsViewModel: FeedsViewModel
    private lateinit var adapter: MyRecyclerViewAdapter
    private lateinit var videoAdapter: MyRecyclerVideoAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var retService: RetrofitService
    private var page: Int = 1;
    var isLastPage: Boolean = false
    var isLoading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val dao = FeedsDatabase.getInstance(application).feedsDAO
        val repository = FeedsRepository(dao)
        val factory = FeedsViewModelFactory(repository)
        binding.fabAdd.hide()

        retService = RetrofitInstance
            .getRetrofitInstance()
            .create(RetrofitService::class.java)

        feedsViewModel = ViewModelProvider(this, factory).get(FeedsViewModel::class.java)
        binding.myViewModel = feedsViewModel
        binding.lifecycleOwner = this


        //binding.chLive.setTextColor(ContextCompat.getColor(this, R.color.white))

        feedsViewModel.message.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })

        feedsViewModel.isCreatedUpdated.observe(this, Observer {
            slideUpDownBottomSheet()
        })

        binding.fabAdd.setOnClickListener {
            feedsViewModel.initCreate()
            binding.nameText.isEnabled = true
            slideUpDownBottomSheet()
        }

        binding.clearAllOrDeleteButton.setOnClickListener {
            slideUpDownBottomSheet()
        }

        binding.txtHeaderFeeds.setOnClickListener {
            binding.txtHeaderFeeds.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.txtHeaderVideo.setTextColor(ContextCompat.getColor(this, R.color.grey_727272))
            binding.fabAdd.show()
            binding.cardHeaderFeeds.setCardBackgroundColor(ContextCompat.getColor(this, R.color.yellow_FFC491))
            binding.cardHeaderVideo.setCardBackgroundColor(ContextCompat.getColor(this, R.color.black_292929))
            binding.reVideo.visibility = View.GONE
            binding.feedsRecyclerView.visibility = View.VISIBLE
        }

        binding.txtHeaderVideo.setOnClickListener {
            binding.txtHeaderFeeds.setTextColor(ContextCompat.getColor(this, R.color.grey_727272))
            binding.txtHeaderVideo.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.fabAdd.hide()
            binding.cardHeaderFeeds.setCardBackgroundColor(ContextCompat.getColor(this, R.color.black_292929))
            binding.cardHeaderVideo.setCardBackgroundColor(ContextCompat.getColor(this, R.color.yellow_FFC491))
            binding.reVideo.visibility = View.VISIBLE
            binding.feedsRecyclerView.visibility = View.GONE
        }

        bottomSheetBehavior = BottomSheetBehavior.from<ConstraintLayout>(binding.bottomSheet)

        initRecyclerView()
        page = 1;
        getRequestWithQueryParameters(page)
    }

    private fun slideUpDownBottomSheet() {
        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED;
        }
    }

    private fun initRecyclerView() {
        binding.feedsRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MyRecyclerViewAdapter({ selectedItem: Feeds -> listItemClicked(selectedItem) })
        binding.feedsRecyclerView.adapter = adapter
        displaySubscribersList()

        val layoutManager = LinearLayoutManager(this)
        binding.videoRecyclerView.layoutManager = layoutManager
        videoAdapter = MyRecyclerVideoAdapter(this@MainActivity)
        binding.videoRecyclerView.adapter = videoAdapter

        binding.videoRecyclerView.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                isLoading = true
                //you have to call loadmore items to get more data
                ++page
                getRequestWithQueryParameters(page)
            }
        })
    }

    private fun displaySubscribersList() {
        feedsViewModel.getSavedSubscribers().observe(this, Observer {
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })
    }

    private fun listItemClicked(subscriber: Feeds) {
        slideUpDownBottomSheet()
        binding.nameText.isEnabled = false
        feedsViewModel.initUpdateAndDelete(subscriber)
    }

    private fun getRequestWithQueryParameters(page: Int) {
        binding.pbVideo.visibility = View.VISIBLE
        val responseLiveData: LiveData<Response<VideoListResponse>> = liveData {
            val response = retService.getVideoList(page,10,"45.521728,-122.67326",100)
            emit(response)
        }
        responseLiveData.observe(this, Observer {
            binding.pbVideo.visibility = View.GONE
            isLoading = false
            val videoList = it.body()?.incidents
            if (videoList != null) {
                videoAdapter.setList(videoList)
            }
        })
    }
}