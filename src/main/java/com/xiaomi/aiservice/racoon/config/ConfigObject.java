package com.xiaomi.aiservice.racoon.config;

import com.google.gson.JsonArray;

import java.io.Serializable;

public class ConfigObject implements Serializable {

	private JsonArray corrections;

	public JsonArray getCorrections() {
		return corrections;
	}

	public void setCorrections(JsonArray corrections) {
		this.corrections = corrections;
	}
}
