import DataProcessing.DataProcess;
import Entity.NewPrices;
import Entity.Prices;
import Hibernate.HibernateSessionFactory;
import org.hibernate.HibernateException;
import org.hibernate.Metamodel;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.metamodel.EntityType;

import java.util.List;
import java.util.Map;

public class Main {

    public static void main(final String[] args) throws Exception {
        try {
            List<NewPrices> newPricesList = DataProcess.getNewPrices();
            DataProcess.updatePrices(newPricesList);
        } finally {
            HibernateSessionFactory.shutdown();
            System.out.println("END");
        }
    }
}