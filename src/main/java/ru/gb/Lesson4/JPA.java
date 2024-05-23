package ru.gb.Lesson4;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class JPA {
    public static void main(String[] args) throws SQLException {
        // JPA Спецификация для работы с БД через ORM
        // Набор интерфейсов и аннотаций (jakarta....)
        // Hibernate Реализация JPA
        // EclipseLink - одна из реализаций JPA

        // Entity - сущность

        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3307/USERS", "root", "admin")) {
//      prepareTables(connection);
            run(connection);
        }
    }

    private static void prepareTables(Connection connection) throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute("""
        create table users(
          id bigint,
          login varchar(256),
          active boolean
        )
        """);
        }

        try (Statement st = connection.createStatement()) {
            st.execute("""
        insert into users(id, login, active) values
          (11, 'inchestnov', true),
          (2, 'john', true),
          (3, 'peter', false)
        """);
        }

    }

    private static void run(Connection connection) throws SQLException {
        // Hikari Connection Pool

        Configuration configuration = new Configuration().configure();
        try (SessionFactory sessionFactory = configuration.buildSessionFactory()) {
//      hibernateCrud(sessionFactory);

            try (Session session = sessionFactory.openSession()) {
                User user = new User();
                user.setId(123L);
                user.setLogin("pet_owner");

                Pet pet = new Pet();
                pet.setId(1L);
                pet.setName("pet");
                pet.setOwner(user);

                Transaction tx = session.beginTransaction();
                session.persist(user);
                session.persist(pet);
                tx.commit();
            }

            User user;
            try (Session session = sessionFactory.openSession()) {
                user = session.find(User.class, 123L);
//        System.out.println("AFTER SELECT");
//        System.out.println("AFTER RELOAD");
            }

            System.out.println(user.getPets());
            System.out.println(user);

//      System.out.println();
//      System.out.println();
//      System.out.println();
//      try (Session session = sessionFactory.openSession()) {
//        List<User> users = session.createQuery("select u from User u where u.login in ('inchestnov', 'peter')", User.class).getResultList();
//        System.out.println(users);
//      }

        }
    }

    private static void hibernateCrud(SessionFactory sessionFactory) {
        User user = new User();
        user.setId(1L);
        user.setLogin("Igor");
        user.setActive(Boolean.TRUE);
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(user); // INSERT
            tx.commit();
        }

        // SELECT
        try (Session session = sessionFactory.openSession()) {
            User _user = session.find(User.class, 1L);
            System.out.println(_user);
        }

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(user); // UPDATE
            tx.commit();
        }

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(user); // DELETE
            tx.commit();
        }

        // SELECT
        try (Session session = sessionFactory.openSession()) {
            User savedUser = session.find(User.class, 4L);
            System.out.println(savedUser);
        }
    }

}

