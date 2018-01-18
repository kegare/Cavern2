package cavern.config.property;

public class ConfigBiomeType
{
	private int value;

	public int getValue()
	{
		return value;
	}

	public void setValue(int type)
	{
		value = type;
	}

	public Type getType()
	{
		return Type.get(getValue());
	}

	public enum Type
	{
		NATURAL(0),
		SQUARE(1),
		LARGE_SQUARE(2);

		public static final Type[] VALUES = new Type[values().length];

		private final int type;

		private Type(int type)
		{
			this.type = type;
		}

		public int getType()
		{
			return type;
		}

		public static Type get(int type)
		{
			if (type < 0 || type >= VALUES.length)
			{
				type = 0;
			}

			return VALUES[type];
		}

		static
		{
			for (Type type : values())
			{
				VALUES[type.getType()] = type;
			}
		}
	}
}