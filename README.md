# Legends: Monsters and Heroes  
A turn-based RPG strategy game where you lead a party of heroes through a dangerous world, battle monsters, trade in markets, and grow in power.

Built in **Java**, with clean **Object-Oriented Design** and a polished **terminal interface**.

---

# Table of Contents
- [Overview](#overview)  
- [Features](#features)  
- [Game Structure](#game-structure)  
- [How to Play](#how-to-play)  
- [Installation & Run](#installation--run)  
- [Design Pattern](#design-pattern)  
- [File Breakdown](#file-breakdown)

---

# Overview
**Legends: Monsters and Heroes** is a **grid-based role-playing game** where you assemble a party of 3 heroes—**Warriors**, **Sorcerers**, or **Paladins**—and explore a procedurally generated fantasy world.

You fight monsters, visit markets, and level up as you progress.

---

# Features

## 🎲 Dynamic World
- Procedurally generated grid map  
- Market tiles, Common tiles, and Inaccessible tiles  
- Clean color-coded terminal board  

## ⚔️ Turn-Based Combat
- Attacks, spells, potions, and equipment  
- Monster types with unique strengths  
- Hero class advantages  

## 🛒 Market System
Buy/sell:
- Weapons  
- Armor  
- Spells  
- Potions  

## 📈 RPG Progression
- Level-ups increase stats  
- Favored stats grow faster per class  
- HP/MP fully restored on level-up  

## 🖥️ Terminal UI
- ANSI colors  
- Input validation  
- Restart loop on game-over  

---

# Game Structure

## 🌍 Tile Types
| Tile | Symbol | Meaning |
|------|--------|---------|
| Common | . | Fight may occur |
| Market | M | Buy/sell items |
| Inaccessible | X | Wall |
| Party | P | Your party location |

## 🧙 Hero Classes
- **Warrior** → strong melee  
- **Sorcerer** → strong spells  
- **Paladin** → balanced, tanky  

## 👹 Monster Types
- **Dragon**, **Exoskeleton**, **Spirit**  

---

# How to Play

## ⌨️ Controls
| Key | Action |
|-----|--------|
| W | Move Up |
| A | Move Left |
| S | Move Down |
| D | Move Right |
| M | Enter Market |
| I | Show Info |
| Q | Quit Game |

## ⚔️ Battle Options
- Attack  
- Cast Spell  
- Use Potion  
- Equip  

---

# Installation & Run

## Prerequisites
- Java JDK 8+  
- Terminal with ANSI colors  

## Compile
```bash
javac -d bin src/Main.java src/common/*.java src/game/*.java \
src/utils/*.java src/items/*.java src/entities/*.java src/board/*.java

# Run after compiling
java -cp bin Main
