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
      "description": "This broadcaster is very good",
      "feedHash": "QmeXUMmynWvHPXjwPy6UijhM3HEm28DxGLwAiHFaoPnN46"
    },
    {
      "name": "Broadcaster 2",
      "description": "This broadcaster is not quite as good good",
      "feedHash": "QmSQ76iVCLcTee8r4F4kKSwUAqw4JAuUHbbXQzo9SesykP"
    },
    {
      "name": "Broadcaster 3",
      "description": "This broadcaster is quite poor",
      "feedHash": "QmeXUMmynWvHPXjwPy6UijhM3HEm28DxGLwAiHFaoPnN46"
    },
    {
      "name": "Broadcaster 4",
      "description": "This broadcaster is very bad",
      "feedHash": "QmeXUMmynWvHPXjwPy6UijhM3HEm28DxGLwAiHFaoPnN46"
    },
    {
      "name": "Broadcaster 5",
      "description": "This broadcaster has video",
      "feedHash": "QmUyg9amBWpAmVRN9i3JHFNncZTcSHAqt3jcZDVwMWcxNN"
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
      "name": "episode 1", 
      "description": "Allegro_from_27_Pieces_for_Unaccompanied_Viola_da_Gamba.ogg",
      "file": "Qmbkt6wzfVG4F3EuYspC1M5vti21R8ZRT7xhhM4kHoHgwM"
    },
    {
      "name": "episode 2", 
      "description": "Han_kommer_med_sommer.ogg",
      "file": "QmTMgHMYxhp5xWzg6J973oZZhE88Jo4GHRuAkaZo7FX85P"
    },
    {
      "name": "episode 3", 
      "description": "Her_har_jeg_stået_i_tusinde_år.ogg",
      "file": "QmNusrCZ9zQL81qwfmKRTNAjX3HXJkNwtJQmD9aV6YP7wX"
    },
    {
      "name": "episode 4", 
      "description": "Sonata_in_G-Major.ogg",
      "file": "QmXK7Gc2bot8Nygmf7C3vcWzWShfVbcHMD5D3ux97n6vQk"
    }
  ]
}
```

## Note: this app will only function if my daemon is running, providing the required files to the network