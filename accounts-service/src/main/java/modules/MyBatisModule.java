package modules;

import com.google.inject.name.Names;
import main.ServerConfiguration;
import mappers.AccountMapper;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.mybatis.guice.datasource.builtin.PooledDataSourceProvider;
import java.util.Properties;

public class MyBatisModule extends org.mybatis.guice.MyBatisModule {

    private final ServerConfiguration serverConfiguration;

    public MyBatisModule(ServerConfiguration serverConfiguration){
        this.serverConfiguration = serverConfiguration;
    }

    @Override
    protected void initialize() {
        bindDataSourceProviderType(PooledDataSourceProvider.class);
        bindTransactionFactoryType(JdbcTransactionFactory.class);
        Names.bindProperties(this.binder(), createProperties());
        addMapperClass(AccountMapper.class);
        bind(DefaultObjectWrapperFactory.class);
        bind(DefaultObjectFactory.class);


    }

    private Properties createProperties() {
        Properties myBatisProperties = new Properties();
        String jdbcUri = "jdbc:mysql://"+serverConfiguration.getDataBaseHost()+":"
                +serverConfiguration.getDataBasePort()+"/"+serverConfiguration.getDataBaseName();
        myBatisProperties.setProperty("mybatis.environment.id", "development");
        myBatisProperties.setProperty("JDBC.driver", "com.mysql.jdbc.Driver");
        myBatisProperties.setProperty("JDBC.url", jdbcUri);
        myBatisProperties.setProperty("JDBC.username", serverConfiguration.getDataBaseUser());
        myBatisProperties.setProperty("JDBC.password", serverConfiguration.getDataBasePassword());
        myBatisProperties.setProperty("JDBC.autoCommit", "false");
        return myBatisProperties;

    }

}
