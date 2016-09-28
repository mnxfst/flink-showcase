/**
 * Copyright (c) 2016, otto group and/or its affiliates. All rights reserved.
 * OTTO GROUP PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.ottogroup.bi.streaming.showcase;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;

/**
 * @author mnxfst
 * @since 29.09.2016
 */
public class LogUpdateStatsdReporter extends RichSinkFunction<Tuple2<String, Integer>> {

	private static final long serialVersionUID = -278786357966071880L;

	private StatsDClient statsdClient = null;
	
	/**
	 * @see org.apache.flink.api.common.functions.AbstractRichFunction#open(org.apache.flink.configuration.Configuration)
	 */
	public void open(Configuration parameters) throws Exception {
		this.statsdClient = new NonBlockingStatsDClient("wikiedits", "localhost", 8125);
	}

	/**
	 * @see org.apache.flink.streaming.api.functions.sink.RichSinkFunction#invoke(java.lang.Object)
	 */
	public void invoke(Tuple2<String, Integer> value) throws Exception {
		if(value != null && value.f0 != null && value.f1 != null) {
			this.statsdClient.gauge(value.f0.replace("#", "."), value.f1);
		}
	}

}
