/**
 * 
 */
package com.taotao.rest.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.ExceptionUtil;
import com.taotao.rest.dao.JedisClient;
import com.taotao.rest.service.RedisService;

/**
 * <p> Description: 
 * 		当后台管理系统添加商品分类id对应的内容后，清空缓存中商品分类id对应的缓存数据
 * </p>
 * @author fengda
 * @date 2017年3月3日 上午11:21:26
 */
@Service
public class RedisServiceImpl implements RedisService {

	@Autowired
	private JedisClient jedisClient;
	
	@Value("${INDEX_CONTENT_REDIS_KEY}")
	private String content_redis_key; 	
	
	/* (non-Javadoc)
	 * @see com.taotao.rest.service.RedisService#syncContent(long)
	 */
	@Override
	public TaotaoResult syncContent(long contentCid) {
		try {
			jedisClient.hdel(content_redis_key, contentCid+"");
		} catch (Exception e) {
			e.printStackTrace();
			return TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
		}
		return TaotaoResult.ok();
	}

}
