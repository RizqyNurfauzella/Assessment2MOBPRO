package org.d3if3074.assessment2.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.d3if3074.assessment2.model.Music

@Dao
interface MusicDao {

    @Insert
    suspend fun insert(music: Music)

    @Update
    suspend fun update(music: Music)

    @Query("SELECT * FROM music ORDER BY judul COLLATE NOCASE ASC")
    fun getAllMusic(): Flow<List<Music>>

    @Query("SELECT * FROM music WHERE id = :id")
    suspend fun getMusicById(id: Long): Music?

    @Query("DELETE FROM music WHERE id = :id")
    suspend fun deleteById(id: Long)
}
