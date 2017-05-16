/**
 * 
 */
package com.taotao.rest.service;

import java.util.List;

import com.taotao.pojo.TbContent;

/**
 * <p> Description: </p>
 * @author fengda
 * @date 2017年2月28日 下午4:47:40
 */
public interface ContentService {

	public List<TbContent> getContentList(long contentCid);
}
