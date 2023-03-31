package net.xa.racoon.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

/**
 * @author aldywang
 */

public class RacoonGson {

  private static final Gson gson = new Gson();

  public static String getAsJson(Object object) {
    return gson.toJson(object);
  }

  public static <T> T getTObject(String json, Class<T> classOfT) {
    try {
      return gson.fromJson(json, classOfT);
    } catch (JsonSyntaxException e) {
      return null;
    }
  }

  public static <T> T getTObject(JsonElement jsonElement, Class<T> classOfT) {
    try {
      return gson.fromJson(jsonElement, classOfT);
    } catch (JsonSyntaxException e) {
      return null;
    }
  }
}