package Consumer;

import client.AccountsServiceClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import pojos.account.Account;
import util.InfraUtil;


import javax.inject.Inject;
import java.io.IOException;
import java.util.Map;

import static java.util.Objects.requireNonNull;


public class KafkaReceiver {
    private final String X_ACCOUNT_TOKEN = "X-ACCOUNT-TOKEN";
    private static Logger logger = LogManager.getLogger(KafkaReceiver.class);
    private final RestHighLevelClient elasticSearchClient;
    private final Consumer<String, String> consumer;
    private final AccountsServiceClient accountsServiceClient;


    @Inject
    public KafkaReceiver(RestHighLevelClient elasticSearchClient, Consumer<String, String> consumer, client.AccountsServiceClient accountsServiceClient) {
        this.elasticSearchClient = requireNonNull(elasticSearchClient);
        this.consumer = requireNonNull(consumer);
        this.accountsServiceClient = requireNonNull(accountsServiceClient);
    }

    public void start() {
        while (true) {
            BulkRequest bulkRequest = new BulkRequest();
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {

                Map<String, Object> map = InfraUtil.stringToObject(record.value(), Map.class);
                Account accountByToken = accountsServiceClient.getAccountByToken( (String) map.get(X_ACCOUNT_TOKEN));
                map.remove(X_ACCOUNT_TOKEN);

                ObjectMapper objectMapper = new ObjectMapper();
                if (accountByToken != null) {
                    try {
                        bulkRequest.add(new IndexRequest(accountByToken.getEsIndexName(),"_doc")
                                .source(objectMapper.writeValueAsString(map), XContentType.JSON));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                } else {
                    logger.error("unauthorized account");
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

