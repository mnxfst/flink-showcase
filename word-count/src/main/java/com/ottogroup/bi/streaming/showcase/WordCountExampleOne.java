/**
 * Copyright (c) 2016, otto group and/or its affiliates. All rights reserved.
 * OTTO GROUP PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.ottogroup.bi.streaming.showcase;

import java.util.Arrays;

import org.apache.flink.api.common.ProgramDescription;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class WordCountExampleOne implements ProgramDescription {

	public void run() throws Exception {
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		DataStream<String> source = env.readTextFile("/home/mnxfst/git/flink-showcase/word-count/src/main/resources/macbeth.txt");		
		source.map((String line) -> line.toLowerCase().split("\\W+"))
		      .flatMap((String[] tokens, Collector<Tuple2<String, Integer>> out) ->
			      {
					Arrays.stream(tokens).filter(t -> t.length() > 0).forEach(t -> out.collect(new Tuple2<>(t, 1)));
			      })
		      .keyBy(0).sum(1).print();
		env.execute();		
	}
	
	/**
	 * @see org.apache.flink.api.common.ProgramDescription#getDescription()
	 */
	public String getDescription() {
		return "Simple Word Count Example";
	}

	public static void main(String[] args) throws Exception {
		new WordCountExampleOne().run();
	}
	
}
