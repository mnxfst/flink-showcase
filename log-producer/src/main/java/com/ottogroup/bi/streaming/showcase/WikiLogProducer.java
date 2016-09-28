/**
 * Copyright (c) 2016, otto group and/or its affiliates. All rights reserved.
 * OTTO GROUP PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.ottogroup.bi.streaming.showcase;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer09;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducerBase;
import org.apache.flink.streaming.connectors.wikiedits.WikipediaEditEvent;
import org.apache.flink.streaming.connectors.wikiedits.WikipediaEditsSource;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;

import com.ottogroup.bi.streaming.operator.json.converter.JsonObjectToString;
import com.ottogroup.bi.streaming.sink.kafka.KafkaProducerBuilder;

public class WikiLogProducer {

	public void run() throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		KafkaProducerBuilder<String> kafkaProducerBuilder = KafkaProducerBuilder.getInstance();
		kafkaProducerBuilder.brokerList("localhost:9092").topic("wikiedits")
				.serializationSchema(new SimpleStringSchema()).addProperty(FlinkKafkaProducerBase.KEY_DISABLE_METRICS, "true");
		FlinkKafkaProducer09<String> kafakSink = kafkaProducerBuilder.create();

		DataStream<WikipediaEditEvent> germanEdits = env.addSource(new WikipediaEditsSource(WikipediaEditsSource.DEFAULT_HOST, WikipediaEditsSource.DEFAULT_PORT, "#de.wikipedia"));
		DataStream<WikipediaEditEvent> englishEdits = env.addSource(new WikipediaEditsSource(WikipediaEditsSource.DEFAULT_HOST, WikipediaEditsSource.DEFAULT_PORT, "#en.wikipedia"));
		DataStream<WikipediaEditEvent> frenchEdits = env.addSource(new WikipediaEditsSource(WikipediaEditsSource.DEFAULT_HOST, WikipediaEditsSource.DEFAULT_PORT, "#fr.wikipedia"));
		
		germanEdits.connect(englishEdits).map(new WikiEditMerger())
			.connect(frenchEdits).map(new WikiEditMerger())		
			.map(new WikiEditEventConverter()).map(new JsonObjectToString()).addSink(kafakSink);
		
		env.execute("wiki-log-producer");
	}
	
	
	public static void main(String[] args) throws Exception {
		new WikiLogProducer().run();
	}
}
