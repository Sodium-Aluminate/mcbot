package com.NaAlOH4;

import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.net.Proxy;

public class ProxySetting {
    public String type;
    public String address;
    public int port;

    public @NotNull Proxy getProxy(){
        if(type==null||address==null||port==0) return Proxy.NO_PROXY;
        if(type.length()==0||address.length()==0)return Proxy.NO_PROXY;

        if(type.equalsIgnoreCase("socks")||
                type.equalsIgnoreCase("socks5")){
            return new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(address,port));
        }
        if(type.equalsIgnoreCase("http")){
            return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(address,port));
        }
        return Proxy.NO_PROXY;
    }
}
