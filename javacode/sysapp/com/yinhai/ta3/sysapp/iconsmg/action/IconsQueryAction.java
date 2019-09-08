package com.yinhai.ta3.sysapp.iconsmg.action;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.AllowedMethods;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;

import com.yinhai.webframework.BaseAction;

@Namespace("/sysapp")
@AllowedMethods({"queryIconAll"})
@Action(value="iconsQueryAction", results={
		@Result (name="success", location="/sysapp/iconsmg/iconsQuery.jsp")})
public class IconsQueryAction extends BaseAction {

	@Override
	public String execute() throws Exception {

		return super.execute();
	}

	@SuppressWarnings("deprecation")
	public String queryIconAll() throws Exception {
		ArrayList<HashMap<String, String>> localArrayList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = null;
		String str1 = "ta/resource/themes/base";
		String str2 = request.getRealPath("/") + str1 + "/ta-icon-all.css";
		BufferedReader localBufferedReader = new BufferedReader(new FileReader(str2));
		String str3 = null;
		while ((str3 = localBufferedReader.readLine()) != null) {
			if (str3.startsWith(".")) {
				map = new HashMap<String, String>();
				map.put("picclass", str3.substring(1, str3.indexOf('{')));
			}
			if (str3.indexOf("url(") > 0) {
				map.put("picaddress", str1 + "/" + str3.substring(str3.indexOf("url(") + 4, str3.indexOf(')')));
				map.put("picinx", str3.substring(str3.indexOf(')')+1, str3.indexOf("no-repeat")).trim());
				map.put("picname", str3.substring(str3.lastIndexOf('/') + 1, str3.indexOf(')')));
				localArrayList.add(map);
			}
		}
		localBufferedReader.close();
		setList("picGrid", localArrayList);
		return JSON;
	}
}
