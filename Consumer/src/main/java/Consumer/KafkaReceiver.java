package Consumer;

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

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class KafkaReceiver {
    private static Logger logger = LogManager.getLogger(KafkaReceiver.class);
    private final RestHighLevelClient elasticSearchClient;
    private final Consumer<String, String> consumer;

    @Inject
    public KafkaReceiver(RestHighLevelClient elasticSearchClient, Consumer<String, String> consumer) {
        this.elasticSearchClient = elasticSearchClient;
        this.consumer = consumer;
    }

    public void start() {
        Map<String, String> map = new HashMap<>();
        BulkRequest bulkRequest = new BulkRequest();
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                logger.debug("offset = %s , key = %s, value = %s \n", record.offset(), record.key(), record.value());
                logger.debug("Record partition " + record.partition());
                JsonObject jObject = InfraUtil.stringToJson(record.value());
                map.put("message", jObject.get("message").toString());
                map.put("User-Agent", jObject.get("User-Agent").toString());
                bulkRequest.add(new IndexRequest("index", "_doc").source(new Gson().toJson(map), XContentType.JSON));
            }
            if (bulkRequest.numberOfActions() > 0) {
                indexBulk(bulkRequest);
                bulkRequest = new BulkRequest();
            }
        }
    }

    private void indexBulk(BulkRequest bulkRequest) {
            try {
                BulkResponse bulkResp = elasticSearchClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                if (bulkResp.hasFailures()) {
                    logger.error("error in bulkIndex" + bulkResp.buildFailureMessage());
                } else {
                    logger.debug("Number of actions Bulk indexing " + bulkRequest.numberOfActions());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}

