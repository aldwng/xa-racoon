package com.xiaomi.aiservice.racoon.core;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.xiaomi.aiservice.racoon.config.ConfigObject;
import com.xiaomi.aiservice.racoon.config.ZkRacoonLoader;
import kafka.common.InvalidConfigException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.xiaomi.aiservice.utils.GsonUtils.*;

/**
 * @author wanglingda@xiaomi.com
 */

public class RacoonCore {
	private static final Logger LOGGER = LoggerFactory.getLogger(RacoonCore.class);
	private volatile ZkRacoonLoader zkRacoonLoader;
	private volatile ConfigObject configObject;
	private volatile Map<String, String> intentionTable;

	private void verify(ConfigObject confObj) throws InvalidConfigException {
		if (confObj == null) {
			throw new InvalidConfigException("NullRacoonConfig");
		}
		if (isEmpty(confObj.getCorrections())) {
			throw new InvalidConfigException("EmptyRacoonCorrection");
		}
	}

	private void update(ConfigObject configObject) {

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
		zkRacoonLoader.watch((Function<String, Object>) newConf -> {
			try {
				initCore(newConf);
				LOGGER.info("update racoon core successfully {}", newConf);
			} catch (InvalidConfigException e) {
				zkRacoonLoader.recover(getAsJson(this.configObject));
				LOGGER.error("update racoon core {} failed due to {}", newConf, Throwables.getStackTraceAsString(e));
				LOGGER.error("update racoon core failed and recover with {}", this.configObject);
			}
			return null;
		});
	}

	public Pair<String, Experiment> getExperiment(String layer, String hashKey) {
		if (layer != null && hashKey != null && this.experimentQuotaTable.containsKey(layer)) {
			Experiment experiment = this.getExperimentByWhitelist(layer, hashKey);
			if (experiment != null) {
				return Pair.of((Object)null, experiment);
			} else {
				String domain = null;
				long random = Math.abs(CityHash.cityHash64(hashKey)) % 100L + 1L;
				int total = 0;
				Iterator var8 = this.domainQuotaTable.entrySet().iterator();

				while(var8.hasNext()) {
					Map.Entry<String, Integer> entry = (Map.Entry)var8.next();
					total += (Integer)entry.getValue();
					if ((long)total >= random) {
						domain = (String)entry.getKey();
						break;
					}
				}

				experiment = this.getExperiment(domain, layer, hashKey);
				return Pair.of(domain, experiment);
			}
		} else {
			return Pair.of((Object)null, (Object)null);
		}
	}

	private Experiment getExperimentByWhitelist(String layer, String hashKey) {
		if (MapUtils.isNotEmpty((Map)this.whitelistTable.get(layer))) {
			Iterator var3 = ((Map)this.whitelistTable.get(layer)).entrySet().iterator();

			while(var3.hasNext()) {
				Map.Entry<String, List<String>> entry = (Map.Entry)var3.next();
				if (((List)entry.getValue()).contains(hashKey)) {
					return (Experiment)((Map)this.experimentTable.get(layer)).get(entry.getKey());
				}
			}
		}

		return null;
	}

	public Experiment getExperiment(String domain, String layer, String hashKey) {
		if (domain != null && layer != null && hashKey != null && this.domainQuotaTable.containsKey(domain) && this.experimentQuotaTable.containsKey(layer)) {
			Experiment experiment = this.getExperimentByWhitelist(layer, hashKey);
			if (experiment != null) {
				return experiment;
			} else {
				long random = Math.abs(CityHash.cityHash64(hashKey + layer)) % 100L + 1L;
				int total = 0;
				Iterator var8 = ((Map)((Map)this.experimentQuotaTable.get(layer)).get(domain)).entrySet().iterator();

				while(var8.hasNext()) {
					Map.Entry<String, Integer> entry = (Map.Entry)var8.next();
					total += (Integer)entry.getValue();
					if ((long)total >= random) {
						experiment = (Experiment)((Map)this.experimentTable.get(layer)).get(entry.getKey());
						break;
					}
				}

				return experiment;
			}
		} else {
			return null;
		}
	}

	public CantorConfig getConfig() {
		return this.currentCantorConfig;
	}
}
