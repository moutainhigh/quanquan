package com.circle.netty.formation.util;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.commons.pool2.impl.PooledSoftReference;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Properties;

public class ProducerPool<K,V> {
	private static final int DEF_MAXIDEL = 100;
	private static final int DEF_MINIDEL = 30;
	private static final int DEF_MAXTOTAL = 10000;
	private static final String DEF_COFFILE = "config/producer.properties";
	private GenericObjectPool<Producer<K, V>> pool;
	/**
	 * use all def config
	 * @throws IOException
	 */
	public ProducerPool() throws IOException {
		this(DEF_COFFILE);
	}
	
	public ProducerPool(String configName) throws IOException {
		Properties p4 = new Properties();
		p4.load(new FileInputStream(configName));
		ProducerConfig proConfig = new ProducerConfig(p4);
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMaxIdle(DEF_MINIDEL);
		config.setMaxIdle(DEF_MAXIDEL);
		config.setMaxTotal(DEF_MAXTOTAL);
		//config.setMaxWaitMillis(DEF_WAIT);// 一分钟
		//config.setTestOnBorrow(false);//获取对象是是否检测 该对象是否有效,默认false
		pool = new GenericObjectPool<>(new PoolFactory(proConfig), config);
	}
	
	public ProducerPool(ProducerConfig proConfig) {
		this(proConfig, DEF_MAXIDEL, DEF_MAXTOTAL,DEF_MINIDEL);
	}
	/**
	 * 
	 * @param proConfig
	 * @param maxidel 最大空闲数
	 * @param maxtotal 最大连接数
	 * @param minIdel 最小空闲数
	 */
	public ProducerPool(ProducerConfig proConfig, int maxidel, int maxtotal, int minIdel) {
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMaxIdle(maxidel);
		config.setMaxTotal(maxtotal);
		config.setMinIdle(minIdel);
		//config.setMaxWaitMillis(wait);// 一分钟
		pool = new GenericObjectPool<>(new PoolFactory(
				proConfig), config);
	}
	
	public boolean send(KeyedMessage<K, V> data){
		Producer<K, V> producer = null;
		try {
			producer = getProducer();
			producer.send(data);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally{
			release(producer);
		}
	}
	public boolean send(List<KeyedMessage<K, V>> datas){
		Producer<K, V> producer = null;
		try {
			producer = getProducer();
			producer.send(datas);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally{
			release(producer);
		}
	}

	/**
	 * 将对象还给pool
	 * @param producer
	 */
	public void release(Producer<K, V> producer) {
		if(producer!=null)
			pool.returnObject(producer);
	}
	
	/**
	 * 借一个对象
	 * @return
	 * @throws Exception
	 */
	public Producer<K, V> getProducer() throws Exception{
		return pool.borrowObject();
	}
	private class PoolFactory implements
			PooledObjectFactory<Producer<K, V>> {
		private ProducerConfig producerConfig;

		public PoolFactory(ProducerConfig config) {
			this.producerConfig = config;
		}

		@Override
		public void activateObject(PooledObject<Producer<K, V>> po)
				throws Exception {
		}

		@Override
		public void destroyObject(PooledObject<Producer<K, V>> po)
				throws Exception {
			// 销毁对象
			po.getObject().close();
		}

		@Override
		public PooledObject<Producer<K, V>> makeObject()
				throws Exception {
			// 创建对象 , 创建生产者
			Producer<K, V> producer = new Producer<K, V>(
					producerConfig);
			PooledObject<Producer<K, V>> object = new PooledSoftReference<>(
					new SoftReference<>(producer));
			return object;
		}

		@Override
		public void passivateObject(PooledObject<Producer<K, V>> po)
				throws Exception {
			// 即清楚buffer 等操作
		}
		@Override
		public boolean validateObject(PooledObject<Producer<K, V>> po) {
			// 检测对象是否有效
			return true;
		}
	}
}
