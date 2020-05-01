/*
 * -------------------------------------------------------------------------------------------------------
 * Class: ModMetals
 * This class is part of Metallurgy 4 Reforged
 * Complete source code is available at: https://github.com/Davoleo/Metallurgy-4-Reforged
 * This code is licensed under GNU GPLv3
 * Authors: ItHurtsLikeHell & Davoleo
 * Copyright (c) 2020.
 * --------------------------------------------------------------------------------------------------------
 */

package it.hurts.metallurgy_reforged.material;

import it.hurts.metallurgy_reforged.Metallurgy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ModMetals {

	public static Map<String, Metal> metalMap = new HashMap<>();

	public static Metal ADAMANTINE;
	public static Metal ALDUORITE;
	public static Metal AMORDRINE;
	public static Metal ANGMALLEN;
	public static Metal ASTRAL_SILVER;
	public static Metal ATLARUS;
	public static Metal BLACK_STEEL;
	public static Metal BRASS;
	public static Metal BRONZE;
	public static Metal CARMOT;
	public static Metal CELENEGIL;
	public static Metal CERUCLASE;
	public static Metal COPPER;
	public static Metal DAMASCUS_STEEL;
	public static Metal DEEP_IRON;
	public static Metal DESICHALKOS;
	public static Metal ELECTRUM;
	public static Metal ETHERIUM;
	public static Metal EXIMITE;
	public static Metal HADEROTH;
	public static Metal HEPATIZON;
	public static Metal IGNATIUS;
	public static Metal INFUSCOLIUM;
	public static Metal INOLASHITE;
	public static Metal KALENDRITE;
	public static Metal KRIK;
	public static Metal LEMURITE;
	public static Metal LUTETIUM;
	public static Metal MANGANESE;
	public static Metal MEUTOITE;
	public static Metal MIDASIUM;
	public static Metal MITHRIL;
	public static Metal ORICHALCUM;
	public static Metal OSMIUM;
	public static Metal OURECLASE;
	public static Metal PLATINUM;
	public static Metal PROMETHEUM;
	public static Metal QUICKSILVER;
	public static Metal RUBRACIUM;
	public static Metal SANGUINITE;
	public static Metal SHADOW_IRON;
	public static Metal SHADOW_STEEL;
	public static Metal SILVER;
	public static Metal STEEL;
	public static Metal TARTARITE;
	public static Metal TIN;
	public static Metal VULCANITE;
	public static Metal VYROXERES;
	public static Metal ZINC;

	public static void init()
	{
		Set<MetalStats> defaultStats = JsonMaterialHandler.readConfig(JsonMaterialHandler.DEFAULT_CONFIG, null);

		boolean copied = JsonMaterialHandler.copyConfig();

		Set<MetalStats> playerStats = defaultStats;

		if (!copied)
		{
			playerStats = JsonMaterialHandler.readConfig(Metallurgy.materialConfig, defaultStats);
		}

		playerStats.forEach(MetalStats::createMetal);
	}

}