package modules;

import com.google.inject.name.Names;
import main.ServerConfiguration;
import mappers.UserMapper;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.mybatis.guice.datasource.builtin.PooledDataSourceProvider;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;

import javax.inject.Singleton;
import javax.inject.Inject;
import java.util.Properties;

public class MyBatisModule extends org.mybatis.guice.MyBatisModule {

//    private ServerConfiguration serverConfiguration;
//    @Inject
//    @Singleton
//    public MyBatisModule(ServerConfiguration serverConfiguration){
//        this.serverConfiguration = serverConfiguration;
//    }
    @Override
    protected void initialize() {
        bindDataSourceProviderType(PooledDataSourceProvider.class);
        bindTransactionFactoryType(JdbcTransactionFactory.class);
        Names.bindProperties(this.binder(), createProperties());
        addMapperClass(UserMapper.class);

        // This is for the mapper
//        bind(DefaultObjectWrapperFactory.class);
//        bind(DefaultObjectFactory.class);
    }

    private Properties createProperties() {
        Properties myBatisProperties = new Properties();
        //String jdbcUrl = ("jdbc:mysql://"+serverConfiguration.getDataBaseHost()+":"+serverConfiguration.getDataBasePort()+"/"+serverConfiguration.getDataBaseName());
        myBatisProperties.setProperty("mybatis.environment.id", "development");
        myBatisProperties.setProperty("JDBC.driver", "com.mysql.jdbc.Driver");
        myBatisProperties.setProperty("JDBC.url", "jdbc:mysql://db:3306/db");
        myBatisProperties.setProperty("JDBC.username", "user");
        myBatisProperties.setProperty("JDBC.password", "password");
        myBatisProperties.setProperty("JDBC.autoCommit", "false");
        return myBatisProperties;
    }

}
