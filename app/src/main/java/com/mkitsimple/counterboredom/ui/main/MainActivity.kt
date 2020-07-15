package com.mkitsimple.counterboredom.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mkitsimple.counterboredom.BaseApplication
import com.mkitsimple.counterboredom.R
import com.mkitsimple.counterboredom.data.models.User
import com.mkitsimple.counterboredom.ui.auth.RegisterActivity
import com.mkitsimple.counterboredom.utils.toast
import com.mkitsimple.counterboredom.viewmodels.ViewModelFactory
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
        const val CHANNEL_ID = "MainActivity"
        var currentUser: User? = null
    }

    private lateinit var viewModel: MainViewModel

    @Inject
    lateinit var factory: ViewModelFactory
    private lateinit var job1: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ( this.applicationContext as BaseApplication).appComponent
            .newMainComponent().inject(this)

        viewModel = ViewModelProviders.of(this@MainActivity, factory)[MainViewModel::class.java]
        job1 = Job()

        toolbar.setTitle("")
        setSupportActionBar(toolbar)
        val latestChatsFragment = LatestChatsFragment()
        val friendsFragment = FriendsListFragment()

        tab_layout.setupWithViewPager(view_pager)

        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, 1)
        viewPagerAdapter.addFragment(latestChatsFragment, getString(R.string.LATEST_CHATS))
        viewPagerAdapter.addFragment(friendsFragment, getString(R.string.FRIENDS_LIST))
        view_pager.setAdapter(viewPagerAdapter)
//
//        circleImageViewMain.setOnClickListener {
//            //startActivity(intentFor<ProfileActivity>())
//            val intent = Intent(this, ProfileActivity::class.java)
//            startActivity(intent)
//        }
//
        verifyUserIsLoggedIn()
        fetchCurrentUser()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.menu_logout) {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        if (id == R.id.menu_account) {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        return true
    }

    private class ViewPagerAdapter(fm: FragmentManager, behavior: Int) : FragmentPagerAdapter(fm, behavior) {
        private val fragments: MutableList<Fragment> = ArrayList()
        private val fragmentTitle: MutableList<String> = ArrayList()
        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            fragmentTitle.add(title)
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        @Nullable
        override fun getPageTitle(position: Int): CharSequence? {
            return fragmentTitle[position]
        }
    }

    private fun verifyUserIsLoggedIn() {
        val uid = viewModel.uid()
//        if (uid == null) {
//            val intent = Intent(this, RegisterActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
//        }
    }

    private fun fetchCurrentUser() {
//        CoroutineScope(Dispatchers.Main + job1).launch {
//
//        }
        //toast("fetchCurrentUser")

        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                //toast("called")
                currentUser = p0.getValue(User::class.java)
                loadImageView(currentUser!!)
//                //Log.d("LatestMessages", "Current user ${cUser.profileImageUrl}")
                //toast(currentUser!!.profileImageUrl)
                //Picasso.get().load(currentUser!!.profileImageUrl).into(circleImageViewMain)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

//        viewModel.fetchCurrentUser()
//        viewModel.currentUser.observe(this, Observer {
//            toast(it.profileImageUrl)
//        })
//        viewModel.fetchCurrentUserResult?.observe(this@MainActivity, Observer {
//            //currentUser = it
//            //loadImageView(it.profileImageUrl)
//            //if (currentUser!!.profileImageUrl != "null") {
//            toast(it.profileImageUrl)
//
//            //}
//        })
    }

    private fun loadImageView(user: User) {
        toast(user.profileImageUrl)
        //Picasso.get().load(user.profileImageUrl).into(circleImageViewMain)
    }
//
//    override fun onResume() {
//        super.onResume()
//        //fetchCurrentUser()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        if(::job1.isInitialized) job1.cancel()
//    }
}
