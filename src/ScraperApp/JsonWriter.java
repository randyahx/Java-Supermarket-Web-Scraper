package ScraperApp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;

public class JsonWriter
{
	public static String rootFolder = System.getProperty("user.dir") + "/src/Data/data.json";

	public static void writeJson() throws IOException
	{
		JSONObject objects;
		JSONArray arrays = new JSONArray();
		for (Object a : Scraper.productsList)
		{
			objects = new JSONObject();
			ScraperProduct obj = (ScraperProduct) a;
			objects.put("url", obj.getUrl());
			objects.put("img", obj.getImage());
			objects.put("name", obj.getTitle());
			objects.put("brand", obj.getBrand());
			objects.put("category", obj.getCategory());
			objects.put("supermarket", obj.getSupermarket());
			objects.put("supermarketImg", obj.getSupermarketImage());
			objects.put("vol", obj.getVolume());
			objects.put("price", obj.getPrice());
			arrays.put(objects);
		}

		JSONObject data = new JSONObject().put("data", arrays);

		FileWriter writer = new FileWriter(rootFolder);
		writer.write(data.toString());
		writer.close();
	}
}
