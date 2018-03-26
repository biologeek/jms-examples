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
import org.springframework.context.annotation.DependsOn;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
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
 * @see https://www.javaworld.com/article/2077714/java-web-development/xa-transactions-using-spring.html
 * 
 * @author xavier
 *
 */
@EnableTransactionManagement
public class BeansConfig {
	
	
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
	@DependsOn(value= {"atomikosDataSourceBean", "atomikosConnectionFactoryBean"})
	public UserTransactionManager atomikosTransactionManager() {
		return new UserTransactionManager();
	}

	/* *********************************************** */
	/* ****         1st participant : DB          **** */
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
	
	@Bean(name="entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
		bean.setDataSource(atomikosDataSourceBean());
		bean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		bean.setPackagesToScan("io.biologeek");
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
	/* ****    2nd participant : JMS broker       **** */
	/* *********************************************** */
	@Bean
	public AtomikosConnectionFactoryBean atomikosConnectionFactoryBean() {
		AtomikosConnectionFactoryBean bean = new AtomikosConnectionFactoryBean();
		bean.setXaConnectionFactory(activeMQXAConnectionFactory());
		bean.setUniqueResourceName("atomikosConnectionFactory");
		bean.setLocalTransactionMode(true);
		return bean;
	}

	@Bean
	public JmsTemplate template() {
		JmsTemplate tpl = new JmsTemplate();
		tpl.setConnectionFactory(atomikosConnectionFactoryBean());		
		tpl.setPubSubDomain(true);
		tpl.setDefaultDestination(topic());
		tpl.setSessionTransacted(true);
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

}
