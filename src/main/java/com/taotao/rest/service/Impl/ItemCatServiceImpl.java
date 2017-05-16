/**
 * 
 */
package com.taotao.rest.service.Impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.utils.JsonUtils;
import com.taotao.mapper.TbItemCatMapper;
import com.taotao.pojo.TbItemCat;
import com.taotao.pojo.TbItemCatExample;
import com.taotao.pojo.TbItemCatExample.Criteria;
import com.taotao.rest.dao.JedisClient;
import com.taotao.rest.pojo.CatNode;
import com.taotao.rest.pojo.CatResult;
import com.taotao.rest.service.ItemCatService;

/**
 * <p> Description: 
 * 		商品分类服务
 * </p>
 * @author fengda
 * @date 2017年2月27日 下午12:17:28
 */
@Service
public class ItemCatServiceImpl implements ItemCatService{

	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	@Autowired
	private JedisClient jedisClient;

	@Value("${INDEX_ITEMCAT_REDIS_KEY}")
	private String INDEX_ITEMCAT_REDIS_KEY;
	/**
	 * 获取商品分类列表,首页分类树的展示
	 */
	@Override
	public CatResult getItemCatList() {
		
		CatResult catResult = new CatResult();
		
		
		//从redis缓存中获取数据
		try {
			String result = jedisClient.hget(INDEX_ITEMCAT_REDIS_KEY, 0+"");
			if(StringUtils.isNotBlank(result)){
				//将字符串转成list
				List<Object> resultList = JsonUtils.jsonToList(result, Object.class);
				catResult.setData(resultList);
				return catResult;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		List<?> catResult2 = catResult(0);
		catResult.setData(catResult2);
		
		
		//向缓存中添加分类树的数据
		try {
			String cacheResult = JsonUtils.objectToJson(catResult2);
			jedisClient.hset(INDEX_ITEMCAT_REDIS_KEY, 0+"", cacheResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return catResult;
	}
	
	private List<?> catResult(long parentId){
		
		//创建查询条件
		TbItemCatExample catExample = new TbItemCatExample();
		Criteria criteria = catExample.createCriteria();
		criteria.andParentIdEqualTo(parentId);

		//执行查询
		List<TbItemCat> list = itemCatMapper.selectByExample(catExample);
		List resultList = new ArrayList();
		int count = 0;
		
		for (TbItemCat tbItemCat : list) {
			CatNode node = new CatNode();
			//判断是否是父节点
			if(tbItemCat.getIsParent()) {
				node.setUrl("/products/"+tbItemCat.getId()+".html");
				
				if(tbItemCat.getParentId() == 0) {	//第一层节点
					node.setName("<a href='/products/"+tbItemCat.getId()+".html'>"+tbItemCat.getName()+"</a>");
				} else {
					node.setName(tbItemCat.getName());
				}
				node.setItem(catResult(tbItemCat.getId()));
				resultList.add(node);
				
				//由于页面上只能展示出14项出来,所以第一层只取14条记录
				count ++;
				if (parentId == 0 && count >=14) {
					break;
				}
			//如果是叶子节点
			}else {
				resultList.add("/products/"+tbItemCat.getId()+".html|" + tbItemCat.getName());
			}
		}
		
		return resultList;
	}
}
