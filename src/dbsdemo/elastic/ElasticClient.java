package dbsdemo.elastic;

import dbsdemo.entities.Station;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.elasticsearch.action.fieldstats.FieldStats.Date;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

/**
 *
 * @author Baxos
 */
public class ElasticClient {
    private TransportClient client;
    
    public ElasticClient(){
        try {
            this.client = TransportClient.builder().build()
                .addTransportAddress(new InetSocketTransportAddress(
                        InetAddress.getByName("localhost"),
                        9300
                ));
        } catch (UnknownHostException ex) {
            Logger.getLogger(ElasticClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void insert(Station station){
        Map<String, Object> jsonDocument = new HashMap<String, Object>();

        jsonDocument.put("station_brand", station.getBrand().getBrand());
        jsonDocument.put("city", station.getCity().getName());
        jsonDocument.put("location", station.getLocation());
        
        try {
            this.client.prepareIndex("dbs", "station", Integer.toString(station.getId()))
                    .setSource(jsonBuilder()
                            .startObject()
                            .field("station_brand", station.getBrand().getBrand())
                            .field("city", station.getCity().getName())
                            .field("location", station.getLocation())
                            .endObject()
                    )
                    .execute()
                    .actionGet();
        } catch (IOException ex) {
            Logger.getLogger(ElasticClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<String> getElasticResult(String query){
        
        List<String> data = new ArrayList<>();
        
        SearchResponse response = this.client
                .prepareSearch("dbs")
                .setQuery(QueryBuilders.termQuery("location", query.toLowerCase()))
                .addAggregation(AggregationBuilders.terms("by_location").field("location.raw").size(0))
                .execute().actionGet();
        
        Terms terms = response.getAggregations().get("by_location");
        Collection<Terms.Bucket> results = terms.getBuckets();

        for (Terms.Bucket bucket : results) {
            data.add(bucket.getKeyAsString());
        }
        return data;
    }
    
    public void close(){
        this.client.close();
        this.client.threadPool().shutdown();
    }
}
