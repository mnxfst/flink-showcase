/**
 * Copyright (c) 2016, otto group and/or its affiliates. All rights reserved.
 * OTTO GROUP PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.ottogroup.bi.streaming.ct;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.flink.api.common.ProgramDescription;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;

public class WordCountExample implements ProgramDescription {

	public void run() throws Exception {
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setParallelism(1);
		DataStream<String> source = env.readTextFile("/home/mnxfst/projects/flink-operator-library/workspace/code-talks-examples/src/main/resources/macbeth.txt");		

		source.map((String line) -> line.toLowerCase().split("\\W+"))
		      .flatMap((String[] tokens, Collector<Tuple2<String, Integer>> out) ->
				      {
						Arrays.stream(tokens).filter(t -> t.length() > 0).forEach(t -> out.collect(new Tuple2<>(t, 1)));
				      })
		      .keyBy(0)
		      .sum(1)
		      .print();
		env.execute();		
	}
	
	public void run2() throws Exception {
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setParallelism(1);
		env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
		DataStream<String> source = env.readTextFile("/home/mnxfst/projects/flink-operator-library/workspace/code-talks-examples/src/main/resources/macbeth.txt");		

		source.map((String line) -> line.toLowerCase().split("\\W+"))
		      .flatMap((String[] tokens, Collector<Tuple2<String, Integer>> out) ->
				      {				    	  
						Arrays.stream(tokens).filter(t -> t.length() > 0).forEach(t -> out.collect(new Tuple2<>(t, 1)));
				      })
		      .keyBy(0).sum(1)
		      .countWindowAll(10000)
//		      .apply((TimeWindow timeWindow, Iterable<Tuple2<String, Integer>> in, Collector<Tuple2<String, Integer>> out) ->
////		      .timeWindowAll(Time.of(5, TimeUnit.SECONDS)).max(0).print();
		      .apply((GlobalWindow timeWindow, Iterable<Tuple2<String, Integer>> in, Collector<Tuple2<String, Integer>> out) -> 
		      		  {
		      			  Map<String, Integer> m = new HashMap<>();
		      			  in.forEach((Tuple2<String,Integer> element) -> {
		      				 Integer v = m.get(element.f0) ;
		      				 if(v == null)
		      					 v = Integer.valueOf(element.f1);
		      				 else
		      					 v = Integer.valueOf(v.intValue() + element.f1.intValue());
		      				 m.put(element.f0, v);
		      			  });
		      			  m.forEach((String key, Integer value) -> {
		      				 out.collect(Tuple2.of(key, value));
		      			  });
		      		  }).print();

		env.execute();		
	}
	
	/**
	 * @see org.apache.flink.api.common.ProgramDescription#getDescription()
	 */
	public String getDescription() {
		return "Simple Word Count Example";
	}

	public static Options getOpts() {
		Options opts = new Options();
		opts.addOption("e", true, "Example ID to run");
		return opts;
	}
	
	public static void main(String[] args) throws Exception {
		
		CommandLineParser clp = new PosixParser();
		CommandLine cl = clp.parse(getOpts(), args);
		if(!cl.hasOption("e"))
			System.out.println("Please choose example: 1, 2, 3, ...");
		
		String value = cl.getOptionValue("e");
		
		if(value.equals("1"))
			new Wo
		new WordCountExample().run2();
	}
	
}
