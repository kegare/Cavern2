package cavern.util;

import java.util.List;
import java.util.Random;

import net.minecraft.util.WeightedRandom;

public class WeightedRandomHelper
{
	public static <T extends WeightedRandom.Item> int getTotalWeight(final List<T> list1, final List<T> list2)
	{
		int weight = 0;

		for (int i = 0, size = list1.size(); i < size; ++i)
		{
			weight += list1.get(i).itemWeight;
		}

		for (int i = 0, size = list2.size(); i < size; ++i)
		{
			weight += list2.get(i).itemWeight;
		}

		return weight;
	}

	public static <T extends WeightedRandom.Item> T getRandomItem(Random random, final List<T> list1, final List<T> list2, int totalWeight)
	{
		if (totalWeight <= 0)
		{
			throw new IllegalArgumentException();
		}
		else
		{
			int i = random.nextInt(totalWeight);

			return (T)getRandomItem(list1, list2, i);
		}
	}

	public static <T extends WeightedRandom.Item> T getRandomItem(final List<T> list1, final List<T> list2,  int weight)
	{
		for (int i =0, j = list1.size(); i < j; ++i)
		{
			T t = list1.get(i);
			weight -= t.itemWeight;

			if (weight < 0)
			{
				return t;
			}
		}

		for (int i =0, j = list2.size(); i < j; ++i)
		{
			T t = list2.get(i);
			weight -= t.itemWeight;

			if (weight < 0)
			{
				return t;
			}
		}

		return (T)null;
	}

	public static <T extends WeightedRandom.Item> T getRandomItem(Random random, final List<T> list1, final List<T> list2)
	{
		return (T)getRandomItem(random, list1, list2, getTotalWeight(list1, list2));
	}
}