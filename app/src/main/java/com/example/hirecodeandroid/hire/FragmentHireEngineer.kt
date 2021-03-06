package com.example.hirecodeandroid.hire

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hirecodeandroid.HomeActivity
import com.example.hirecodeandroid.R
import com.example.hirecodeandroid.databinding.FragmentHireEngineerBinding
import com.example.hirecodeandroid.project.detailproject.DetailProjectActivity
import com.example.hirecodeandroid.remote.ApiClient
import com.example.hirecodeandroid.util.GeneralResponse
import com.example.hirecodeandroid.util.SharePrefHelper
import kotlinx.coroutines.*

class FragmentHireEngineer: Fragment(), HireListAdapter.OnListHireClickListener {

    private lateinit var binding: FragmentHireEngineerBinding
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var sharePref: SharePrefHelper
    private lateinit var viewModel: ListHireEngineerViewModel
    var listHire = ArrayList<HireModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_hire_engineer,container,false)
        coroutineScope = CoroutineScope(Job() + Dispatchers.Main)
        val service = ApiClient.getApiClient(requireContext())?.create(HireApiService::class.java)
        sharePref = SharePrefHelper(requireContext())

        viewModel = ViewModelProvider(this@FragmentHireEngineer).get(ListHireEngineerViewModel::class.java)
        if (service != null) {
            viewModel.setService(service)
        }

        binding.rvHire.adapter = HireListAdapter(listHire, this)
        binding.rvHire.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val engineerId = sharePref.getString(SharePrefHelper.ENG_ID)
        viewModel.getListHire(engineerId!!)
        subscribeLiveData()
    }

    fun subscribeLiveData() {
        viewModel.isLiveData.observe(this, Observer {
            if (it) {
                binding.progressBar.visibility = View.GONE
                binding.rvHire.visibility = View.VISIBLE
                binding.tvDataNotFound.visibility = View.GONE

            } else {
                binding.progressBar.visibility = View.VISIBLE
                binding.rvHire.visibility = View.GONE
                binding.tvDataNotFound.visibility = View.GONE
            }
        })

        viewModel.resultFail.observe(this, Observer {
            if (it) {
                binding.tvDataNotFound.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        })

        viewModel.listHire.observe(this, Observer {
            (binding.rvHire.adapter as HireListAdapter).addList(it)
        })
    }

    private fun subscribeUpdateLiveData() {
        viewModel.isUpdateLiveData.observe(this, Observer {
            if (it) {
                Toast.makeText(requireContext(), "Update Hire Success", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to Update Hire Status", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onHireRejectClicked(position: Int) {
        showDialogReject(position)
    }

    override fun onHireApproveClicked(position: Int) {
        showDialogAprrove(position)
    }

    override fun onDetailProjectClicked(position: Int) {
        val intent = Intent(requireContext(), DetailProjectActivity::class.java)
        intent.putExtra("project_id", listHire[position].projectId?.toInt())
        startActivity(intent)
    }

    private fun showDialogAprrove(position: Int) {
        val id = listHire[position].hireId
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Approve Hire")
        builder.setMessage("Are you sure to approve this hiring?")
        builder.setPositiveButton("Yes") { dialogInterface : DialogInterface, i : Int ->
            viewModel.updateHireStatus(id!!, "approve")
            subscribeUpdateLiveData()
            moveActivity()
        }
        builder.setNegativeButton("No") { dialogInterface : DialogInterface, i : Int ->}
        builder.show()
    }

    private fun showDialogReject(position: Int) {
        val id = listHire[position].hireId
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Reject Hire")
        builder.setMessage("Are you sure to reject this hiring?")
        builder.setPositiveButton("Yes") { dialogInterface : DialogInterface, i : Int ->
            viewModel.updateHireStatus(id!!, "reject")
            subscribeUpdateLiveData()
            moveActivity()
        }
        builder.setNegativeButton("No") { dialogInterface : DialogInterface, i : Int ->}
        builder.show()
    }

    private fun moveActivity() {
        startActivity(Intent(requireContext(), HomeActivity::class.java))
        activity?.finish()
    }

    override fun onDestroy() {
        coroutineScope.cancel()
        super.onDestroy()
    }
}