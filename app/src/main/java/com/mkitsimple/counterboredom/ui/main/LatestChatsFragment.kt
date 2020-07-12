package com.mkitsimple.counterboredom.ui.main

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.mkitsimple.counterboredom.BaseApplication

import com.mkitsimple.counterboredom.R
import com.mkitsimple.counterboredom.data.models.ChatMessage
import com.mkitsimple.counterboredom.ui.views.LatestChatItems
import com.mkitsimple.counterboredom.viewmodels.ViewModelFactory
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_latest_chats.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class LatestChatsFragment : Fragment() {

    companion object {
        val TAG = "LatestMessages"
        var latestMessagesMap = HashMap<String, ChatMessage>()
        val USER_KEY = "USER_KEY"
    }

    private lateinit var viewModel: LatestChatsViewModel
    val adapter = GroupAdapter<ViewHolder>()
    @Inject
    lateinit var factory: ViewModelFactory

    private lateinit var job1: Job

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (  activity?.applicationContext as BaseApplication).appComponent
                .newMainComponent().inject(this)

        viewModel = ViewModelProviders.of(this, factory).get(LatestChatsViewModel::class.java)
        job1 = Job()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_latest_chats, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // set item click listener on your adapter
        adapter.setOnItemClickListener { item, _ ->
            val row = item as LatestChatItems
            val intent = Intent(context, ChatLogActivity::class.java)
            intent.putExtra(USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }

        //Setup RecyclerView
        listenForLatestMessages()
    }

    private fun listenForLatestMessages() {
        CoroutineScope(Main + job1).launch{
            viewModel.listenForLatestMessages()
            viewModel.listenForLatestMessagesResult?.observe(viewLifecycleOwner, Observer {
                latestMessagesMap = it.first
                refreshRecyclerViewMessages(it.second)
            })
        }
    }

    private fun refreshRecyclerViewMessages(mWhen: String) {
        adapter.clear()
        val resultMap = latestMessagesMap.entries.sortedByDescending { it.value.timestamp }.associate { it.toPair() }
        resultMap.values.forEach {
            adapter.add(LatestChatItems(it, mWhen))
        }
        recyclerviewLatestChats.adapter = adapter

        if (adapter.itemCount >= 1){
            textViewNoChats.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(::job1.isInitialized) job1.cancel()
    }
}