/**
 * 
 */
package com.taotao.rest.jedis;

import java.util.HashSet;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

/**
 * <p> Description: </p>
 * @author fengda
 * @date 2017年3月2日 下午4:29:47
 */
public class JedisTest {

	@Test
	public void testJedisSingle(){
		//创建一个jedis对象
		Jedis jedis = new Jedis("192.168.218.140", 6379);
		//调用jedis方法，方法和名称和redis的命令相同
		jedis.set("test", "123");
		String string = jedis.get("test");
		System.out.println(string);
		//关闭jedis
		jedis.close();
	}
	
	@Test
	public void testJedisPool(){
		//创建jedis连接池
		JedisPool jedisPool = new JedisPool("192.168.218.140", 6379);
		//从连接池中获取jedis对象
		Jedis jedis = jedisPool.getResource();
		String string = jedis.get("test");
		System.out.println(string);
		jedis.close();							//关闭jedis，连接返回给连接池，如果不关闭，那么连接池中的连接会被用完
		jedisPool.close();
	}
	
	/**
	 * 集群版测试
	 */
	@Test
	public void testJedisCluster() {
		HashSet<HostAndPort> nodes = new HashSet<HostAndPort>();
		nodes.add(new HostAndPort("192.168.218.140", 7001));
		nodes.add(new HostAndPort("192.168.218.140", 7002));
		nodes.add(new HostAndPort("192.168.218.140", 7003));
		nodes.add(new HostAndPort("192.168.218.140", 7004));
		nodes.add(new HostAndPort("192.168.218.140", 7005));
		nodes.add(new HostAndPort("192.168.218.140", 7006));
		
		JedisCluster cluster = new JedisCluster(nodes);
		
		cluster.set("key1", "1000");
		String string = cluster.get("key1");
		System.out.println(string);
		
		cluster.close();
	}
	
	
	/**
	 * 单机版整合spring测试
	 */
	@Test
	public void testSpringJedisSingle(){
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
		JedisPool jedisPool = (JedisPool) applicationContext.getBean("redisClient");
		Jedis jedis = jedisPool.getResource();
		String string = jedis.get("test");
		System.out.println(string);
		jedis.close();
		jedisPool.close();
	}
}
