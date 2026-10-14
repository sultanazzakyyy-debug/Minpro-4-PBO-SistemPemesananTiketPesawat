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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Penerbangan;
import model.Penumpang;
import model.Tiket;
import model.TiketBisnis;
import model.TiketEkonomi;

/**
 *
 * @author Adbang 18
 */

public class TiketDAOJdbc implements TiketDAO {

    private static final String SELECT_TIKET
            = "SELECT t.id_tiket, t.kelas, t.status_tiket, "
            + "p.id_penumpang, p.nama, p.no_ktp, p.no_telepon, "
            + "f.kode_penerbangan, f.asal, f.tujuan, f.jam_keberangkatan, f.harga, f.kursi_tersedia "
            + "FROM tiket t "
            + "JOIN penumpang p ON t.id_penumpang = p.id_penumpang "
            + "JOIN penerbangan f ON t.kode_penerbangan = f.kode_penerbangan ";

    @Override
    public void simpan(Tiket tiket) {
        Connection conn = null;
        try {
            conn = Koneksi.getConnection();
            conn.setAutoCommit(false);

            Penumpang penumpang = tiket.getPenumpang();
            String kode = tiket.getPenerbangan().getKodePenerbangan();

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO penumpang (nama, no_ktp, no_telepon) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, penumpang.getNama());
            ps.setString(2, penumpang.getNoKTP());
            ps.setString(3, penumpang.getNoTelepon());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                penumpang.setIdPenumpang(keys.getInt(1));
            }

            ps = conn.prepareStatement(
                    "UPDATE penerbangan SET kursi_tersedia = kursi_tersedia - 1 WHERE kode_penerbangan = ?");
            ps.setString(1, kode);
            ps.executeUpdate();

            ps = conn.prepareStatement(
                    "INSERT INTO tiket (id_penumpang, kode_penerbangan, kelas, status_tiket) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, penumpang.getIdPenumpang());
            ps.setString(2, kode);
            ps.setString(3, tiket.getNamaKelas().toUpperCase());
            ps.setString(4, tiket.getStatusTiket());
            ps.executeUpdate();
            keys = ps.getGeneratedKeys();
            if (keys.next()) {
                tiket.setId(keys.getInt(1));
            }

            conn.commit();
        } catch (SQLException e) {
            batalkanTransaksi(conn);
            throw new DataAccessException("Gagal menyimpan tiket: " + e.getMessage(), e);
        } finally {
            tutupKoneksi(conn);
        }
    }

    @Override
    public List<Tiket> ambilSemua() {
        String sql = SELECT_TIKET + "ORDER BY t.id_tiket";
        List<Tiket> hasil = new ArrayList<>();

        try (Connection conn = Koneksi.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hasil.add(petakan(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Gagal mengambil data tiket: " + e.getMessage(), e);
        }
        return hasil;
    }

    @Override
    public Tiket cariById(String idTiket) {
        Integer id = Tiket.ekstrakId(idTiket);
        if (id == null) {
            return null;
        }
        String sql = SELECT_TIKET + "WHERE t.id_tiket = ?";

        try (Connection conn = Koneksi.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return petakan(rs);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Gagal mencari tiket: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean updateStatus(String idTiket, String statusBaru) {
        Integer id = Tiket.ekstrakId(idTiket);
        if (id == null) {
            return false;
        }
        String sql = "UPDATE tiket SET status_tiket = ? WHERE id_tiket = ?";

        try (Connection conn = Koneksi.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statusBaru);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Gagal mengubah status tiket: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean hapus(String idTiket) {
        Integer id = Tiket.ekstrakId(idTiket);
        if (id == null) {
            return false;
        }

        Connection conn = null;
        try {
            conn = Koneksi.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT id_penumpang, kode_penerbangan FROM tiket WHERE id_tiket = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                conn.rollback();
                return false;
            }
            int idPenumpang = rs.getInt("id_penumpang");
            String kode = rs.getString("kode_penerbangan");

            ps = conn.prepareStatement("DELETE FROM tiket WHERE id_tiket = ?");
            ps.setInt(1, id);
            ps.executeUpdate();

            ps = conn.prepareStatement("DELETE FROM penumpang WHERE id_penumpang = ?");
            ps.setInt(1, idPenumpang);
            ps.executeUpdate();

            ps = conn.prepareStatement(
                    "UPDATE penerbangan SET kursi_tersedia = kursi_tersedia + 1 WHERE kode_penerbangan = ?");
            ps.setString(1, kode);
            ps.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            batalkanTransaksi(conn);
            throw new DataAccessException("Gagal membatalkan tiket: " + e.getMessage(), e);
        } finally {
            tutupKoneksi(conn);
        }
    }

    private void batalkanTransaksi(Connection conn) {
        try {
            if (conn != null) {
                conn.rollback();
            }
        } catch (SQLException e) {
        }
    }

    private void tutupKoneksi(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            // abaikan
        }
    }

    private Tiket petakan(ResultSet rs) throws SQLException {
        Penumpang penumpang = new Penumpang(
                rs.getString("nama"),
                rs.getString("no_ktp"),
                rs.getString("no_telepon"));
        penumpang.setIdPenumpang(rs.getInt("id_penumpang"));

        Penerbangan penerbangan = new Penerbangan(
                rs.getString("kode_penerbangan"),
                rs.getString("asal"),
                rs.getString("tujuan"),
                rs.getString("jam_keberangkatan"),
                rs.getDouble("harga"),
                rs.getInt("kursi_tersedia"));

        Tiket tiket;
        if (rs.getString("kelas").equalsIgnoreCase("BISNIS")) {
            tiket = new TiketBisnis(penumpang, penerbangan);
        } else {
            tiket = new TiketEkonomi(penumpang, penerbangan);
        }
        tiket.setId(rs.getInt("id_tiket"));
        tiket.setStatusTiket(rs.getString("status_tiket"));
        return tiket;
    }
}