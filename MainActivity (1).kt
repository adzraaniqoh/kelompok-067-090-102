import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ===== Palet warna baru (biru) - kontras teks >= 4.5:1 =====
val Primary = Color(0xFF0D47A1)            // biru tua, teks putih di atasnya kontras tinggi
val PrimaryContainer = Color(0xFFD6E6FA)   // biru muda, teks Primary/OnSurface tetap terbaca
val OnSurface = Color(0xFF111827)
val TextSecondary = Color(0xFF475569)      // pengganti Color.Gray (abu-abu lama kontras rendah)
val BackgroundColor = Color(0xFFF1F5FA)
val Success = Color(0xFF0277BD)            // biru langit tua
val Warning = Color(0xFFA16207)
val ErrorColor = Color(0xFFBE123C)

// Lebar minimum tiap kartu pada grid adaptif
val GridMinSize = 260.dp

// ===== Model data =====
data class Catatan(
    val judul: String,
    val deskripsi: String,
    val tanggal: String,
    val ikon: ImageVector,
    val warnaIkon: Color
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Primary,
                    primaryContainer = PrimaryContainer,
                    onSurface = OnSurface,
                    background = BackgroundColor
                )
            ) {
                AppRoot()
            }
        }
    }
}

// ===== Pengatur halaman: login dulu, baru halaman utama =====
@Composable
fun AppRoot() {
    var namaUser by rememberSaveable { mutableStateOf<String?>(null) }
    val nama = namaUser
    if (nama == null) {
        LoginScreen(onLogin = { namaUser = it })
    } else {
        KampusKuScreen(nama = nama, onLogout = { namaUser = null })
    }
}

// ===== Halaman login =====
@Composable
fun LoginScreen(onLogin: (String) -> Unit) {
    var nama by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var tampilPassword by remember { mutableStateOf(false) }
    var errorNama by remember { mutableStateOf(false) }
    var errorPassword by remember { mutableStateOf(false) }

    fun coba() {
        errorNama = nama.isBlank()
        errorPassword = password.length < 4
        if (!errorNama && !errorPassword) onLogin(nama.trim())
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // widthIn(max) supaya form tidak melebar berlebihan di tablet
        Column(
            modifier = Modifier
                .widthIn(max = 420.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .background(Primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.School,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
            Text("KampusKu", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Primary)
            Text("Masuk untuk melanjutkan", fontSize = 14.sp, color = TextSecondary)
            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    OutlinedTextField(
                        value = nama,
                        onValueChange = { nama = it; errorNama = false },
                        label = { Text("Nama") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        isError = errorNama,
                        supportingText = { if (errorNama) Text("Nama tidak boleh kosong") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorPassword = false },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { tampilPassword = !tampilPassword }) {
                                Icon(
                                    if (tampilPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Tampilkan password"
                                )
                            }
                        },
                        visualTransformation = if (tampilPassword) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        singleLine = true,
                        isError = errorPassword,
                        supportingText = { if (errorPassword) Text("Minimal 4 karakter") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { coba() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Masuk", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Gunakan nama dan password apa saja (min. 4 karakter)",
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KampusKuScreen(nama: String, onLogout: () -> Unit) {
    // ===== State =====
    val daftar = remember {
        mutableStateListOf(
            Catatan("Pemrograman Mobile", "Latihan Jetpack Compose", "10 Okt 2026", Icons.Default.MenuBook, Primary),
            Catatan("Basis Data", "Normalisasi dan ERD", "8 Okt 2026", Icons.Default.Storage, BiruMuda),
            Catatan("Struktur Data", "Array dan List", "5 Okt 2026", Icons.Default.BarChart, Warning),
            Catatan("Pemrograman Web", "Studi kasus CMS", "1 Okt 2026", Icons.Default.Article, ErrorColor),
            // ===== Kartu mata kuliah tambahan (poin 1) =====
            Catatan(
                "Jaringan Komputer",
                "Konfigurasi subnetting, VLAN, dan routing statis pada topologi kampus beserta analisis kinerja jaringan menggunakan Cisco Packet Tracer",
                "12 Okt 2026",
                Icons.Default.Wifi,
                Color(0xFF1565C0)
            ),
            Catatan(
                "Rekayasa Perangkat Lunak",
                "Menyusun dokumen kebutuhan, use case diagram, dan rencana pengujian untuk proyek aplikasi catatan mahasiswa",
                "11 Okt 2026",
                Icons.Default.Code,
                Color(0xFF86198F)
            ),
            Catatan(
                "Interaksi Manusia dan Komputer",
                "Evaluasi usability dan heuristik Nielsen",
                "9 Okt 2026",
                Icons.Default.Devices,
                Color(0xFF9D174D)
            )
        )
    }
    var tampilSemua by remember { mutableStateOf(false) }
    var menuTerbuka by remember { mutableStateOf(false) }
    var modeCari by remember { mutableStateOf(false) }
    var kataCari by remember { mutableStateOf("") }
    var dialogTambah by remember { mutableStateOf(false) }
    var catatanDiedit by remember { mutableStateOf<Catatan?>(null) }
    var dialogProfil by remember { mutableStateOf(false) }
    var catatanDipilih by remember { mutableStateOf<Catatan?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun pesan(teks: String) {
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(teks)
        }
    }

    fun hapus(catatan: Catatan) {
        val posisi = daftar.indexOf(catatan)
        if (posisi < 0) return
        daftar.removeAt(posisi)
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            val hasil = snackbarHostState.showSnackbar(
                message = "Catatan dihapus",
                actionLabel = "Urungkan"
            )
            if (hasil == SnackbarResult.ActionPerformed) {
                daftar.add(posisi.coerceAtMost(daftar.size), catatan)
            }
        }
    }

    val hasilCari = daftar.filter {
        it.judul.contains(kataCari, ignoreCase = true) ||
                it.deskripsi.contains(kataCari, ignoreCase = true)
    }
    // Default menampilkan 6 kartu (kelipatan 2 dan 3 kolom), sisanya lewat "Lihat Semua"
    val tampil = if (tampilSemua || modeCari) hasilCari else hasilCari.take(6)

    Scaffold(
        containerColor = BackgroundColor,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                navigationIcon = {
                    if (modeCari) {
                        IconButton(onClick = { modeCari = false; kataCari = "" }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Tutup pencarian")
                        }
                    } else {
                        Box {
                            IconButton(onClick = { menuTerbuka = true }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                            DropdownMenu(
                                expanded = menuTerbuka,
                                onDismissRequest = { menuTerbuka = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Lihat semua catatan") },
                                    leadingIcon = { Icon(Icons.Default.List, contentDescription = null) },
                                    onClick = { tampilSemua = true; menuTerbuka = false }
                                )
                                DropdownMenuItem(
                                    text = { Text("Hapus semua catatan") },
                                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                                    onClick = {
                                        daftar.clear()
                                        menuTerbuka = false
                                        pesan("Semua catatan dihapus")
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Keluar") },
                                    leadingIcon = { Icon(Icons.Default.Logout, contentDescription = null) },
                                    onClick = { menuTerbuka = false; onLogout() }
                                )
                            }
                        }
                    }
                },
                title = {
                    if (modeCari) {
                        TextField(
                            value = kataCari,
                            onValueChange = { kataCari = it },
                            singleLine = true,
                            placeholder = { Text("Cari catatan...", color = Color.White.copy(alpha = 0.85f)) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    } else {
                        Column {
                            Text("KampusKu", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Catatan Mahasiswa", fontSize = 12.sp)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (modeCari) kataCari = "" else modeCari = true
                    }) {
                        Icon(
                            if (modeCari) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Cari"
                        )
                    }
                    IconButton(onClick = { dialogProfil = true }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profil")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { dialogTambah = true },
                containerColor = Primary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah catatan")
            }
        }
    ) { padding ->
        // ===== Grid adaptif (poin 3): jumlah kolom menyesuaikan lebar layar =====
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = GridMinSize),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!modeCari) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    HeaderCard(nama = nama, onTulis = { dialogTambah = true })
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (modeCari) "Hasil Pencarian" else "Catatan Terbaru",
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                    if (!modeCari) {
                        TextButton(onClick = { tampilSemua = !tampilSemua }) {
                            Text(if (tampilSemua) "Ringkas" else "Lihat Semua", color = Primary)
                        }
                    }
                }
            }

            if (tampil.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        "Tidak ada catatan",
                        color = TextSecondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    )
                }
            }

            items(tampil) { catatan ->
                CatatanItem(
                    catatan = catatan,
                    onClick = { catatanDipilih = catatan },
                    onEdit = { catatanDiedit = catatan },
                    onDelete = { hapus(catatan) }
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) { Spacer(Modifier.height(72.dp)) }
        }
    }

    // ===== Dialog tambah catatan =====
    if (dialogTambah) {
        DialogCatatan(
            judulDialog = "Catatan Baru",
            judulAwal = "",
            deskripsiAwal = "",
            onDismiss = { dialogTambah = false },
            onSimpan = { judul, deskripsi ->
                val tanggal = SimpleDateFormat("d MMM yyyy", Locale("id", "ID")).format(Date())
                daftar.add(0, Catatan(judul, deskripsi, tanggal, Icons.Default.Edit, Primary))
                dialogTambah = false
                pesan("Catatan berhasil ditambahkan")
            }
        )
    }

    // ===== Dialog edit catatan (Update) =====
    catatanDiedit?.let { lama ->
        DialogCatatan(
            judulDialog = "Edit Catatan",
            judulAwal = lama.judul,
            deskripsiAwal = lama.deskripsi,
            onDismiss = { catatanDiedit = null },
            onSimpan = { judul, deskripsi ->
                val posisi = daftar.indexOf(lama)
                if (posisi >= 0) {
                    daftar[posisi] = lama.copy(judul = judul, deskripsi = deskripsi)
                }
                catatanDiedit = null
                pesan("Catatan berhasil diperbarui")
            }
        )
    }

    // ===== Dialog detail catatan =====
    catatanDipilih?.let { c ->
        AlertDialog(
            onDismissRequest = { catatanDipilih = null },
            icon = { Icon(c.ikon, contentDescription = null, tint = c.warnaIkon) },
            title = { Text(c.judul) },
            text = {
                Column {
                    Text(c.deskripsi)
                    Spacer(Modifier.height(8.dp))
                    Text(c.tanggal, fontSize = 12.sp, color = TextSecondary)
                }
            },
            confirmButton = {
                Row {
                    TextButton(onClick = {
                        catatanDiedit = c
                        catatanDipilih = null
                    }) { Text("Edit") }
                    TextButton(onClick = { catatanDipilih = null }) { Text("Tutup") }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    hapus(c)
                    catatanDipilih = null
                }) { Text("Hapus", color = ErrorColor) }
            }
        )
    }

    // ===== Dialog profil =====
    if (dialogProfil) {
        AlertDialog(
            onDismissRequest = { dialogProfil = false },
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Primary) },
            title = { Text(nama) },
            text = { Text("Jumlah catatan: ${daftar.size}") },
            confirmButton = {
                TextButton(onClick = { dialogProfil = false }) { Text("Tutup") }
            },
            dismissButton = {
                TextButton(onClick = {
                    dialogProfil = false
                    onLogout()
                }) { Text("Keluar", color = ErrorColor) }
            }
        )
    }
}

@Composable
fun DialogCatatan(
    judulDialog: String,
    judulAwal: String,
    deskripsiAwal: String,
    onDismiss: () -> Unit,
    onSimpan: (String, String) -> Unit
) {
    var judul by remember { mutableStateOf(judulAwal) }
    var deskripsi by remember { mutableStateOf(deskripsiAwal) }
    var salah by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(judulDialog) },
        text = {
            Column {
                OutlinedTextField(
                    value = judul,
                    onValueChange = { judul = it; salah = false },
                    label = { Text("Judul / Mata Kuliah") },
                    singleLine = true,
                    isError = salah,
                    supportingText = { if (salah) Text("Judul tidak boleh kosong") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    label = { Text("Deskripsi") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (judul.isBlank()) salah = true
                else onSimpan(judul.trim(), deskripsi.trim().ifEmpty { "Tanpa deskripsi" })
            }) { Text("Simpan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
fun HeaderCard(nama: String, onTulis: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryContainer)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Halo, $nama!", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Primary)
                Spacer(Modifier.height(4.dp))
                Text("Mari catat semua aktivitas perkuliahanmu", fontSize = 13.sp, color = OnSurface)
                Spacer(Modifier.height(8.dp))
                Button(onClick = onTulis) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Tulis Catatan")
                }
            }
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(Primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.School,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

// Tombol aksi dipindah ke baris bawah supaya teks tetap punya ruang lebar (poin 4)
@Composable
fun CatatanItem(
    catatan: Catatan,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(catatan.warnaIkon.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(catatan.ikon, contentDescription = null, tint = catatan.warnaIkon)
                }
                Spacer(Modifier.width(12.dp))
                // Tanpa maxLines/overflow: teks panjang otomatis turun ke baris berikutnya
                Column(modifier = Modifier.weight(1f)) {
                    Text(catatan.judul, fontWeight = FontWeight.SemiBold, color = OnSurface)
                    Spacer(Modifier.height(2.dp))
                    Text(catatan.deskripsi, fontSize = 13.sp, color = TextSecondary)
                    Spacer(Modifier.height(2.dp))
                    Text(catatan.tanggal, fontSize = 12.sp, color = TextSecondary)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = ErrorColor)
                }
            }
        }
    }
}


