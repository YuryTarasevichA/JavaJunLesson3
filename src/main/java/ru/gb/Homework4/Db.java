package ru.gb.Homework4;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.sql.*;

/**
 * Повторить все, что было на семниаре на таблице Student с полями
 * 1. id - bigint
 * 2. first_name - varchar(256)
 * 3. second_name - varchar(256)
 * 4. group - varchar(128)
 * <p>
 * Написать запросы:
 * 1. Создать таблицу
 * 2. Наполнить таблицу данными (insert)
 * 3. Поиск всех студентов
 * 4. Поиск всех студентов по имени группы
 * <p>
 * Доп. задания:
 * 1. ** Создать таблицу group(id, name); в таблице student сделать внешний ключ на group
 * 2. ** Все идентификаторы превратить в UUID
 * <p>
 * Замечание: можно использовать ЛЮБУЮ базу данных: h2, postgres, mysql, ...
 */

/**
 * Перенести структуру дз третьего урока на JPA:
 * 1. Описать сущности Student и Group
 * 2. Написать запросы: Find, Persist, Remove
 * 3. ... поупражняться с разными запросами ...
 */

public class Db {
    private static final String URL = "jdbc:mysql://localhost:3307/";
    private static final String DB_NAME = "STUDENTS";
    private static final String USER = "root";
    private static final String PASSWORD = "admin";


    public static void con() {
        try {
            Connection con = DriverManager.getConnection(URL + DB_NAME, USER, PASSWORD);
            Statement stmt = con.createStatement();
            //stmt.execute("DROP SCHEMA STUDENTS");
            //stmt.execute("CREATE SCHEMA STUDENTS");

            // Создание таблицы студентов
            String createTableQuery = "CREATE TABLE IF NOT EXISTS students (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                    "first_name VARCHAR(256)," +
                    "second_name VARCHAR(256)," +
                    "group_name VARCHAR(128)" +
                    ")";
            stmt.executeUpdate(createTableQuery);

            // Наполнение таблицы данными
            String insertDataQuery = "INSERT INTO students (first_name, second_name, group_name) VALUES " +
                    "('Иван', 'Иванов', 'Группа 1')," +
                    "('Петр', 'Петров', 'Группа 1')," +
                    "('Юрий', 'Тарасевич', 'Группа 1')," +
                    "('Игорь', 'Наумов', 'Группа 2')," +
                    "('Вася', 'Дмитриенко', 'Группа 2')," +
                    "('Аня', 'Васильева', 'Группа 2')";
            stmt.executeUpdate(insertDataQuery);

            // Поиск всех студентов
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM students");
            while (resultSet.next()) {
                System.out.println(resultSet.getString("first_name") + " " + resultSet.getString("second_name"));
            }

            // Поиск всех студентов по имени группы
            String groupName = "Группа 1";
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM students WHERE group_name = ?");
            preparedStatement.setString(1, groupName);
            ResultSet groupResultSet = preparedStatement.executeQuery();
            while (groupResultSet.next()) {
                System.out.println(groupResultSet.getString("first_name") + " " + groupResultSet.getString("second_name"));
            }

            con.close();

        } catch (SQLException e) {
            System.err.println("Не удалось подключиться к БД: " + e.getMessage());
        }
    }

    public static Student findStudentById(Long id) {
        EntityManager entityManager = getEntityManager();
        return entityManager.find(Student.class, id);
    }

    public static void persistStudent(Student student) {
        EntityManager entityManager = getEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(student);
        entityManager.getTransaction().commit();
    }

    public static void removeStudent(Long id) {
        EntityManager entityManager = getEntityManager();
        entityManager.getTransaction().begin();
        Student student = entityManager.find(Student.class, id);
        if (student != null) {
            entityManager.remove(student);
        }
        entityManager.getTransaction().commit();
    }

    private static EntityManager getEntityManager() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("persistenceUnitName");
        return entityManagerFactory.createEntityManager();
    }
}
