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
    private static final SuggestionProvider<FabricClientCommandSource> PLAYERS = (commandContext, suggestionsBuilder) -> suggestPlayers(suggestionsBuilder);

    private static CompletableFuture<Suggestions> suggestPlayers(SuggestionsBuilder suggestionsBuilder) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient.world == null || minecraftClient.getNetworkHandler() == null)
            return suggestionsBuilder.buildFuture();

        for (PlayerListEntry playerListEntry : minecraftClient.getNetworkHandler().getPlayerList())
            if (CommandSource.shouldSuggest(suggestionsBuilder.getRemaining().toLowerCase(), playerListEntry.getProfile().name().toLowerCase()) && playerListEntry.getProfile().name().length() > 2)
                suggestionsBuilder.suggest(playerListEntry.getProfile().name(), () -> "Search tiers for " + playerListEntry.getProfile().name());

        if (CommandSource.shouldSuggest(suggestionsBuilder.getRemaining().toLowerCase(), "-config"))
            suggestionsBuilder.suggest("-config", () -> "Open Tiers config screen");
        if (CommandSource.shouldSuggest(suggestionsBuilder.getRemaining().toLowerCase(), "-toggle"))
            suggestionsBuilder.suggest("-toggle", () -> "Toggle " + (TiersClient.toggleMod ? "off" : "on") + " Tiers");

        return suggestionsBuilder.buildFuture();
    }

    public static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess) -> commandDispatcher.register(
                ClientCommandManager.literal("tiers").executes(ignored -> {
                            TiersClient.toggleMod(null);
                            return 1;
                        })
                        .then(ClientCommandManager.argument("Name", StringArgumentType.string()).suggests(PLAYERS).executes(context -> {
                                    TiersClient.tiersCommand(StringArgumentType.getString(context, "Name"));
                                    return 1;
                                })
                        )
        ));
    }
}