package com.ict.sentimentclassify.application;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.*;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.apache.commons.lang3.StringEscapeUtils;

public class ReadJSON {

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("H:\\sample.json"));
		try {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    String everything = (String)sb.toString();
		    //String ev = everything.escapeJava();
		    
		    //readString(everything);
		    String bla = "{\"created_at\":\"Wed Oct 19 02:24:43 +0000 2011\",\"id\":126483887923793920,\"id_str\":\"126483887923793920\",\"text\":\"#microsoft makes every surface a touch screen http:\\/\\/t.co\\/n2GC2gK4 #touch #gesture\",\"source\":\"\\u003ca href=\\\"http:\\/\\/itunes.apple.com\\/us\\/app\\/twitter\\/id409789998?mt=12\\\" rel=\\\"nofollow\\\"\\u003eTwitter for Mac\\u003c\\/a\\u003e\",\"truncated\":false,\"in_reply_to_status_id\":null,\"in_reply_to_status_id_str\":null,\"in_reply_to_user_id\":null,\"in_reply_to_user_id_str\":null,\"in_reply_to_screen_name\":null,\"user\":{\"id\":44167225,\"id_str\":\"44167225\",\"name\":\"Vijay Hanumolu\",\"screen_name\":\"vijju2k\",\"location\":\"\",\"description\":\"Evangelising, Strategizing, Designing Customer Experiences for all Human Digital touch points\\/\\\\\",\"url\":null,\"entities\":{\"description\":{\"urls\":[]}},\"protected\":false,\"followers_count\":183,\"friends_count\":303,\"listed_count\":63,\"created_at\":\"Tue Jun 02 17:54:00 +0000 2009\",\"favourites_count\":140,\"utc_offset\":-18000,\"time_zone\":\"Eastern Time (US & Canada)\",\"geo_enabled\":true,\"verified\":false,\"statuses_count\":3492,\"lang\":\"en\",\"contributors_enabled\":false,\"is_translator\":false,\"is_translation_enabled\":false,\"profile_background_color\":\"EBEBEB\",\"profile_background_image_url\":\"http:\\/\\/abs.twimg.com\\/images\\/themes\\/theme7\\/bg.gif\",\"profile_background_image_url_https\":\"https:\\/\\/abs.twimg.com\\/images\\/themes\\/theme7\\/bg.gif\",\"profile_background_tile\":false,\"profile_image_url\":\"http:\\/\\/pbs.twimg.com\\/profile_images\\/1731162289\\/Volleyball_Player_Icon_normal.gif\",\"profile_image_url_https\":\"https:\\/\\/pbs.twimg.com\\/profile_images\\/1731162289\\/Volleyball_Player_Icon_normal.gif\",\"profile_banner_url\":\"https:\\/\\/pbs.twimg.com\\/profile_banners\\/44167225\\/1360359088\",\"profile_link_color\":\"990000\",\"profile_sidebar_border_color\":\"DFDFDF\",\"profile_sidebar_fill_color\":\"F3F3F3\",\"profile_text_color\":\"333333\",\"profile_use_background_image\":true,\"has_extended_profile\":false,\"default_profile\":false,\"default_profile_image\":false,\"following\":false,\"follow_request_sent\":false,\"notifications\":false},\"geo\":null,\"coordinates\":null,\"place\":null,\"contributors\":null,\"is_quote_status\":false,\"retweet_count\":0,\"favorite_count\":0,\"entities\":{\"hashtags\":[{\"text\":\"microsoft\",\"indices\":[0,10]},{\"text\":\"touch\",\"indices\":[67,73]},{\"text\":\"gesture\",\"indices\":[74,82]}],\"symbols\":[],\"user_mentions\":[],\"urls\":[{\"url\":\"http:\\/\\/t.co\\/n2GC2gK4\",\"expanded_url\":\"http:\\/\\/research.microsoft.com\\/en-us\\/news\\/features\\/touch-101711.aspx\",\"display_url\":\"research.microsoft.com\\/en-us\\/news\\/fea\\u2026\",\"indices\":[46,66]}]},\"favorited\":false,\"retweeted\":false,\"possibly_sensitive\":false,\"possibly_sensitive_appealable\":false,\"lang\":\"en\"}";
		    readString(everything);
		} finally {
		    br.close();
		} 
	}

	@SuppressWarnings("unchecked")
	private static void readString(String input)
	{
		System.out.println(input);
		String jsonText = input;
		  JSONParser parser = new JSONParser();
		  ContainerFactory containerFactory = new ContainerFactory(){
		    public List<String> creatArrayContainer() {
		      return new LinkedList<String>();
		    }

		    public Map<String, Object> createObjectContainer() {
		      return new LinkedHashMap<String, Object>();
		    }
		                        
		  };
		                
		  try{
			Map<String, Object> json = (Map<String, Object>)parser.parse(jsonText, containerFactory);
		    Iterator<Entry<String, Object>> iter = json.entrySet().iterator();
		    System.out.println("==iterate result==");
		    while(iter.hasNext()){
		      Entry<String, Object> entry = iter.next();
		      System.out.println(entry.getKey() + "=>" + entry.getValue());
		    }
		                        
		    System.out.println("==toJSONString()==");
		    System.out.println(JSONValue.toJSONString(json));
		  }
		  catch(ParseException pe){
		    System.out.println(pe);
		  }
	}
}
