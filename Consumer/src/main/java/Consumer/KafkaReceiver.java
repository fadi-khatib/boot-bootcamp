package Consumer;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;


import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;


public class KafkaReceiver {

    RestHighLevelClient elasticSearchClient;
    Consumer<String, String> consumer;

    @Inject
    public KafkaReceiver(RestHighLevelClient elasticSearchClient, Consumer<String, String> consumer ){
        this.elasticSearchClient = elasticSearchClient;
        this.consumer = consumer;
    }
    public void start() {
        Map<String,String> map = new HashMap<>();
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("offset = %s , key = %s, value = %s \n", record.offset(), record.key(), record.value());
                System.out.println("Record partition " + record.partition());
                // call elastic search handler here
                JsonObject jObject = Util.StringToJson(record.value());
                if(jObject == null){
                    System.out.println("failed to parse json file for: \n" + record.value());
                }
                else{
                    map.put(jObject.get("User-Agent").toString(),jObject.get("message").toString());
                    IndexResponse indexResponse = index(map,"index");
                    if(indexResponse == null){
                        System.out.println("fail to send to elastic search");
                    }
                    else{
                        System.out.println("indexResponse status: "+indexResponse.status().toString());
                    }
                }
            }
        }
    }

    private IndexResponse index(Map<String, String> map, String index){
        IndexRequest request = new IndexRequest(index,"_doc");

        request.source(new Gson().toJson(map) , XContentType.JSON);
        IndexResponse indexResponse = null ;
        try{
            indexResponse= elasticSearchClient.index((IndexRequest) request, RequestOptions.DEFAULT);
        }catch(Exception e){
            System.out.println("Exception while sending message to elastic search");
            System.out.println(e.getMessage());
        }
        return indexResponse;
    }

}

