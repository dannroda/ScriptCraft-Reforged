package com.ar.dann.scriptCraft;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.graalvm.polyglot.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public final class ScriptCraftPlugin extends JavaPlugin {
    public boolean bukkit = true;
    public boolean canary = false;
    private String NO_JAVASCRIPT_MESSAGE = "No JavaScript Engine available. ScriptCraft will not work without Javascript.";

    private Context engine;

    @Override
    public void onEnable() {
//        try (Context context = Context.newBuilder("js")
//                .allowHostAccess(HostAccess.ALL)
//                .allowHostClassLookup(className -> true)
//                .allowExperimentalOptions(true)
//                .hostClassLoader(getClassLoader())
//                .option("js.nashorn-compat", "true")
//                .build()) {
//            this.engine = context;
//
//            String bootScript = new String(this.getResource("boot.js").readAllBytes(), StandardCharsets.UTF_8);
//            Value script = context.eval("js", bootScript);
//
//            Value scboot = context.getBindings("js").getMember("__scboot");
//            assert scboot.canExecute();
//            scboot.execute(this, context);
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        Context context = Context.newBuilder("js")
                .allowHostAccess(HostAccess.ALL)
                .allowHostClassLookup(className -> true)
                .allowExperimentalOptions(true)
                .hostClassLoader(getClassLoader())
                .option("js.nashorn-compat", "true")
                .build();
        this.engine = context;

        String bootScript = null;
        try {
            bootScript = new String(this.getResource("boot.js").readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Value script = context.eval("js", bootScript);

        Value scboot = context.getBindings("js").getMember("__scboot");
        assert scboot.canExecute();
        scboot.execute(this, context);

        getCommand("scriptcraft").setExecutor(new aboutPluginCMD());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd,
                                      String alias,
                                      String[] args) {
        List<String> result = new ArrayList<String>();
        if (this.engine == null) {
            this.getLogger().severe(NO_JAVASCRIPT_MESSAGE);
            return null;
        }
        try {
            Value __onTabComplete = this.engine.getBindings("js").getMember("__onTabComplete");
            assert __onTabComplete.canExecute();
            __onTabComplete.execute(result, sender, cmd, alias, args);

        } catch (Exception e) {
            sender.sendMessage(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        boolean result = false;
        Value jsResult = null;
//        Object jsResult = null;
        if (this.engine == null) {
            this.getLogger().severe(NO_JAVASCRIPT_MESSAGE);
            return false;
        }
        try {
            Value __onCommand = this.engine.getBindings("js").getMember("__onCommand");
            assert __onCommand.canExecute();
            jsResult = __onCommand.execute(sender, cmd, label, args);
        } catch (Exception se) {
            this.getLogger().severe(se.toString());
            se.printStackTrace();
            sender.sendMessage(se.getMessage());
        }
        if (jsResult != null && jsResult.isBoolean()) {

            return jsResult.asBoolean();
        }
        return result;
    }
    // For some reason this works here, but not from JS file
    public String convertJsFileToString(File fileName) {
        try {
            return FileUtils.readFileToString(fileName, StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // ScriptCraft relied on setTimeout, it isn't on ECMASCRIPT which is what GraalJS follows.
    // The thing is that Graal devs are kinda picky and didn't implement setTimeout, and other methods, just because
    // it's a feature of web browsers, NodeJs and alikes
    // So here is used Bukkit internal tasks scheduler to mimic the result
    public void setTimeout(Value callback, int ms) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (callback.canExecute()) {
                callback.execute();
            }
        }, ms, ms);
        task.cancel();


    }

    public class aboutPluginCMD implements CommandExecutor{
        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
            commandSender.sendMessage("ScriptCraft Reforged");
            commandSender.sendMessage("Original work by Walter Higgins");
            commandSender.sendMessage("Author: DTO");
            return false;
        }
    }
}
