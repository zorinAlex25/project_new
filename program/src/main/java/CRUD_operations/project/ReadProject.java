package CRUD_operations.project;

import org.hibernate.Session;
import CRUD_operations.tables.Project;
import util.HibernateUtil;
public class ReadProject
{
    public static void readProject(Long userId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Project project = session.get(Project.class, userId);
            if (project != null) {
                System.out.println("Project details: " + project.getPr_name() + ", " + project.getId());
            } else {
                System.out.println("Project not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
