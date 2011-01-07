# Minequery 1.0

Minequery is a server plugin for the Minecraft server mod, Bukkit. It creates a small server listening for requests and responds with of the Minecraft server port, how many players are online, and the player list.

Looking for the old hMod version of Minequery? It has been moved to the [hmod](https://github.com/kramerc/minequery/tree/hmod) branch.

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

Copyright (c) 2011 Blake Beaupain and Kramer Campbell, released under the GPL v3 License.