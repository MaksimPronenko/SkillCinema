package edu.skillbox.skillcinema.models.collection

import edu.skillbox.skillcinema.models.filmAndSerial.film.InterestedViewItem

data class Interested(
    val type: Int,
    val interestedViewItem: InterestedViewItem
) : RecyclerViewItem()