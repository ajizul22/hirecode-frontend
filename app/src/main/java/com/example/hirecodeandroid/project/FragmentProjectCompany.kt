package com.example.hirecodeandroid.project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hirecodeandroid.R
import com.example.hirecodeandroid.databinding.FragmentProjectCompanyBinding
import com.example.hirecodeandroid.project.addproject.AddProjectActivity
import com.example.hirecodeandroid.remote.ApiClient
import com.example.hirecodeandroid.util.SharePrefHelper
import kotlinx.coroutines.*

class FragmentProjectCompany: Fragment(), ProjectListAdapter.OnListProjectClickListener, ProjectContract.View {

    private lateinit var binding: FragmentProjectCompanyBinding
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var service: ProjectApiService
    private lateinit var sharePref: SharePrefHelper
    private var listProject = ArrayList<ProjectModel>()
    private var presenter: ProjectPresenter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_project_company, container, false)
        coroutineScope = CoroutineScope(Job() + Dispatchers.Main)
        service = ApiClient.getApiClient(requireContext())!!.create(ProjectApiService::class.java)
        sharePref = SharePrefHelper(requireContext())

        binding.rvProject.adapter = ProjectListAdapter(listProject, this)
        binding.rvProject.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        presenter = ProjectPresenter(coroutineScope, service, sharePref)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddProject.setOnClickListener {
            val intent = Intent(requireContext(), AddProjectActivity:: class.java)
            startActivity(intent)
        }
    }

    override fun addListProject(list: List<ProjectModel>) {
        (binding.rvProject.adapter as ProjectListAdapter).addList(list)
    }

    override fun onProjectItemClicked(position: Int) {
        val intent = Intent(requireContext(),DetailProjectActivity::class.java)
        intent.putExtra("image", listProject[position].projectImage)
        intent.putExtra("title", listProject[position].projectName)
        intent.putExtra("company", listProject[position].companyId)
        intent.putExtra("desc", listProject[position].projectDesc)
        intent.putExtra("deadline", listProject[position].projectDeadline)

        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        presenter?.bindToView(this)
        presenter?.callProjectApi()
    }

    override fun onStop() {
        presenter?.unBind()
        super.onStop()
    }

    override fun onDestroy() {
        coroutineScope.cancel()
        presenter = null
        super.onDestroy()
    }


}