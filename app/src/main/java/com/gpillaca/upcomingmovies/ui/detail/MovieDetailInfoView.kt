package com.gpillaca.upcomingmovies.ui.detail

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import com.gpillaca.upcomingmovies.domain.Movie

class MovieDetailInfoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    fun setMovieInfo(movie: Movie) {
        with(movie) {
           text = buildSpannedString {
               bold { append("Original language: ")}
               appendLine(originalLanguage)

               bold { append("Original title: ")}
               appendLine(originalTitle)

               bold { append("Original date: ")}
               appendLine(releaseDate)

               bold { append("Popularity: ")}
               appendLine(popularity.toString())

               bold { append("Vote average: ")}
               append(voteAverage.toString())
           }
        }
    }
}
