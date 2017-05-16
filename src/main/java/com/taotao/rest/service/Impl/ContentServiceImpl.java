/**
 * 
 */
package com.taotao.rest.service.Impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.utils.JsonUtils;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample;
import com.taotao.pojo.TbContentExample.Criteria;
import com.taotao.rest.dao.JedisClient;
import com.taotao.rest.service.ContentService;

/**
 * <p> Description: 
 * 		portal的内容管理
 * </p>
 * @author fengda
 * @date 2017年2月28日 下午4:49:40
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper tbContentMapper;
	
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${INDEX_CONTENT_REDIS_KEY}")
	private String content_redis_key;
	
	/**
	 * 根据内容分类id查询内容列表,大广告位的展示
	 */
	@Override
	public List<TbContent> getContentList(long contentCid) {
		
		//从缓存中取数据，添加缓存不能影响正常的业务逻辑
		try {
			long startTime = System.currentTimeMillis();
			String result = jedisClient.hget(content_redis_key, contentCid+"");
			System.out.println("result time --------------" + (System.currentTimeMillis() - startTime));
			if(StringUtils.isNoneBlank(result)){
				//把字符串转换成list
				List<TbContent> resultList = JsonUtils.jsonToList(result, TbContent.class);
				return resultList;
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		//根据内容分类id查询内容列表
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(contentCid);
		//执行查询
		List<TbContent> list = tbContentMapper.selectByExample(example);
		
		
		//向缓存中添加内容
		try {
			//把list转换成字符串
			String cacheString = JsonUtils.objectToJson(list);
			jedisClient.hset(content_redis_key, contentCid+"", cacheString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}
