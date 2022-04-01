package com.xiaomi.aiservice.racoon.config;

import com.xiaomi.miliao.zookeeper.EnvironmentType;
import com.xiaomi.miliao.zookeeper.ZKClient;
import com.xiaomi.miliao.zookeeper.ZKFacade;

import java.util.function.Function;

/**
 * @author wanglingda@xiaomi.com
 */

public class ZkRacoonLoader {
  private final String zkPath;
  private final ZKClient client;

  public ZkRacoonLoader(String env, String zkPath) {
    this.zkPath = zkPath;
    this.client = ZKFacade.getClient(EnvironmentType.valueOf(env));
  }

  public String getZkPath() {
    return this.zkPath;
  }

  public String load() {
    return this.client.getData(String.class, this.getZkPath());
  }

  public void recover(String config) {
    this.client.writeData(this.getZkPath(), config, -1);
  }

  public void watch(Function<String, ?> callback) {
    this.client.registerDataChanges(String.class, this.getZkPath(), (path, data) -> callback.apply(data));
  }
}