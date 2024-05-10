package org.d3if3074.assessment2.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.d3if3074.assessment2.database.MusicDao
import org.d3if3074.assessment2.model.Music
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailViewModel(private val dao: MusicDao) : ViewModel() {

    private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    fun insert(judul: String, penyanyi: String, genre: String, tahunTerbit: String) {
        val music = Music(
            tanggalTerbit = formatter.format(Date()),
            judul = judul,
            penyanyi = penyanyi,
            genre = genre,
            tahunTerbit = tahunTerbit
        )

        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(music)
        }
    }

    suspend fun getMusic(id: Long): Music? {
        return dao.getMusicById(id)
    }

    fun update(id: Long, judul: String, penyanyi: String, genre: String, tahunTerbit: String) {
        val music = Music(
            id = id,
            tanggalTerbit = formatter.format(Date()),
            judul = judul,
            penyanyi = penyanyi,
            genre = genre,
            tahunTerbit = tahunTerbit
        )

        viewModelScope.launch(Dispatchers.IO) {
            dao.update(music)
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteById(id)
        }
    }
}
