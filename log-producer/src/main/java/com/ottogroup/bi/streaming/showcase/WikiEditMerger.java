/**
 * Copyright (c) 2016, otto group and/or its affiliates. All rights reserved.
 * OTTO GROUP PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.ottogroup.bi.streaming.showcase;

import org.apache.flink.streaming.api.functions.co.CoMapFunction;
import org.apache.flink.streaming.connectors.wikiedits.WikipediaEditEvent;

public class WikiEditMerger implements CoMapFunction<WikipediaEditEvent, WikipediaEditEvent, WikipediaEditEvent> {

	private static final long serialVersionUID = -3365038596192970171L;

	public WikipediaEditEvent map1(WikipediaEditEvent value) throws Exception {
		return value;
	}
	public WikipediaEditEvent map2(WikipediaEditEvent value) throws Exception {
		return value;
	}
}
