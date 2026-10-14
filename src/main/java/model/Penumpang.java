/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 *
 * @author Adbang 18
 */

@Entity
@Table(name = "penumpang")
public class Penumpang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_penumpang")
    private Integer idPenumpang;

    @Column(name = "nama")
    private String nama;

    @Column(name = "no_ktp")
    private String noKTP;

    @Column(name = "no_telepon")
    private String noTelepon;

    protected Penumpang() {
    }

    public Penumpang(String nama, String noKTP, String noTelepon) {
        setNama(nama);
        setNoKTP(noKTP);
        setNoTelepon(noTelepon);
    }

    public Integer getIdPenumpang() {
        return idPenumpang;
    }

    public void setIdPenumpang(Integer idPenumpang) {
        this.idPenumpang = idPenumpang;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNoKTP() {
        return noKTP;
    }

    public void setNoKTP(String noKTP) {
        this.noKTP = noKTP;
    }

    public String getNoTelepon() {
        return noTelepon;
    }

    public void setNoTelepon(String noTelepon) {
        this.noTelepon = noTelepon;
    }
}