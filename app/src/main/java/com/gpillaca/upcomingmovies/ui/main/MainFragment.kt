package com.gpillaca.upcomingmovies.ui.main

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.gpillaca.upcomingmovies.R
import com.gpillaca.upcomingmovies.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {

    private lateinit var binding: FragmentMainBinding

    private val mainViewModel: MainViewModel by viewModels()

    private val adapter = MoviesAdapter { movie ->
        mainState.onMovieClick(movie.id)
    }

    private lateinit var mainState: MainState

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(view)

        mainState = buildMainState()
        binding.recyclerMovies.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.state.map { it.loading }.distinctUntilChanged().collect { isLoading ->
                    binding.progress.isVisible = isLoading
                    binding.swipeRefresh.isRefreshing = isLoading
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.state.map { state -> state.movies }.distinctUntilChanged()
                    .collect { movies ->
                        adapter.submitList(movies)
                    }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.state.collect { error ->
                    error.error?.let { errorMsg ->
                        Toast.makeText(
                            requireActivity(),
                            mainState.errorToString(errorMsg),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        mainState.requestLocationPermissions {
            mainViewModel.onUiReady()
        }

        binding.swipeRefresh.setOnRefreshListener {
            mainViewModel.requestPopularMovies()
        }

        setupProfileImageClickListener()
    }

    private fun setupProfileImageClickListener() {
        val profileImageView: ImageView = binding.toolbar.findViewById(R.id.profile_image)
        profileImageView.setOnClickListener {
            val action = MainFragmentDirections.actionMainToProfileFragment()
            findNavController().navigate(action)
        }
    }
}