# Tic_Tac_Toe_WifiDirect
Simple weekend project implementing multiplayer(2) tic-tac-toe game for Android.
## Description
The game is rendered on [CanvasView](https://developer.android.com/guide/topics/graphics/2d-graphics.html).<br>
For players communication [WifiDirect](https://developer.android.com/guide/topics/connectivity/wifip2p.html) API is used on peer-to-peer logic. On each player device, after enabling WifiDirect, list of available opponents(players on same wifi network) is presented. By clicking a player's name an invitation is send. Accepting an invitation starts game and players moves are rendered in real-time.<br>
### Requirements
Android 4.0 or later required.<br>
WifiDirect enabled hardware on phone required.
