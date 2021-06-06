package com.NaAlOH4;

import java.io.IOException;
import java.util.Objects;

public class Bot {
    private LoginInformation l;
    public Bot(LoginInformation l){
        this.l=l;
    }
    public String name(){return l.name;}
    private boolean hasProcess;
    public boolean isLogin(){
        return hasProcess;
    }

    private Process process;
    public void login() throws IOException {
        if(hasProcess){
            process.destroy();
        }
        System.out.println("logining "+l.name);
        process = Runtime.getRuntime().exec(
                Objects.requireNonNullElse(Main.config.pythonCommand, "python3")
                +" "+Main.config.scriptPath+" --server "+Main.config.server+" --username "+l.mail+" -p "+l.pass);
        hasProcess = true;
    }
    public void logout(){
        if(hasProcess) process.destroy();
        hasProcess = false;
    }
}
