package edu.skillbox.skillcinema.models

data class Interested(
    val type: Int,
    val interestedViewItem: InterestedViewItem
) : RecyclerViewItem()