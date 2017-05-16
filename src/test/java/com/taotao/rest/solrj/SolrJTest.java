/**
 * 
 */
package com.taotao.rest.solrj;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

/**
 * <p> Description: 
 * 		solrj测试代码
 * </p>
 * @author fengda
 * @date 2017年3月6日 上午10:27:16
 */
public class SolrJTest {

	@Test
	public void addDocument() throws Exception{
		
		//创建一个连接
		SolrServer solrService = new HttpSolrServer("http://192.168.218.141:8080/solr");
		//创建一个文档对象
		SolrInputDocument document = new SolrInputDocument();
		document.addField("id", "test001");
		document.addField("item_title", "测试商品");
		document.addField("item_price", 5321);
		//把文档对象写入索引库
		solrService.add(document);
		//提交
		solrService.commit();
	}
	
	
	@Test
	public void selectHeightDocument() throws SolrServerException{
		//创建一个连接
		SolrServer solrServer = new HttpSolrServer("http://192.168.218.141:8080/solr");
		//创建一个查询对象
		SolrQuery query = new SolrQuery();
		//添加查询条件
		query.setQuery("手机");
		//设置分页
		query.setStart(2);
		query.setRows(20);
		//设置默认搜索域
		query.set("df", "item_keywords");
		//设置高亮显示
		query.setHighlight(true);
		query.addHighlightField("item_title");
		query.setHighlightSimplePre("<em style=\"color:red\">");
		query.setHighlightSimplePost("</em>");
		//执行查询
		QueryResponse queryResponse = solrServer.query(query);
		//取出商品列表
		SolrDocumentList solrDocumentList = queryResponse.getResults();
		//取出高亮字段
		Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
		String title = "";
		//获取查询结果每条数据封装到实体
		for(SolrDocument solrDocument : solrDocumentList){
			//Item item = new Item();
			String id = (String)solrDocument.get("id");
			List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
			if(list != null && list.size() >0 ){
				title = list.get(0);
			}else{
				title = (String)solrDocument.get("item_title");
			}
			String name = (String)solrDocument.get("item_category_name");
			
			System.out.println(" id " + id + " title " + title + " name " + name);
		}
	}
	
	
	@Test
	public void deleteDocument() throws SolrServerException, IOException{
		//创建一个连接
		SolrServer solrService = new HttpSolrServer("http://192.168.218.141:8080/solr");
		
		//solrService.deleteById("test001");
		solrService.deleteByQuery("*:*");
		solrService.commit();
	}
	
	@Test
	public void selectDocument() throws SolrServerException{
		//创建一个连接
		SolrServer solrService = new HttpSolrServer("http://192.168.218.141:8080/solr");
		
		//创建一个查询对象
		SolrQuery query = new SolrQuery();
		//添加查询条件
		query.setQuery("*:*");
		query.setStart(5);
		query.setRows(10);
		
		//执行查询
		QueryResponse response = solrService.query(query);
		SolrDocumentList solrDocumentList = response.getResults();
		long number = solrDocumentList.getNumFound();
		System.out.println("共查询到的记录数 " + number);
		
		for(SolrDocument list : solrDocumentList){
			System.out.println("商品名称 " + list.get("item_title"));
		}
	}
	
	@Test
	public void test(){
		Set<String> set = new HashSet<String>();
		set.add("123");
		System.out.println(set);
	}
}
