package tables;

import jakarta.persistence.*;

/*
Создаём таблицу устройства, связанную с таблицей проекта
CREATE TABLE Device(
t_code int, -- код типа
m_code int, -- код модели
dev_name varchar(200), -- название устр-ва
voltage int, -- напр-е
power int, -- мощность
price int, -- цена
pr_id int, -- ключ проекта

FOREIGN KEY (pr_id) REFERENCES Project(id),
-- привязываем ключ проекта к таблице проектов

CONSTRAINT device_type_model
PRIMARY KEY(t_code, m_code)
-- делаем из типа и модели первичный ключ
)
 */

@Entity
public class Device {
    @Id
    private int t_code;
    @Id
    private int m_code;

    private String dev_name; // название устр-ва
    private int voltage;
    private int power;
    private int price;
    private int pr_id;

    @ManyToOne
    @JoinColumn(name = "pr_id",
            foreignKey = @ForeignKey(name = "device_pr_id_fkey")
    )
    // name = "pr_id" - название столбца
    // name = "device_pr_id_fkey" - название ограничения

    public int getT_code() {
        return t_code;
    }

    public void setT_code(int t_code) {
        this.t_code = t_code;
    }

    public int getM_code() {
        return m_code;
    }

    public void setM_code(int m_code) {
        this.m_code = m_code;
    }

    public String getDev_name() {
        return dev_name;
    }

    public void setDev_name(String dev_name) {
        this.dev_name = dev_name;
    }

    public int getVoltage() {
        return voltage;
    }

    public void setVoltage(int voltage) {
        this.voltage = voltage;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPr_id() {
        return pr_id;
    }

    public void setPr_id(int pr_id) {
        this.pr_id = pr_id;
    }
}
