package com.yinhai.sysframework.cache.ehcache.listener;

import java.io.InputStream;
import java.net.ConnectException;
import java.net.URI;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import com.yinhai.sysframework.cache.ehcache.service.ServerAddressService;
import com.yinhai.sysframework.service.ServiceLocator;
import com.yinhai.sysframework.util.ValidateUtil;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

public class TaCacheEventListener implements CacheEventListener {

	private static Log logger = LogFactory.getLog(TaCacheEventListener.class);

	public void dispose() {
	}

	public void notifyElementEvicted(Ehcache cache, Element element) {
	}

	public void notifyElementExpired(Ehcache cache, Element element) {
	}

	public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
	}

	public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
		ServerAddressService service = (ServerAddressService) ServiceLocator.getService("serverAddressService");
		List<String> list = service.getAllUsefulServerAddress();
		if (ValidateUtil.isNotEmpty(list)) {
			list.forEach(s -> {
				try {
					myNotify(cache.getName(), (String) element.getObjectKey(), s);
				} catch (ConnectException e1) {
					if (logger.isErrorEnabled()) {
						logger.error("服务：" + s + ",缓存名称：" + cache.getName() + ",key："
								+ element.getObjectKey() + "缓存清除通知失败,原因：连接超时");
					}
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("服务：" + s + ",缓存名称：" + cache.getName() + ",key："
								+ element.getObjectKey() + "缓存清除通知失败");
					}

				}
			});
		} else if (logger.isInfoEnabled()) {
			logger.info("提示:没有配置集群的server地址，无法获取当前用户访问的server与端口，请在[集群server地址配置]中配置,如果您在开发环境中可以无需理会本提示");
		}
	}

	private boolean myNotify(String cacheName, String key, String address) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug(key + " will be notify " + address + " to removed.");
		}

		HttpClient httpclient = HttpClients.createDefault();
		// 设置请求配置
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(300).setConnectionRequestTimeout(300)
				.setSocketTimeout(300).build();
		HttpGet httpget = new HttpGet();
		httpget.setConfig(requestConfig);
		httpget.setURI(new URI(address + (address.endsWith("/") ? "" : "/") + "codetable/synCodeaction.do?key=" + key
				+ "&cacheName=" + cacheName));
		try {
			HttpResponse response = httpclient.execute(httpget);
			InputStream is = response.getEntity().getContent();
			byte[] bytes = new byte[64];
			StringBuffer sb = new StringBuffer();
			while (is.read(bytes) > 0) {
				sb.append(new String(bytes));
				bytes = new byte[64];
			}
			if (sb.toString().indexOf("true") != -1) {
				return true;
			}
			throw new Exception("更新失败");
		} finally {
			httpget.releaseConnection();
		}
	}

	public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
	}

	public void notifyRemoveAll(Ehcache cache) {
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
