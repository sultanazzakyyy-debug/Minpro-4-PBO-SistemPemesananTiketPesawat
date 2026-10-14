/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;
import java.util.List;
import model.Penerbangan;
import model.Tiket;
import java.util.List;

/**
 *
 * @author Adbang 18
 */

public class View {

    public void tampilkanPilihanAkses() {
        System.out.println("=== PILIH CARA AKSES DATABASE ===");
        System.out.println("1. JDBC (PreparedStatement + DAO)");
        System.out.println("2. ORM (Hibernate + DAO)");
    }

    public void tampilkanMenu() {
        System.out.println("");
        System.out.println("=== SISTEM PEMESANAN TIKET PESAWAT ===");
        System.out.println("1. Tambah Data Penerbangan");
        System.out.println("2. Lihat Jadwal Penerbangan");
        System.out.println("3. Pesan Tiket");
        System.out.println("4. Lihat Semua Tiket");
        System.out.println("5. Update Status Tiket");
        System.out.println("6. Batalkan Tiket");
        System.out.println("7. Keluar");
        System.out.print("Pilih menu: ");
    }

    public void tampilkanPesan(String pesan) {
        System.out.println(pesan);
    }

    public void tampilkanPesan(String label, String pesan) {
        System.out.println("[" + label + "] " + pesan);
    }

    public void tampilkanDaftarPenerbanganRingkas(List<Penerbangan> daftar) {
        for (int i = 0; i < daftar.size(); i++) {
            Penerbangan p = daftar.get(i);
            System.out.println((i + 1) + ". " + p.getKodePenerbangan() + " | " + p.getAsal() + " - " + p.getTujuan()
                    + " | " + p.getJamKeberangkatan() + " | Rp" + p.getHarga() + " | Sisa kursi: " + p.getKursiTersedia());
        }
    }

    public void tampilkanJadwalPenerbangan(List<Penerbangan> daftar) {
        if (daftar.isEmpty()) {
            tampilkanPesan("Belum ada data penerbangan.");
        } else {
            for (int i = 0; i < daftar.size(); i++) {
                Penerbangan p = daftar.get(i);
                System.out.println("Penerbangan ke-" + (i + 1));
                System.out.println("Kode Penerbangan : " + p.getKodePenerbangan());
                System.out.println("Rute             : " + p.getAsal() + " - " + p.getTujuan());
                System.out.println("Jam Keberangkatan: " + p.getJamKeberangkatan());
                System.out.println("Harga Dasar      : Rp" + p.getHarga());
                System.out.println("Sisa Kursi       : " + p.getKursiTersedia());
                System.out.println("-----------------------------------");
            }
        }
    }

    public void tampilkanDaftarTiket(List<Tiket> daftar) {
        if (daftar.isEmpty()) {
            tampilkanPesan("Belum ada data tiket.");
        } else {
            for (int i = 0; i < daftar.size(); i++) {
                System.out.println("Data ke-" + (i + 1));
                daftar.get(i).tampilkanInfo();
            }
        }
    }

    public void tampilkanDaftarTiketRingkas(List<Tiket> daftar) {
        if (daftar.isEmpty()) {
            tampilkanPesan("Belum ada data tiket.");
        } else {
            for (Tiket t : daftar) {
                t.tampilkanInfo(true);
            }
        }
    }
}