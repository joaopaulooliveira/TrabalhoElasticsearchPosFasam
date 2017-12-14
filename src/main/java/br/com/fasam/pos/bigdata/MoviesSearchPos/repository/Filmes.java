package br.com.fasam.pos.bigdata.MoviesSearchPos.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.lucene.queryparser.flexible.core.builders.QueryBuilder;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.stereotype.Repository;
import com.alibaba.fastjson.JSON;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import br.com.fasam.pos.bigdata.MoviesSearchPos.model.Filme;

@Repository
public class Filmes {
	private TransportClient client;

	@SuppressWarnings("resource")
	public Filmes() throws IOException {
		Settings settings = Settings.builder().put("cluster.name", "fundao").build();
		InetSocketAddress inetSocketAddress;
		try {
			inetSocketAddress = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 9300);
			TransportAddress transportAddress = new InetSocketTransportAddress(inetSocketAddress);
			this.client = new PreBuiltTransportClient(settings).addTransportAddress(transportAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		try {
			client.prepareIndex("site", "movies").get().getResult().toString();
		} catch (ActionRequestValidationException e) {
			try {
				File file = new File("data/movies_metadata.csv");
				Reader in = new FileReader(file);
				Scanner scan = new Scanner(file);
				String header = scan.nextLine();
				String[] headerVals = header.split(",");

				client.admin().indices().prepareExists("site").execute().actionGet().isExists();

				XContentBuilder mapping = XContentFactory.jsonBuilder().startObject()
						.startObject("proprieties")
						.startObject("overview").field("type", "text").endObject()
						.startObject("title").field("type","text").endObject()
						.startObject("release_date").field("type","date").endObject()
						.startObject("popularity").field("type","double").endObject()
					.endObject()
				.endObject();

				client.admin().indices().prepareCreate("site").addMapping("movies",mapping).execute().actionGet();
				
				try {
					
					Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
					
					BulkRequestBuilder prepareBulk = client.prepareBulk();
					int bulkCount = 0;
					for (CSVRecord record : records) {
						Map<String, Object> movie = new HashMap<>();
						
						int count = 0;
						for(String s: headerVals) {
							try{
								movie.put(s, record.get(count++));
							} catch (Exception e2) {
								break;
							}
}
						try {
							
							IndexRequestBuilder source = client.prepareIndex("site", "movies").setSource(movie);
							prepareBulk.add(source);
							bulkCount++;
							
							if(bulkCount>500) {
								prepareBulk.get();
								bulkCount = 0;
								prepareBulk = client.prepareBulk();
							}
							
							
						}catch (Exception e3) {
						}
						
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		}
	}

	
	public List<Filme> getTopFilmes() {
		List<Filme> filmes = new ArrayList<>();
        SearchResponse response = client.prepareSearch("site").setTypes("movies").addSort("popularity", SortOrder.DESC).execute().actionGet();
        List<SearchHit> searchHits = Arrays.asList(response.getHits().getHits());
        searchHits.forEach(hit -> {
            filmes.add(JSON.parseObject(hit.getSourceAsString(), Filme.class));
        });

        return filmes;
	}

	public List<Filme> getSearchFilmes(String titulo, String desc, Integer ano) {
		List<Filme> filmes = new ArrayList<>();
		SearchResponse response = client.prepareSearch("movies")
			.setTypes("movie")
			.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
			.setQuery(QueryBuilders.termQuery("title",titulo))
			.setFrom(0).setSize(60).setExplain(true)
			.get();

		List<SearchHit> searchHits = Arrays.asList(response.getHits().getHits());
		searchHits.forEach( hit-> {
			filmes.add(JSON.parseObject(hit.getSourceAsString(), Filme.class));
		});
		return filmes;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.client.close();
	}
}
