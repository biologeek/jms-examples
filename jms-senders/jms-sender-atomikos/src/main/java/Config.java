import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.XAConnectionFactory;
import javax.sql.XADataSource;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.h2.jdbcx.JdbcDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.atomikos.jms.AtomikosConnectionFactoryBean;

@Configuration
@EnableJms
/**
 * @see https://github.com/nielspeter/atomikos-jta-jdbc-jms-example/blob/master/src/main/resources/applicationContext.xml
 * @see http://download.oracle.com/otndocs/jcp/jta-1.1-spec-oth-JSpec/?submit=Download
 * 
 * @author xavier
 *
 */
public class Config {
	
	
	@Bean
	/**
	 * 					
	 * @return
	 * @throws SystemException
	 */
	public JtaTransactionManager jtaTransactionManager() throws SystemException {
		JtaTransactionManager manager = new JtaTransactionManager();
		manager.setTransactionManager(atomikosTransactionManager());	
		manager.setUserTransaction(userTransaction());
		return manager;
	}
	
	@Bean
	public UserTransaction userTransaction() throws SystemException {
		UserTransactionImp imp = new UserTransactionImp();
		imp.setTransactionTimeout(30);
		return imp;
	}

	@Bean(initMethod="init", destroyMethod="close")
	public UserTransactionManager atomikosTransactionManager() {
		return new UserTransactionManager();
	}

	/* *********************************************** */
	/* ****       1 er participant : la BDD       **** */
	/* *********************************************** */
	@Bean
	public AtomikosDataSourceBean atomikosDataSourceBean() {
		AtomikosDataSourceBean bean = new AtomikosDataSourceBean();
		
		bean.setXaDataSource(datasource());
		bean.setTestQuery("select 1");
		bean.setUniqueResourceName("AtomikosDataSource");
		bean.setPoolSize(10);
		return bean;
	}
	
	public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean() {
		LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
		bean.setDataSource(atomikosDataSourceBean());
		bean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		return bean;
	}
	
	
	@Bean
	public XADataSource datasource() {
		JdbcDataSource ds = new JdbcDataSource();
		ds.setURL("jdbc:h2:mem:db");
		ds.setUser("sa");
		ds.setPassword("sa");
		return ds;
	}


	/* *********************************************** */
	/* ****   2ème participant : le broker JMS    **** */
	/* *********************************************** */
	@Bean
	public AtomikosConnectionFactoryBean atomikosConnectionFactoryBean() {
		AtomikosConnectionFactoryBean bean = new AtomikosConnectionFactoryBean();
		bean.setXaConnectionFactory(activeMQXAConnectionFactory());
		return bean;
	}

	@Bean
	public JmsTemplate template() {
		JmsTemplate tpl = new JmsTemplate();
		tpl.setConnectionFactory(atomikosConnectionFactoryBean());		
		tpl.setPubSubDomain(true);
		tpl.setDefaultDestination(topic());
		
		return tpl;
	}
	
	@Bean
	public Destination topic() {
		return new ActiveMQTopic("testTopic");
	}

	@Bean
	public XAConnectionFactory activeMQXAConnectionFactory() {
		return new ActiveMQXAConnectionFactory("tcp://localhost:61616");
	}

	@Bean
	public Session session() throws JMSException {
		return activeMQXAConnectionFactory().createXAConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
	}
}
