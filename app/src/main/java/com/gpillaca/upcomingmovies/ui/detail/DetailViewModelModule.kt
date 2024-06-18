package com.gpillaca.upcomingmovies.ui.detail

import androidx.lifecycle.SavedStateHandle
import com.gpillaca.upcomingmovies.di.MovieId
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class DetailViewModelModule {

    @Provides
    @ViewModelScoped
    @MovieId
    fun provideMovieId(savedStateHandle: SavedStateHandle) =
        MovieDetailFragmentArgs.fromSavedStateHandle(savedStateHandle).movieId

}
