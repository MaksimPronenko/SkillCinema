package edu.skillbox.skillcinema.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import edu.skillbox.skillcinema.App
import edu.skillbox.skillcinema.models.filmAndSerial.film.FilmItemData

class SearchPagingSource (private val application: App, val repository: RepositoryMainLists) :
    PagingSource<Int, FilmItemData>() {
    override fun getRefreshKey(state: PagingState<Int, FilmItemData>): Int = FIRST_PAGE

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FilmItemData> {
        val page = params.key ?: FIRST_PAGE
        return kotlin.runCatching {
            repository.searchFilmsExtended(
                country = application.country,
                genre = application.genre,
                order = application.order,
                type = application.type,
                ratingFrom = application.ratingFrom,
                ratingTo = application.ratingTo,
                yearFrom = application.yearFrom,
                yearTo = application.yearTo,
                keyword = application.keyword,
                page = page
            ).first
        }.fold(
            onSuccess = {
                LoadResult.Page(
                    data = it.orEmpty(),
                    prevKey = null,
                    nextKey = if (it.isNullOrEmpty()) null else page + 1
                )
            },
            onFailure = { LoadResult.Error(it) }
        )
    }

    private companion object {
        private const val FIRST_PAGE = 1
    }
}