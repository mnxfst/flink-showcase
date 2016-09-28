/**
 * Copyright (c) 2016, otto group and/or its affiliates. All rights reserved.
 * OTTO GROUP PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.ottogroup.bi.streaming.showcase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer09;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;
import org.apache.flink.util.Collector;
import org.apache.sling.commons.json.JSONObject;

import com.ottogroup.bi.streaming.operator.json.converter.JsonObjectToByteArray;
import com.ottogroup.bi.streaming.operator.json.converter.StringToJsonObject;
import com.ottogroup.bi.streaming.sink.elasticsearch.ElasticsearchNodeAddress;
import com.ottogroup.bi.streaming.sink.elasticsearch.ElasticsearchSink;
import com.ottogroup.bi.streaming.source.kafka.KafkaConsumerBuilder;

public class LogAnalyticsExample {

	public void run() throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment
			    .getExecutionEnvironment();

		// configure kafka consumer
		KafkaConsumerBuilder<String> builder = KafkaConsumerBuilder.getInstance();
		FlinkKafkaConsumer09<String> kafkaSource = builder.addProperty(KafkaConsumerBuilder.KAFKA_PROPS_AUTO_COMMIT_ENABLE, "true")
				.addProperty(KafkaConsumerBuilder.KAFKA_PROPS_BOOTSTRAP_SERVERS, "localhost:9092")
				.addProperty(KafkaConsumerBuilder.KAFKA_PROPS_GROUP_ID, "log-analysis-example")
				.deserializationSchema(new SimpleStringSchema())
				.topic("wikiedits")
				.create();

		// prepare elasticsearch producer
		ArrayList<ElasticsearchNodeAddress> esServers = new ArrayList<>();
		esServers.add(new ElasticsearchNodeAddress("localhost", 9300));
		ElasticsearchSink esSink = new ElasticsearchSink("actuarius", "wikiedits", "wikiedits", esServers);
		
		// attach kafka consumer to environment
		DataStream<String> wikiEditsStream = env.addSource(kafkaSource);
		// convert messages into json objects
		DataStream<JSONObject> jsonStream = wikiEditsStream.flatMap(new StringToJsonObject());
		// write json content to elasticsearch
		jsonStream.map(new JsonObjectToByteArray()).addSink(esSink);
		
		jsonStream.timeWindowAll(Time.seconds(5)).apply(new AllWindowFunction<JSONObject, Tuple2<String, Integer>, TimeWindow>() {
			private static final long serialVersionUID = 1204228886436247416L;

			public void apply(TimeWindow window, Iterable<JSONObject> values, Collector<Tuple2<String, Integer>> out)
					throws Exception {
				
				Map<String, Integer> languageCounts = new HashMap<>();
				for(JSONObject v : values) {
					String channel = v.getString("channel");
					Integer count = languageCounts.get(channel);
					count = (count != null ? count.intValue() + 1 : Integer.valueOf(1));
					languageCounts.put(channel, count);
				}
				languageCounts.forEach((String key, Integer value) -> { out.collect(Tuple2.of(key, value)); });
			}
		}).addSink(new LogUpdateStatsdReporter());
		env.execute();
		
			
	}
	
	
	public static void main(String[] args) throws Exception {
		new LogAnalyticsExample().run();
	}
}
