package net.xa.racoon;

import com.google.common.base.Throwables;
import net.xa.racoon.config.ConfigObject;
import net.xa.racoon.config.ZkRacoonLoader;
import net.xa.racoon.core.RacoonCore;
import kafka.common.InvalidConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aldywang
 */

public class Racoon {
	private static final Logger LOGGER = LoggerFactory.getLogger(Racoon.class);
	private static volatile Racoon instance;
	private final RacoonCore racoonCore;

	public static void init(ZkRacoonLoader loader) {
		LOGGER.info("init racoon path:{}", loader.getZkPath());
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

	public static String getCorrection(String intention) {
		return instance.racoonCore.getCorrection(intention);
	}

	public static ConfigObject getConfig() {
		return instance.racoonCore.getConfig();
	}
}
