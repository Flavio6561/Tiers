package com.tiers.misc;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.tiers.TiersClient;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandSource;

import java.util.concurrent.CompletableFuture;

public class CommandRegister {
    private static final SuggestionProvider<FabricClientCommandSource> PLAYERS =
            (context, builder) -> suggestPlayers(builder);

    private static CompletableFuture<Suggestions> suggestPlayers(SuggestionsBuilder builder) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.getNetworkHandler() == null)
            return builder.buildFuture();

        for (PlayerListEntry entry : client.getNetworkHandler().getPlayerList())
            if (CommandSource.shouldSuggest(builder.getRemaining().toLowerCase(), entry.getProfile().getName().toLowerCase()) &&
                    entry.getProfile().getName().length() > 2)
                builder.suggest(entry.getProfile().getName(), () -> "Search tiers for " + entry.getProfile().getName());

        if (CommandSource.shouldSuggest(builder.getRemaining().toLowerCase(), "config"))
            builder.suggest("config", () -> "Open the config screen");
        if (CommandSource.shouldSuggest(builder.getRemaining().toLowerCase(), "toggle"))
            builder.suggest("toggle", () -> "Toggle " + (TiersClient.toggleMod ? "off" : "on") + " Tiers");

        return builder.buildFuture();
    }

    public static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                ClientCommandManager.literal("tiers")
                        .executes(TiersClient::toggleMod)
                        .then(ClientCommandManager.argument("Name", StringArgumentType.string())
                                .suggests(PLAYERS)
                                .executes(context -> {
                                    String name = StringArgumentType.getString(context, "Name");
                                    return TiersClient.searchPlayer(name);
                                })
                        )
        ));
    }
}