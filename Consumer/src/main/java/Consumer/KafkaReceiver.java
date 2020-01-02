package Consumer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import util.InfraUtil;

import javax.inject.Inject;
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
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                logger.debug("offset = %s , key = %s, value = %s \n", record.offset(), record.key(), record.value());
                logger.debug("Record partition " + record.partition());
                JsonObject jObject = InfraUtil.StringToJson(record.value());
                map.put("message", jObject.get("message").toString());
                map.put("User-Agent", jObject.get("User-Agent").toString());
                IndexResponse indexResponse = index(map, "index");

            }
        }
    }

    private IndexResponse index(Map<String, String> map, String index) {
        IndexRequest request = new IndexRequest(index, "_doc");
        request.source(new Gson().toJson(map), XContentType.JSON);
        IndexResponse indexResponse = null;
        try {
            indexResponse = elasticSearchClient.index((IndexRequest) request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return indexResponse;
    }

}

