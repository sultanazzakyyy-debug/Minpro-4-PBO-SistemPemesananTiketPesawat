/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 *
 * @author Adbang 18
 */

@Entity
@Table(name = "tiket")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "kelas", discriminatorType = DiscriminatorType.STRING)
public abstract class Tiket implements Cetak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tiket")
    private Integer id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_penumpang", nullable = false)
    protected Penumpang penumpang;

    @ManyToOne
    @JoinColumn(name = "kode_penerbangan", nullable = false)
    protected Penerbangan penerbangan;

    @Column(name = "status_tiket")
    protected String statusTiket;

    protected Tiket() {
    }

    public Tiket(Penumpang penumpang, Penerbangan penerbangan) {
        this.penumpang = penumpang;
        setPenerbangan(penerbangan);
        this.statusTiket = "Dipesan";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdTiket() {
        return id == null ? "(belum tersimpan)" : "TKT-" + id;
    }

    public static Integer ekstrakId(String idTiket) {
        if (idTiket == null) {
            return null;
        }
        String teks = idTiket.trim().toUpperCase();
        if (!teks.matches("TKT-\\d{1,9}")) {
            return null;
        }
        return Integer.valueOf(teks.substring(4));
    }

    public Penumpang getPenumpang() {
        return penumpang;
    }

    public Penerbangan getPenerbangan() {
        return penerbangan;
    }

    public void setPenerbangan(Penerbangan penerbangan) {
        this.penerbangan = penerbangan;
    }

    public String getStatusTiket() {
        return statusTiket;
    }

    public void setStatusTiket(String statusTiket) {
        this.statusTiket = statusTiket;
    }

    public abstract String getNamaKelas();

    public abstract double hitungTotalHarga();

    public void tampilkanInfo() {
        System.out.println("ID Tiket         : " + getIdTiket());
        System.out.println("Nama Penumpang   : " + penumpang.getNama());
        System.out.println("No KTP           : " + penumpang.getNoKTP());
        System.out.println("No Telepon       : " + penumpang.getNoTelepon());
        System.out.println("Kode Penerbangan : " + penerbangan.getKodePenerbangan());
        System.out.println("Asal             : " + penerbangan.getAsal());
        System.out.println("Tujuan           : " + penerbangan.getTujuan());
        System.out.println("Jam Keberangkatan: " + penerbangan.getJamKeberangkatan());
        System.out.println("Kelas Kursi      : " + getNamaKelas());
        System.out.println("Total Harga      : Rp" + hitungTotalHarga());
        System.out.println("Status Tiket     : " + statusTiket);
    }

    public void tampilkanInfo(boolean ringkas) {
        if (ringkas) {
            System.out.println(getIdTiket() + " - " + penumpang.getNama() + " (" + getNamaKelas() + ") - " + statusTiket);
        } else {
            tampilkanInfo();
        }
    }

    @Override
    public void cetak() {
        tampilkanInfo();
    }
}