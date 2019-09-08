package com.yinhai.sysframework.cache.ehcache;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import com.yinhai.sysframework.cache.ehcache.service.ServerAddressService;
import com.yinhai.sysframework.service.ServiceLocator;
import com.yinhai.sysframework.util.ValidateUtil;

public class CacheUtil {

	public static void cacheElementRemove(String cacheName, String key) {
		CacheManager ehCacheManager = (CacheManager) ServiceLocator.getService("ehCacheManager");
		Cache cache = ehCacheManager.getCache(cacheName);
		if (cache.get(key) != null) {
			cache.evict(key);
		}
	}

	public static boolean cacheSynCodeRemove(Cache cache, String key) {
		ServerAddressService service = (ServerAddressService) ServiceLocator.getService("serverAddressService");
		List<String> list = service.getAllUsefulServerAddress();
		if (ValidateUtil.isNotEmpty(list)) {
            list.forEach(s -> myNotify(cache.getName(), key, s));
		}
		return false;
	}

	private static void myNotify(String cacheName, String key, String address) {
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

		CloseableHttpClient closeableHttpClient = httpClientBuilder.build();

		HttpPost httpPost = new HttpPost();
		try {
			httpPost.setURI(new URI(address + (address.endsWith("/") ? "" : "/") + "codetable/synCodeaction.do"));

			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			formparams.add(new BasicNameValuePair("key", key));
			formparams.add(new BasicNameValuePair("cacheName", cacheName));

			UrlEncodedFormEntity urlEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
			httpPost.setEntity(urlEntity);

			HttpResponse httpResponse = closeableHttpClient.execute(httpPost);

			HttpEntity entity = httpResponse.getEntity();

			httpResponse.getStatusLine();

			if (entity != null) {
				EntityUtils.toString(entity, "UTF-8");
			}
        } catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		} finally {
			try {
				closeableHttpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
