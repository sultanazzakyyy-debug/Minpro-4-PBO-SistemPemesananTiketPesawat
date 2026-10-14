/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import config.Koneksi;
import util.DataAccessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Penerbangan;

/**
 *
 * @author Adbang 18
 */

public class PenerbanganDAOJdbc implements PenerbanganDAO {

    @Override
    public void simpan(Penerbangan penerbangan) {
        String sql = "INSERT INTO penerbangan (kode_penerbangan, asal, tujuan, jam_keberangkatan, harga, kursi_tersedia) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Koneksi.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, penerbangan.getKodePenerbangan());
            ps.setString(2, penerbangan.getAsal());
            ps.setString(3, penerbangan.getTujuan());
            ps.setString(4, penerbangan.getJamKeberangkatan());
            ps.setDouble(5, penerbangan.getHarga());
            ps.setInt(6, penerbangan.getKursiTersedia());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Gagal menyimpan penerbangan: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Penerbangan> ambilSemua() {
        String sql = "SELECT kode_penerbangan, asal, tujuan, jam_keberangkatan, harga, kursi_tersedia "
                + "FROM penerbangan ORDER BY kode_penerbangan";
        List<Penerbangan> hasil = new ArrayList<>();

        try (Connection conn = Koneksi.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hasil.add(petakan(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Gagal mengambil data penerbangan: " + e.getMessage(), e);
        }
        return hasil;
    }

    @Override
    public Penerbangan cariByKode(String kode) {
        String sql = "SELECT kode_penerbangan, asal, tujuan, jam_keberangkatan, harga, kursi_tersedia "
                + "FROM penerbangan WHERE kode_penerbangan = ?";

        try (Connection conn = Koneksi.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return petakan(rs);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Gagal mencari penerbangan: " + e.getMessage(), e);
        }
        return null;
    }

    private Penerbangan petakan(ResultSet rs) throws SQLException {
        return new Penerbangan(
                rs.getString("kode_penerbangan"),
                rs.getString("asal"),
                rs.getString("tujuan"),
                rs.getString("jam_keberangkatan"),
                rs.getDouble("harga"),
                rs.getInt("kursi_tersedia"));
    }
}
