package com.example.hirecodeandroid.project.detailproject

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.example.hirecodeandroid.R
import com.example.hirecodeandroid.databinding.ActivityDetailProjectBinding
import com.example.hirecodeandroid.hire.HireApiService
import com.example.hirecodeandroid.project.ProjectApiService
import com.example.hirecodeandroid.project.detailproject.listhirebyproject.HireByProjectResponse
import com.example.hirecodeandroid.util.GeneralResponse
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.coroutines.CoroutineContext

class DetailProjectViewModel: ViewModel(), CoroutineScope {

    val isProjectLiveData = MutableLiveData<Boolean>()
    val isProjectDeleteLiveData = MutableLiveData<Boolean>()
    val isHireLiveData = MutableLiveData<Boolean>()
    val listDataModel = MutableLiveData<List<DetailProjectResponse.Project>>()

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Main

    private lateinit var service: ProjectApiService
    private lateinit var serviceHire: HireApiService

    fun setServiceHire(serviceHire: HireApiService) {
        this.serviceHire = serviceHire
    }

    fun setService(service: ProjectApiService) {
        this.service = service
    }

    fun getDataproject(id: Int) {
        launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    service?.getProjectByProjectId(id)
                } catch (e: Throwable) {
                    e.printStackTrace()

                    withContext(Dispatchers.Main) {
                        isProjectLiveData.value = false
                    }
                }
            }

            if (result is DetailProjectResponse) {
                if (result.success) {
                    listDataModel.value = result.data
                    isProjectLiveData.value = true
                } else {
                    isProjectLiveData.value = false
                }
            }

        }
    }

    fun getHireByProject(id: Int) {
        launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    serviceHire?.getHireByProjectId(id)
                } catch (e: Throwable) {
                    e.printStackTrace()

                    withContext(Dispatchers.Main) {
                        isHireLiveData.value = false
                    }
                }
            }

            if (result is HireByProjectResponse) {
                if (result.success) {
                    if (result.data[0].hireStatus == "approve" || result.data[0].hireStatus == "wait") {
                        isHireLiveData.value = true
                    }
                }
            }
        }
    }

    fun deleteProject(id: Int) {
        launch {
            val result = withContext(Dispatchers.IO) {
                try {

                    service?.deleteProject(id)
                } catch (e :Throwable) {
                    e.printStackTrace()

                    withContext(Dispatchers.Main) {
                        isProjectDeleteLiveData.value = false
                    }
                }
            }

            if (result is GeneralResponse) {
                if (result.success) {
                    isProjectDeleteLiveData.value = true
                } else {
                    isProjectDeleteLiveData.value = false
                }
            }
        }
    }

}