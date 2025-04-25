# FishMiner

**FishMiner** is a game development project conducted as part of the *TDT4240 Software Architecture* course at NTNU. The goal of this project is to develop a functional multiplayer game for Android, designed using software architecture principles covered in the course. The game includes multiplayer support, online components, and server-based functionality. The architecture prioritizes **modifiability** as the primary quality attribute and **usability** and **performance** as the secondary.

FishMiner is a 2D arcade-style fishing game where players control a hook to catch fish, earn points, and progress through increasingly difficult levels. As players advance, they can unlock and purchase upgrades to improve their gear, such as stronger reels, faster sinkers, and more precise hooks. The game features a level-based structure, smooth real-time gameplay, and an online leaderboard where players can compete for high scores.

---

## Documentation

- 📄 [Requirements Documentation](#)  
- 🧱 [Architecture Documentation](#)  
- 💻 [Implementation Documentation](#)

## Installation

**1. Download and setup:**

- Clone the repository from GitHub or download the source files.
- Ensure that you have **Java** (version 8 or above) and **Android Studio** installed.
- Open Android Studio and select **Open Project**, then navigate to the **Fish Miner** folder to load the project.

**2. Dependencies:**

- The project uses the **LibGDX** framework and other external libraries such as **Ashley ECS** and **Scene2D**. These dependencies are managed automatically by **Gradle**.
- Missing dependencies can be installed by syncing **Gradle** in Android Studio.

**3. Running the game:**

- Open **Android Studio** and click on the **Run** button to build and deploy the game on an emulator or connected Android device.
- Choose the target device (emulator or connected Android device) and click **Run**.
- The game can also be ran on desktop by selecting `Lwjgl3Launcher.java` file as the main class and running it.

## How to play
<p align="center">
  <img src= "https://github.com/user-attachments/assets/bbbc8ba5-9460-4648-a166-843a010c8c75">
</p>

### Objective:

- Your goal when playing Fish
Miner is to catch enough fish to reach a target score and advance to the next level.
- As you progress, you unlock additional levels with increasing difficulty.
- Fish that are further down are worth more, but require more time and longer reels to catch.

<p align="center">
  <img src="https://github.com/user-attachments/assets/502f8676-3382-4023-bf2b-964598679399" alt="TDT4240 Game" width="150"/>
  <img src="https://github.com/user-attachments/assets/bfa6c89b-8abc-4494-8856-6771ac466455" alt="TDT4240 Level 5" width="150"/>
  <img src="https://github.com/user-attachments/assets/b1f6ae27-439e-401f-8a09-241ece520f37" alt="TDT4240 Group09 Level 10" width="150"/>
</p>

<p align="center"><em>Figure: Fish Miner levels</em></p>


*Figure: Fish Miner levels*

### Gameplay Controls

- The hook automatically swings back and forth. **Tap the screen** anywhere to release the hook in the direction it is pointing. Aim for the fish!


<p align="center">
  <img src="https://github.com/user-attachments/assets/3e68ddcd-873c-4639-b2b0-66f4926428b7" alt="Hook fired" width="150"/>
  <img src="https://github.com/user-attachments/assets/bc873b3b-9bf0-4ed7-a3ea-7c14c8a75b9d" alt="Fish Hooked" width="150"/>
  <img src="https://github.com/user-attachments/assets/280039e3-2fe7-45e7-8b77-5cb946eedb17" alt="Points gathered" width="150"/>
</p>

<p align="center"><em>Figure: Catching a fish</em></p>


### Store and Upgrades

After each win, the **Upgrade Store** will appear. Here you can buy upgrades which last until you lose. You can upgrade your hook, reel, or sinker once between each level. When you buy upgrades, the cost is deducted from your score, so use your points wisely!
<p align="center">
  <img src="https://github.com/user-attachments/assets/85d54b5e-1cfd-4e5f-ac06-cb124dd70b1d" alt="TDT4240 Game" width="150"/>
</p>

<p align="center"> <em>Figure: Upgrades screen</em></p>
