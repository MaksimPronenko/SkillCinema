package edu.skillbox.skillcinema.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import edu.skillbox.skillcinema.App
import edu.skillbox.skillcinema.models.FilmFiltered
import javax.inject.Inject

//class FilmsFiltered2PagingSource @Inject constructor(val genre:Int, val country:Int) : PagingSource<Int, FilmFiltered>() {
class FilmsFiltered2PagingSource (private val application: App, dao: FilmDao) :
    PagingSource<Int, FilmFiltered>() {
    private val repository = Repository(dao)
    override fun getRefreshKey(state: PagingState<Int, FilmFiltered>): Int = FIRST_PAGE

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FilmFiltered> {
        val page = params.key ?: FIRST_PAGE
        return kotlin.runCatching {
            repository.getFilmFiltered(
                application.genre2key,
                application.country2key,
                page
            ).items.shuffled()
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