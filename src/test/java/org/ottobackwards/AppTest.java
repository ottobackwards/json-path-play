/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ottobackwards;

import org.adrianwalker.multilinestring.Multiline;
import org.junit.Test;

public class AppTest
{
  /**
   {
   "foo" :
   [
   { "name" : "otto", "value" : "test"},
   { "name" : "scoobie", "value" : "snack"}
   ]
   }
   */
  @Multiline
  static String JSON_LIST;

  /**{"name":"otto","value":"test"}*/
  @Multiline
  static String JSON_SINGLE;

  /**{"name":"scoobie","value":"snack"}*/
  @Multiline
  static String JSON_SINGLE2;

  @Test
  public void test() {
    App app = new App();
    app.parserMany(JSON_LIST.getBytes(),"$.foo").forEach((j) -> System.out.println(j.toJSONString()));
    app.parse(JSON_SINGLE.getBytes()).forEach((j) -> System.out.println(j.toJSONString()));
    app.parse(JSON_SINGLE2.getBytes()).forEach((j) -> System.out.println(j.toJSONString()));
  }
}
