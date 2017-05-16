/**
 * 
 */
package com.taotao.rest.service;

import com.taotao.common.pojo.TaotaoResult;

/**
 * <p> Description: 
 * </p>
 * @author fengda
 * @date 2017年3月7日 下午5:49:12
 */
public interface ItemService {

	public TaotaoResult getItemBaseInfo(long itemId);
	
	public TaotaoResult getItemDesc(long itemId);
	
	public TaotaoResult getItemParam(long itemId);
}
