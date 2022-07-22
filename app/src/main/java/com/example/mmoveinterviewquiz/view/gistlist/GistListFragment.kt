package com.example.mmoveinterviewquiz.view.gistlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mmoveinterviewquiz.databinding.FragmentGistListBinding
import com.example.mmoveinterviewquiz.util.launchAndRepeatWithViewLifecycle
import com.example.mmoveinterviewquiz.view.common.BaseFragment
import com.example.mmoveinterviewquiz.viewmodel.GistListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GistListFragment : BaseFragment<FragmentGistListBinding>() {

    private val viewModel: GistListViewModel by viewModels()

    override fun onViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentGistListBinding {
        return FragmentGistListBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initViewModel()
    }

    override fun setupView() {
        with(binding) {
            gistListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            gistListRecyclerView.adapter = GistListRecyclerViewAdapter()

            launchAndRepeatWithViewLifecycle {
                viewModel.gistListUIModel.collect {
                    val adapter = gistListRecyclerView.adapter
                    if (adapter is GistListRecyclerViewAdapter) {
                        adapter.uiList = it.gistList
                    }
                }

            }
        }

    }

}