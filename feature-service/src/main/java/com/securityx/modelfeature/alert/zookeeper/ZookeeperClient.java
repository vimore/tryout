package com.securityx.modelfeature.alert.zookeeper;

import com.securityx.modelfeature.alert.AlertHandler;
import io.dropwizard.lifecycle.Managed;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class ZookeeperClient implements Managed {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperClient.class);
    ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
    CuratorFramework curatorFramework;
    PathChildrenCache alertChildrenPathCache = null;


    public ZookeeperClient(String connectionString,
                           PathChildrenCacheListener pathChildrenCacheListener) throws Exception {
        curatorFramework = CuratorFrameworkFactory.newClient(connectionString, retryPolicy);
        LOGGER.debug("Starting curator-framework");
        curatorFramework.start();
        alertChildrenPathCache = new PathChildrenCache(curatorFramework, AlertHandler.ZOOKEEKER_BASE_PATH, true);
        LOGGER.debug("Starting PathChildrenCache");
        alertChildrenPathCache.start();
        alertChildrenPathCache.getListenable().addListener(pathChildrenCacheListener);
       // curatorFramework.getCuratorListenable().addListener(listener);
    }

    /**
     * uploads dat to zookeeper
     * @param data
     * @param path
     * @throws Exception
     */
    public void setData(String data, String path) throws Exception {
        if(path == null || path.isEmpty())
            return;
        if(!pathExists(path)){
            curatorFramework.create().creatingParentsIfNeeded().withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE).forPath(path);
        }

        //watcher
  //      curatorFramework.getChildren().watched().forPath(pathString);

        //set data
        curatorFramework.setData().inBackground().forPath(path, data.getBytes());
    }

    public Optional<String> getData(String path) throws Exception {
        if(pathExists(path)) {
            byte [] dataByteArr = null;
            List<String> children = curatorFramework.getChildren().forPath(path);
            for(String child : children){
                dataByteArr = curatorFramework.getData().forPath(path + "/" + child);
            }
            if(dataByteArr != null)
                return Optional.of(new String(dataByteArr));
        }
        return Optional.empty();
    }

    private boolean pathExists(String path){
        try {
            Stat stat = curatorFramework.checkExists().forPath(path);
            if(stat != null){
                return true;
            }
        } catch (Exception e) {
            LOGGER.error("Error while checking for path => ", e);
        }
        return false;
    }

    public void deleteData(String path) {
        try {
            curatorFramework.delete().forPath(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start() throws Exception {
           //no-op
    }

    @Override
    public void stop() throws Exception {
        LOGGER.debug("Stopping curator-framework");
        curatorFramework.close();
        LOGGER.debug("Stopping alert-children-pathCache");
        alertChildrenPathCache.close();
    }
}
