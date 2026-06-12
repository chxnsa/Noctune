# 🎵 NOCTUNE 

**Noctune** adalah aplikasi penjelajah musik berbasis Android. Aplikasi ini memungkinkan pengguna untuk memantau tangga lagu global, mengeksplorasi genre, mencari lirik, serta mengelola pustaka musik pribadi secara lokal.

---

## 🛠️ Implementasi Spesifikasi Teknis

Aplikasi ini dibangun untuk memenuhi standar kompetensi pemrograman mobile berikut:

### 1. Activity & Intent
- **Multi-Activity**: Terdiri dari `MainActivity`, `DetailActivity`, `ArtistDetailActivity`, `PlaylistDetailActivity`, dan `SplashActivity`.
- **Launcher**: `SplashActivity` bertindak sebagai pintu masuk utama yang mengarahkan ke `MainActivity`.
- **Komunikasi Data**: Menggunakan `Intent` (Explicit) untuk berpindah antar activity dengan menyertakan data *Parcelable* (objek Track) atau *Extra* (ID/Nama).

### 2. Fragment & Navigation
- **Modular UI**: Menggunakan 3 Fragment utama: `HomeFragment` (Top Charts), `ExploreFragment` (Genre Hub), dan `FavesFragment` (Personal Library).
- **Navigation Component**: Perpindahan antar fragment dikelola secara deklaratif melalui `nav_graph.xml` untuk memastikan manajemen *backstack* yang bersih.

### 3. RecyclerView
- **Daftar Dinamis**: Digunakan di hampir seluruh halaman untuk menampilkan daftar lagu, artis, playlist, dan hasil pencarian dengan *ViewHolder pattern* untuk performa yang optimal.

### 4. Background Thread (Concurrency)
- **Executor & Handler**: Operasi berat seperti akses database SQLite dan pengolahan data API dijalankan di *background thread* menggunakan `ExecutorService` untuk menjaga UI tetap responsif (bebas *lag*).

### 5. Networking (Retrofit)
- **Last.fm API Integration**: Mengambil data musik *real-time* menggunakan library **Retrofit**.
- **Error Handling**: Dilengkapi dengan logika deteksi jaringan. Jika koneksi terputus, aplikasi akan menampilkan tombol **[ RETRY ]** untuk menyegarkan data.

### 6. Local Data Persistent (SQLite)
- **Penyimpanan Lokal**: Menggunakan SQLite melalui `MusicHelper` untuk menyimpan daftar lagu favorit dan manajemen playlist buatan pengguna.
- **Offline Mode**: Pengguna tetap dapat melihat lagu yang sudah disimpan ke dalam "Favorites" atau "Playlist" meskipun perangkat tidak terhubung ke internet.

### 7. Adaptive Theme
- **Dark/Light Support**: Mendukung transisi tema gelap dan terang secara native tanpa merusak integritas desain brutalist.

---

## 🚀 Fitur Kreativitas & Inovasi
- **Brutalist UI/UX**: Penggunaan *thick borders*, warna kontras tinggi, dan tipografi monospaced untuk memberikan identitas visual yang unik.
- **Lyrics Integration**: Fitur penampil lirik otomatis di halaman detail lagu.
- **Custom Confirmation Dialog**: Dialog konfirmasi kustom "Purge Data" untuk mencegah penghapusan playlist secara tidak sengaja.
- **Smart Validation**: Sistem proteksi agar lagu yang sama tidak terduplikasi di dalam satu playlist.

---

## 📦 Pustaka Pihak Ketiga (Libraries)
- **Retrofit & GSON**: Untuk konsumsi REST API.
- **Picasso**: Untuk manajemen *caching* dan pemuatan gambar secara asinkron.
- **Navigation UI**: Untuk manajemen fragment.
- **Material Design**: Untuk komponen UI dasar.

---

**Dibuat oleh:**  
👤 **ANDI KHAERUNNISA ODDANG**  
🆔 **H071241068**  
🎓 **Universitas Hasanuddin — Pemrograman Mobile (Final Lab)**ersitas Hasanuddin — Pemrograman Mobile (Final Lab)