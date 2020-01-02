package boot;

import org.json.simple.JSONObject;

import java.io.File;
import util.InfraUtil;



public class ServerConfiguration {

    public ServerConfiguration(){
    }
    public static final String configFilePath = "src/main/resources/server.config" ;
    public static final String additionalConfigFilePath = "logsHandler/src/main/resources/server.config" ;

    private int port;
    private int elasticPort;
    private int additionalElasticPort;
    private String elasticHost;

    private int kafkaPort;
    private String kafkaHost;
    private String CLIENT_ID;

    public String getCLIENT_ID() {
        return CLIENT_ID;
    }
    public int getPort() {
        return port;
    }
    public int getElasticPort() {
        return elasticPort;
    }
    public int getAdditionalElasticPort() {
        return additionalElasticPort;
    }
    public String getElasticHost() {
        return elasticHost;
    }
    public int getKafkaPort() {
        return kafkaPort;
    }
    public String getKafkaHost() {
        return kafkaHost;
    }

}
