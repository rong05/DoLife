package com.rong.dolife.home.ui.bill

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rong.dolife.R

class BillFragment : Fragment() {

    private lateinit var viewModel: BillViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_bill, container, false)
        viewModel = ViewModelProviders.of(this).get(BillViewModel::class.java)
        return root
    }


}