/**
 * 
 */
package com.taotao.rest.service;

import com.taotao.common.pojo.TaotaoResult;

/**
 * <p> Description: </p>
 * @author fengda
 * @date 2017年3月3日 上午11:20:15
 */
public interface RedisService {

	public TaotaoResult syncContent(long contentCid);
}
