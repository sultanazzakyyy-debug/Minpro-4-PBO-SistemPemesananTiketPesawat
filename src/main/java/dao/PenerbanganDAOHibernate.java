/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import config.HibernateUtil;
import util.DataAccessException;
import java.util.List;
import model.Penerbangan;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Adbang 18
 */

public class PenerbanganDAOHibernate implements PenerbanganDAO {

    @Override
    public void simpan(Penerbangan penerbangan) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            session.persist(penerbangan);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new DataAccessException("Gagal menyimpan penerbangan: " + e.getMessage(), e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Penerbangan> ambilSemua() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Penerbangan p order by p.kodePenerbangan", Penerbangan.class).list();
        } catch (RuntimeException e) {
            throw new DataAccessException("Gagal mengambil data penerbangan: " + e.getMessage(), e);
        }
    }

    @Override
    public Penerbangan cariByKode(String kode) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Penerbangan p where p.kodePenerbangan = :kode", Penerbangan.class)
                    .setParameter("kode", kode)
                    .uniqueResult();
        } catch (RuntimeException e) {
            throw new DataAccessException("Gagal mencari penerbangan: " + e.getMessage(), e);
        }
    }
}