package DataAnalysis;

public class aboveOneKilogram extends Product
{
	//  Price per litre
	private double pricePerUnit;

	public void setPricePerUnit()
	{
		pricePerUnit = getPrice() / getVolume();
	}

	public double getPricePerUnit()
	{
		return pricePerUnit;
	}
}
