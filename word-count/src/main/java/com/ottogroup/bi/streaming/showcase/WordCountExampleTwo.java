package com.ottogroup.bi.streaming.showcase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;

public class WordCountExampleTwo {

	@SuppressWarnings("serial")
	public void run() throws Exception {
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setParallelism(1);
		env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
		DataStream<String> source = env.readTextFile("/home/mnxfst/git/flink-showcase/word-count/src/main/resources/macbeth.txt");		

		source.map((String line) -> line.toLowerCase().split("\\W+"))
			.flatMap((String[] tokens, Collector<Tuple2<String, Integer>> out) ->
				{ Arrays.stream(tokens).filter(t -> t.length() > 0).forEach(t -> out.collect(new Tuple2<>(t, 1)));})
			.keyBy(0).sum(1)
			.countWindowAll(12000)
			.apply(new AllWindowFunction<Tuple2<String,Integer>, Tuple2<String,Integer>, GlobalWindow>() {
		    	public void apply(GlobalWindow window, Iterable<Tuple2<String, Integer>> values,
		    			Collector<Tuple2<String, Integer>> out) throws Exception {
		    		Map<String, Integer> counts = new HashMap<>();
		    		for(Tuple2<String,Integer> v : values) {
		    			Integer c = counts.get(v.f0) ;
		    			c = (c == null ? Integer.valueOf(v.f1) : Integer.valueOf(c.intValue() + v.f1.intValue())); 
			      		counts.put(v.f0, c);
					}
					counts.forEach((String key, Integer value) -> { out.collect(Tuple2.of(key, value)); });
		    	}		    	  
			}).print();

		env.execute();		
	}
	
	/**
	 * @see org.apache.flink.api.common.ProgramDescription#getDescription()
	 */
	public String getDescription() {
		return "Simple Word Count Example";
	}

	public static void main(String[] args) throws Exception {
		new WordCountExampleTwo().run();
	}
}
