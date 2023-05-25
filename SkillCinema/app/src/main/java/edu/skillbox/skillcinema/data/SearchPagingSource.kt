package edu.skillbox.skillcinema.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import edu.skillbox.skillcinema.App
import edu.skillbox.skillcinema.models.FilmFiltered

class SearchPagingSource (private val application: App, dao: FilmDao) :
    PagingSource<Int, FilmFiltered>() {
    private val repository = Repository(dao)
    override fun getRefreshKey(state: PagingState<Int, FilmFiltered>): Int = FIRST_PAGE

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FilmFiltered> {
        val page = params.key ?: FIRST_PAGE
        return kotlin.runCatching {
            repository.searchFilms(
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
            ).items
        }.fold(
            onSuccess = {
                LoadResult.Page(
                    data = it,
                    prevKey = null,
                    nextKey = if (it.isEmpty()) null else page + 1
                )
            },
            onFailure = { LoadResult.Error(it) }
        )
    }

    private companion object {
        private const val FIRST_PAGE = 1
    }
}