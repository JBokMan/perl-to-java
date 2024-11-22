package com.sippy.wrapper.parent.database;

import com.sippy.wrapper.parent.database.dao.TnbDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class DatabaseConnection {

  @PersistenceContext(unitName = "CustomDB")
  private EntityManager entityManager;

  private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConnection.class);

  public int countTheEntries() {
    return ((Number) entityManager.createNativeQuery("SELECT COUNT(*) FROM tnbs").getSingleResult())
        .intValue();
  }

  @SuppressWarnings("unchecked")
  public List<TnbDao> getAllTnbs() {
    Query query = entityManager.createNativeQuery("SELECT * FROM tnbs", TnbDao.class);
    return query.getResultList();
  }

  public List<TnbDao> getTnbsByNumber(String number) {
    Query query = entityManager.createQuery("SELECT * FROM tnbs WHERE tnb = :number", TnbDao.class);
    query.setParameter("number", number);
    return query.getResultList();
  }
}
