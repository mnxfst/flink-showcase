/**
 * Copyright (c) 2016, otto group and/or its affiliates. All rights reserved.
 * OTTO GROUP PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.ottogroup.bi.streaming.showcase;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.connectors.wikiedits.WikipediaEditEvent;
import org.apache.sling.commons.json.JSONObject;

public class WikiEditEventConverter implements MapFunction<WikipediaEditEvent, JSONObject> {

	private static final long serialVersionUID = -3887822211835703110L;

	/**
	 * @see org.apache.flink.api.common.functions.MapFunction#map(java.lang.Object)
	 */
	public JSONObject map(WikipediaEditEvent value) throws Exception {		
		JSONObject json = new JSONObject();
		json.put("bytediff", value.getByteDiff());
		json.put("channel", value.getChannel());
		json.put("diffurl", value.getDiffUrl());
		json.put("summary", value.getSummary());
		json.put("@timestamp", value.getTimestamp());
		json.put("title", value.getTitle());
		json.put("user", value.getUser());		
		return json;
	}

}
