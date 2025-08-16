# Shiny Charm Mod

A Fabric mod for Minecraft 1.21.1 that adds a configurable Shiny Charm item for Cobblemon, enhancing your Shiny Pokémon Hunting experience!

## ✨ Features

- **Shiny Charm Item**: A trinket that increases your chances of finding shiny Pokémon
- **Configurable Rates**: Easily adjust shiny spawn rates through a JSON config file
- **Real-time Config Reload**: Change rates without restarting the server using commands
- **Trinket Integration**: Wear the Shiny Charm as an accessory using Trinkets
- **Multilingual Support**: Available in English and French

## 🎮 How it Works

When you wear the Shiny Charm (equipped as a trinket), your chances of encountering shiny Pokémon are increased according to the configured rates. 

The mod automatically detects when a Pokémon spawns near a player wearing the charm and applies the enhanced shiny chance.

Currently only obtainable in Creative. Working on it...

## 📋 Requirements

- **Minecraft**: 1.21.1
- **Fabric Loader**: Latest version
- **Fabric API**: Required
- **Cobblemon**: 1.6.0+
- **Trinkets**: Required (for wearing the Shiny Charm)

## 🔧 Installation

1. Download and install [Cobblemon](https://www.curseforge.com/minecraft/mc-mods/cobblemon)
2. Download and install [Trinkets](https://www.curseforge.com/minecraft/mc-mods/trinkets)
3. Download this mod and place it in your `mods` folder
4. Launch Minecraft!

## ⚙️ Configuration

The mod creates a configuration file at `.minecraft/config/shiny_config.json`:

```json
{
  "shinyCharmChance": 1365
}
```

- `shinyCharmChance`: The denominator for shiny chance (1/x chance)
- Lower values = higher shiny rates
- Default: 1365 (approximately 0.073% chance)

### Example Configurations

```json
{
  "shinyCharmChance": 1000
}
```

## 🎯 Commands

All commands require OP level 2 (operator permissions):

### Basic Commands
- `/shiny reload` - Reload the configuration from file
- `/shiny info` - Display current shiny rate information

### Advanced Commands
- `/shinyconfig reload` - Reload configuration
- `/shinyconfig info` - Show current settings

### Command Examples
```
/shiny reload
/shiny info
```

## 🌍 Localization

The mod supports multiple languages:

- **English** (`en_us.json`)
- **French** (`fr_fr.json`)

Contributions for additional languages are welcome!

## 🔨 For Developers

### Building from Source

1. Clone the repository
2. Run `./gradlew build`
3. Find the compiled mod in `build/libs/`

### Dependencies

```gradle
dependencies {
    modImplementation("net.fabricmc:fabric-loader:0.16.5")
    modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:0.104.0+1.21.1")
    modImplementation(fabricApi.module("fabric-command-api-v2", "0.104.0+1.21.1"))
    modImplementation("net.fabricmc:fabric-language-kotlin:1.12.3+kotlin.2.0.21")
    modImplementation("com.cobblemon:fabric:1.6.0+1.21.1-SNAPSHOT")
    modImplementation("dev.emi:trinkets:3.10.0")
}
```

## 🐛 Known Issues

- None currently reported

## 📝 Changelog

### Version 1.0.0
- Initial release
- Added Shiny Charm trinket item
- Configurable shiny rates
- Real-time config reloading
- English and French localization
- Admin commands for rate management


## 🙏 Credits

- Built with [Fabric](https://fabricmc.net/)
- Integrates with [Cobblemon](https://cobblemon.com/)
- Uses [Trinkets](https://github.com/emilyploszaj/trinkets) for equipment

---

**Enjoy your Shiny Hunting Trainers ! ✨**