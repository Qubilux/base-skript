/**
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright Peter Güttinger, SkriptLang team and contributors
 */
package ch.njol.skript.classes.data;

import org.skriptlang.skript.lang.converter.Converters;

public class DefaultConverters {
	
	public DefaultConverters() {}
	
	static {
		// Number to subtypes converters
		Converters.registerConverter(Number.class, Byte.class, Number::byteValue);
		Converters.registerConverter(Number.class, Double.class, Number::doubleValue);
		Converters.registerConverter(Number.class, Float.class, Number::floatValue);
		Converters.registerConverter(Number.class, Integer.class, Number::intValue);
		Converters.registerConverter(Number.class, Long.class, Number::longValue);
		Converters.registerConverter(Number.class, Short.class, Number::shortValue);

//		// Entity - String (UUID) // Very slow, thus disabled for now
//		Converters.registerConverter(String.class, Entity.class, new Converter<String, Entity>() {
//
//			@Override
//			@Nullable
//			public Entity convert(String f) {
//				Collection<? extends Player> players = PlayerUtils.getOnlinePlayers();
//				for (Player p : players) {
//					if (p.getName().equals(f) || p.getUniqueId().toString().equals(f))
//						return p;
//				}
//
//				return null;
//			}
//
//		});

		// Number - Vector; DISABLED due to performance problems
//		Converters.registerConverter(Number.class, Vector.class, new Converter<Number, Vector>() {
//			@Override
//			@Nullable
//			public Vector convert(Number number) {
//				return new Vector(number.doubleValue(), number.doubleValue(), number.doubleValue());
//			}
//		});

//		// World - Time
//		Skript.registerConverter(World.class, Time.class, new Converter<World, Time>() {
//			@Override
//			public Time convert(final World w) {
//				if (w == null)
//					return null;
//				return new Time((int) w.getTime());
//			}
//		});

//		// Slot - Inventory
//		Skript.addConverter(Slot.class, Inventory.class, new Converter<Slot, Inventory>() {
//			@Override
//			public Inventory convert(final Slot s) {
//				if (s == null)
//					return null;
//				return s.getInventory();
//			}
//		});

//		// Item - ItemStack
//		Converters.registerConverter(Item.class, ItemStack.class, new Converter<Item, ItemStack>() {
//			@Override
//			public ItemStack convert(final Item i) {
//				return i.getItemStack();
//			}
//		});

		// Location - World
//		Skript.registerConverter(Location.class, World.class, new Converter<Location, World>() {
//			private final static long serialVersionUID = 3270661123492313649L;
//
//			@Override
//			public World convert(final Location l) {
//				if (l == null)
//					return null;
//				return l.getWorld();
//			}
//		});

		// Location - Block
//		Converters.registerConverter(Location.class, Block.class, new Converter<Location, Block>() {
//			@Override
//			public Block convert(final Location l) {
//				return l.getBlock();
//			}
//		});

	}

}
