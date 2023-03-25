package edu.skillbox.skillcinema.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import edu.skillbox.skillcinema.models.FilmFiltered

class SeriesPagingSource: PagingSource<Int, FilmFiltered>() {
    private val repository = Repository()
    override fun getRefreshKey(state: PagingState<Int, FilmFiltered>): Int = FIRST_PAGE

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FilmFiltered> {
        val page = params.key ?: FIRST_PAGE
        return kotlin.runCatching {
            repository.getSeries(page).items.shuffled()
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