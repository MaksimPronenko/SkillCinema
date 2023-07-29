package edu.skillbox.skillcinema.models.filmFiltered

class PagedFilmFilteredList(
    val total: Int,
    val totalPages: Int,
    val items: List<FilmFiltered>
)