/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import config.HibernateUtil;
import util.DataAccessException;
import java.util.List;
import model.Penerbangan;
import model.Tiket;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Adbang 18
 */

public class TiketDAOHibernate implements TiketDAO {

    @Override
    public void simpan(Tiket tiket) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            // ambil data penerbangan dari database, lalu kurangi kursinya
            Penerbangan penerbangan = session.find(Penerbangan.class,
                    tiket.getPenerbangan().getKodePenerbangan());
            penerbangan.kurangiKursi();          // otomatis di-UPDATE saat commit
            tiket.setPenerbangan(penerbangan);

            session.persist(tiket);              // penumpang ikut tersimpan (cascade)
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new DataAccessException("Gagal menyimpan tiket: " + e.getMessage(), e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Tiket> ambilSemua() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Tiket t order by t.id", Tiket.class).list();
        } catch (RuntimeException e) {
            throw new DataAccessException("Gagal mengambil data tiket: " + e.getMessage(), e);
        }
    }

    @Override
    public Tiket cariById(String idTiket) {
        Integer id = Tiket.ekstrakId(idTiket);
        if (id == null) {
            return null;
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.find(Tiket.class, id);
        } catch (RuntimeException e) {
            throw new DataAccessException("Gagal mencari tiket: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateStatus(String idTiket, String statusBaru) {
        Integer id = Tiket.ekstrakId(idTiket);
        if (id == null) {
            return false;
        }
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            Tiket tiket = session.find(Tiket.class, id);
            if (tiket == null) {
                tx.rollback();
                return false;
            }
            tiket.setStatusTiket(statusBaru);    // perubahan otomatis tersimpan saat commit
            tx.commit();
            return true;
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new DataAccessException("Gagal mengubah status tiket: " + e.getMessage(), e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public boolean hapus(String idTiket) {
        Integer id = Tiket.ekstrakId(idTiket);
        if (id == null) {
            return false;
        }
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            Tiket tiket = session.find(Tiket.class, id);
            if (tiket == null) {
                tx.rollback();
                return false;
            }

            tiket.getPenerbangan().tambahKursi();  // kursi dikembalikan
            session.remove(tiket);
            session.remove(tiket.getPenumpang());

            tx.commit();
            return true;
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new DataAccessException("Gagal membatalkan tiket: " + e.getMessage(), e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
