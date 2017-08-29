package Entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "new_prices", schema = "javastudy", catalog = "")
public class NewPrices {

    @Id
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
        return "NewPrices{" +
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
