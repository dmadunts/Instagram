package com.example.renai.instagram.screens.search

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.GridLayoutManager
import android.text.Editable
import android.text.TextWatcher
import com.example.renai.instagram.R
import com.example.renai.instagram.screens.common.BaseActivity
import com.example.renai.instagram.screens.common.ImagesAdapter
import com.example.renai.instagram.screens.common.setupAuthGuard
import com.example.renai.instagram.screens.common.setupBottomNavigation
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : BaseActivity(), TextWatcher {
    private lateinit var mAdapter: ImagesAdapter
    private lateinit var mViewModel: SearchViewModel
    private var isSearchEntered = false

    companion object {
        const val TAG = "SearchActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setupAuthGuard { uid ->
            setupBottomNavigation(uid, 1)
            mAdapter = ImagesAdapter()
            search_results_recycler.layoutManager = GridLayoutManager(this, 3)
            search_results_recycler.adapter = mAdapter

            mViewModel = initViewModel()
            mViewModel.posts.observe(this, Observer {
                it?.let { posts ->
                    mAdapter.updateImages(posts.map { it.image })
                }
            })

            search_input.addTextChangedListener(this)
            mViewModel.setSearchText("")
        }
    }

    override fun afterTextChanged(s: Editable?) {
        if (!isSearchEntered) {
            isSearchEntered = true
            Handler().postDelayed({
                isSearchEntered = false
                mViewModel.setSearchText(search_input.text.toString())
            }, 500)
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
}
