# mcbot
使用 Telegram 调用 pyCraft 来在服务器召唤挂机假人.

# Install
Install python3, java11

Install [pyCraft](https://github.com/ammaraskar/pyCraft)

edit the pyCraft file `minecraft/authentication.py` **if you are using unofficial auth server**.

line 7:
```
AUTH_SERVER = "https://authserver.mojang.com"
SESSION_SERVER = "https://sessionserver.mojang.com/session/minecraft"
```
```
AUTH_SERVER = "https://[your auth server]/api/yggdrasil/authserver"
SESSION_SERVER = "https://[your auth server]/api/yggdrasil/sessionserver/session/minecraft"
```

line 260: `"selectedProfile": self.profile.to_dict(),` -> `"selectedProfile": self.profile.id_,`

test pyCraft:
```
python3 ./start.py --server [your minecraft server] --user [your account id or email] -p [password]
```

**warning: your password will be saved in bash history, see `man bash` to avoid it.**

edit example config, then save it in `[you home dir]/.config/mcbot.json`.
All keys named help is unnecessary.

if you are using Windows, save it to anywhere and pass the path of file as argument.

# Todo
## Command
add - add user and write config file;
reload - reload config file;
## UI
add button to easily control bots

## bugs
fix command injection in calling pyCraft;
