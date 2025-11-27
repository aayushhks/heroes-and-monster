# Legends: Monsters and Heroes

A turn-based RPG strategy game where you lead a party of heroes through a dangerous world, battle monsters, trade in markets, and grow in power.  
Built in **Java**, with clean **Object-Oriented Design** and a polished **terminal interface**.

---

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Game Structure](#game-structure)
- [How to Play](#how-to-play)
- [Installation & Run](#installation--run)
- [Design Pattern](#design-pattern)
- [File Breakdown](#file-breakdown)

---

## Overview

**Legends: Monsters and Heroes** is a grid-based role-playing game.  
Players assemble a team of up to **3 heroes**—Warriors, Sorcerers, or Paladins—and navigate a procedurally generated world.  
Your goal is to **survive**, **defeat monsters**, and **level up infinitely**.

---

## Features

### 🎲 Dynamic World
- Randomly generated grid map (4×4 to 20×20)
- Common spaces, Markets, and Inaccessible walls
- Party marker (`P`) displayed in purple
- Clean, color-coded terminal UI

### ⚔️ Turn-Based Combat
- Physical attacks, spells (Fire, Ice, Lightning), potions, and equipment
- Tactical monster abilities:
    - **Dragons** → High damage
    - **Exoskeletons** → High defense
    - **Spirits** → High dodge

### 🛒 Market System
Buy/sell:
- Weapons
- Armor
- Spells
- Potions

### 📈 RPG Progression
- Heroes level up with increasing stats
- Class-favored stats grow faster (+10%)
- HP/MP refill on level-up

### 🖥️ Smart UI
- ANSI color-coded messages
- Aligned ASCII tables for stats & items
- Clean grid display
- Strong input validation

---

## Game Structure

### 🌍 The World

| Tile Type      | Symbol | Description                              |
|----------------|--------|------------------------------------------|
| Common         | `.`    | Normal tile with **50% ambush chance**   |
| Market         | `M`    | Safe trade zone                          |
| Inaccessible   | `X`    | Wall tile                                |
| Party          | `P`    | Shows your current location              |

---

### 🧙 Heroes

| Class     | Strength | Dexterity | Agility | Description                     |
|-----------|----------|-----------|---------|---------------------------------|
| Warrior   | High     | Medium    | High    | Strong melee fighter            |
| Sorcerer  | Low      | High      | High    | Spell specialist                |
| Paladin   | High     | High      | Medium  | Balanced tank/DPS hybrid        |

---

### 👹 Monsters

| Type         | Specialty         |
|--------------|-------------------|
| Dragon       | High base damage  |
| Exoskeleton  | High defense      |
| Spirit       | High dodge chance |

---

## How to Play

### ⌨️ Controls

| Key | Action     | Description                  |
|-----|------------|------------------------------|
| W   | Move Up    | Move north                   |
| A   | Move Left  | Move west                    |
| S   | Move Down  | Move south                   |
| D   | Move Right | Move east                    |
| M   | Market     | Enter shop (only on `M`)     |
| I   | Info       | Show stats and inventory     |
| Q   | Quit       | Exit game                    |

---

### ⚔️ Combat System

| Action      | Description                                |
|-------------|--------------------------------------------|
| Attack      | Physical damage (Strength + Weapon)        |
| Cast Spell  | Uses Mana (Dexterity scales damage)        |
| Use Potion  | Heal or boost stats                        |
| Equip       | Change gear mid-battle                     |

**Spell Types:**
- **Fire** → Lowers enemy defense
- **Ice** → Lowers enemy damage
- **Lightning** → Lowers enemy dodge

---

### ⚖️ Mechanics & Balance
- **Dodge** scales from Agility (capped at ~60–75%)
- **Level Ups** increase stats by 5% (favored +10%)
- **Selling** returns 50% of item value

---

## Installation & Run

### Prerequisites
- Java **JDK 8** or higher
- Terminal with ANSI color support

---

### Compile
```bash
javac -d bin src/Main.java src/common/*.java src/game/*.java src/utils/*.java src/items/*.java src/entities/*.java src/board/*.java
