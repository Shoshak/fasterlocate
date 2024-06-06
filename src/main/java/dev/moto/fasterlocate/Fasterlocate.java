package dev.moto.fasterlocate;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

import java.io.*;
import java.util.Optional;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Fasterlocate.MODID)
public class Fasterlocate {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "fasterlocate";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    private static long SEED;

    public Fasterlocate() {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static Optional<BlockPos> getTreasureLocation(BlockPos startingPos) {
        Runtime r = Runtime.getRuntime();
        try {
            Process p = r.exec(String.format("./cubiomes_generator %s %s %s", SEED, startingPos.getX(), startingPos.getZ()));
            p.waitFor();
            BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader errors = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String err = errors.readLine();
            if (err != null && !err.isEmpty()) {
                System.out.println(err);
                return Optional.empty();
            }
            String answer = b.readLine();
            String[] xz = answer.split(" ");
            return Optional.of(new BlockPos(Integer.parseInt(xz[0]), 64, Integer.parseInt(xz[1])));
        } catch (IOException e) {
            LOGGER.error("Could not read input stream");
            return Optional.empty();
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted input stream");
            return Optional.empty();
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        SEED = event.getServer().overworld().getSeed();
    }
}
