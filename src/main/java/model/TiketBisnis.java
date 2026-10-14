/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 *
 * @author Adbang 18
 */

@Entity
@DiscriminatorValue("BISNIS")
public class TiketBisnis extends Tiket {

    private static final int BAGASI_KG = 30;

    protected TiketBisnis() {
    }

    public TiketBisnis(Penumpang penumpang, Penerbangan penerbangan) {
        super(penumpang, penerbangan);
    }

    @Override
    public String getNamaKelas() {
        return "Bisnis";
    }

    @Override
    public double hitungTotalHarga() {
        return getPenerbangan().getHarga() * 1.5;
    }

    @Override
    public void tampilkanInfo() {
        System.out.println("-----------------------------------");
        System.out.println("[KATEGORI: TIKET BISNIS]");
        super.tampilkanInfo();
        System.out.println("Bagasi           : " + BAGASI_KG + " kg");
        System.out.println("-----------------------------------");
    }
}