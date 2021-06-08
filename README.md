# mcbot
使用 Telegram 调用 pyCraft 来在服务器召唤挂机假人.

# Install
## depends
Install python3, java11

Install [pyCraft](https://github.com/ammaraskar/pyCraft)

## edit the pyCraft file

edit pyCrafy file: `minecraft/authentication.py` **if you are using unofficial auth server**.

(if you are using mojang auth, skip this step.)

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

## test pyCraft
```
python3 ./start.py --server [your minecraft server] --user [your account id or email] -p [password]
```

**warning: your password will be saved in bash history, see `man bash` to avoid it.**

## edit example config

config file should be saved it in `[you home dir]/.config/mcbot.json`.

- All keys named `help` is unnecessary.

- if you are using Windows, save it to anywhere and pass the path of file as argument.

# Todo
## Command
add - add user and write config file;
reload - reload config file;
## UI
add button to easily control bots

## bugs
fix command injection in calling pyCraft;
