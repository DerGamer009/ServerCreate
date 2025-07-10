# ServerCreate

A simple Spigot plugin for Minecraft 1.21 that demonstrates server management commands. Servers are created through the TitanNode admin API and stored in a local SQLite database.

## Building

This project uses Maven. To build the plugin run:

```bash
mvn package
```

The resulting jar can be found in `target/` and placed into your server's `plugins` directory.
The plugin requires internet access so it can reach the TitanNode API. Both the
admin and user API keys are embedded in `TitanNodeApi.java` and can be changed
there if needed.

By default new servers are created with 4 GB RAM, 16 GB disk space and use the "OpInsel" egg. The default allocation returned by TitanNode is stored in a local SQLite database.
Newly created servers are automatically started once the creation process is complete.

## Commands

- `/server create <mode> <name>` - Creates a new server entry.
- `/server join <name>` - Joins the specified server (placeholder implementation).
- `/server addtime <name> <duration>` - Adds time to a server. Duration format is like `1d2h3m4s`.
- `/server settime <name> <duration|0>` - Sets the server time or `0` for unlimited.

Author: DerGamer09
