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
@DiscriminatorValue("EKONOMI")
public class TiketEkonomi extends Tiket {

    private static final int BAGASI_KG = 20;

    protected TiketEkonomi() {
    }

    public TiketEkonomi(Penumpang penumpang, Penerbangan penerbangan) {
        super(penumpang, penerbangan);
    }

    @Override
    public String getNamaKelas() {
        return "Ekonomi";
    }

    @Override
    public double hitungTotalHarga() {
        return getPenerbangan().getHarga();
    }

    @Override
    public void tampilkanInfo() {
        System.out.println("-----------------------------------");
        System.out.println("[KATEGORI: TIKET EKONOMI]");
        super.tampilkanInfo();
        System.out.println("Bagasi           : " + BAGASI_KG + " kg");
        System.out.println("-----------------------------------");
    }
}