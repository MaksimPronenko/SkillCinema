package edu.skillbox.skillcinema.models.filmAndSerial.film

data class FilmDbViewed(
    val filmDb: FilmDb,
    val viewed: Boolean
) {
    fun convertToFilmItemData(): FilmItemData = FilmItemData(
        filmId = filmDb.filmTable.filmId,
        name = filmDb.filmTable.name,
        year = if (filmDb.filmTable.year == null) null else filmDb.filmTable.year.toString(),
        genres = filmDb.genres.joinToString(", ") { it.genre },
        poster = filmDb.filmTable.posterSmall,
        rating = if (filmDb.filmTable.rating == null) null else filmDb.filmTable.rating.toString(),
        viewed = viewed
    )
}