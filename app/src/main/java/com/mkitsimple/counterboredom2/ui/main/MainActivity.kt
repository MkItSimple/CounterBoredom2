package com.mkitsimple.counterboredom2.ui.main

import android.content.Intent
import android.os.Bundle
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
import com.mkitsimple.counterboredom2.BaseApplication
import com.mkitsimple.counterboredom2.R
import com.mkitsimple.counterboredom2.data.models.User
import com.mkitsimple.counterboredom2.ui.auth.RegisterActivity
import com.mkitsimple.counterboredom2.viewmodels.ViewModelFactory
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = "MainActivity"
        val CHANNEL_ID = "MainActivity"
        val CHANNEL_NAME = "Simplified Coding"
        val CHANNEL_DESC = "Simplified Coding Notifications"
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

        viewModel = ViewModelProviders.of(this, factory)[MainViewModel::class.java]
        job1 = Job()

        toolbar.setTitle("")
        setSupportActionBar(toolbar)

        val latestChatsFragment = LatestChatsFragment()
        val friendsFragment = FriendsListFragment()

        tab_layout.setupWithViewPager(view_pager)

        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, 1)
        viewPagerAdapter.addFragment(latestChatsFragment, "LATEST CHATS")
        viewPagerAdapter.addFragment(friendsFragment, "FRIENDS LIST")
        view_pager.setAdapter(viewPagerAdapter)

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

        if (uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun fetchCurrentUser() {
        CoroutineScope(Dispatchers.Main + job1).launch {
            viewModel.fetchCurrentUser()
            viewModel.fetchCurrentUserResult?.observe(this@MainActivity, Observer {
                currentUser = it
                if (currentUser?.profileImageUrl != "null") {
                    Picasso.get().load(currentUser?.profileImageUrl).into(circleImageViewMain)
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        fetchCurrentUser()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(::job1.isInitialized) job1.cancel()
    }
}
