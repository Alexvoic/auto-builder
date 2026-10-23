BUILD STEPS (Minecraft 1.21.11, Fabric)
1. Install JDK 21 and a recent Gradle (or run `gradle wrapper` once to make ./gradlew).
2. Copy your Litematica and MaLiLib jars for 1.21.11 into /libs and rename them
   litematica.jar and malilib.jar.
3. Run:  gradle build
4. Your mod is at build/libs/autobuilder-1.21.11-1.0.0.jar
5. Put it in .minecraft/mods with Fabric Loader, Fabric API, Litematica, MaLiLib.

USE
- Load a schematic in Litematica and make it the selected placement.
- Stock the blocks in your hotbar (9 slots).
- Press RIGHT SHIFT, turn Auto Builder ON, walk around the build area.
