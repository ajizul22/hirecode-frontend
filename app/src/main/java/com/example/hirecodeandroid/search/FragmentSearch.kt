package com.example.hirecodeandroid.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hirecodeandroid.R
import com.example.hirecodeandroid.databinding.FragmentSearchBinding
import com.example.hirecodeandroid.listengineer.EngineerApiService
import com.example.hirecodeandroid.listengineer.ListEngineerModel
import com.example.hirecodeandroid.remote.ApiClient
import kotlinx.coroutines.*

class FragmentSearch: Fragment(), SearchContract.View {

    private lateinit var binding : FragmentSearchBinding
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var service: EngineerApiService
    var listEngineer = ArrayList<ListEngineerModel>()

    private var presenter: SearchPresenter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search,container,false)
        coroutineScope = CoroutineScope(Job() + Dispatchers.Main)
        service = ApiClient.getApiClient(requireContext())!!.create(EngineerApiService::class.java)

        presenter = SearchPresenter(coroutineScope, service)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvSearch.adapter = SearchAdapter(listEngineer)
        binding.rvSearch.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        setSearchView()
        setRecyclerView()

    }

    override fun onResultSuccess(list: List<ListEngineerModel>) {
        (binding.rvSearch.adapter as SearchAdapter).addList(list)
        binding.rvSearch.visibility = View.VISIBLE
        binding.tvDataNotFound.visibility = View.GONE
    }

    override fun onResultFail(message: String) {
        binding.rvSearch.visibility = View.GONE
        binding.tvDataNotFound.visibility = View.VISIBLE
    }

    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvSearch.visibility = View.GONE
        binding.tvDataNotFound.visibility = View.GONE
    }

    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    private fun setSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
        android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText == "") {
                    presenter?.callServiceSearch(null)
                } else {
                    if (newText?.length!! == 3) {
                        presenter?.callServiceSearch(newText)
                    }
                }
                return true
            }
        })
    }

    private fun setRecyclerView() {
        binding.rvSearch.isNestedScrollingEnabled = false
        binding.rvSearch.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        val adapter = SearchAdapter(listEngineer)
        binding.rvSearch.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        presenter?.bindToView(this)
        presenter?.callServiceSearch(null)
    }

    override fun onStop() {
        presenter?.unbind()
        super.onStop()
    }

    override fun onDestroy() {
        coroutineScope.cancel()
        presenter = null
        super.onDestroy()
    }
}