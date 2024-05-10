package org.d3if3074.assessment2.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.d3if3074.assessment2.R
import org.d3if3074.assessment2.database.MusicDb
import org.d3if3074.assessment2.model.Music
import org.d3if3074.assessment2.navigation.Screen
import org.d3if3074.assessment2.ui.theme.Assessment2Theme
import org.d3if3074.assessment2.util.SettingsDataStore
import org.d3if3074.assessment2.util.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    val dataStore = SettingsDataStore(LocalContext.current)
    val db = MusicDb.getInstance(LocalContext.current)
    val factory = ViewModelFactory(db.dao)
    val viewModel: MainViewModel = viewModel(factory = factory)
    val data by viewModel.data.collectAsState()

    val showList by dataStore.layoutFlow.collectAsState(initial = true)

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = null, modifier = Modifier.size(55.dp))
                        Spacer(modifier = Modifier.width(68.dp))
                        Image(
                            painter = painterResource(id = R.drawable.text),
                            contentDescription = null, modifier = Modifier.size(120.dp))
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                dataStore.saveLayout(!showList)
                            }
                        }) {
                            Icon(
                                painter = painterResource(
                                    if (showList) R.drawable.baseline_grid_view_24
                                    else R.drawable.baseline_view_list_24
                                ),
                                contentDescription = stringResource(
                                    if (showList) R.string.grid
                                    else R.string.list
                                ),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.width(1.dp))

                        IconButton(onClick = {
                            navController.navigate(Screen.About.route)
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = stringResource(id = R.string.tentang_aplikasi),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Screen.FormBaru.route)
            }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.tambah_lagu),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) { padding ->
        ScreenContent(showList, data, Modifier.padding(padding), navController)
    }
}

@Composable
fun ScreenContent(showList: Boolean,data: List<Music>, modifier: Modifier, navController: NavHostController) {
    if (data.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painter= painterResource(
                id = R.drawable.baseline_music_off_24) ,
                contentDescription = stringResource(id = R.string.list_kosong),
                modifier = Modifier.aspectRatio(5f))
            Text(text = stringResource(id = R.string.list_kosong))
        }
    }
    else {
        if (showList) {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 84.dp)
            ) {
                items(data) {
                    ListItem(music = it) {
                        navController.navigate(Screen.FormUbah.withId(it.id))
                    }
                    Divider()
                }
            }
        }
        else {
            LazyVerticalStaggeredGrid(
                modifier = modifier.fillMaxSize(),
                columns = StaggeredGridCells.Fixed(2),
                verticalItemSpacing = 8.dp,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 84.dp)
            ) {
                items(data) {
                    GridItem(music = it) {
                        navController.navigate(Screen.FormUbah.withId(it.id))
                    }
                }
            }
        }
    }
}

@Composable
fun ListItem(music: Music, onClick: () -> Unit ) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row {
            Image(painter= painterResource(
                id = R.drawable.baseline_music_video_24) ,
                contentDescription = stringResource(id = R.string.list_lagu),
                modifier = Modifier)
                Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = music.judul,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = "${stringResource(id = R.string.penyanyi)}: ${music.penyanyi}",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "${stringResource(id = R.string.genre)}: ${music.genre}",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "${stringResource(id = R.string.tahun_terbit)}: ${music.tahunTerbit}",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun GridItem(music: Music, onClick: () -> Unit ) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row {
                Image(painter= painterResource(
                    id = R.drawable.baseline_music_video_24) ,
                    contentDescription = stringResource(id = R.string.list_lagu),
                    modifier = Modifier)
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = music.judul,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "${stringResource(id = R.string.penyanyi)}: ${music.penyanyi}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${stringResource(id = R.string.genre)}: ${music.genre}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${stringResource(id = R.string.tahun_terbit)}: ${music.tahunTerbit}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun MainScreenPreview() {
    Assessment2Theme {
        MainScreen(rememberNavController())
    }
}