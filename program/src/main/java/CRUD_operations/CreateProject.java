package CRUD_operations;

import CRUD_operations.tables.Project;
import util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.Date;

public class CreateProject {
    public static void createProject() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Date date = Date.valueOf("2022-12-27"); // 2022-12-27

            Project project = new Project(1,"123","123", date);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
