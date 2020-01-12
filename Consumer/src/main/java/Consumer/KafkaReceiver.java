package Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import util.InfraUtil;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;


import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;


public class KafkaReceiver {
    private static Logger logger = LogManager.getLogger(KafkaReceiver.class);
    private final RestHighLevelClient elasticSearchClient;
    private final Consumer<String, String> consumer;

    @Inject
    public KafkaReceiver(RestHighLevelClient elasticSearchClient, Consumer<String, String> consumer) {
        this.elasticSearchClient = requireNonNull(elasticSearchClient);
        this.consumer = requireNonNull(consumer);
    }

    public void start() {
        Map<String, String> map = new HashMap<>();
        while (true) {
            BulkRequest bulkRequest = new BulkRequest();
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                logger.debug("offset = %s , key = %s, value = %s \n", record.offset(), record.key(), record.value());
                logger.debug("Record partition " + record.partition());
                JsonObject jObject = InfraUtil.stringToJson(record.value());
                map.put("message", jObject.get("message").toString());
                map.put("User-Agent", jObject.get("User-Agent").toString());
                System.out.println("record added to consumer");//*local*
                System.out.println(jObject.get("X-ACCOUNT-TOKEN").getAsString());//*local*
                ///////////////////////get user by token
                WebTarget webTarget = ClientBuilder.newClient().target("http://accounts-service:8083/");
                // return User
                Response response = null;
                response = webTarget.path("account/token")
                        .request(MediaType.APPLICATION_JSON)
                        .header("X-ACCOUNT-TOKEN", jObject.get("X-ACCOUNT-TOKEN").getAsString())
                        .get();
                String jsonString = response.readEntity(String.class);
                System.out.println(jsonString);//*local*

                if (response.getStatus() == 200) {
                    //Object O = response.getEntity();
                    JsonObject JSONObject = InfraUtil.stringToJson(jsonString);
                    String esIndexName = JSONObject.get("esIndexName").getAsString();
                    System.out.println("esIndex name in consumer get it from account services");
                    System.out.println(esIndexName);
                    bulkRequest.add(new IndexRequest(esIndexName, "_doc").source(new Gson().toJson(map), XContentType.JSON));
                    //response= sendToKafka(jsonString,"Mozilla/5.0 (Macintosh; Intel Mac OS X)",accountToken );
                }
                /////////////////////////////////////////////
                //bulkRequest.add(new IndexRequest(jObject.get("X-ACCOUNT-TOKEN").toString(), "_doc").source(new Gson().toJson(map), XContentType.JSON));
            }
            if (bulkRequest.numberOfActions() > 0) {
                System.out.println(bulkRequest.numberOfActions());
                indexBulk(bulkRequest);
            }
        }
    }

    private void indexBulk(BulkRequest bulkRequest) {
        try {
            BulkResponse bulkResp = elasticSearchClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            if (bulkResp.hasFailures()) {
                System.out.println("error in bulkIndex" + bulkResp.buildFailureMessage());//*local*
                logger.error("error in bulkIndex" + bulkResp.buildFailureMessage());
            } else {
                System.out.println("Number of actions Bulk indexing " + bulkRequest.numberOfActions());//*local*
                logger.debug("Number of actions Bulk indexing " + bulkRequest.numberOfActions());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());//*local*
            e.printStackTrace();
        }
    }
}

