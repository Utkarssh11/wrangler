/*
 * Copyright © 2025 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


 // your existing imports and class code stay the same
 
package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class TimeDuration implements Token {
  private final long milliseconds;

  public TimeDuration(String tokenStr) {
    if (tokenStr == null || tokenStr.isEmpty()) {
      throw new IllegalArgumentException("TimeDuration token string is null or empty.");
    }

    tokenStr = tokenStr.trim().toLowerCase();
    if (tokenStr.endsWith("ms")) {
      milliseconds = Long.parseLong(tokenStr.replace("ms", "").trim());
    } else if (tokenStr.endsWith("s")) {
      milliseconds = Long.parseLong(tokenStr.replace("s", "").trim()) * 1000;
    } else if (tokenStr.endsWith("m")) {
      milliseconds = Long.parseLong(tokenStr.replace("m", "").trim()) * 60 * 1000;
    } else if (tokenStr.endsWith("h")) {
      milliseconds = Long.parseLong(tokenStr.replace("h", "").trim()) * 60 * 60 * 1000;
    } else {
      throw new IllegalArgumentException("Invalid format for TimeDuration: " + tokenStr);
    }
  }

  public long getMilliseconds() {
    return milliseconds;
  }

  @Override
  public Object value() {
    return milliseconds;
  }

  @Override
  public TokenType type() {
    return TokenType.TIME_DURATION;
  }

  @Override
  public JsonElement toJson() {
    JsonObject json = new JsonObject();
    json.addProperty("milliseconds", milliseconds);
    return json;
  }
}
