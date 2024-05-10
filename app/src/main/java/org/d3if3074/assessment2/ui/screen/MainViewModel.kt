package org.d3if3074.assessment2.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.d3if3074.assessment2.database.MusicDao
import org.d3if3074.assessment2.model.Music

class MainViewModel(dao: MusicDao) : ViewModel() {

    val data: StateFlow<List<Music>> = dao.getAllMusic().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )
}
