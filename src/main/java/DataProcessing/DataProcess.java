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

    private static List<Prices> getOldPrices(Integer productCode, Integer numb, Integer depart) {
        List<Prices> oldPricesList = new ArrayList<Prices>();
        try {
            Query query = session.createQuery("from Prices p " +
                    "where p.productCode = :productCode " +
                    "and p.number = :numb " +
                    "and p.depart = :depart  order by p.begin");
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
        List<Prices> rezList = new ArrayList<Prices>();
        for (int i = 0; i < newPricesList.size(); i++) {
            NewPrices newPrice = newPricesList.get(i);

            List<Prices> oldPricesList = getOldPrices(newPrice.getProductCode(), newPrice.getNumber(), newPrice.getDepart());
            // если нет старой записи то создадим ее
            if (oldPricesList == null) {
                saveDataForNewPrice(newPrice);
                continue;
            }
            for (int j = 0; j < oldPricesList.size(); j++) {
                Prices oldPrice = oldPricesList.get(j);

                Prices tmpPrice = new Prices(newPrice.getProductCode()
                        , newPrice.getNumber()
                        , newPrice.getDepart()
                        , newPrice.getBegin()
                        , newPrice.getEnd()
                        , newPrice.getValue());
                if (!oldPrice.equals(tmpPrice)) {
                    if (newPrice.getBegin().after(oldPrice.getBegin())
                            && newPrice.getBegin().before(oldPrice.getEnd())
                            && newPrice.getEnd().before(oldPrice.getEnd())) {
                        if (!newPrice.getValue().equals(oldPrice.getValue())) {
                            Timestamp temp = oldPrice.getEnd();
                            oldPrice.setEnd(newPrice.getBegin());

                            Prices createPrice = new Prices(
                                    oldPrice.getProductCode()
                                    , oldPrice.getNumber()
                                    , oldPrice.getDepart()
                                    , newPrice.getEnd()
                                    , temp
                                    , oldPrice.getValue());
                            rezList.add(createPrice);
                        }
                    }
                    // если цены совпадают то продлеваем период или создаем новую запись
                    if (newPrice.getValue().equals(oldPrice.getValue())) {
                        if (newPrice.getBegin().after(oldPrice.getBegin()) || newPrice.getBegin().equals(oldPrice.getEnd())) {
                            oldPrice.setEnd(newPrice.getEnd());
                            newPricesList.remove(newPrice);
                            i--;
                        }
                    }
                    if (newPrice.getBegin().equals(oldPrice.getBegin())
                            && newPrice.getEnd().before(oldPrice.getEnd())
                            && newPrice.getEnd().before(oldPrice.getEnd())) {
                        oldPrice.setBegin(newPrice.getEnd());
                    }
                    if (newPrice.getEnd().equals(oldPrice.getEnd())
                            && newPrice.getEnd().before(oldPrice.getEnd())
                            && newPrice.getBegin().after(oldPrice.getBegin())) {
                        oldPrice.setEnd(newPrice.getBegin());
                    }
                    if ((newPrice.getBegin().after(oldPrice.getBegin()) || newPrice.getBegin().equals(oldPrice.getBegin()))
                            && newPrice.getBegin().before(oldPrice.getEnd())
                            && newPrice.getEnd().after(oldPrice.getEnd())) {
                        if (newPrice.getValue().equals(oldPrice.getValue())) {
                            oldPrice.setEnd(newPrice.getEnd());
                        } else {
                            oldPrice.setEnd(newPrice.getBegin());
                        }
                    }
                    if (newPrice.getBegin().before(oldPrice.getBegin())
                            && newPrice.getEnd().after(oldPrice.getBegin())
                            && newPrice.getEnd().before(oldPrice.getEnd())) {
                        if (newPrice.getValue().equals(oldPrice.getValue())) {
                            oldPrice.setBegin(newPrice.getBegin());
                        } else {
                            oldPrice.setBegin(newPrice.getEnd());
                        }
                    }
                    if ((newPrice.getEnd().after(oldPrice.getBegin()) || newPrice.getEnd().equals(oldPrice.getBegin()))
                            && newPrice.getEnd().before(oldPrice.getEnd())
                            && newPrice.getBegin().before(oldPrice.getBegin())) {
                        oldPrice.setBegin(newPrice.getEnd());
                    }
                } else {
                    newPricesList.remove(newPrice);
                    i--;
                }
            }
            // ищем цены для удаления и апдэйтим
            for (Prices oldPrice : oldPricesList) {
                if (!oldPrice.getBegin().equals(oldPrice.getEnd())) {
                    Transaction tx = session.getTransaction();
                    tx.begin();
                    session.update(oldPrice);
                    tx.commit();
                } else {
                    Transaction tx = session.getTransaction();
                    tx.begin();
                    session.remove(oldPrice);
                    tx.commit();
                }
            }
            for (Prices prices : rezList) {
                Transaction tx = session.getTransaction();
                tx.begin();
                session.save(prices);
                tx.commit();
            }
        }
        for (NewPrices newPrices : newPricesList) {
            // добавляем новые цены. если цены совпадают не добавляем
            Prices createPrice = new Prices(
                    newPrices.getProductCode()
                    , newPrices.getNumber()
                    , newPrices.getDepart()
                    , newPrices.getBegin()
                    , newPrices.getEnd()
                    , newPrices.getValue());
            Transaction tx = session.getTransaction();
            tx.begin();
            session.save(createPrice);
            tx.commit();
        }


    }

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
