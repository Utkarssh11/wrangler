


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

public class ByteSize implements Token {
  private final long bytes;

  public ByteSize(String tokenStr) {
    if (tokenStr == null || tokenStr.isEmpty()) {
      throw new IllegalArgumentException("ByteSize token string is null or empty.");
    }

    tokenStr = tokenStr.trim().toUpperCase();
    if (tokenStr.endsWith("KB")) {
      bytes = Long.parseLong(tokenStr.replace("KB", "").trim()) * 1024;
    } else if (tokenStr.endsWith("MB")) {
      bytes = Long.parseLong(tokenStr.replace("MB", "").trim()) * 1024 * 1024;
    } else if (tokenStr.endsWith("GB")) {
      bytes = Long.parseLong(tokenStr.replace("GB", "").trim()) * 1024L * 1024L * 1024L;
    } else if (tokenStr.endsWith("B")) {
      bytes = Long.parseLong(tokenStr.replace("B", "").trim());
    } else {
      throw new IllegalArgumentException("Invalid format for ByteSize: " + tokenStr);
    }
  }

  public long getBytes() {
    return bytes;
  }

  @Override
  public Object value() {
    return bytes;
  }

  @Override
  public TokenType type() {
    return TokenType.BYTE_SIZE;
  }

  @Override
  public JsonElement toJson() {
    JsonObject json = new JsonObject();
    json.addProperty("bytes", bytes);
    return json;
  }
}
