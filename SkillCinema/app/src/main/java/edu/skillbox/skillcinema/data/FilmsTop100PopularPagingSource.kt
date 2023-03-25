package edu.skillbox.skillcinema.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import edu.skillbox.skillcinema.models.FilmTop

class FilmsTop100PopularPagingSource: PagingSource<Int, FilmTop>() {
    private val repository = Repository()
    override fun getRefreshKey(state: PagingState<Int, FilmTop>): Int = FIRST_PAGE

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FilmTop> {
        val page = params.key ?: FIRST_PAGE
        return kotlin.runCatching {
            repository.getTop100Popular(page).films.shuffled()
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