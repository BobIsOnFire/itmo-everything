package com.bobisonfire;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class HistoryDataAccess implements AutoCloseable {
    private static SessionFactory factory = new Configuration().configure().buildSessionFactory();

    private Session session;

    public HistoryDataAccess() {
        session = factory.openSession();
    }

    public List<ORMHistoryNode> getNodes() {
        session.beginTransaction();
        List<ORMHistoryNode> list = session.createQuery("from history order by id", ORMHistoryNode.class).list();
        session.getTransaction().commit();
        return list;
    }

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
