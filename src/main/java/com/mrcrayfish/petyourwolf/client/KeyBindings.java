package com.mrcrayfish.petyourwolf.client;

import com.mrcrayfish.petyourwolf.Reference;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

/**
 * Author: MrCrayfish
 */
public class KeyBindings
{
    public static final KeyBinding PET = new KeyBinding(Reference.MOD_ID + ".key.pet", GLFW.GLFW_KEY_P, "key.categories." + Reference.MOD_ID);

    public static void init()
    {
        ClientRegistry.registerKeyBinding(PET);
    }
}
