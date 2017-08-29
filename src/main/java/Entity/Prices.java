package Entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
public class Prices {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Basic
    @Column(name = "PRODUCT_CODE", nullable = true)
    private Integer productCode;

    @Basic
    @Column(name = "NUMBER", nullable = true)
    private Integer number;

    @Basic
    @Column(name = "DEPART", nullable = true)
    private Integer depart;

    @Basic
    @Column(name = "BEGIN", nullable = true)
    private Timestamp begin;

    @Basic
    @Column(name = "END", nullable = true)
    private Timestamp end;

    @Basic
    @Column(name = "VALUE", nullable = true)
    private Integer value;

    public Prices() {
    }

    public Prices(Integer productCode, Integer number, Integer depart, Timestamp begin, Timestamp end, Integer value) {
        this.productCode = productCode;
        this.number = number;
        this.depart = depart;
        this.begin = begin;
        this.end = end;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getProductCode() {
        return productCode;
    }

    public void setProductCode(Integer productCode) {
        this.productCode = productCode;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getDepart() {
        return depart;
    }

    public void setDepart(Integer depart) {
        this.depart = depart;
    }

    public Timestamp getBegin() {
        return begin;
    }

    public void setBegin(Timestamp begin) {
        this.begin = begin;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Prices{" +
                "id=" + id +
                ", productCode=" + productCode +
                ", number=" + number +
                ", depart=" + depart +
                ", begin=" + begin +
                ", end=" + end +
                ", value=" + value +
                '}';
    }
}
