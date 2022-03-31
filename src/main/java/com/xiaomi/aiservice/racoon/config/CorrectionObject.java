package com.xiaomi.aiservice.racoon.config;

import java.io.Serializable;

/**
 * @author wanglingda@xiaomi.com
 */

public class CorrectionObject implements Serializable {

	private String before;
	private String after;

	public String getBefore() {
		return before;
	}

	public void setBefore(String before) {
		this.before = before;
	}

	public String getAfter() {
		return after;
	}

	public void setAfter(String after) {
		this.after = after;
	}
}
