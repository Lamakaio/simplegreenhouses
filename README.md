
# A simple greenhouse mod for minecraft
A simple, performant, highly configurable, greenhouse neoforge mod for minecraft. 

I made this because low-tech plant farms are nice to have on some modpacks / servers, and a lot of them can be either laggy (due to lots of entities / block state change) or underwhelming (looking at you, garden cloche).

This mod should work, but more functionalities are planned, and testing has been sparse. Please report any issues on github.

The following are *not* planned : 
- Fabric port
- any backport
- port to any 1.21 version that isn't 1.21.1 (major version might receive ports)

However, the mod is under MIT license (except for the borrowed assets, which are under their original license), so you're free to do it yourself or open a PR on this repository.

## Features 
- A single multiblock, made of a greenhouse controller, a layer of rich soil, and greenhouse glass above each Rich Soil block. 
- The greenhouse can optionally consume fertilizer and water to speed up crop production.
- Should work for most vanilla and modded plants by default (crops, flowers, sugarcane, cactus...)
- Highly configurable : in game configuration include a whitelist and a blacklist for cultivated items, the maximum width and height of the multiblock, and multipliers for pretty much everything. 
- I'm bad at textures, so instead of doing my own cool glass, right clicking on a greenhouse glass block with any glass block will change its texture (without consuming the glass block).
- Convenient to access and use : after activating the multiblock by right clicking on the controller, any rich soil or greenhouse glass can be used to access the inventory. The also provide fluid and item capabilities (that means pipes, hopper and the like can access the greenhouse through them).
- Hopefully performance-friendly. Large performance or memory costs will be considered bugs and I will fix them to the best of my ability.

## Planned features
- Auto-grow : auto apply fertilizer on each crop, so they are fully grown immediatly. Currently, the greenhouse produces the current loot table from whatever is planted.
- Trees : somehow include trees. The current plan is to require saplings on greenhouse activation, and try to grow them into trees with auto-grow, then take note of all different blocks on the tree and make them produce. 
- Energy : optionally uses forge energy to function. 
- Whatever reasonable idea ppl suggest. 
- Additional compatibility with some mods (I'm thinking of Botania). I would need someone to tell me exactly what it needed though. 

## Installation
Curseforge or Modrinth

# Credits
- Commoble and their mod https://github.com/Commoble/jumbo-furnace has been an inspiration for the multiblock code
- The greenhouse controller and glass textures are taken from github/Futureazoo/TextureRepository