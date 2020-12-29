package com.example.hirecodeandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.hirecodeandroid.R
import com.example.hirecodeandroid.databinding.FragmentHomeEngineerBinding
import com.example.hirecodeandroid.util.SharePrefHelper

class FragmentHomeEngineer: Fragment() {

    private lateinit var binding: FragmentHomeEngineerBinding
    private lateinit var sharePref: SharePrefHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_engineer,container,false)
        sharePref = SharePrefHelper(this.activity!!)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvGreetingName.text = sharePref.getString(SharePrefHelper.AC_NAME)
    }

}