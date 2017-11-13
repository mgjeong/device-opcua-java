/******************************************************************
 *
 * Copyright 2017 Samsung Electronics All Rights Reserved.
 *
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************/

package org.edge.protocol.opcua.api.common;

import java.util.Set;
import com.google.common.collect.ImmutableSet;

public enum EdgeIdentifier {
  READ(1),
  WRITE(2),
  HISTORY_READ(4),
  HISTORY_WRITE(8),
  SEMANTIC_CHANGE(10),
  STATUS_WRITE(20),
  TIMESTAMP_WRITE(40),

  CREATE_SUB(100),
  MODIFY_SUB(101),

  VARIABLE_NODE(1000),
  ARRAY_NODE(1001),
  OBJECT_NODE(1002),
  VARIABLE_TYPE_NODE(1003),
  OBJECT_TYPE_NODE(1004),
  REFERENCE_TYPE_NODE(1005),
  DATA_TYPE_NODE(1006),
  VIEW_NODE(1007),
  SINGLE_FOLDER_NODE_TYPE(1008),
  MILTI_FOLDER_NODE_TYPE(1009);

  public static final ImmutableSet<EdgeIdentifier> NONE = ImmutableSet.of();
  public static final ImmutableSet<EdgeIdentifier> READ_ONLY = ImmutableSet.of(READ);
  public static final ImmutableSet<EdgeIdentifier> READ_WRITE = ImmutableSet.of(READ, WRITE);
  public static final ImmutableSet<EdgeIdentifier> HISTORY_READ_ONLY =
      ImmutableSet.of(HISTORY_READ);
  public static final ImmutableSet<EdgeIdentifier> HISTORY_READ_WRITE =
      ImmutableSet.of(HISTORY_READ, HISTORY_WRITE);

  private final Integer id;

  EdgeIdentifier(Integer id) {
    this.id = id;
  }

  private Integer value() {
    return id;
  }

  public static int getAccessLevel(EdgeIdentifier... levels) {
    int accessLevel = 0;
    for (EdgeIdentifier level : levels) {
      accessLevel |= level.value().intValue();
    }
    return accessLevel;
  }

  public static int getAccessLevel(Set<EdgeIdentifier> levels) {
    int accessLevel = 0;
    for (EdgeIdentifier level : levels) {
      accessLevel |= level.value().intValue();
    }
    return accessLevel;
  }

  public static String convertAccessLevel(int level) {
    String accessLevel = null;
    switch (level) {
      case 1:
        accessLevel = "Read";
        break;
      case 2:
        accessLevel = "Write";
        break;
      case 3:
        accessLevel = "ReadWrite";
        break;
      default:
        break;
    }
    return accessLevel;
  }
}
