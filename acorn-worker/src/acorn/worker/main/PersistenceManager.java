package acorn.worker.main;


import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author kuba
 */
public class PersistenceManager {
 
  private static final PersistenceManager singleton = new PersistenceManager();
  
  protected EntityManagerFactory emf;
  
  public static PersistenceManager getInstance() {
    return singleton;
  }
  
  public EntityManagerFactory getEntityManagerFactory() {
    
    if (emf == null)
      createEntityManagerFactory();
    return emf;
  }
  
  public void closeEntityManagerFactory() {
    
    if (emf != null) {
      emf.close();
      emf = null;
    }
  }
  
  protected void createEntityManagerFactory() {
    this.emf = Persistence.createEntityManagerFactory("acorn-dbPU");
  }
}
