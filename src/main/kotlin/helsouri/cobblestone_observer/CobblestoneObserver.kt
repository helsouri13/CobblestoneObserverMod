package helsouri.cobblestone_observer

import helsouri.cobblestone_observer.block.CobblestoneObserverBlock
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import org.slf4j.LoggerFactory

object Cobblestone_observer : ModInitializer {
	const val MOD_ID: String = "cobblestone_observer"

	private val LOGGER = LoggerFactory.getLogger(MOD_ID)

	private val BLOCK_ID = Identifier.fromNamespaceAndPath(MOD_ID, "cobblestone_observer")
	private val BLOCK_KEY = ResourceKey.create(Registries.BLOCK, BLOCK_ID)
	private val ITEM_ID = Identifier.fromNamespaceAndPath(MOD_ID, "cobblestone_observer")
	private val ITEM_KEY = ResourceKey.create(Registries.ITEM, ITEM_ID)

	val OBSERVER_BLOCK: Block = CobblestoneObserverBlock(
		BlockBehaviour.Properties.of()
			.setId(BLOCK_KEY)
			.strength(3.5f)
			.requiresCorrectToolForDrops()
	)

	override fun onInitialize() {
		Registry.register(BuiltInRegistries.BLOCK, BLOCK_ID, OBSERVER_BLOCK)
		Registry.register(
			BuiltInRegistries.ITEM,
			ITEM_ID,
			BlockItem(OBSERVER_BLOCK, Item.Properties().setId(ITEM_KEY))
		)

		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.REDSTONE_BLOCKS).register { entries ->
			entries.accept(OBSERVER_BLOCK)
		}

		LOGGER.info("Cobblestone Observer loaded.")
	}

	fun id(path: String): Identifier = Identifier.fromNamespaceAndPath(MOD_ID, path)
}