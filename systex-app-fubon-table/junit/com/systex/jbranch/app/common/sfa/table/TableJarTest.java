package com.systex.jbranch.app.common.sfa.table;

import static org.junit.Assert.*;

import java.io.IOException;

import org.hibernate.MappingException;
import org.hibernate.cfg.Configuration;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class TableJarTest {
	
	private Logger logger = LoggerFactory.getLogger(TableJarTest.class);

	/**
	 * 測試自動產生出來的XML檔是否有問題, 通常為沒有設定PK的錯誤
	 */
	@Test
	public void testLoadXmlResource() throws IOException {
		PathMatchingResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

		boolean mappingException = false;
		Resource[] resources;
		// 使用Spring取得所有資料表的xml資源檔
		resources = resourceResolver.getResources("classpath*:com/systex/jbranch/app/common/fps/table/*.hbm.xml");
		// 建立hibernate設定
		Configuration configuration = new Configuration();
		// 加入hibernate的清單中
		for (Resource resource : resources) {
			try {
				configuration.addResource("com/systex/jbranch/app/common/fps/table/" + resource.getFilename());
			} catch (MappingException e) {
				mappingException = true;
				logger.error("{} settings failed.", resource.getFilename());
			}
		}
		assertFalse(mappingException);
	}

}
