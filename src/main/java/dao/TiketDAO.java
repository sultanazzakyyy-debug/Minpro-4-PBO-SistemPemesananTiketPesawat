/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.List;
import model.Tiket;

/**
 *
 * @author Adbang 18
 */

public interface TiketDAO {

    void simpan(Tiket tiket);

    List<Tiket> ambilSemua();

    Tiket cariById(String idTiket);

    boolean updateStatus(String idTiket, String statusBaru);

    boolean hapus(String idTiket);
}