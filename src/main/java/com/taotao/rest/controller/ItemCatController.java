/**
 * 
 */
package com.taotao.rest.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.utils.JsonUtils;
import com.taotao.rest.pojo.CatResult;
import com.taotao.rest.service.ItemCatService;

/**
 * <p> Description: 
 * 		商品分类列表
 * </p>
 * @author fengda
 * @date 2017年2月27日 下午2:09:52
 */
@Controller
public class ItemCatController {
	@Autowired
	private ItemCatService itemCatService;
	
//	@RequestMapping(value="/itemcat/list",produces=MediaType.APPLICATION_JSON_VALUE+";charset=utf-8")
//	@ResponseBody
//	public String getItemCatList(String callback){
//		CatResult list = itemCatService.getItemCatList();
//		//将pofo转换成字符串
//		String json = JsonUtils.objectToJson(list);
//		//拼装返回值
//		String result= callback + "(" + json + ")";
//		return result;
//	}
	
	//方法二	spring4.1	以后才能使用
	@RequestMapping(value="/itemcat/list")
	@ResponseBody
	public Object getItemCatList(String callback){
		CatResult list = itemCatService.getItemCatList();
		MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(list);
		mappingJacksonValue.setJsonpFunction(callback);
		return mappingJacksonValue;
	}
	
}
