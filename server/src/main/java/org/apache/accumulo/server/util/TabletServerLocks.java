/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.accumulo.server.util;

import java.util.List;

import org.apache.accumulo.core.Constants;
import org.apache.accumulo.core.cli.Help;
import org.apache.accumulo.core.zookeeper.ZooUtil;
import org.apache.accumulo.fate.zookeeper.IZooReaderWriter;
import org.apache.accumulo.server.client.HdfsZooInstance;
import org.apache.accumulo.server.zookeeper.ZooLock;
import org.apache.accumulo.server.zookeeper.ZooReaderWriter;

import com.beust.jcommander.Parameter;

public class TabletServerLocks {

  static class Opts extends Help {
    @Parameter(names="-list")
    boolean list = false;
    @Parameter(names="-delete")
    String delete = null;
  }
  /**
   * @param args
   */
  public static void main(String[] args) throws Exception {

    String tserverPath = ZooUtil.getRoot(HdfsZooInstance.getInstance()) + Constants.ZTSERVERS;
    Opts opts = new Opts();
    opts.parseArgs(TabletServerLocks.class.getName(), args);

    if (opts.list) {
      IZooReaderWriter zoo = ZooReaderWriter.getInstance();

      List<String> tabletServers = zoo.getChildren(tserverPath);

      for (String tabletServer : tabletServers) {
        byte[] lockData = ZooLock.getLockData(tserverPath + "/" + tabletServer);
        String holder = null;
        if (lockData != null) {
          holder = new String(lockData);
        }

        System.out.printf("%32s %16s%n", tabletServer, holder);
      }
    } else if (opts.delete != null) {
      ZooLock.deleteLock(tserverPath + "/" + args[1]);
    } else {
      System.out.println("Usage : " + TabletServerLocks.class.getName() + " -list|-delete <tserver lock>");
    }

  }

}
