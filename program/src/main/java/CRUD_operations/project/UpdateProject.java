package CRUD_operations.project;

import org.hibernate.Session;
import org.hibernate.Transaction;
import CRUD_operations.tables.Project;
import util.HibernateUtil;

public class UpdateProject {
    public static void updateProject(Long userId, String newPrName, String newApplField) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            Project project = session.get(Project.class, userId);
            if (project != null) {
                transaction = session.beginTransaction();

                project.setPrName(newPrName);
                project.setApplField(newApplField);
                session.update(project);
                transaction.commit();
            } else {
                System.out.println("User not found!");
            }
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
