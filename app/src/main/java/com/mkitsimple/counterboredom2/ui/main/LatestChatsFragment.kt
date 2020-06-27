package com.mkitsimple.counterboredom2.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.mkitsimple.counterboredom2.R

class LatestChatsFragment : Fragment() {

    companion object {
        fun newInstance() = LatestChatsFragment()
    }

    private lateinit var viewModel: LatestChatsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.latest_chats_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LatestChatsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
