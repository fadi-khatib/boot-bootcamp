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
                JsonObject messageAsJson = InfraUtil.stringToJson(record.value());
                Response accountByTokenResponse = accountsServiceWebTarget.path("account/token")
                        .request(MediaType.APPLICATION_JSON)
                        .header("X-ACCOUNT-TOKEN", messageAsJson.get("X-ACCOUNT-TOKEN").getAsString())
                        .get();
                messageAsJson.remove("X-ACCOUNT-TOKEN");

                if (accountByTokenResponse.getStatus() == 200) {
                    String accountByToken = accountByTokenResponse.readEntity(String.class);
                    JsonObject accountJsonByToken = InfraUtil.stringToJson(accountByToken);
                    bulkRequest.add(new IndexRequest(accountJsonByToken.get("esIndexName")
                            .getAsString(), "_doc")
                            .source(messageAsJson, XContentType.JSON));
                } else {
                    throw new RuntimeException(accountByTokenResponse.readEntity(String.class));
                }
            }
            if (bulkRequest.numberOfActions() > 0) {
                indexBulk(bulkRequest);
            }
        }
    }

    private void indexBulk(BulkRequest bulkRequest) {
        BulkResponse bulkResp = null;
        try {
            bulkResp = elasticSearchClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bulkResp.hasFailures()) {
            logger.error("error in bulkIndex" + bulkResp.buildFailureMessage());
        } else {
            logger.debug("Number of actions Bulk indexing " + bulkRequest.numberOfActions());
        }
    }
}

