/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import config.HibernateUtil;
import util.DataAccessException;
import dao.PenerbanganDAO;
import dao.TiketDAO;
import dao.PenerbanganDAOJdbc;
import dao.TiketDAOJdbc;
import dao.PenerbanganDAOHibernate;
import dao.TiketDAOHibernate;
import java.util.List;
import java.util.Scanner;
import model.Penerbangan;
import model.Penumpang;
import model.Tiket;
import model.TiketBisnis;
import model.TiketEkonomi;
import view.View;

/**
 *
 * @author Adbang 18
 */

public class Controller {

    private final Scanner input = new Scanner(System.in);
    private final View view = new View();

    private PenerbanganDAO penerbanganDAO;
    private TiketDAO tiketDAO;

    public void jalankanAplikasi() {
        pilihCaraAkses();

        if (!cekKoneksi()) {
            HibernateUtil.tutup();
            return;
        }

        int pilihan;

        do {
            view.tampilkanMenu();
            String teksPilihan = input.nextLine();
            try {
                pilihan = Integer.parseInt(teksPilihan);
            } catch (NumberFormatException e) {
                pilihan = -1;
            }

            try {
                switch (pilihan) {
                    case 1 -> tambahPenerbanganDariInput();
                    case 2 -> {
                        System.out.println("=== JADWAL PENERBANGAN ===");
                        view.tampilkanJadwalPenerbangan(penerbanganDAO.ambilSemua());
                    }
                    case 3 -> pesanTiketDariInput();
                    case 4 -> {
                        System.out.println("=== DAFTAR TIKET ===");
                        view.tampilkanDaftarTiket(tiketDAO.ambilSemua());
                    }
                    case 5 -> updateStatusTiketDariInput();
                    case 6 -> batalkanTiketDariInput();
                    case 7 -> view.tampilkanPesan("Terima kasih telah menggunakan sistem ini!");
                    default -> view.tampilkanPesan("Pilihan tidak tersedia!");
                }
            } catch (DataAccessException e) {
                view.tampilkanPesan("ERROR", e.getMessage());
            }

        } while (pilihan != 7);

        HibernateUtil.tutup();
    }

    private void pilihCaraAkses() {
        view.tampilkanPilihanAkses();
        int mode = inputPilihanAngka("Pilihan: ", 1, 2);

        if (mode == 2) {
            penerbanganDAO = new PenerbanganDAOHibernate();
            tiketDAO = new TiketDAOHibernate();
        } else {
            penerbanganDAO = new PenerbanganDAOJdbc();
            tiketDAO = new TiketDAOJdbc();
        }
    }

    private boolean cekKoneksi() {
        try {
            penerbanganDAO.ambilSemua();
            view.tampilkanPesan("Berhasil terhubung ke database.");
            return true;
        } catch (DataAccessException e) {
            view.tampilkanPesan("ERROR", e.getMessage());
            view.tampilkanPesan("ERROR", "Pastikan MySQL sudah menyala dan file database/db_tiket_pesawat.sql sudah di-import.");
            return false;
        }
    }

    private void validasi(String pesan) {
        view.tampilkanPesan("VALIDASI", pesan);
    }

    private String inputTeksWajib(String label) {
        String hasil;
        do {
            System.out.print(label);
            hasil = input.nextLine().trim();
            if (hasil.equals("")) {
                validasi("Input tidak boleh kosong!");
            }
        } while (hasil.equals(""));
        return hasil;
    }

    private String inputHurufSaja(String label, String namaField) {
        String hasil;
        boolean valid;
        do {
            System.out.print(label);
            hasil = input.nextLine().trim();
            if (hasil.equals("")) {
                validasi(namaField + " tidak boleh kosong!");
                valid = false;
            } else if (!hasil.matches("[a-zA-Z\\s]+")) {
                validasi(namaField + " harus berupa huruf, tidak boleh angka/simbol!");
                valid = false;
            } else {
                valid = true;
            }
        } while (!valid);
        return hasil;
    }

    private String inputNoKTP(String label) {
        String hasil;
        do {
            System.out.print(label + " (16 digit, contoh: 6472011503990001): ");
            hasil = input.nextLine().trim();
            if (!hasil.matches("\\d{16}")) {
                validasi("No KTP harus 16 digit angka!");
            }
        } while (!hasil.matches("\\d{16}"));
        return hasil;
    }

    private String inputNoTelepon(String label) {
        String hasil;
        do {
            System.out.print(label + " (contoh: 081234567890): ");
            hasil = input.nextLine().trim();
            if (!hasil.matches("\\d{10,13}")) {
                validasi("No Telepon harus berupa angka, 10-13 digit!");
            }
        } while (!hasil.matches("\\d{10,13}"));
        return hasil;
    }

    private double inputHargaValid(String label) {
        double hasil = 0;
        boolean valid = false;
        do {
            System.out.print(label + " (contoh: 1500000): ");
            String teks = input.nextLine();
            try {
                hasil = Double.parseDouble(teks);
                if (hasil > 0) {
                    valid = true;
                } else {
                    validasi("Harga harus lebih dari 0!");
                }
            } catch (NumberFormatException e) {
                validasi("Harga harus berupa angka, contoh: 1500000.");
            }
        } while (!valid);
        return hasil;
    }

    private int inputAngkaPositif(String label) {
        int hasil = 0;
        boolean valid = false;
        do {
            System.out.print(label);
            String teks = input.nextLine();
            try {
                hasil = Integer.parseInt(teks);
                if (hasil > 0) {
                    valid = true;
                } else {
                    validasi("Nilai harus lebih dari 0!");
                }
            } catch (NumberFormatException e) {
                validasi("Masukkan angka yang valid, bukan huruf/simbol.");
            }
        } while (!valid);
        return hasil;
    }

    private int inputPilihanAngka(String label, int min, int max) {
        int hasil = 0;
        boolean valid = false;
        do {
            System.out.print(label);
            String teks = input.nextLine();
            try {
                hasil = Integer.parseInt(teks);
                if (hasil >= min && hasil <= max) {
                    valid = true;
                } else {
                    validasi("Pilihan harus di antara " + min + " dan " + max + "!");
                }
            } catch (NumberFormatException e) {
                validasi("Masukkan angka yang valid, bukan huruf/simbol.");
            }
        } while (!valid);
        return hasil;
    }

    private String inputJamValid(String label) {
        String jam;
        do {
            System.out.print(label + " (format jam.menit, contoh: 08.00): ");
            jam = input.nextLine().trim();
            if (!jam.matches("([01]\\d|2[0-3])\\.[0-5]\\d")) {
                validasi("Format jam salah. Gunakan format seperti 08.00 atau 14.30.");
            }
        } while (!jam.matches("([01]\\d|2[0-3])\\.[0-5]\\d"));
        return jam;
    }

    private String inputStatusValid(String label) {
        String status;
        do {
            System.out.print(label);
            status = input.nextLine().trim();
            if (!status.equals("Dipesan") && !status.equals("Lunas") && !status.equals("Dibatalkan")) {
                validasi("Status tidak dikenali! Gunakan Dipesan/Lunas/Dibatalkan.");
            }
        } while (!status.equals("Dipesan") && !status.equals("Lunas") && !status.equals("Dibatalkan"));
        return status;
    }

    private void tambahPenerbanganDariInput() {
        System.out.println("=== TAMBAH DATA PENERBANGAN ===");

        String kodePenerbangan;
        boolean sudahAda;
        do {
            kodePenerbangan = inputTeksWajib("Kode Penerbangan (Contoh: GA401): ");
            sudahAda = penerbanganDAO.cariByKode(kodePenerbangan) != null;
            if (sudahAda) {
                validasi("Kode penerbangan \"" + kodePenerbangan + "\" sudah terdaftar, gunakan kode lain!");
            }
        } while (sudahAda);

        String asal = inputHurufSaja("Kota Asal: ", "Nama kota");
        String tujuan = inputHurufSaja("Kota Tujuan: ", "Nama kota");
        String jam = inputJamValid("Jam Keberangkatan");
        double harga = inputHargaValid("Harga Dasar");
        int kursi = inputAngkaPositif("Jumlah Kursi: ");

        penerbanganDAO.simpan(new Penerbangan(kodePenerbangan, asal, tujuan, jam, harga, kursi));
        view.tampilkanPesan("Data penerbangan berhasil ditambahkan!");
    }

    private void pesanTiketDariInput() {
        System.out.println("=== PESAN TIKET ===");

        List<Penerbangan> daftar = penerbanganDAO.ambilSemua();
        if (daftar.isEmpty()) {
            view.tampilkanPesan("Belum ada penerbangan yang bisa dipesan.");
            return;
        }

        System.out.println("Daftar penerbangan yang tersedia:");
        view.tampilkanDaftarPenerbanganRingkas(daftar);

        int nomor;
        do {
            nomor = inputAngkaPositif("Pilih nomor penerbangan: ");
            if (nomor > daftar.size()) {
                validasi("Nomor penerbangan tidak tersedia!");
            }
        } while (nomor > daftar.size());

        Penerbangan penerbanganDipilih = daftar.get(nomor - 1);

        if (penerbanganDipilih.getKursiTersedia() <= 0) {
            view.tampilkanPesan("Maaf, kursi untuk penerbangan ini sudah habis.");
            return;
        }

        String nama = inputHurufSaja("Nama Penumpang: ", "Nama");
        String noKTP = inputNoKTP("No KTP");
        String noTelepon = inputNoTelepon("No Telepon");

        Penumpang penumpang = new Penumpang(nama, noKTP, noTelepon);

        System.out.println("Pilih kelas kursi:");
        System.out.println("1. Ekonomi");
        System.out.println("2. Bisnis");
        int pilihKelas = inputPilihanAngka("Pilihan: ", 1, 2);

        Tiket tiket;
        if (pilihKelas == 2) {
            tiket = new TiketBisnis(penumpang, penerbanganDipilih);
        } else {
            tiket = new TiketEkonomi(penumpang, penerbanganDipilih);
        }

        tiketDAO.simpan(tiket);

        view.tampilkanPesan("Tiket berhasil dipesan! ID Tiket: " + tiket.getIdTiket());
        System.out.println("Total harga: Rp" + tiket.hitungTotalHarga());
        tampilkanSisaKursi(penerbanganDipilih.getKodePenerbangan());
    }

    private void updateStatusTiketDariInput() {
        System.out.println("Daftar tiket saat ini:");
        view.tampilkanDaftarTiketRingkas(tiketDAO.ambilSemua());

        System.out.print("Masukkan ID Tiket yang ingin diubah statusnya: ");
        String idUbah = input.nextLine().trim();
        Tiket tiketUbah = tiketDAO.cariById(idUbah);

        if (tiketUbah == null) {
            view.tampilkanPesan("Tiket tidak ditemukan.");
            return;
        }

        System.out.println("Status saat ini: " + tiketUbah.getStatusTiket());
        String statusBaru = inputStatusValid("Status Baru (Dipesan/Lunas/Dibatalkan): ");

        tiketDAO.updateStatus(idUbah, statusBaru);
        view.tampilkanPesan("Status tiket berhasil diubah!");
    }

    private void batalkanTiketDariInput() {
        System.out.println("Daftar tiket saat ini:");
        view.tampilkanDaftarTiketRingkas(tiketDAO.ambilSemua());

        System.out.print("Masukkan ID Tiket yang ingin dibatalkan: ");
        String idHapus = input.nextLine().trim();
        Tiket tiketHapus = tiketDAO.cariById(idHapus);

        if (tiketHapus == null) {
            view.tampilkanPesan("Tiket tidak ditemukan.");
            return;
        }

        String kodeTerkait = tiketHapus.getPenerbangan().getKodePenerbangan();
        
        tiketDAO.hapus(idHapus);

        view.tampilkanPesan("Tiket berhasil dibatalkan.");
        tampilkanSisaKursi(kodeTerkait);
    }

    private void tampilkanSisaKursi(String kodePenerbangan) {
        Penerbangan terbaru = penerbanganDAO.cariByKode(kodePenerbangan);
        if (terbaru != null) {
            System.out.println("Sisa kursi penerbangan " + kodePenerbangan
                    + " sekarang: " + terbaru.getKursiTersedia());
        }
    }
}