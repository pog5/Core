# <p align=center> Prismarine Core [WIP] [Abandoned]
## Introduction
I was bored at the time and I wanted to open my own (small) minecraft FFA/PvP server. 

EssentialsX was way too overused and I wanted to have a little challenge so I wrote this, 

a PaperMC Core/Essentials plugin with all the basic things like `/gamemode`, `/tp`, `/staffchat`, etc...

The plugin should support anything above 1.16.4 and it's only dependancy is ProtocolLib

You are free to modify and distribute and fork this project as the license says.


### Todo:
Dates are in the format of `DD/MM/YYYY`
- [x] Core (General Commands and Chat formatting)
  - [x] Chat and Private Messages [done on 1/9/2023]
    - [ ] Prefix and Suffix support (custom or PAPI)
    - [ ] Discord Bridge using Webhooks or Bot
  - [ ] Punishments ([Temp]Mute, Kick, [Temp]&[IP]Ban, [IP]Blacklist) 
    - [ ] Reason Handling (Either Chat(?) GUI or [If string contains cheating > "Unfair Advantage"])
    - [ ] History
  - [ ] Auth System
    - [ ] Login & Register Command
    - [ ] Auto Login based on IP
    - [ ] Auto Login Premium
  - [ ] TAB
  - [ ] Scoreboard
  - [ ] Economy
    - [ ] Hook into Vault and expose API 
  - [ ] Permissions
    - [ ] Groups
      - [ ] Permissions
        - [ ] Temporary 
      - [ ] Prefix and Suffix
      - [ ] Parents
      - [ ] Weight (for TAB sorting)
    - [ ] Users
      - [ ] Group grants
        - [ ] Temporary
        - [ ] Reason
      - [ ] Prefix and Suffix
      - [ ] Permissions 
        - [ ] Temporary
    - [ ] API
    - [ ] GUI
      - [ ] Group Editor
      - [ ] User Editor
      - [ ] Group Selector
      - [ ] User Selector
    - [ ] LuckPerms Integration
      - [ ] Group Sync
      - [ ] User Sync
      - [ ] Prefix and Suffix Sync
      - [ ] Permissions Sync
      - [ ] Parent Sync
      - [ ] Weight Sync

# PvP Plugin
- [ ] PvP
  - [ ] Clans/Guilds
  - [ ] Custom Death Messages
  - [ ] Statistics
    - [ ] Kills, Deaths, KDR
    - [ ] Killstreaks
  - [ ] Kits
    - [ ] Potion Effects
  - [ ] FFA
    - [ ] Warps/Spawns
      - [ ] Command Arguments
      - [ ] GUI
        - [ ] Customizable Items and Names
        - [ ] Customizable Slots
  - [ ] Duels
    - [ ] Arenas
      - [ ] Saving
      - [ ] Loading
    - [ ] Queues
      - [ ] Unranked
      - [ ] Ranked
