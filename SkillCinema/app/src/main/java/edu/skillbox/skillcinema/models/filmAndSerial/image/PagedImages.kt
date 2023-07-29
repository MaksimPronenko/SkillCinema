package edu.skillbox.skillcinema.models.filmAndSerial.image

data class PagedImages(
    val total: Int,
    val totalPages: Int,
    val items: List<Image>
)
