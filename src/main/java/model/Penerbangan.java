/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 *
 * @author Adbang 18
 */

@Entity
@Table(name = "penerbangan")
public class Penerbangan {

    @Id
    @Column(name = "kode_penerbangan")
    private String kodePenerbangan;

    @Column(name = "asal")
    private String asal;

    @Column(name = "tujuan")
    private String tujuan;

    @Column(name = "jam_keberangkatan")
    private String jamKeberangkatan;

    @Column(name = "harga")
    private double harga;

    @Column(name = "kursi_tersedia")
    private int kursiTersedia;

    protected Penerbangan() {
    }

    public Penerbangan(String kodePenerbangan, String asal, String tujuan, String jamKeberangkatan, double harga, int kursiTersedia) {
        this.kodePenerbangan = kodePenerbangan;
        setAsal(asal);
        setTujuan(tujuan);
        setJamKeberangkatan(jamKeberangkatan);
        setHarga(harga);
        this.kursiTersedia = kursiTersedia;
    }

    public String getKodePenerbangan() {
        return kodePenerbangan;
    }

    public String getAsal() {
        return asal;
    }

    public void setAsal(String asal) {
        this.asal = asal;
    }

    public String getTujuan() {
        return tujuan;
    }

    public void setTujuan(String tujuan) {
        this.tujuan = tujuan;
    }

    public String getJamKeberangkatan() {
        return jamKeberangkatan;
    }

    public void setJamKeberangkatan(String jamKeberangkatan) {
        this.jamKeberangkatan = jamKeberangkatan;
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        if (harga > 0) {
            this.harga = harga;
        }
    }

    public int getKursiTersedia() {
        return kursiTersedia;
    }

    public void kurangiKursi() {
        kursiTersedia = kursiTersedia - 1;
    }

    public void tambahKursi() {
        kursiTersedia = kursiTersedia + 1;
    }
}