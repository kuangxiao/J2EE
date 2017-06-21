package test;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;

class Wa implements Runnable {

	public void run() {

		try {
			// 连接启动ZooKeeper
			ZooKeeper zk = new ZooKeeper("192.168.2.6:2181", 500000, new Watcher() {
				// 监控所有被触发的事件
				public void process(WatchedEvent event) {
					System.out.println("changing...");
				}
			});

			// 设置监听器
			Watcher wc = new Watcher() {

				public void process(WatchedEvent event) {
					if (event.getType() == EventType.NodeDataChanged) {
						System.out.println(event.getPath() + " NodeDataChanged");
					}
					if (event.getType() == EventType.NodeChildrenChanged) {
						System.out.println(event.getPath() + " NodeChildrenChanged");
					}
					if (event.getType() == EventType.NodeDeleted) {
						System.out.println(event.getPath() + " NodeDeleted");
					}
					if (event.getType() == EventType.NodeCreated) {
						System.out.println(event.getPath() + " NodeCreated");
					}
				}

			};

			// 进行轮询，其中exists方法用来询问状态，并且设置了监听器，如果发生变化，则会回调监听器里的方法。
			while (true) {
				zk.exists("/silence", wc);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}

public class ZookeeperTest {

	public static void main(String[] args) throws IOException, KeeperException, InterruptedException {

		Thread t = new Thread(new Wa());
		t.start();

	}

}
