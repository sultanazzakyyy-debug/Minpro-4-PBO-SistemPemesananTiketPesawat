/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import java.util.List;
import model.Penerbangan;
import java.util.List;

/**
 *
 * @author Adbang 18
 */

public interface PenerbanganDAO {

    void simpan(Penerbangan penerbangan);

    List<Penerbangan> ambilSemua();

    Penerbangan cariByKode(String kode);
}