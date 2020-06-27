package com.mkitsimple.counterboredom2.ui.main

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.mkitsimple.counterboredom2.BaseApplication

import com.mkitsimple.counterboredom2.R
import com.mkitsimple.counterboredom2.data.models.User
import com.mkitsimple.counterboredom2.ui.views.UserItem
import com.mkitsimple.counterboredom2.utils.Coroutines
import com.mkitsimple.counterboredom2.viewmodels.ViewModelFactory
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_friends_list.*
import javax.inject.Inject

class FriendsListFragment : Fragment() {

    companion object {
        fun newInstance() = FriendsListFragment()
        val USER_KEY = "USER_KEY"
        val TAG = "FriendsListFragment"
    }

    private lateinit var viewModel: FriendsListViewModel
    @Inject
    lateinit var factory: ViewModelFactory
    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friends_list, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (  activity?.applicationContext as BaseApplication).appComponent
                .newMainComponent().inject(this)

        viewModel = ViewModelProviders.of(this, factory).get(FriendsListViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //setDummyData()
        fetchUsers()
    }

    private fun fetchUsers() {
        Coroutines.main(){
            viewModel.fetchUsers()
            viewModel.users?.observe(this, Observer { users ->
                setupRecyclerView(users)
            })
        }
    }

    private fun setupRecyclerView(users: List<User>) {
        for (user in users){
            //Log.d(TAG, "User: "+ user.username)
            val uid = FirebaseAuth.getInstance().uid
            if (user.uid != uid) {
                adapter.add(
                        UserItem(user)
                )
            }
        }

        adapter.setOnItemClickListener { item, view ->
            val userItem = item as UserItem
            val intent = Intent(view.context, ChatLogActivity::class.java)
            intent.putExtra(USER_KEY, userItem.user)
            startActivity(intent)
        }

        recyclerviewFriendsList.adapter = adapter
    }
}
