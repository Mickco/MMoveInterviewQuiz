package com.example.mmoveinterviewquiz.view.gistdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mmoveinterviewquiz.databinding.FragmentGistDetailBinding
import com.example.mmoveinterviewquiz.util.launchAndRepeatWithViewLifecycle
import com.example.mmoveinterviewquiz.view.common.BaseFragment
import com.example.mmoveinterviewquiz.viewmodel.gistdetail.GistDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GistDetailFragment: BaseFragment<FragmentGistDetailBinding>() {

    companion object {
        const val ARG_KEY_IS_FAV_CHANGED = "ARG_KEY_IS_FAV_CHANGED"
    }

    private val viewModel: GistDetailViewModel by viewModels()
    private val args: GistDetailFragmentArgs by navArgs()

    override fun onViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentGistDetailBinding {
        return FragmentGistDetailBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().apply {
                previousBackStackEntry?.savedStateHandle?.set(ARG_KEY_IS_FAV_CHANGED, viewModel.isFavChanged)
                popBackStack()
            }
        }
        viewModel.initViewModel(args.selectedGist)
    }

    override fun setupView() {
        with(binding) {
            gistDetailFavoriteButton.setOnClickListener {
                viewModel.onClickFavorite()
            }

            launchAndRepeatWithViewLifecycle {
                viewModel.displayText.collect {
                    gistDetailTextView.text = it.getString(requireContext())
                }
            }
            launchAndRepeatWithViewLifecycle {
                viewModel.isFavorite.collect {
                    gistDetailFavoriteButton.isFavorite = it
                }
            }

        }

    }


}