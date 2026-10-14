/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package config;
import util.DataAccessException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Penerbangan;
import model.Penumpang;
import model.Tiket;
import model.TiketBisnis;
import model.TiketEkonomi;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author Adbang 18
 */

public class HibernateUtil {

    private static final Logger LOG_HIBERNATE = Logger.getLogger("org.hibernate");
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                LOG_HIBERNATE.setLevel(Level.WARNING);

                Configuration cfg = new Configuration();
                cfg.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
                cfg.setProperty("hibernate.connection.url", Koneksi.getUrl());
                cfg.setProperty("hibernate.connection.username", Koneksi.getUser());
                cfg.setProperty("hibernate.connection.password", Koneksi.getPassword());
                cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");

                cfg.addAnnotatedClass(Penerbangan.class);
                cfg.addAnnotatedClass(Penumpang.class);
                cfg.addAnnotatedClass(Tiket.class);
                cfg.addAnnotatedClass(TiketEkonomi.class);
                cfg.addAnnotatedClass(TiketBisnis.class);

                sessionFactory = cfg.buildSessionFactory();
            } catch (RuntimeException e) {
                throw new DataAccessException("Gagal menyiapkan Hibernate: " + e.getMessage(), e);
            }
        }
        return sessionFactory;
    }

    public static void tutup() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
    }
}