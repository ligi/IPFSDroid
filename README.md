# Interplanetary Broadcast
## Android App for providing Broadcast Files over the The InterPlanetary File System(IPFS)

This app is an experiment in how broadcasting files (like podcasts) would work over the IPFS

This app is a fork of [IPFS Droid](https://github.com/ligi/IPFSDroid)
and uses the [IPFS Kotlin API](https://github.com/ligi/ipfs-api-kotlin)

### Features:
*Loads and plays audio files served over IPFS
*All files are currently test files for fictitious broadcasters with meta data for broadcasters and 
files provided by JSON files on the network

The list of broadcasters is currently as follows (loaded from ipfs hash = QmQyiSTZ2LFzBgVSMVP1tpCcugudfMfTMwb7VBZrUJmbTq):
```json
{
  "broadcasters": [
    {
      "name": "Broadcaster 1",
      "description": "This broadcaster has classical music",
      "feedHash": "QmP86Ww558qNZm8MShMofHPU1HWMHKDxspN4qhbrTq4xGM"
    },
    {
      "name": "Broadcaster 2",
      "description": "This broadcaster also has classical music...",
      "feedHash": "QmPwho9dja8YSBSarmU6mRrEyfH1YcLAzTpED75BExv1mm"
    },
    {
      "name": "Broadcaster 3",
      "description": "This broadcaster has Audio files of computer music from Wikimedia Commons",
      "feedHash": "QmeiSQwFoeT3zEA3Q3Spvf1Cir333K8k4cMFDdQsVA5zZN"
    },
    {
      "name": "Broadcaster 4",
      "description": "This broadcaster also has Audio files of computer music from Wikimedia Commons",
      "feedHash": "QmVvzqup4yg1TH9Hf5hjTspqtYMFtET8B2Z3utirRKrg2S"
    },
    {
      "name": "Broadcaster 5",
      "description": "This broadcaster has Big Buck Bunny Video and Audio",
      "feedHash": "QmRzMt6Lum9fx9RHRkmg1UypLGJqGBSwBvQJFz8yNBGesg"
    }
  ]
}
```

Each broadcaster has a file published on the network like this:

```json
{
  "name": "Broadcaster 1",
  "content": [
    {
      "title": "Allegro from 27 peices", 
      "description": "Description for Allegro...",
      "fileName": "Allegro_from_27_Pieces_for_Unaccompanied_Viola_da_Gamba.ogg",
      "link": "Qmbkt6wzfVG4F3EuYspC1M5vti21R8ZRT7xhhM4kHoHgwM"
    },
    {
      "title": "Hans Kommer Med Summer", 
      "description": "Description for Hans Kommer Med Summer",
      "fileName": "Han_kommer_med_sommer.ogg",
      "link": "QmTMgHMYxhp5xWzg6J973oZZhE88Jo4GHRuAkaZo7FX85P"
    },
    {
      "title": "Her har jeg", 
      "description": "Description for Her har jeg",
      "fileName": "Her_har_jeg_stået_i_tusinde_år.ogg",
      "link": "QmNusrCZ9zQL81qwfmKRTNAjX3HXJkNwtJQmD9aV6YP7wX"
    },
    {
      "title": "Sonata in G-Major", 
      "description": "Description for Sonata in G-Major",
      "fileName": "Sonata_in_G-Major.ogg",
      "link": "QmXK7Gc2bot8Nygmf7C3vcWzWShfVbcHMD5D3ux97n6vQk"
    }
  ]
}
```

## Note: this app will only function if my daemon is running, providing the required files to the network

## TODO 
- [x] Create a playlist for playing downloaded items (probably using Room)
- [x] Add more data to the broadcasters as there is currently a good deal of overlap
- [ ] Add thumbnails to the items in the feeds
- [ ] Move Feeds.json to an IPNS namespace so it is mutable (potentially re-architect the whole thing so that each broadcaster's feed is at an IPNS)
- [x] Fix oddities with player 
- [ ] Benchmark download speeds under various conditions of nodes on the network with the requested file