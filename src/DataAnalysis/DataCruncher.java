package DataAnalysis;

import java.awt.*;
import java.lang.Math;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.stream.Collectors;

public class DataCruncher
{
	public Product[] crunch(double cumulativePrice, int count, Product[] products, Results results, Dictionary<String, Color> brandColors)
	{
		double mean = cumulativePrice / count;
		double variance = 0;

		for (Product product : products)
		{
			product.setColor(brandColors.get(product.getBrand()));
			product.setMeanDelta(product.getPricePerUnit() - mean);

			variance += Math.pow((product.getPricePerUnit() - mean), 2);
			if (product.getBrand().equals(results.getEconomicalBrand()))
			{
				if (results.economicalIsNull())
				{
					results.setLowestEconomical(product.getPricePerUnit());
					results.setHighestEconomical(product.getPricePerUnit());
				} else
				{
					if (product.getPricePerUnit() < results.getLowestEconomical())
						results.setLowestEconomical(product.getPricePerUnit());
					else if (product.getPricePerUnit() > results.getHighestEconomical())
						results.setHighestEconomical(product.getPricePerUnit());
				}
			}

			if (product.getBrand().equals(results.getExpensiveBrand()))
			{
				if (results.expensiveIsNull())
				{
					results.setLowestExpensive(product.getPricePerUnit());
					results.setHighestExpensive(product.getPricePerUnit());
				} else
				{
					if (product.getPricePerUnit() < results.getLowestExpensive())
						results.setLowestExpensive(product.getPricePerUnit());
					else if (product.getPricePerUnit() > results.getHighestExpensive())
						results.setHighestExpensive(product.getPricePerUnit());
				}
			}
		}

		double SD = Math.sqrt(variance / count);
		results.setStatistics(new double[]{mean, SD});
		return products;
	}

	public Product[] removeOutliers(Product[] products, String option)
	{
		double Q1 = 0;
		double Q3 = 0;
		double IQR;
		int medianIndex, Q1Index, Q3Index;
//		Sort first

		if (option.equals("Volume"))
		{
			Arrays.sort(products, new Comparator<Product>()
			{
				public int compare(Product product1, Product product2)
				{
					return (int) (product1.getVolume() - product2.getVolume());
				}
			});
		} else if (option.equals("Price"))
		{
			Arrays.sort(products, new Comparator<Product>()
			{
				public int compare(Product product1, Product product2)
				{
					return (int) (product1.getPrice() - product2.getPrice());
				}
			});
		}


//		if even length
		if (products.length % 2 == 0)
		{
//			if data set is even, halve the data set and find q1 and q3 as the middle of the two halves
//			get middle two numbers, sum and halve to find median

			Q1Index = (int) Math.ceil(products.length / 4.0);
			Q3Index = ((products.length / 2) + 1) + (int) Math.floor(products.length / 4.0);

			if (option.equals("Volume"))
			{
				Q1 = products[Q1Index - 1].getVolume();
				Q3 = products[Q3Index - 1].getVolume();
			} else if (option.equals("Price"))
			{
				Q1 = products[Q1Index - 1].getPrice();
				Q3 = products[Q3Index - 1].getPrice();
			}
		} else
		{
//        	data set is odd,
			medianIndex = (int) Math.ceil(products.length / 2.0);

			if (medianIndex % 2 == 0)
//        	q1 and q3 are between even numbers
			{
				Q1Index = (int) Math.floor(medianIndex / 2.0);

				Q3Index = medianIndex + Q1Index;

				if (option.equals("Volume"))
				{
					Q1 = (products[Q1Index - 1].getVolume() + products[Q1Index].getVolume()) / 2;
					Q3 = (products[Q3Index - 1].getVolume() + products[Q3Index].getVolume()) / 2;
				} else if (option.equals("Price"))
				{
					Q1 = (products[Q1Index - 1].getPrice() + products[Q1Index].getPrice()) / 2;
					Q3 = (products[Q3Index - 1].getPrice() + products[Q3Index].getPrice()) / 2;
				}
			} else
//			q1 and a3 are between odd numbers
			{
				Q1Index = (int) Math.ceil(products.length / 4.0);
				Q3Index = medianIndex + Q1Index;

				if (option.equals("Volume"))
				{
					Q1 = products[Q1Index - 1].getVolume();
					Q3 = products[Q3Index - 1].getVolume();
				} else if (option.equals("Price"))
				{
					Q1 = products[Q1Index - 1].getPrice();
					Q3 = products[Q3Index - 1].getPrice();
				}
			}
		}
		IQR = 1.5 * (Q3 - Q1);
		final double fQ1 = Q1;
		final double fQ3 = Q3;

		if (option.equals("Volume"))
			products = Arrays.stream(products).filter(elem -> elem.getVolume() >= (fQ1 - IQR) && elem.getVolume() <= (fQ3 + IQR)).toArray(Product[]::new);
		else if (option.equals("Price"))
			products = Arrays.stream(products).filter(elem -> elem.getPrice() >= (fQ1 - IQR) && elem.getPrice() <= (fQ3 + IQR)).toArray(Product[]::new);

		return products;
	}
}
