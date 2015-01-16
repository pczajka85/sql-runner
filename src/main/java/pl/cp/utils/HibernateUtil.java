package pl.cp.utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class HibernateUtil {

	private static SessionFactory sessionFactory;
	private static ServiceRegistry serviceRegistry;

	public static SessionFactory createSessionFactory() {
		try {
			Configuration conf = new Configuration();
			conf.configure();
			serviceRegistry = new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry();
			sessionFactory = conf.buildSessionFactory(serviceRegistry);
			return sessionFactory;
		} catch (Throwable ex) {
			System.err.println("Database connection error :) "+ ex);
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	
}
