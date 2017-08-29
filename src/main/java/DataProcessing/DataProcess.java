package DataProcessing;

import Entity.NewPrices;
import Entity.Prices;
import Hibernate.HibernateSessionFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DataProcess {
    private static Session session;

    static {
        createBDConnection();
    }

    public static List<Prices> getOldPrices(Integer productCode, Integer numb, Integer depart) {
        List<Prices> oldPricesList = new ArrayList<Prices>();
        try {
            Query query = session.createQuery("from Prices p " +
                    "where p.productCode = :productCode " +
                    "and p.number = :numb " +
                    "and p.depart = :depart ");
            query.setParameter("productCode", productCode);
            query.setParameter("numb", numb);
            query.setParameter("depart", depart);
            oldPricesList = query.getResultList();
            return oldPricesList;
        } catch (Exception e) {
            System.out.println("Что то пошло не так при получении листа Prices : " + e);
            session.close();
        }
        return oldPricesList;
    }

    public static List<NewPrices> getNewPrices() {
        List<NewPrices> newPricesList = new ArrayList<NewPrices>();
        try {
            Query query = session.createQuery("from NewPrices");
            newPricesList = query.getResultList();
            return newPricesList;
        } catch (Exception e) {
            System.out.println("Что то пошло не так при получении листа NewPrices : " + e);
            session.close();
        }
        return newPricesList;
    }

    public static void updatePrices(List<NewPrices> newPricesList) {
        for (int i = 0; i < newPricesList.size(); i++) {
            NewPrices newPrice = newPricesList.get(i);
            Integer productCode = newPrice.getProductCode();
            Integer number = newPrice.getNumber();
            Integer depart = newPrice.getDepart();
            Timestamp begin = newPrice.getBegin();
            Timestamp end = newPrice.getEnd();
            Integer value = newPrice.getValue();
            List<Prices> oldPricesList = getOldPrices(productCode, number, depart);
            // если нет старой записи то создадим ее
            if (oldPricesList == null) {
                saveDataForNewPrice(newPrice);
                continue;
            }

            for (int j = 0; j < oldPricesList.size(); j++) {
                Prices oldPrice = oldPricesList.get(j);
                // если цена пустая
                if (oldPrice.getValue() == null) {
                    oldPrice.setValue(value);
                    oldPrice.setBegin(begin);
                    oldPrice.setEnd(end);
                    Transaction tx = session.getTransaction();
                    tx.begin();
                    session.update(oldPrice);
                    tx.commit();
                }
                // если цены равны и ...
//                if (oldPrice.getValue().equals(value)) {
//                    updateDataForEqualsPrices(newPrice, oldPrice);
//                } else {
                // если дата начала новой цены больше даты начала старой, но меньше конца
                // и дата окончания новой цены больше даты окончания старой цены
//                    if (begin.after(oldPrice.getBegin())
//                            && (begin.before(oldPrice.getEnd()) || begin.equals(oldPrice.getEnd()))
//                            && end.after(oldPrice.getEnd())) {
//                        oldPrice.setEnd(begin);
//                        Transaction tx = session.getTransaction();
//                        tx.begin();
//                        session.update(oldPrice);
//                        tx.commit();
//                        Prices price = new Prices(productCode, number, depart, begin, end, value);
//                        tx.begin();
//                        session.save(price);
//                        tx.commit();
//
//                    }
//                     если дата начала новой цены больше даты начала старой, но меньше\равна концу
                // , а дата окончания новой цены меньше даты окончания старой цены
//                    if (begin.after(oldPrice.getBegin()) && (begin.before(oldPrice.getEnd()) || begin.equals(oldPrice.getEnd())) && end.before(oldPrice.getEnd())) {
//                        Timestamp oldPriceDateEnd = oldPrice.getEnd();
//                        oldPrice.setEnd(begin);
//                        Transaction tx = session.getTransaction();
//                        tx.begin();
//                        session.update(oldPrice);
//                        tx.commit();
//                        Prices price = new Prices(productCode, number, depart, begin, end, value);
//                        tx.begin();
//                        session.save(price);
//                        tx.commit();
//                        Prices price2 = new Prices(productCode, number, depart, end, oldPriceDateEnd, oldPrice.getValue());
//                        tx.begin();
//                        session.save(price2);
//                        tx.commit();
//                    }
//                    if (begin.after(oldPrice.getEnd())) {
//                        saveDataForNewPrice(newPrice);
//                    }
//                }
            }
        }
    }

//    private static void updateDataForEqualsPrices(NewPrices newPrice, Prices oldPrice) {
//        // если новая дата до старой даты, не знаю нужно ли обновлять, но обновлю
//        if (newPrice.getBegin().before(oldPrice.getBegin())) {
//            oldPrice.setBegin(newPrice.getBegin());
//        }
//        // если новая дата после старой даты и
//        // начало нд до окончания старой даты
//        if (newPrice.getEnd().after(oldPrice.getEnd()) && newPrice.getBegin().before(oldPrice.getBegin())) {
//            oldPrice.setEnd(newPrice.getEnd());
//        }
//        Transaction tx = session.beginTransaction();
//        session.update(oldPrice);
//        session.flush();
//        tx.commit();
//    }

    private static void saveDataForNewPrice(NewPrices newPrice) {
        Transaction tx = session.beginTransaction();
        Prices oldPrice = new Prices(
                newPrice.getProductCode()
                , newPrice.getNumber()
                , newPrice.getDepart()
                , newPrice.getBegin()
                , newPrice.getEnd()
                , newPrice.getValue());
        session.save(oldPrice);
        session.flush();
        tx.commit();
    }


    private static void createBDConnection() {
        // создаем коннект к БД (сессию для хибера)
        System.out.println("Try create connection to BD");
        session = HibernateSessionFactory.getSessionFactory().openSession();
        System.out.println("Connection success");
    }
}
