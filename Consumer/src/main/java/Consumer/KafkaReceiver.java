package Consumer;

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
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;


import javax.inject.Inject;
import java.io.IOException;

import static java.util.Objects.requireNonNull;


public class KafkaReceiver {
    private static Logger logger = LogManager.getLogger(KafkaReceiver.class);
    private final RestHighLevelClient elasticSearchClient;
    private final Consumer<String, String> consumer;
    private final WebTarget accountsServiceWebTarget;


    @Inject
    public KafkaReceiver(RestHighLevelClient elasticSearchClient, Consumer<String, String> consumer, WebTarget accountsServiceWebTarget) {
        this.elasticSearchClient = requireNonNull(elasticSearchClient);
        this.consumer = requireNonNull(consumer);
        this.accountsServiceWebTarget = requireNonNull(accountsServiceWebTarget);
    }

    public void start() {
        while (true) {
            BulkRequest bulkRequest = new BulkRequest();
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                JsonObject recordAsJson = InfraUtil.stringToJson(record.value());
                String accountToken = recordAsJson.get("X-ACCOUNT-TOKEN").getAsString();
                recordAsJson.remove("X-ACCOUNT-TOKEN");

                Response accountByTokenResponse = accountsServiceWebTarget.path("account/token")
                        .request(MediaType.APPLICATION_JSON)
                        .header("X-ACCOUNT-TOKEN", accountToken)
                        .get();

                if (accountByTokenResponse.getStatus() == 200) {
                    String responseEntity = accountByTokenResponse.readEntity(String.class);
                    JsonObject responseEntityAsJson = InfraUtil.stringToJson(responseEntity);
                    System.out.println(responseEntity);//*local*
                    bulkRequest.add(new IndexRequest(responseEntityAsJson.get("esIndexName")
                            .getAsString(), "_doc")
                            .source(recordAsJson, XContentType.JSON));
                }
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

//                System.out.println("offset = %s , key = %s, value = %s \n" +  record.offset() + record.key() + record.value());


