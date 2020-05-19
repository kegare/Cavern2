package cavern.config.property;

public class ConfigDisplayPos
{
	private int value;

	public int getValue()
	{
		return value;
	}

	public void setValue(int pos)
	{
		value = pos;
	}

	public Type getType()
	{
		return Type.get(getValue());
	}

	public enum Type
	{
		TOP_RIGHT(0),
		TOP_LEFT(1),
		BOTTOM_RIGHT(2),
		BOTTOM_LEFT(3),
		HIDDEN(4);

		public static final Type[] VALUES = new Type[values().length];

		private int type;

		private Type(int type)
		{
			this.type = type;
		}

		public int getType()
		{
			return type;
		}

		public boolean isTop()
		{
			return this == TOP_RIGHT || this == TOP_LEFT;
		}

		public boolean isBottom()
		{
			return this == BOTTOM_RIGHT || this == BOTTOM_LEFT;
		}

		public boolean isRight()
		{
			return this == TOP_RIGHT || this == BOTTOM_RIGHT;
		}

		public boolean isLeft()
		{
			return this == TOP_LEFT || this == BOTTOM_LEFT;
		}

		public boolean isHidden()
		{
			return this == HIDDEN;
		}

		public static Type get(int type)
		{
			if (type < 0)
			{
				type = 0;
			}

			int max = VALUES.length - 1;

			if (type > max)
			{
				type = max;
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