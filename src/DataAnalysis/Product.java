package DataAnalysis;

import java.awt.*;

public abstract class Product
{
	public String title;
	private String link;
	private String image;
	private double price;
	private double volume;
	private String desc;
	private String brand;
	private String supermarket;
	private String superMarketImg;

	private Color color;
	private double meanDelta;


	public Product() {
	}

	public void setPrice(Double price)
	{
		this.price = price;
	}

	public double getPrice()
	{
		return this.price;
	}

	public void setTitle(String title)
	{
		this.title = toTitle(title);
	}

	public String getTitle()
	{
		return this.title;
	}

	public void setDesc(String desc)
	{
		this.desc = desc;
	}

	public String getDesc()
	{
		return this.desc.toLowerCase();
	}

	public void setBrand(String brand)
	{
		this.brand = toTitle(brand);
	}

	public String getBrand()
	{
		return this.brand;
	}

	public void setSupermarket(String supermarket)
	{
		this.supermarket = toTitle(supermarket);
	}

	public String getSupermarket()
	{
		return this.supermarket;
	}

	public void setSupermarketImg(String superMarketImg)
	{
		this.superMarketImg = superMarketImg;
	}

	public String getSupermarketImg()
	{
		return superMarketImg;
	}

	public void setLink(String link)
	{
		this.link = link;
	}

	public String getLink()
	{
		return link;
	}

	public void setImage(String image)
	{
		this.image = image;
	}

	public String getImage()
	{
		return image;
	}

	public abstract double getPricePerUnit();

	public double getVolume()
	{
		return volume;
	}

	public void setVolume(double volume)
	{
		this.volume = volume;
	}

	public void setColor(Color color){
		this.color = color;
	}

	public Color getColor()
	{
		return color;
	}

	public void setMeanDelta(double meanDelta)
	{
		this.meanDelta = meanDelta;
	}

	public double getMeanDelta()
	{
		return meanDelta;
	}



	private static String toTitle(String text) {
		if (text == null || text.isEmpty()) {
			return text;
		}

		StringBuilder converted = new StringBuilder();

		boolean convertNext = true;
		for (char ch : text.toCharArray()) {
			if (Character.isSpaceChar(ch)) {
				convertNext = true;
			} else if (convertNext) {
				ch = Character.toTitleCase(ch);
				convertNext = false;
			} else {
				ch = Character.toLowerCase(ch);
			}
			converted.append(ch);
		}

		return converted.toString();
	}
}
