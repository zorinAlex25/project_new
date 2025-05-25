package tables;

import jakarta.persistence.*;

import java.sql.Date;

// @Embeddable
@Entity
@Table(name = "Project")
public class Project {
    @Id
    @GeneratedValue //(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "pr_name")
    private String prName; // название пр-та (name)
    @Column(name = "appl_field")
    private String applField; // обл-ть применения (scope)
    @Column(name = "date")
    private Date date; // дата начала пр-та (start date)

    public Project(int id, String pr_name, String appl_field, Date date)
    {
        this.id = id;
        this.prName = pr_name;
        this.applField = appl_field;
        this.date = date;
    }

    public Project()
    {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPr_name() {
        return prName;
    }

    public void setPrName(String pr_name) {
        this.prName = pr_name;
    }

    public String getApplField() {
        return applField;
    }

    public void setApplField(String appl_field) {
        this.applField = appl_field;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}

/*
Создаём таблицу проекта
CREATE TABLE Project (
id SERIAL PRIMARY KEY,
pr_name varchar(200), -- название пр-та (name)
appl_field varchar(200), -- обл-ть применения (scope)
s_date date -- дата начала пр-та (start date)
)
 */
