package com.xiaomi.aiservice.racoon;

import com.google.common.base.Throwables;
import com.xiaomi.aiservice.racoon.config.ZkRacoonLoader;
import com.xiaomi.aiservice.racoon.core.RacoonCore;
import kafka.common.InvalidConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wanglingda@xiaomi.com
 */

public class Racoon {
	private static final Logger LOGGER = LoggerFactory.getLogger(Racoon.class);
	private static volatile Racoon instance;
	private RacoonCore racoonCore;

	public static void init(ZkRacoonLoader loader) {
		if (instance == null) {
			synchronized(Racoon.class) {
				if (instance == null) {
					try {
						instance = new Racoon(loader);
					} catch (InvalidConfigException e) {
						LOGGER.error("create racoon instance failed due to {}", Throwables.getStackTraceAsString(e));
						throw new RuntimeException("RacoonInstanceInitException " + Throwables.getStackTraceAsString(e));
					}
				}
			}
		}

	}

	private Racoon(ZkRacoonLoader loader) throws InvalidConfigException {
		this.racoonCore = new RacoonCore(loader);
	}
//
//	public static Pair<String, Experiment> getExperiment(String layer, String hashKey) {
//		return instance.cantorEngine.getExperiment(layer, hashKey);
//	}
//
//	public static Experiment getExperiment(String domain, String layer, String hashKey) {
//		return instance.cantorEngine.getExperiment(domain, layer, hashKey);
//	}
//
//	public static CantorConfig getConfig() {
//		return instance.cantorEngine.getConfig();
//	}
}
