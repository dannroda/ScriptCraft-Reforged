# ScriptCraft-Reforged - Modding Minecraft with Javascript

Due to the removal of the js script engine from Java 15 was released, the only way was to use GraalVM JDK instead of a Stock JDK.
This makes the plugin unusable if the hosting provider doesn't provide Graal runtime or the necessity of an overly complex setup to run it.

The solution? GraalJS, which is a ECMAScript-compliant runtime execute JavaScript and Node.js applications that can be used standalone with Stock JDK... with some tweaks.
The major downside is that is ECMASCRIPT-compliant, so Node's functions like setTimeout aren't present, but for now a workaround is using Bukkit's taskScheduler.

The original work is from Walter Higgins and you can find the [repo here](https://github.com/walterhiggins/ScriptCraft)
The [original README](README_original.md) was renamed so this one can be use kinda of a tracker of things I want to address. I got it working on Spigot 1.20 and later with stock JDK 21, but the build system is kinda messy.

## TODO
- [] Cleanup old files
- [] Remove obsolete CanaryMod code
- [] Improve setTimeout implementation (possible memory issues)
- [] Finish removing obsolete Ant's build-system in favor of Maven,
- [] Update documentation of plugin usage to address Graal
- [] Fix ScriptCraft documentation generation
- [] Documentation website?
- [] Explore splitting ScriptCraft into plugin wrapper and ScriptCraft itself (this can maybe expand the possibility to add more languages like python)
- [] Make use of Github's CI/CD to build releases
- [] Replace setTimeout workaround with [task-bukkit](src/main/js/lib/task-bukkit.js) 
- [] Improve converting JSFiles to String due to GraalJS only accepting Strings instead of FileStreams