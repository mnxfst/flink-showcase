package com.ottogroup.bi.streaming.showcase;

import org.apache.flink.api.common.ProgramDescription;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class SimpleExample implements ProgramDescription {

	public void run() throws Exception {
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setParallelism(1);
		DataStream<String> source = env.readTextFile("/home/mnxfst/git/flink-showcase/basic-example/src/main/resources/word-list.txt");
		source.print();
		env.execute();
	}
	
	/**
	 * @see org.apache.flink.api.common.ProgramDescription#getDescription()
	 */
	public String getDescription() {
		return "This is a very simple Apache Flink based application"; 
	}

	public static void main(String[] args) throws Exception {
		new SimpleExample().run();
	}
	
}
