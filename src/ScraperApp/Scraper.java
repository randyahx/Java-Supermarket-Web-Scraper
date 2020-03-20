package ScraperApp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Scraper
{
	static ArrayList<ScraperProduct> productsList = new ArrayList<>();

	public Scraper()
	{
	}

	public static void scrape(String input) throws IOException
	{
		File json = new File(JsonWriter.rootFolder);
		if (json.exists()) {
			productsList.clear();
			json.delete();
		}
		try {
			fairpriceScraper(input);
			giantScraper(input);
			JsonWriter.writeJson();
		}
		catch (Exception e) {
			productsList.clear();
			json.delete();
		}
	}

	private static void fairpriceScraper(String query) throws IOException
	{
		String fairpricelink = ("https://www.fairprice.com.sg/search?query=" + query);

		Document doc = Jsoup.connect(fairpricelink).userAgent("School Project").get();
		String linkSelector = "product-container sc-1plwklf-0 evtkKt";
		Elements link = doc.getElementsByTag("script");
		Element linkdata = link.get(1);
		String fairpricedata = linkdata.childNode(0).toString();
		JSONObject json = new JSONObject(fairpricedata);
		JSONArray jsonarray = json.getJSONObject("props").getJSONObject("pageProps").getJSONObject("data").getJSONObject("data").getJSONObject("page").getJSONArray("layouts").getJSONObject(1).getJSONObject("value").getJSONObject("collection").getJSONArray("product");
		for (int i = 0; i < jsonarray.length(); i++)
		{
				String productjson = jsonarray.get(i).toString();
				JSONObject innerjson = new JSONObject(productjson);
				String name = innerjson.getString("name");
				String brand = innerjson.getJSONObject("brand").getString("name");
				String category = innerjson.getJSONObject("primaryCategory").getString("name");
				String img = innerjson.getJSONArray("images").get(0).toString();
				String producturl = "fairprice.com.sg/product/" + innerjson.get("slug").toString();
				Double price = Double.parseDouble(innerjson.getJSONArray("storeSpecificData").getJSONObject(0).get("mrp").toString());
				Float discount = Float.parseFloat(innerjson.getJSONArray("storeSpecificData").getJSONObject(0).get("discount").toString());
				price = price - discount;
				price = numberformat(price);

				String rawVol = innerjson.getJSONObject("metaData").get("Unit Of Weight").toString();
				Float vol = Float.parseFloat(regexString(rawVol));
				// Returns volume depending on whether conversion is needed
				vol = regexVol(rawVol, vol);

				String supermarketImg = "https://ntuc.org.sg/wps/wcm/connect/4891be0044f2abd384f59d22f54c8cec/privileges-fairprice.jpg?MOD=AJPERES&CACHEID=4891be0044f2abd384f59d22f54c8cec";
				String supermarket = "fairprice";

				category = query;

				productsList.add(new ScraperProduct(producturl, img, name, brand, category, supermarket, supermarketImg, vol, price));
		}
	}


	private static void giantScraper(String query) throws IOException
	{
		String giantlink = ("https://giant.sg/search?q=" + query);

		Document doc = Jsoup.connect(giantlink).userAgent("School Project").get();
		Elements mainClass = doc.getElementsByClass("col-lg-2 col-md-4 col-6 col_product open-product-detail open-single-page algolia-click");
		int i = 0;
		String name = "", brand = "", rawurl = "", producturl = "", rawVol = "", img = "", supermarketImg = "";
		Float vol = 0.0f;
		Double price = 0.0;
		if (mainClass != null) {
			for (Element a : mainClass)
			{
				try {
					Element itemBrand = a.getElementsByClass("to-brand-page").first();
					Element itemName = a.getElementsByClass("product-link").first();
					Element itemUrl = a.getElementsByClass("product_name").first();
					itemUrl = itemUrl.select("a").first();
					Element itemPrice = a.getElementsByClass("price_now f-green price_normal").first();
					if (itemPrice == null)
					{
						itemPrice = a.getElementsByClass("price_now price_normal f-green").first();
						if (itemPrice == null)
						{
							itemPrice = a.getElementsByClass("price_now f-grey price_normal").first();
						}
					}
					Element itemVol = doc.getElementsByClass("product_desc").get(i);
					Element itemImg = doc.getElementsByClass("product_images").get(i);
					Element itemsupermarketImg = doc.getElementsByClass("header-logo img-fluid").first();
					itemsupermarketImg = itemsupermarketImg.select("img").first();
					name = itemName.text();
					;
					brand = itemBrand.text();
					rawurl = itemUrl.attr("href");
					producturl = "https://giant.sg" + rawurl;

					rawVol = itemVol.select("span").text();
					vol = Float.parseFloat(regexString(rawVol));
					// Remove text, regex doesn't work well if it's not removed
					String removeText = "Size: ";
					rawVol = rawVol.replaceAll(removeText, "");
					// Returns volume depending on whether conversion is needed
					vol = regexVol(rawVol, vol);

					price = Double.parseDouble(itemPrice.attr("data-price"));
					price = numberformat(price);
					img = itemImg.select("img").first().attr("src");
					supermarketImg = itemsupermarketImg.attr("src");
					String supermarket = "giant";
					String category = query;


					productsList.add(new ScraperProduct(producturl, img, name, brand, category, supermarket, supermarketImg, vol, price));
				}
				catch (Exception e) {
					System.out.println("Scraping error.");
				}
				i++;
			}
		}


	}

//	regex methods


	static public Double numberformat(double num)
	{
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		return num;
	}

	static public String regexString(String vol)
	{
		String regex = "[^-+0-9.]";
		return vol.replaceAll(regex, "");
	}

	// check if conversion is needed
	static public Float regexVol(String rawVol, Float vol)
	{
		Pattern pattern = Pattern.compile("(\\d*\\.?\\d+)\\s*((?:(?:g\\s*)?L|(?:g(?:\\.|\\b))?))");
		Matcher matcher = pattern.matcher(rawVol);

		if (matcher.matches())
		{
			vol = vol * 1000;
		}
		return vol;
	}
}