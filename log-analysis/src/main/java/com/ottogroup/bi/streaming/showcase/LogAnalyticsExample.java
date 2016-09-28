/**
 * Copyright (c) 2016, otto group and/or its affiliates. All rights reserved.
 * OTTO GROUP PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.ottogroup.bi.streaming.showcase;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.wikiedits.WikipediaEditEvent;
import org.apache.flink.streaming.connectors.wikiedits.WikipediaEditsSource;

import com.ottogroup.bi.streaming.sink.kafka.KafkaProducerBuilder;

public class LogAnalyticsExample {

	public void run() throws Exception {
			StreamExecutionEnvironment env = StreamExecutionEnvironment
			    .getExecutionEnvironment();

			DataStream<WikipediaEditEvent> edits = env
			    .addSource(new WikipediaEditsSource());
			edits.print();
			
			KafkaProducerBuilder<Serializable>
			env.execute();
	}
	
	
	public static void main(String[] args) throws Exception {
		new LogAnalyticsExample().run();
	}
}
