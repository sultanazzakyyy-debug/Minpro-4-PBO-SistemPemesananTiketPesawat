# Minpro 4 PBO - Sistem Pemesanan Tiket Pesawat

Nama : Muhammad Nadhir Sultan Azzaky

NIM : 2509116080

Kelas : Sistem Informasi 25'B

## 1. Deskripsi Singkat Program

Program ini adalah lanjutan dari Mini Project 3, masih berupa aplikasi berbasis *console* memakai Java untuk mengelola pemesanan tiket pesawat. Perbedaan terbesarnya: data sekarang **tidak lagi disimpan di `ArrayList`**, tapi di database MySQL (`db_tiket_pesawat`) yang diakses lewat **JDBC** memakai `PreparedStatement` dan pola **DAO** (*Data Access Object*). Jadi data tidak hilang lagi setiap program ditutup.

Data awal (2 penerbangan, 2 penumpang, dan 2 tiket) tidak lagi diisi lewat kode (`isiDataAwal()` sudah dihapus), tapi lewat file **`database/db_tiket_pesawat.sql`**. Begitu file itu di-import, menu "Lihat Jadwal Penerbangan" dan "Lihat Semua Tiket" langsung menampilkan data tanpa harus input dari awal.

Sebagai nilai tambah, program juga menerapkan **framework ORM (Hibernate)**. Saat program dijalankan, user bisa memilih cara akses database: **1. JDBC** atau **2. ORM (Hibernate)**. Keduanya mengakses database dan tabel yang sama, dan seluruh fitur (tambah penerbangan, pesan tiket, update status, batalkan tiket) berjalan sama persis di kedua mode.

Fitur yang tersedia: tambah data penerbangan, lihat jadwal penerbangan, pesan tiket (pilihan kelas Ekonomi/Bisnis), lihat seluruh tiket, ubah status tiket, dan batalkan tiket. Semua perbaikan dari Mini Project 3 (validasi input, ID tiket otomatis, contoh format di tiap prompt) tetap dipertahankan.

## 2. Penjelasan Struktur Package

```text
Minpro-4-PBO-SistemPemesananTiketPesawat
├── database/
│   └── db_tiket_pesawat.sql                  -> struktur tabel + dummy data awal
├── pom.xml                                   -> dependency: mysql-connector-j & hibernate-core
└── src/main/java/
    ├── SistemPemesananTiketPesawat.java                             -> titik masuk aplikasi
    ├── config/
    │   ├── Koneksi.java                      -> pengaturan koneksi database
    │   └── HibernateUtil.java                -> menyiapkan Hibernate (SessionFactory)
    ├── controller/
    │   └── Controller.java                   -> alur menu, validasi input, memanggil DAO dan View
    ├── dao/
    │   ├── PenerbanganDAO.java               -> interface DAO penerbangan
    │   ├── TiketDAO.java                     -> interface DAO tiket
    │   ├── PenerbanganDAOJdbc.java           -> implementasi DAO memakai JDBC
    │   ├── TiketDAOJdbc.java
    │   ├── PenerbanganDAOHibernate.java      -> implementasi DAO memakai Hibernate (ORM)
    │   └── TiketDAOHibernate.java
    ├── model/
    │   ├── Penerbangan.java
    │   ├── Penumpang.java
    │   ├── Tiket.java                        -> abstract class
    │   ├── TiketEkonomi.java
    │   ├── TiketBisnis.java
    │   └── Cetak.java                        -> interface
    ├── util/
    │   └── DataAccessException.java          -> pembungkus error database
    └── view/
        └── View.java                         -> semua tampilan ke layar
```

| Class / Interface | Package | Peran |
|---|---|---|
| `Penumpang` | `model` | Menyimpan data diri penumpang (juga di-*mapping* ke tabel `penumpang`) |
| `Penerbangan` | `model` | Menyimpan data jadwal, harga, dan kursi (di-*mapping* ke tabel `penerbangan`) |
| `Tiket` | `model` | *Abstract class* induk tiket, menggabungkan penumpang dan penerbangan (di-*mapping* ke tabel `tiket`) |
| `TiketEkonomi`, `TiketBisnis` | `model` | Subclass dari `Tiket` untuk tiap kelas kursi |
| `Cetak` | `model` | Interface yang di-*implements* oleh `Tiket` |
| `View` | `view` | Menampilkan menu, daftar, dan pesan ke layar |
| `Controller` | `controller` | Mengatur alur menu, validasi input, dan memanggil DAO |
| `PenerbanganDAO`, `TiketDAO` | `dao` | Interface yang menentukan operasi apa saja yang bisa dilakukan ke database |
| `DataAccessException` | `util` | Exception pembungkus error database (`SQLException` / `HibernateException`) |
| `PenerbanganDAOJdbc`, `TiketDAOJdbc` | `dao` | Implementasi operasi database memakai JDBC + `PreparedStatement` |
| `PenerbanganDAOHibernate`, `TiketDAOHibernate` | `dao` | Implementasi operasi database memakai Hibernate (ORM) |
| `Koneksi` | `config` | Menyimpan URL, user, password database dan membuat koneksi JDBC |
| `HibernateUtil` | `config` | Membangun `SessionFactory` Hibernate |
| `SistemPemesananTiketPesawat` | (root) | Menjalankan program |

Pembagian MVC-nya tetap sama: **Model** (`model/`), **View** (`view/`), dan **Controller** (`controller/`), dengan tambahan lapisan **DAO** (`dao/`) yang khusus mengurus komunikasi ke database sehingga `Controller` tidak perlu menulis query SQL sama sekali.

### Struktur Database

Database `db_tiket_pesawat` punya 3 tabel:

| Tabel | Kolom | Keterangan |
|---|---|---|
| `penerbangan` | `kode_penerbangan` (PK), `asal`, `tujuan`, `jam_keberangkatan`, `harga`, `kursi_tersedia` | Data jadwal penerbangan |
| `penumpang` | `id_penumpang` (PK, auto increment), `nama`, `no_ktp`, `no_telepon` | Data penumpang, satu baris dibuat setiap pemesanan |
| `tiket` | `id_tiket` (PK, auto increment mulai 1001), `id_penumpang` (FK), `kode_penerbangan` (FK), `kelas`, `status_tiket` | Data tiket, kolom `kelas` berisi `EKONOMI` atau `BISNIS` |

Kolom `id_tiket` dibuat otomatis oleh database dan ditampilkan di program dengan awalan `TKT-` (misalnya `TKT-1001`).

## 3. Penjelasan Alur Program

Program dimulai dari `Main.java` yang hanya memanggil satu method, `new Controller().jalankanAplikasi()`. Dari sana alurnya:

1. **Pilih cara akses database** — user memilih `1` (JDBC) atau `2` (ORM/Hibernate). Pilihan ini menentukan class DAO mana yang dipakai.
2. **Cek koneksi** — program mencoba mengambil data penerbangan lewat DAO. Kalau berhasil muncul "Berhasil terhubung ke database."; kalau gagal (misalnya MySQL belum menyala atau file `.sql` belum di-import), program menampilkan pesan `[ERROR]` yang jelas lalu berhenti.
3. **Menu utama** tampil berulang dengan 7 pilihan sampai user memilih keluar. Error dari database di tengah jalan (misalnya koneksi putus) juga ditangkap dan hanya menampilkan pesan `[ERROR]` tanpa membuat program berhenti.

<img width="257" height="179" alt="Awala" src="https://github.com/user-attachments/assets/a5cd530c-744e-4ce0-9f04-b215a6814f53" />

1. **Tambah Data Penerbangan**

   User memasukkan kode penerbangan, kota asal, kota tujuan, jam keberangkatan, harga dasar, dan jumlah kursi. Semua input divalidasi dulu (kode tidak boleh kembar — dicek langsung ke database lewat `cariByKode()`, kota harus huruf, jam harus berformat `08.00`, harga dan kursi harus angka). Pesan validasi selalu diberi awalan `[VALIDASI]`. Setelah valid, data disimpan ke tabel `penerbangan` lewat `PenerbanganDAO.simpan()`.

<img width="586" height="231" alt="Menu1" src="https://github.com/user-attachments/assets/78359c05-0261-486f-8095-1f12fb4c05af" />

2. **Lihat Jadwal Penerbangan**

   Program mengambil seluruh isi tabel `penerbangan` lewat `PenerbanganDAO.ambilSemua()`. Berkat dummy data dari file `.sql`, data langsung tampil begitu program dijalankan, tanpa input apa pun:

<img width="718" height="376" alt="Menu2" src="https://github.com/user-attachments/assets/158f392f-1ac2-42a0-9a64-cf3a66a63206" />

3. **Pesan Tiket**

   User memilih nomor penerbangan dari daftar yang diambil dari database, lalu mengisi data penumpang (nama harus huruf, No KTP 16 digit angka, No Telepon 10-13 digit angka) dan memilih kelas kursi. Sebelum lanjut, `Controller` mengecek sisa kursi; kalau sudah habis, pemesanan ditolak. Kalau masih ada, lewat `TiketDAO.simpan()` program menyimpan data penumpang, data tiket, **dan** mengurangi sisa kursi penerbangan dalam satu transaksi — kalau salah satu langkah gagal, semuanya dibatalkan. ID tiket dibuat otomatis oleh database.

<img width="630" height="305" alt="image" src="https://github.com/user-attachments/assets/16d6d830-5fa4-4383-81d2-233d2e05b585" />

   Percobaan memesan lagi di penerbangan yang kursinya sudah habis:

<img width="537" height="196" alt="Menu3 habis" src="https://github.com/user-attachments/assets/18ddba3f-126b-428e-9727-001c6852ea04" />

4. **Lihat Semua Tiket**

   Program mengambil seluruh tiket dari database lewat `TiketDAO.ambilSemua()` (gabungan tabel `tiket`, `penumpang`, dan `penerbangan`), termasuk dua tiket dari dummy data. Tiket ekonomi dan bisnis tampil dengan label kategori dan info bagasi yang berbeda:

<img width="816" height="416" alt="image" src="https://github.com/user-attachments/assets/844eed30-c316-45ab-859a-a400c7d28fa2" />

5. **Update Status Tiket**

   Program menampilkan daftar tiket ringkas, lalu user memasukkan ID tiket (misalnya `TKT-1001`). Program mencarinya lewat `TiketDAO.cariById()`, lalu status baru (hanya `Dipesan`, `Lunas`, atau `Dibatalkan`) disimpan ke database lewat `TiketDAO.updateStatus()`.

<img width="510" height="209" alt="image" src="https://github.com/user-attachments/assets/1404735d-8800-4447-b675-c886abdaf878" />

<img width="507" height="250" alt="image" src="https://github.com/user-attachments/assets/6044e26b-83ff-4f7d-babe-0b63af2e962b" />

6. **Batalkan Tiket**

   User memasukkan ID tiket yang ingin dibatalkan. Lewat `TiketDAO.hapus()`, baris tiket dan data penumpangnya dihapus dari database, dan sisa kursi penerbangan dikembalikan (+1), semuanya dalam satu transaksi.

<img width="490" height="205" alt="image" src="https://github.com/user-attachments/assets/809124b4-fbb8-4d8b-bd06-883d23c2a4f2" />

7. **Keluar**

   User memilih menu 7, program mencetak "Terima kasih telah menggunakan sistem ini!", koneksi Hibernate ditutup (kalau mode ORM dipakai), dan program selesai.

### Cara Menjalankan

1. Nyalakan MySQL (lewat Laragon atau XAMPP).
2. Import file `database/db_tiket_pesawat.sql` (lewat phpMyAdmin tab *SQL* / *Import*, HeidiSQL, MySQL Workbench, atau terminal `mysql -u root < db_tiket_pesawat.sql`). File ini otomatis membuat database `db_tiket_pesawat`, semua tabelnya, dan dummy data awal.
3. Cek pengaturan koneksi di `config/Koneksi.java` (default: `localhost:3306`, user `root`, password kosong) dan sesuaikan kalau berbeda.
4. Buka project di NetBeans (Maven). Saat pertama kali dibuka, dependency (`mysql-connector-j` dan `hibernate-core`) akan diunduh otomatis, jadi butuh koneksi internet. Kalau perlu, klik kanan project lalu pilih *Reload POM* dan *Clean and Build*.
5. Pastikan **Main Class** mengarah ke `Main`, lalu jalankan project dan pilih cara akses database (1 = JDBC, 2 = ORM).

Kebutuhan: Java 17 atau lebih baru, Maven, dan MySQL.

## 4. Penjelasan Penerapan Encapsulation dan Inheritance

### Encapsulation

Seperti sebelumnya, semua atribut di class `Penerbangan`, `Penumpang`, dan `Tiket` bertipe `private` (khusus `Tiket` memakai `protected` untuk `penumpang`, `penerbangan`, dan `statusTiket` supaya bisa diwariskan) dan hanya bisa diakses lewat getter dan setter. Validasi di dalam setter tetap dipertahankan, contohnya `Penerbangan.setHarga()` yang menolak harga 0 atau negatif:

<img width="325" height="83" alt="image" src="https://github.com/user-attachments/assets/dc42c084-35cb-46b8-a6de-b666fa14e68b" />

Jumlah kursi tetap tidak punya setter bebas — perubahannya hanya boleh lewat `kurangiKursi()` dan `tambahKursi()`. Pengaturan koneksi database (`URL`, `USER`, `PASSWORD`) juga dikumpulkan di satu tempat, yaitu class `Koneksi`, supaya tidak tersebar di banyak class.

Ada dua penyesuaian kecil demi ORM: constructor kosong dibuat `protected` (dibutuhkan Hibernate, tapi tidak bisa dipakai sembarangan dari luar), dan ID tiket kini disimpan sebagai angka dari database, lalu ditampilkan lewat `getIdTiket()` dengan format `TKT-1001`.

### Inheritance

`Tiket` tetap menjadi superclass, dan `TiketEkonomi` serta `TiketBisnis` menjadi subclass-nya (`extends Tiket`, constructor memanggil `super(...)`):

```text
Tiket  (abstract)
├── TiketEkonomi
└── TiketBisnis
```

Di database, kedua jenis tiket disimpan dalam **satu tabel `tiket`**, dibedakan oleh kolom `kelas` (`EKONOMI`/`BISNIS`). Saat data dibaca dari database, program membuat objek `TiketEkonomi` atau `TiketBisnis` sesuai isi kolom itu. Satu perubahan kecil dari Mini Project 3: info bagasi (20 kg / 30 kg) sekarang berupa konstanta di tiap subclass, karena nilainya tetap per kelas dan tidak perlu disimpan di database.

## 5. Penjelasan Penerapan Polymorphism dan Abstraction

### Abstraction

- **Abstract class**: `Tiket` adalah `abstract class` dengan dua `abstract method`, `getNamaKelas()` dan `hitungTotalHarga()`. `Tiket` tidak bisa dibuat langsung, harus lewat `TiketEkonomi` atau `TiketBisnis` yang wajib mengisi dua method itu sendiri.
- **Interface sebagai abstraksi akses data**: `PenerbanganDAO` dan `TiketDAO` adalah interface yang hanya menyebutkan *apa* yang bisa dilakukan ke database (`simpan`, `ambilSemua`, `cariById`, `updateStatus`, `hapus`, dst), tanpa menyebutkan *bagaimana* caranya. `Controller` hanya mengenal interface ini, sehingga tidak perlu tahu apakah di belakangnya memakai JDBC atau Hibernate.

### Polymorphism - Method Overriding

`TiketEkonomi` dan `TiketBisnis` meng-*override* `getNamaKelas()`, `hitungTotalHarga()`, dan `tampilkanInfo()` dengan perhitungan dan tampilan masing-masing (harga Bisnis dikali 1.5, bagasi dan label kategori berbeda). Saat tiket diambil dari database dan dipanggil lewat referensi `Tiket`, method yang jalan tetap sesuai jenis objek aslinya.

### Polymorphism - Method Overloading

- `Tiket.tampilkanInfo()` versus `Tiket.tampilkanInfo(boolean ringkas)` untuk tampilan lengkap atau ringkas.
- `View.tampilkanPesan(String pesan)` versus `View.tampilkanPesan(String label, String pesan)` untuk pesan biasa atau pesan berlabel seperti `[VALIDASI]` dan `[ERROR]`.

### Polymorphism pada DAO

Di `Controller`, DAO dipegang lewat tipe interface, dan objek aslinya ditentukan saat program dijalankan:

<img width="304" height="30" alt="image" src="https://github.com/user-attachments/assets/f9b37238-f8d6-487b-8ae0-f2bd76f8b90a" />

<img width="355" height="100" alt="image" src="https://github.com/user-attachments/assets/ebd3de1e-6b8c-41de-982c-ef10e528d12b" />

Setelah itu, pemanggilan seperti `tiketDAO.simpan(tiket)` di seluruh `Controller` sama persis untuk kedua mode, tapi yang dijalankan berbeda sesuai objek aslinya.

### Interface `Cetak`

Interface `Cetak` dari Mini Project 3 tetap dipakai: `Tiket` meng-*implements* `Cetak` lewat method `cetak()`.

## 6. Penjelasan Penerapan JDBC

### Koneksi database

Class `config/Koneksi.java` menyimpan URL, user, dan password database, lalu membuat koneksi dengan `DriverManager.getConnection()`. Driver yang dipakai adalah `mysql-connector-j` (ditambahkan lewat `pom.xml`).

<img width="439" height="52" alt="image" src="https://github.com/user-attachments/assets/98a7ec36-1cfd-4de9-85a3-1f906d8ed05f" />

### PreparedStatement

Semua query ke database memakai `PreparedStatement` dengan tanda `?` sebagai tempat nilai, lalu nilainya diisi lewat `setString()`, `setInt()`, `setDouble()`. Dengan cara ini input user tidak pernah digabung langsung ke teks SQL, sehingga aman dari *SQL injection*. Contoh pada `dao/PenerbanganDAOJdbc.java`:

<img width="729" height="254" alt="image" src="https://github.com/user-attachments/assets/1153c8b0-b3df-466c-ad31-a031645008da" />

Koneksi dan `PreparedStatement` dibuka dengan *try-with-resources*, jadi otomatis ditutup walaupun terjadi error.

### Pola DAO

Operasi database dipisahkan dari `Controller` ke class DAO. Pemetaan operasi CRUD-nya:

| Operasi | Menu | Method DAO |
|---|---|---|
| Create | Tambah Data Penerbangan | `PenerbanganDAO.simpan()` |
| Create | Pesan Tiket | `TiketDAO.simpan()` |
| Read | Lihat Jadwal Penerbangan, Pesan Tiket, Tambah Penerbangan (cek kode kembar) | `PenerbanganDAO.ambilSemua()`, `cariByKode()` |
| Read | Lihat Semua Tiket, Update Status, Batalkan Tiket | `TiketDAO.ambilSemua()`, `cariById()` |
| Update | Update Status Tiket | `TiketDAO.updateStatus()` |
| Delete | Batalkan Tiket | `TiketDAO.hapus()` |

Hasil query (`ResultSet`) diubah menjadi objek `Penerbangan`, `Penumpang`, dan `TiketEkonomi`/`TiketBisnis` di method `petakan()`. Untuk tiket, data diambil sekaligus dari tiga tabel memakai `JOIN`.

### Transaksi

Pemesanan tiket butuh tiga langkah (simpan penumpang, kurangi kursi, simpan tiket), dan pembatalan juga tiga langkah (hapus tiket, hapus penumpang, kembalikan kursi). Supaya data tidak setengah-setengah, langkah-langkah itu dijalankan dalam satu transaksi:


<img width="172" height="19" alt="image" src="https://github.com/user-attachments/assets/049bed1f-54c9-48ea-9b93-38b6cf5e6e3b" />

```// simpan penumpang, kurangi kursi, simpan tiket```

<img width="112" height="18" alt="image" src="https://github.com/user-attachments/assets/8ee1e4aa-1984-4fca-8854-2529c7cf96e1" />

```kalau ada SQLException -> conn.rollback(), semuanya dibatalkan```


Sisa kursi dicek di `Controller` sebelum tiket disimpan, jadi pemesanan untuk penerbangan yang kursinya sudah habis ditolak sebelum menyentuh database.

### Dummy data dan penanganan error

Data awal sepenuhnya berasal dari `database/db_tiket_pesawat.sql` (bagian `INSERT INTO`). Kalau terjadi error database, `SQLException` dibungkus oleh DAO menjadi `DataAccessException` dengan pesan yang jelas, lalu ditangkap di `Controller`, yang menampilkannya sebagai `[ERROR]` tanpa membuat program berhenti. Dengan satu jenis exception ini, `Controller` tidak perlu tahu apakah error berasal dari JDBC atau Hibernate.

## 7. Penjelasan Penerapan Nilai Tambah (ORM - Hibernate)

Nilai tambah yang diterapkan adalah **framework ORM (Object-Relational Mapping) memakai Hibernate**. Dengan ORM, class Java langsung dipetakan ke tabel database, sehingga operasi simpan, cari, ubah, dan hapus bisa dilakukan lewat objek tanpa menulis query SQL manual. ORM dipakai ketika user memilih mode **2** saat program dimulai.

Letak penerapannya:

1. **`model/` — pemetaan class ke tabel lewat anotasi.** Class `Penerbangan`, `Penumpang`, dan `Tiket` diberi anotasi `@Entity` dan `@Table`; field diberi `@Column`, kunci utama diberi `@Id` (dengan `@GeneratedValue` untuk ID otomatis), dan relasi tiket ke penumpang/penerbangan memakai `@ManyToOne` + `@JoinColumn`. Inheritance `Tiket` → `TiketEkonomi`/`TiketBisnis` dipetakan dengan `@Inheritance(SINGLE_TABLE)`, `@DiscriminatorColumn("kelas")`, dan `@DiscriminatorValue("EKONOMI"/"BISNIS")` pada tiap subclass.

<img width="525" height="84" alt="image" src="https://github.com/user-attachments/assets/e7637d36-ffb2-400c-bf8c-a6258fa824dd" />

2. **`config/HibernateUtil.java` — menyiapkan Hibernate.** Class ini membangun `SessionFactory` dari pengaturan koneksi di `Koneksi` dan mendaftarkan semua class entity. `SessionFactory` hanya dibuat saat pertama kali dibutuhkan, jadi mode JDBC tidak ikut memuat Hibernate.

3. **`dao/` — DAO versi Hibernate.** `PenerbanganDAOHibernate` dan `TiketDAOHibernate` meng-*implements* interface DAO yang sama dengan versi JDBC, tapi memakai `Session`: `session.persist()` untuk menyimpan, `session.find()` untuk mencari berdasarkan ID, `session.remove()` untuk menghapus, dan query HQL (misalnya `from Penerbangan p order by p.kodePenerbangan`) untuk mengambil semua data. Perubahan pada objek yang sedang dipantau Hibernate (misalnya `kurangiKursi()` atau `setStatusTiket()`) otomatis tersimpan ke database saat `commit()`, tanpa perlu menulis `UPDATE`. Saat memesan tiket, data penumpang ikut tersimpan lewat `cascade = PERSIST`.

4. **`pom.xml`** — dependency `hibernate-core` ditambahkan di sini.

Perbandingan singkat satu fitur (Update Status Tiket):

| JDBC (`TiketDAOJdbc`) | ORM (`TiketDAOHibernate`) |
|---|---|
| `UPDATE tiket SET status_tiket = ? WHERE id_tiket = ?` lalu `ps.executeUpdate()` | `tiket = session.find(Tiket.class, id)`, `tiket.setStatusTiket(...)`, `commit()` |

Karena `Controller` hanya mengenal interface `PenerbanganDAO` dan `TiketDAO`, penambahan ORM tidak mengubah alur program sama sekali — hanya implementasi di belakangnya yang berbeda.
