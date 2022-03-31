package com.xiaomi.aiservice.racoon.core;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.xiaomi.aiservice.racoon.config.ConfigObject;
import com.xiaomi.aiservice.racoon.config.CorrectionObject;
import com.xiaomi.aiservice.racoon.config.ZkRacoonLoader;
import kafka.common.InvalidConfigException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.xiaomi.aiservice.utils.GsonUtils.getAsJson;
import static com.xiaomi.aiservice.utils.GsonUtils.getTObject;

/**
 * @author wanglingda@xiaomi.com
 */

public class RacoonCore {
	private static final Logger LOGGER = LoggerFactory.getLogger(RacoonCore.class);
	private volatile ConfigObject configObject;
	private volatile Map<String, String> intentionTable;

	private void verify(ConfigObject confObj) throws InvalidConfigException {
		if (confObj == null) {
			throw new InvalidConfigException("NullRacoonConfig");
		}
		if (CollectionUtils.isEmpty(confObj.getCorrections())) {
			throw new InvalidConfigException("EmptyRacoonCorrection");
		}
	}

	private void update(ConfigObject configObject) {
		Map<String, String> iTable = new HashMap<>();
		for (CorrectionObject corrObj : configObject.getCorrections()) {
			iTable.put(corrObj.getBefore(), corrObj.getAfter());
		}
		if (MapUtils.isNotEmpty(iTable)) {
			intentionTable = iTable;
		}
	}

	private void initCore(String config) throws InvalidConfigException {
		ConfigObject confObj = getTObject(config, ConfigObject.class);
		this.verify(confObj);
		this.update(confObj);
		this.configObject = confObj;
	}

	public RacoonCore(ZkRacoonLoader loader) throws InvalidConfigException {
		String config = loader.load();
		initCore(config);
		loader.watch((Function<String, Object>) newConf -> {
			try {
				initCore(newConf);
				LOGGER.info("update racoon core successfully {}", newConf);
			} catch (InvalidConfigException e) {
				loader.recover(getAsJson(this.configObject));
				LOGGER.error("update racoon core {} failed due to {}", newConf, Throwables.getStackTraceAsString(e));
				LOGGER.error("update racoon core failed and recover with {}", this.configObject);
			}
			return null;
		});
	}

	public String getCorrection(String intention) {
		if (MapUtils.isEmpty(intentionTable) || StringUtils.isBlank(intention)) {
			return StringUtils.EMPTY;
		}
		return intentionTable.getOrDefault(intention, StringUtils.EMPTY);
	}

	public ConfigObject getConfig() {
		return this.configObject;
	}
}
