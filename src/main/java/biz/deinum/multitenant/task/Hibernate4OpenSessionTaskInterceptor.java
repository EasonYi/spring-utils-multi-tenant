package biz.deinum.multitenant.task;

import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import biz.deinum.multitenant.task.TaskInterceptor;

public class Hibernate4OpenSessionTaskInterceptor implements TaskInterceptor {

	protected final Log logger = LogFactory.getLog(getClass());

	private final SessionFactory sessionFactory;

	public Hibernate4OpenSessionTaskInterceptor(SessionFactory sessionFactory) {
		Objects.requireNonNull(sessionFactory);
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * Open a new Hibernate {@code Session} according and bind it to the thread via the
	 * {@link org.springframework.transaction.support.TransactionSynchronizationManager}.
	 */
	@Override
	public void beforeExecution() throws DataAccessException {
		logger.debug("Opening Hibernate Session in HibernateOpenSessionTaskInterceptor");
		Session session = openSession();
		SessionHolder sessionHolder = new SessionHolder(session);
		TransactionSynchronizationManager.bindResource(this.sessionFactory, sessionHolder);
	}

	/**
	 * Unbind the Hibernate {@code Session} from the thread and close it).
	 * @see org.springframework.transaction.support.TransactionSynchronizationManager
	 */
	@Override
	public void afterExecution() throws DataAccessException {
		SessionHolder sessionHolder =
				(SessionHolder) TransactionSynchronizationManager.unbindResource(this.sessionFactory);
		logger.debug("Closing Hibernate Session in HibernateOpenSessionTaskInterceptor");
		SessionFactoryUtils.closeSession(sessionHolder.getSession());
	}

	/**
	 * Open a Session for the SessionFactory that this interceptor uses.
	 * <p>The default implementation delegates to the {@link SessionFactory#openSession}
	 * method and sets the {@link Session}'s flush mode to "MANUAL".
	 * @return the Session to use
	 * @throws DataAccessResourceFailureException if the Session could not be created
	 * @see org.hibernate.FlushMode#MANUAL
	 */
	private Session openSession() throws DataAccessResourceFailureException {
		try {
			Session session = this.sessionFactory.openSession();
			session.setFlushMode(FlushMode.MANUAL);
			return session;
		}
		catch (HibernateException ex) {
			throw new DataAccessResourceFailureException("Could not open Hibernate Session", ex);
		}
	}
}
