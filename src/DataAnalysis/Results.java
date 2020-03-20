package DataAnalysis;

public class Results
{
	private boolean initialised;

	private Product first;
	private Product second;
	private Product third;
	private Product expensive;

	private double[] statistics;

	private double lowestEconomical = 0;
	private double highestEconomical = 0;
	private double lowestExpensive = 0;
	private double highestExpensive = 0;

	public Results()
	{
		initialised = false;
	}

	public boolean compareLower(Product x)
	{
		return x.getPricePerUnit() <= first.getPricePerUnit();
	}

	public boolean compareHigher(Product x)
	{
		return x.getPricePerUnit() > expensive.getPricePerUnit();
	}

	public void newTop(Product x)
	{
		third = second;
		second = first;
		first = x;
	}

	public void newExpensive(Product x)
	{
		expensive = x;
	}

	public boolean isNull()
	{
		return !initialised;
	}

	public void initialise(Product x)
	{
		first = expensive = x;
		initialised = true;
	}

	public void setStatistics(double[] statistics)
	{
		this.statistics = statistics;
	}

	public double[] getStatistics()
	{
		return statistics;
	}

	public Product[] getTop3()
	{
		return new Product[] {first, second, third};
	}

	public Product getExpensive()
	{
		return expensive;
	}

	public String getEconomicalBrand()
	{
		return first.getBrand();
	}

	public String getExpensiveBrand()
	{
		return expensive.getBrand();
	}

	public boolean economicalIsNull()
	{
		return (lowestEconomical == 0);
	}

	public boolean expensiveIsNull()
	{
		return (lowestExpensive == 0);
	}

	public void setLowestEconomical(double x)
	{
		lowestEconomical = x;
	}

	public void setHighestEconomical(double x)
	{
		highestEconomical = x;
	}

	public void setLowestExpensive(double x)
	{
		lowestExpensive = x;
	}

	public void setHighestExpensive(double x)
	{
		highestExpensive = x;
	}

	public double getLowestEconomical()
	{
		return lowestEconomical;
	}

	public double getHighestEconomical()
	{
		return highestEconomical;
	}

	public double getLowestExpensive()
	{
		return lowestExpensive;
	}

	public double getHighestExpensive()
	{
		return highestExpensive;
	}


}
