# TACZ Recycler Mod – Design Plan

## Goal

Create a Forge 1.20.1 mod that adds a dedicated **Recycler** machine for TACZ ammunition, magazines, attachments, and guns.

The Recycler exists specifically because:

- Create crushing recipes cannot reliably distinguish TACZ ammo NBT variants.
- Ammo types are unified through TACZ Unidict.
- Recycling should be configurable and balanced.
- Players need JEI-visible recipes and predictable outputs.

---

# Core Feature

## Recycler Block

A powered workstation that converts guns and ammunition into recovered materials.

### Processing Rules

- 1 input slot
- 3 output slots
- Progress bar
- Processing time:

```text
Vanilla furnace time (10 seconds)
+
Additional recycle overhead (10 seconds)

= 20 seconds total
```

Default:
- 400 ticks
- Configurable later

---

# GUI Layout

```text
+--------------------------------+
|           Recycler             |
|                                |
|  [Input] ---> [Progress]       |
|                                |
| [Out1] [Out2] [Out3]           |
|                                |
+--------------------------------+
```

Slots:

- Input: 1
- Output: 3
- Player inventory
- Hotbar

---

# Recycling Categories

## 1. Pistol Ammo

Input:

- tacz_unidict:pistol

Outputs:

- Gunpowder
- Copper Ingot

---

## 2. Rifle Ammo

Input:

- tacz_unidict:rifle

Outputs:

- Gunpowder
- Copper Ingot

---

## 3. Sniper Ammo

Input:

- tacz_unidict:sniper

Outputs:

- Gunpowder
- Copper Ingot
- Lapis Lazuli

---

## 4. Shotgun Shells

Input:

- tacz_unidict:shot

Outputs:

- Gunpowder
- Copper Ingot
- Iron Nugget

---

## 5. General Barrel

Input:

- tacz_unidict:barrel

Outputs:

- Gunpowder
- Copper Ingots
- Iron Ingots

---

## 6. Fuel Tanks

Input:

- tacz_unidict:fuel_tank

Outputs:

- Redstone
- Blaze Powder
- Iron Ingots

---

# Gun Recycling

## Tier System

Instead of defining every gun manually:

### Tier 1
Pistols / SMGs

Examples:
- Glock
- M1911
- Uzi

Outputs:
- Iron Nuggets
- Copper Ingots

### Tier 2
Assault Rifles

Examples:
- AK47
- M4A1
- G36

Outputs:
- Iron Ingots
- Copper Ingots
- Small gunpowder return

### Tier 3
Battle Rifles / LMGs

Examples:
- SCAR-H
- RPK
- M249

Outputs:
- Iron Ingots
- Copper Ingots
- Gunpowder

### Tier 4
Heavy Weapons

Examples:
- RPG
- Minigun
- M107

Outputs:
- Iron
- Copper
- Gold Nugget
- Rare bonus materials

---

# Attachment Recycling

Scopes:
- Glass
- Iron Nuggets

Suppressors:
- Iron Nuggets
- Copper Ingots

Stocks:
- Iron Nuggets

Grips:
- Iron Nuggets

Lasers:
- Redstone
- Iron Nuggets

---

# Recipe System

Use custom JSON recipes.

Example:

```json
{
  "type": "tacz_recycler:recycling",
  "ingredient": {
    "item": "tacz:ammo"
  },
  "nbt": {
    "AmmoId": "tacz_unidict:pistol"
  },
  "results": [
    {
      "item": "minecraft:gunpowder",
      "chance": 0.15
    },
    {
      "item": "minecraft:copper_nugget",
      "chance": 0.25
    }
  ],
  "processingTime": 400
}
```

Benefits:

- JEI support
- Datapack support
- Easy balancing

---

# JEI Integration

Custom recipe category:

Recycler

Display:

```text
Input
 ↓
Outputs
```

Show:

- Output chances
- Processing time

---

# Config Options

Server config:

- Processing time
- Output multipliers
- Gun recycle enable/disable
- Attachment recycle enable/disable
- Ammo recycle enable/disable

---

# Technical Structure

Packages:

```text
com.yourname.taczrecycler

block/
blockentity/
screen/
menu/
recipe/
registry/
network/
config/
compat/jei/
```

---

# Development Order

Phase 1
- Recycler block
- Block entity
- GUI
- Progress system

Phase 2
- Custom recycling recipes
- Ammo support

Phase 3
- Gun recycling
- Attachment recycling

Phase 4
- JEI integration

Phase 5
- Config system
- Balancing pass

---

# Stretch Goals

- Animated recycler model
- Sound effects
- Recycling efficiency upgrades
- Create mod compatibility
- Automation support via pipes and hoppers
- Curios integration
- Recycling statistics screen

---

# Final Vision

A dedicated industrial-style TACZ recycling machine that:

- Properly recognizes TACZ ammo NBT
- Supports all TACZ Unidict ammunition
- Recycles guns and attachments
- Shows recipes in JEI
- Is datapack-configurable
- Works in survival progression without Create hacks