package com.bobisonfire.web;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

/**
 * Класс-утилита, используется для доступа к сессии и обмена данных с БД.
 *
 * Основывается на SessionFactory в библиотеке доступа к БД Hibernate.
 * @author Nikita Akatyev
 * @see ORMHistoryNode
 * @version 1.0.0
 */
public class HistoryDataAccess implements AutoCloseable {
    private static final SessionFactory factory = new Configuration().configure().buildSessionFactory();
    private final Session session;

    public HistoryDataAccess() {
        session = factory.openSession();
    }

    /**
     * Извлечение истории поиска из БД.
     * @return список объектов, отображающий историю и отсортированный по ID.
     */
    public List<ORMHistoryNode> getNodes() {
        session.beginTransaction();
        List<ORMHistoryNode> list = session.createQuery("from history order by id", ORMHistoryNode.class).list();
        session.getTransaction().commit();
        return list;
    }

    /**
     * Добавление нового объекта истории поиска в БД.
     *
     * При добавлении в базу используется метод <i>persist</i> - это гарантирует,
     * что состояние истории не будет изменено вне данной транзакции.
     * @param node объект, который необходимо добавить в БД.
     */
    public void addNode(ORMHistoryNode node) {
        session.beginTransaction();
        session.persist(node);
        session.getTransaction().commit();
    }

    @Override
    public void close() {
        session.close();
    }
}