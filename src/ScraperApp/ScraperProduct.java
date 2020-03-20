package ScraperApp;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScraperProduct
{
	private String url;
	private String image;
	private String title;
	private String brand;
	private String category;
	private String supermarket;
	private String supermarketImage;

	private float volume;
	private double price;

	public ScraperProduct(String url, String image, String title, String brand, String category, String supermarket, String supermarketImage, float volume, double price)
	{
		this.url = url;
		this.image = image;
		this.title = title;
		this.brand = brand;
		this.category = category;
		this.supermarket = supermarket;
		this.supermarketImage = supermarketImage;
		this.volume = volume;
		this.price = price;
	}


	// end of extra functions

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSupermarket() {
		return supermarket;
	}

	public void setSupermarket(String supermarket) {
		this.supermarket = supermarket;
	}

	public String getSupermarketImage() {
		return supermarketImage;
	}

	public void setSupermarketImage(String supermarketImage) {
		this.supermarketImage = supermarketImage;
	}

	public float getVolume()
	{
		return volume;
	}

	public void setVolume(float volume)
	{
		this.volume = volume;
	}

	public double getPrice()
	{
		return price;
	}

	public void setPrice(double price)
	{
		this.price = price;
	}
}