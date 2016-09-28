/**
 * Copyright (c) 2016, otto group and/or its affiliates. All rights reserved.
 * OTTO GROUP PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.ottogroup.bi.streaming.ct;

import org.apache.flink.api.common.ProgramDescription;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class SimpleExample implements ProgramDescription {

	public void run() throws Exception {
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setParallelism(1);
		DataStream<String> source = env.readTextFile("/home/mnxfst/projects/flink-operator-library/workspace/code-talks-examples/src/main/resources/word-list.txt");
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
