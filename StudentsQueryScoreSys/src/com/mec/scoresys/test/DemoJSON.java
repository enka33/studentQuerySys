package com.mec.scoresys.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.sf.json.JSONObject;

public class DemoJSON {
	public static void main(String[] args) {
		Info info = new Info();
		info.setId("123456");
		info.setName("eurje");
		String[] list = {"1", "2"};
		info.setList(list);
		Map<String, String> map = new HashMap<String, String>();
		map.put("ww", "12334546");
		map.put("nvdskjf", "12334546");
		info.setMap(map);
		
		ArrayList<String> arrList = new ArrayList<String>();
		arrList.add("qq");
		arrList.add("aa");
		info.setArrayList(arrList);
		
		System.out.println(JSONObject.fromObject(info).toString());
	}

}
