package org.d3if3074.assessment2.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "music")
data class Music(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val tanggalTerbit: String,
    val judul: String,
    val penyanyi: String,
    val genre: String,
    val tahunTerbit: String
)
