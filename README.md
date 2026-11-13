# RankedSMP for Minecraft 26.3 (Paper / Purpur / Folia)

## Build the .jar
Needs JDK 25 and Maven.

    mvn clean package          # -> target/RankedSMP.jar

No Maven? Push this folder to a GitHub repo: the included workflow (`.github/workflows/build.yml`) builds it on
every push; download `RankedSMP-jar` from the Actions run's Artifacts.

Drop `RankedSMP.jar` in `plugins/` and restart.

## Target
Minecraft **26.3** only (Paper API `26.3.build.243-stable`, Java 25). It also runs on Folia.

## Underworld world (exact Unstable Underworld terrain)
The plugin does not copy the terrain: it uses the real **Unstable Underworld datapack**. Put the datapack in
`world/datapacks/`, restart, and the plugin treats every world from the `unstable_underworld` namespace as the Underworld
(game mode rule, Orb bypass, falling from the Nether). Change `underworld.datapack-namespace` if the datapack uses another namespace,
or set `underworld.world-name` to a world's name. If the datapack already teleports players itself, set `teleport-on-fall: false`.
Without the datapack, Paper/Purpur fall back to a built-in cavern world; Folia cannot create worlds.

## Ritual
Mace + right click on the middle of 5 Heavy Cores laid on the ground in a **+** (or X) shape, during a Blood Moon, as Netherite/Nemesis/ArchNemesis.
Animation only: red beam, cores spiral up, ring facing down, spin inward, Underworld Orb flies to the caster (locked to them).

## Commands
`/rank [player]`, `/ranktop`, `/rankcooldown remove <player>`, `/rankreset <player|all>`, `/rankset <player> <elo>`,
`/orb give <player> <orb> [amount]`, `/orb list`, `/underworld leave|enter [player]`.
Admin commands need `underworld.admin` (OP by default).

## Resource pack
Zip the *contents* of `RankedSMP-ResourcePack` (so `pack.mcmeta` is at the zip root), host it, and set
`resource-pack.url` / `sha1` in `config.yml`, or use `server.properties`.
Rank icons use their own font `rankedsmp:rank` (characters U+E001–U+E008). A pack must not overwrite
`minecraft:font/default.json`, because that would delete every vanilla character.
