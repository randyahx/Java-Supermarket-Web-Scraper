package ScraperApp;

import DataAnalysis.*;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

public class JsonFileHandler
{

	public JsonFileHandler()
	{
		// Empty constructor
	}

	public Product[] readFile(String category)
	{
		Product[] products = null;

		JSONParser parser = new JSONParser();
		try
		{
			JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(System.getProperty("user.dir") + "/src/Data/data.json"));
			// A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.

			JSONArray jsonArray = (JSONArray) jsonObject.get("data");

			int position = 0;
			int productWeight;

			int aboveOne = 0;
			int belowOne = 0;
			int chosenWeightClass;

			// Counting number of objects above weight threshold
			for (JSONObject product : (Iterable<JSONObject>) jsonArray)
			{
				if (product.get("category").equals(category))
				{
					productWeight = Double.parseDouble(product.get("vol").toString()) <= 25 ? 1 : 0;
					switch (productWeight)
					{
						case 0:
							belowOne++;
							break;
						case 1:
							aboveOne++;
							break;
					}
				}
			}

			// Choosing weight class
			chosenWeightClass = Math.max(aboveOne, belowOne);

			// Creating a x-sized array for the chosenWeightClass
			products = new Product[chosenWeightClass];


			for (JSONObject product : (Iterable<JSONObject>) jsonArray)
			{
				if (product.get("category").equals(category))
				{
					boolean pass = false;
					Product object = null;
					productWeight = Double.parseDouble(product.get("vol").toString()) <= 25 ? 1 : 0;
					if (chosenWeightClass == aboveOne && productWeight == 1)
					{
						object = new aboveOneKilogram();
						pass = true;
					} else if (chosenWeightClass == belowOne && productWeight == 0)
					{
						object = new belowOneKilogram();
						pass = true;
					}

					if (pass)
					{
						object.setTitle((String) product.get("name"));
						object.setPrice(Double.parseDouble(product.get("price").toString()));
						object.setVolume(Double.parseDouble(product.get("vol").toString()));
						object.setBrand((String) product.get("brand"));
						object.setSupermarket((String) product.get("supermarket"));
						object.setSupermarketImg((String) product.get("supermarketImg"));
						object.setLink((String) product.get("url"));
						object.setImage((String) product.get("img"));

						products[position] = object;
						position++;
					}
				}
			}
		} catch (Exception e)
		{
//			e.printStackTrace();
			return products;
		}

		return products;
	}
}
