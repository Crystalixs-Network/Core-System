package net.crystalixs.core.paper;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import net.crystalixs.core.paper.bootstrap.PaperCustomEnchantmentBootstrap;
import org.jetbrains.annotations.NotNull;

public final class CorePluginBootstrap implements PluginBootstrap {

    private final PaperCustomEnchantmentBootstrap bootstrap = new PaperCustomEnchantmentBootstrap();

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        bootstrap.registerEnchantments(context);
    }
}
