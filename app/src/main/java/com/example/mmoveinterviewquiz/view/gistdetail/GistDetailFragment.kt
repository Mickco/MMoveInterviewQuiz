package com.example.mmoveinterviewquiz.view.gistdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.mmoveinterviewquiz.databinding.FragmentGistDetailBinding
import com.example.mmoveinterviewquiz.view.common.BaseFragment

class GistDetailFragment: BaseFragment<FragmentGistDetailBinding>() {


    override fun onViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentGistDetailBinding {
        return FragmentGistDetailBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
        val navArgs = arguments?.let {
            GistDetailFragmentArgs.fromBundle(it)
        }

        with(binding) {
        }
    }

}