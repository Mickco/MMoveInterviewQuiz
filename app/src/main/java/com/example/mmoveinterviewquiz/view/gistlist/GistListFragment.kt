package com.example.mmoveinterviewquiz.view.gistlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mmoveinterviewquiz.databinding.FragmentGistListBinding
import com.example.mmoveinterviewquiz.repository.model.Gist
import com.example.mmoveinterviewquiz.util.launchAndRepeatWithViewLifecycle
import com.example.mmoveinterviewquiz.view.common.BaseFragment
import com.example.mmoveinterviewquiz.viewmodel.GistListViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GistListFragment : BaseFragment<FragmentGistListBinding>(), GistListRecyclerViewAdapter.GistItemListener {

    private val viewModel: GistListViewModel by viewModels()
    private var snackBar: Snackbar? = null

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
            gistListRecyclerView.adapter = GistListRecyclerViewAdapter(this@GistListFragment)

            launchAndRepeatWithViewLifecycle {
                viewModel.gistListUIModel.collect {
                    val adapter = gistListRecyclerView.adapter
                    if (adapter is GistListRecyclerViewAdapter) {
                        adapter.uiList = it
                    }
                }
            }
            launchAndRepeatWithViewLifecycle {
                viewModel.showLoadingSpinner.collect {
                    gistListSpinner.isVisible = it
                }
            }
            launchAndRepeatWithViewLifecycle {
                viewModel.snackbarMessage.collect {
                    // Display Error Message using snackbar
                    val msg = it.getString(requireContext())
                    if (msg.isNotEmpty() && snackBar?.isShown != true){
                        val view = this@GistListFragment.view ?: return@collect
                        snackBar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG).apply {
                            show()
                        }
                    }
                }
            }
            launchAndRepeatWithViewLifecycle {
                viewModel.navigateToDetail.collect {
                    gotoGistDetailFragment(it)
                }
            }

        }

    }

    override fun onClickItem(gistId: String) {
        viewModel.onClickItem(gistId)

    }

    override fun onClickFavorite(gistId: String) {
        viewModel.onClickFavoriteGist(gistId)
    }

    private fun gotoGistDetailFragment(gist: Gist) {
        findNavController().navigate(GistListFragmentDirections.actionGistListFragmentToGistDetailFragment(
            selectedGist = gist
        ))
    }
}