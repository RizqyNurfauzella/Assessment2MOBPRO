package org.d3if3074.assessment2.ui.screen

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.d3if3074.assessment2.R
import org.d3if3074.assessment2.database.MusicDb
import org.d3if3074.assessment2.ui.theme.Assessment2Theme
import org.d3if3074.assessment2.util.ViewModelFactory

const val KEY_ID_MUSIC = "idMusic"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController: NavHostController, id: Long? = null) {
    val context = LocalContext.current
    val db = MusicDb.getInstance(context)
    val factory = ViewModelFactory(db.dao)
    val viewModel: DetailViewModel = viewModel(factory = factory)

    var judul by remember { mutableStateOf("") }
    var penyanyi by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var tahunTerbit by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        if (id == null) return@LaunchedEffect
        val data = viewModel.getMusic(id) ?: return@LaunchedEffect
        judul = data.judul
        penyanyi = data.penyanyi
        genre = data.genre
        tahunTerbit = data.tahunTerbit
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.kembali),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                title = {
                    if (id == null)
                        Text(text = stringResource(id = R.string.tambah_lagu))
                    else
                        Text(text = stringResource(id = R.string.edit_lagu))
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    IconButton(onClick = {
                        if (judul == "" || penyanyi == "" || genre == "" || tahunTerbit == "") {
                            Toast.makeText(context, R.string.invalid, Toast.LENGTH_LONG).show()
                            return@IconButton
                        }

                        if (id == null) {
                            viewModel.insert(judul, penyanyi, genre, tahunTerbit)
                        } else {
                            viewModel.update(id, judul, penyanyi, genre, tahunTerbit)
                        }
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = stringResource(R.string.simpan),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (id != null) {
                        DeleteAction { showDialog = true }
                        DisplayAlertDialog(
                            openDialog = showDialog,
                            onDismissRequest = { showDialog = false }) {
                            showDialog = false
                            viewModel.delete(id)
                            navController.popBackStack()
                        }
                    }
                }
            )
        }
    ) { padding ->
        FormMusic(
            judul = judul,
            onJudulChange = { judul = it },
            penyanyi = penyanyi,
            onPenyanyiChange = { penyanyi = it },
            genre = genre,
            onGenreChange = { genre = it },
            tahunTerbit = tahunTerbit,
            onTahunTerbitChange = { tahunTerbit = it },
            modifier = Modifier.padding(padding),
        )
    }
}

@Composable
fun DeleteAction(delete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = stringResource(R.string.lainnya),
            tint = MaterialTheme.colorScheme.primary
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(id = R.string.hapus))
                },
                onClick = {
                    expanded = false
                    delete()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormMusic(
    judul: String, onJudulChange: (String) -> Unit,
    penyanyi: String, onPenyanyiChange: (String) -> Unit,
    genre: String, onGenreChange: (String) -> Unit,
    tahunTerbit: String, onTahunTerbitChange: (String) -> Unit,
    modifier: Modifier
) {
    // Buat daftar tahun dari 2000 hingga 2024
    val years = remember {
        (2000..2024).map { it.toString() }
    }

    var expanded by remember { mutableStateOf(false) }

    val genreOptions = listOf(
        "Musik Klasik",
        "Jazz",
        "Rock",
        "Pop",
        "Reggae",
        "Hip Hop"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = judul,
            onValueChange = { onJudulChange(it) },
            label = { Text(text = stringResource(R.string.judul_lagu)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = penyanyi,
            onValueChange = { onPenyanyiChange(it) },
            label = { Text(text = stringResource(R.string.penyanyi)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Column(
            modifier = Modifier.border(1.dp, Color.Gray, shape = RoundedCornerShape(6.dp)).padding(8.dp)
        ) {
            Text(text = stringResource(id = R.string.genre))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.height(150.dp),
            ) {
                items(genreOptions) { genreSelected ->
                    RadioOption(
                        label = genreSelected,
                        isSelected = genre == genreSelected,
                        onSelected = { onGenreChange(it) },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            label = { Text(text = stringResource(R.string.tahun_terbit)) },
            readOnly = true,
            value = tahunTerbit,
            onValueChange = {},
            trailingIcon = { TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                unfocusedContainerColor = Color.White,
            ),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            years.forEach { pay ->
                DropdownMenuItem(
                    text = { Text(pay) },
                    onClick = {
                        onTahunTerbitChange(pay)
                        expanded = false
                    },
                )
            }
        }
    }
}
}

@Composable
fun RadioOption(label: String, isSelected: Boolean, onSelected: (String) -> Unit, modifier: Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onSelected(label) },
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.inversePrimary,
                unselectedColor = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}


@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun DetailScreenPreview() {
    Assessment2Theme {
        DetailScreen(rememberNavController())
    }
}