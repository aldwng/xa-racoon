package net.xa.racoon.config;

import org.I0Itec.zkclient.ZkClient;

import java.util.function.Function;

// This module may not work without any available zk utils.

/**
 * @author aldywang
 */

public class ZkRacoonLoader {
  private final String zkPath;
//  private final ZKClient client;

//  public ZkRacoonLoader(String env, String zkPath) {
//    this.zkPath = zkPath;
//    this.client = ZKFacade.getClient(EnvironmentType.valueOf(env));
//  }

  private final ZkClient client;

  public ZkRacoonLoader(String zkPath) {
    this.zkPath = zkPath;
    this.client = new ZkClient(zkPath);
  }

  public String getZkPath() {
    return this.zkPath;
  }

//  public String load() {
//    return this.client.getData(String.class, this.getZkPath());
//  }

  public String load() {
    return this.client.readData(this.getZkPath());
  }

  public void recover(String config) {
    this.client.writeData(this.getZkPath(), config, -1);
  }

  public void watch(Function<String, ?> callback) {
//    this.client.registerDataChanges(String.class, this.getZkPath(), (path, data) -> callback.apply(data));
  }
}