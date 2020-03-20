package DataAnalysis;

public class belowOneKilogram extends Product
{
	//  Price per litre
	private double pricePerUnit;

	public void setPricePerUnit()
	{
		pricePerUnit = getPrice() / getVolume() * 1000;
	}

	public double getPricePerUnit()
	{
		return pricePerUnit;
	}

}

