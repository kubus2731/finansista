package pl.pb.finansista.common.web;

import pl.pb.finansista.common.exception.InvalidIfMatchHeaderException;

public final class ETags {

  private ETags() {}

  public static String format(Long version) {
    return "\"" + version + "\"";
  }

  public static Long parseIfMatch(String ifMatch) {
    if (ifMatch == null || ifMatch.isBlank()) {
      throw InvalidIfMatchHeaderException.required();
    }
    try {
      return Long.parseLong(ifMatch.replace("\"", ""));
    } catch (NumberFormatException e) {
      throw InvalidIfMatchHeaderException.invalidFormat();
    }
  }
}
