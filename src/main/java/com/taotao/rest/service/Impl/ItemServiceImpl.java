/**
 * 
 */
package com.taotao.rest.service.Impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.JsonUtils;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.mapper.TbItemParamItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemParamItem;
import com.taotao.pojo.TbItemParamItemExample;
import com.taotao.pojo.TbItemParamItemExample.Criteria;
import com.taotao.rest.dao.JedisClient;
import com.taotao.rest.service.ItemService;

/**
 * <p> Description: 
 * 		查询商品后，点击商品展示商品信息
 * </p>
 * @author fengda
 * @date 2017年3月7日 下午5:50:04
 */
@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private TbItemMapper tbItemMapper;
	@Autowired
	private JedisClient jedisClient;
	@Autowired
	private TbItemDescMapper tbItemDescMapper;
	@Autowired
	private TbItemParamItemMapper itemParamMapper;
	
	//商品信息的基础key
	@Value("${REDIS_ITEM_KEY}")
	private String REDIS_ITEM_KEY;	
	
	//商品信息key的有效期
	@Value("${REDIS_ITEM_EXPIRE}")
	private Integer REDIS_ITEM_EXPIRE;
	/**
	 * 查询商品基本信息
	 */
	@Override
	public TaotaoResult getItemBaseInfo(long itemId) {
		
		try {
			//添加缓存逻辑
			//从缓存中取商品信息，商品id对应的信息
			String json = jedisClient.get(REDIS_ITEM_KEY + ":" + itemId + ":base");
			//判断是否有值
			if (!StringUtils.isBlank(json)) {
				//把json转换成java对象
				TbItem item = JsonUtils.jsonToPojo(json, TbItem.class);
				return TaotaoResult.ok(item);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//查询数据库
		TbItem item = tbItemMapper.selectByPrimaryKey(itemId);
		
		try {
			//将从数据库中查询出来的数据，放到缓存中，要设置过期时间
			jedisClient.set(REDIS_ITEM_KEY+":"+itemId+":base", JsonUtils.objectToJson(item));
			//设置有效期
			jedisClient.expire(REDIS_ITEM_KEY + ":" + itemId + ":base", REDIS_ITEM_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return TaotaoResult.ok(item);
	}
	
	/**
	 * 查询商品描述信息
	 */
	@Override
	public TaotaoResult getItemDesc(long itemId) {
		//从缓存中查询商品描述信息
		try {
			String string = jedisClient.get(REDIS_ITEM_KEY + ":" + itemId + ":desc");
			if(StringUtils.isNotBlank(string)){
				//把json转换成java对象
				TbItemDesc itemDesc = JsonUtils.jsonToPojo(string, TbItemDesc.class);
				return TaotaoResult.ok(itemDesc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//查询数据库
		TbItemDesc itemDesc = tbItemDescMapper.selectByPrimaryKey(itemId);
		
		
		//将商品描述信息存到缓存中
		try {
			String itemDescJson = JsonUtils.objectToJson(itemDesc);
			jedisClient.set(REDIS_ITEM_KEY + ":" + itemId + ":desc", itemDescJson);
			//设置key的有效期
			jedisClient.expire(REDIS_ITEM_KEY + ":" + itemId + ":desc", REDIS_ITEM_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return TaotaoResult.ok(itemDesc);
	}

	/**
	 * 获取商品规格参数
	 */
	@Override
	public TaotaoResult getItemParam(long itemId) {
		
		//从缓存中获取数据
		try {
			String string = jedisClient.get(REDIS_ITEM_KEY + ":" + itemId + ":param");
			if(StringUtils.isNotBlank(string)){
				//将json串转换成实体类对应的java对象
				TbItemParamItem paramItem = JsonUtils.jsonToPojo(string, TbItemParamItem.class);
				return TaotaoResult.ok(paramItem);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//查询数据库
		TbItemParamItemExample example = new TbItemParamItemExample();
		Criteria criteria = example.createCriteria();
		criteria.andItemIdEqualTo(itemId);
		List<TbItemParamItem> list = itemParamMapper.selectByExampleWithBLOBs(example);
		
		TbItemParamItem paramItem = null;
		if(list != null && !list.isEmpty() && list.size() > 0){
			paramItem = list.get(0);
		}
		
		//向缓存中添加数据
		try {
			jedisClient.set(REDIS_ITEM_KEY + ":" + itemId + ":param", JsonUtils.objectToJson(paramItem));
			//设置key的有效期时间
			jedisClient.expire(REDIS_ITEM_KEY + ":" + itemId + ":param", REDIS_ITEM_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return TaotaoResult.ok(paramItem);
	}


}
