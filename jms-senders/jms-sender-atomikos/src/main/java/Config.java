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
	public JtaTransactionManager transactionManager() throws SystemException {
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
		// TODO Auto-generated method stub
		return null;
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

	@Bean
	public JmsTemplate template() {
		JmsTemplate tpl = new JmsTemplate();
		tpl.setConnectionFactory(atomikosConnectionFactoryBean());		
		//tpl.setPubSubDomain(true);
		tpl.setDefaultDestination(topic());
		
		return tpl;
	}


	/* *********************************************** */
	/* ****   2ème participant : le broker JMS    **** */
	/* *********************************************** */
	@Bean
	public AtomikosConnectionFactoryBean atomikosConnectionFactoryBean() {
		AtomikosConnectionFactoryBean bean = new AtomikosConnectionFactoryBean();
		bean.setXaConnectionFactory(activeMQConnectionFactory());
		return bean;
	}
	
	@Bean
	public Destination topic() {
		return new ActiveMQTopic("testTopic");
	}

	@Bean
	public XAConnectionFactory activeMQConnectionFactory() {
		return new ActiveMQXAConnectionFactory("tcp://localhost:61616");
	}

	@Bean
	public Session session() throws JMSException {
		return activeMQConnectionFactory().createXAConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
	}
}
