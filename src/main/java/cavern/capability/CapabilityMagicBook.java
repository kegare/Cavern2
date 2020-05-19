package cavern.capability;

import cavern.magic.MagicBook;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class CapabilityMagicBook implements ICapabilityProvider
{
	private final MagicBook book;

	public CapabilityMagicBook()
	{
		this.book = new MagicBook();
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return capability == CaveCapabilities.MAGIC_BOOK;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if (capability == CaveCapabilities.MAGIC_BOOK)
		{
			return CaveCapabilities.MAGIC_BOOK.cast(book);
		}

		return null;
	}

	public static void register()
	{
		CapabilityManager.INSTANCE.register(MagicBook.class, new EmptyStorage<>(), () -> new MagicBook());
	}
}