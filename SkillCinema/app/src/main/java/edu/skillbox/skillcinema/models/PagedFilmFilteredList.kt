package edu.skillbox.skillcinema.models

class PagedFilmFilteredList(
    val total: Int,
    val totalPages: Int,
    val items: List<FilmFiltered>
)