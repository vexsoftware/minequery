# Minequery 1.3

Minequery is a [hMod](http://www.minecraftforum.net/viewtopic.php?t=23340) server plugin for Minecraft. It creates a small server listening for requests and responds with of the Minecraft server port, how many players are online, and the player list.

## Compiling

In order to compile, you need to obtain `minecraft_server.jar` (stock [Minecraft server](http://minecraft.net/download.jsp)) and `Minecraft_Mod.jar` ([hMod](http://www.minecraftforum.net/viewtopic.php?t=23340)). Place these jars in a folder called `lib` and add these to your classpath.

**To compile from command line:**

    mkdir bin
    javac -cp lib/minecraft_server.jar:lib/Minecraft_Mod.jar src/*.java -d bin

If you are on Windows, you need to change the `:` to `;` in the classpath with the above command.

**To JAR the plugin:**

    jar cvf Minequery.jar -C bin/ .

## Installing

Copy `Minequery.jar` to your plugins directory. Then add Minequery to the plugins line in `server.properties`

## Configuring

There are three settings that can be set for Minequery in `server.properties`

Example:

    minequery-port=25566
    minequery-silent=false
    minequery-verbose=false

`minequery-port` is the port on which the query server runs on.  
`minequery-silent` is to silence Minequery from writing to the log if set to `true`.  
`minequery-verbose` generates output each time a query is received if set to `true`.  

## License

Copyright (c) 2011 [Kramer Campbell](http://kramerc.com), released under the MIT license.