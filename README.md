# Legends: Monsters and Heroes

A turn-based RPG strategy game where you lead a party of heroes through a dangerous world, battle monsters, trade in markets, and grow in power.

Built in Java, with clean Object-Oriented Design and a polished terminal interface.

---

# Table of Contents
- Overview  
- Features  
- Game Structure  
- How to Play  
- Installation & Run  
- Design Pattern  
- File Breakdown  

---

# Overview
Legends: Monsters and Heroes is a grid-based role-playing game.

Players assemble a team of up to 3 heroes—Warriors, Sorcerers, or Paladins—and navigate a procedurally generated world.

Your goal is to survive, defeat monsters, and level up infinitely.

---

# Features

## 🎲 Dynamic World
- Randomly generated grid map (4×4 to 20×20)  
- Common spaces, Markets, and Inaccessible walls  
- Party marker (**P**) displayed in purple  
- Clean, color-coded terminal UI  

## ⚔️ Turn-Based Combat
- Physical attacks, spells (Fire, Ice, Lightning), potions, and equipment  
- Tactical monster abilities:
  - **Dragons** → High damage  
  - **Exoskeletons** → High defense  
  - **Spirits** → High dodge  

## 🛒 Market System
Buy/sell:
- Weapons  
- Armor  
- Spells  
- Potions  

## 📈 RPG Progression
- Heroes level up with increasing stats  
- Class-favored stats grow faster (+10%)  
- HP/MP refill on level-up  

## 🖥️ Smart UI
- ANSI color-coded messages  
- Aligned ASCII tables for stats & items  
- Clean grid display  
- Strong input validation  
- Arcade Loop: Restart immediately after Game Over without relaunching  

---

# Game Structure

## 🌍 The World

| Tile Type     | Symbol | Description                      |
|---------------|--------|----------------------------------|
| Common        | .      | Normal tile with 50% ambush chance |
| Market        | M      | Safe trade zone                  |
| Inaccessible  | X      | Wall tile                        |
| Party         | P      | Shows your current location      |


## 🧙 Heroes

| Class     | Str  | Dex  | Agi  | Description                  |
|-----------|------|------|------|------------------------------|
| Warrior   | High | Med  | High | Strong melee fighter         |
| Sorcerer  | Low  | High | High | Spell specialist             |
| Paladin   | High | High | Med  | Balanced tank/DPS hybrid     |


## 👹 Monsters

| Type        | Specialty        |
|-------------|------------------|
| Dragon      | High base damage |
| Exoskeleton | High defense     |
| Spirit      | High dodge chance |

---

# How to Play

## ⌨️ Controls

| Key | Action     | Description              |
|-----|------------|--------------------------|
| W   | Move Up    | Move north               |
| A   | Move Left  | Move west                |
| S   | Move Down  | Move south               |
| D   | Move Right | Move east                |
| M   | Market     | Enter shop (only on M)   |
| I   | Info       | Show stats and inventory |
| Q   | Quit       | Exit game                |

## ⚔️ Combat System

| Action     | Description                                |
|------------|--------------------------------------------|
| Attack     | Physical damage (Strength + Weapon)        |
| Cast Spell | Uses Mana (Dexterity scales damage)        |
| Use Potion | Heal or boost stats                        |
| Equip      | Change gear mid-battle                     |

### Spell Types:
- **Fire** → Lowers enemy defense  
- **Ice** → Lowers enemy damage  
- **Lightning** → Lowers enemy dodge  

### ⚖️ Mechanics & Balance
- Dodge scales from Agility (capped at ~60–75%)  
- Level Ups increase stats by 5% (favored +10%)  
- Selling returns 50% of item value  

---

# Installation & Run

## Prerequisites
- Java JDK 8 or higher  
- Terminal with ANSI color support  

## Compile and Run
```bash
javac -d bin src/Main.java src/common/*.java src/game/*.java \
src/utils/*.java src/items/*.java src/entities/*.java src/board/*.java

# Run after compiling
java -cp bin Main

# Design Pattern

The project follows a strict **Model-View-Controller (MVC)** inspired architecture with a **Template Method** pattern for the game loop.

### 1. Controller (`src/game/`)
- **Game.java**: Abstract template defining the `play()` lifecycle.  
- **LegendsGame.java**: Concrete implementation managing the RPG flow and UI rendering.  
- **BattleController.java**: Handles turn-based combat logic.  
- **MarketController.java**: Handles buying/selling logic.  

### 2. Model (`src/entities/` & `src/items/`)
- **Hero**, **Monster**, **Party**: Data structures for characters.  
- **Weapon**, **Armor**, **Spell**, **Potion**: Inventory items using Polymorphism.  

### 3. View/Board (`src/board/`)
- **LegendsBoard**: Renders the grid state to the console with ANSI colors.  
- **Cell**: Represents individual tiles.  

### 4. Utilities (`src/utils/` & `src/common/`)
- **GameDataLoader**: Factory pattern to parse `.txt` data files.  
- **InputValidator**: Static helper for safe user input.  
- **RandomGenerator**: Singleton for consistent random number generation.  

---

# File Breakdown

### Core
- **Main.java**: Entry point. Bootstraps the game runner.

### Game Logic
- **Game.java**: Abstract base class for any board game.  
- **LegendsGame.java**: The main engine. Handles the game loop, user input, and screen rendering.  
- **BattleController.java**: Manages the "Fight" state loop.  
- **MarketController.java**: Manages the "Shop" state loop.  

### Data & Assets
- **GameDataLoader.java**: Reads `Dragons.txt`, `Warriors.txt`, etc. from `data/`.  

### Entities
- **RPGCharacter.java**: Abstract parent for Hero and Monster.  
- **Hero.java**: Player character logic (Level up, Equip, Inventory).  
- **Monster.java**: Enemy logic (Scaling, Stats).  
- **Party.java**: Manages the group of heroes.  

### Board
- **LegendsBoard.java**: Handles grid logic and pretty-printing.  
- **CellType.java**: Enum defining grid symbols and colors.  

---

Enjoy your adventure in Legends!
