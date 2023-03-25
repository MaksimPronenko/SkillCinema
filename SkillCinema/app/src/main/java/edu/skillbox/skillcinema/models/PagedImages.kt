package edu.skillbox.skillcinema.models

data class PagedImages(
    val total: Int,
    val totalPages: Int,
    val items: List<Image>
)
