
package com.thlink.testevlc.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class FR_Usuario implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int USR_CODIGO;
    
    private String USR_LOGIN, USR_NOME;
    
    private String USR_SENHA;

    public int getUSR_CODIGO() {
        return USR_CODIGO;
    }

    public void setUSR_CODIGO(int USR_CODIGO) {
        this.USR_CODIGO = USR_CODIGO;
    }

    public String getUSR_LOGIN() {
        return USR_LOGIN;
    }

    public void setUSR_LOGIN(String USR_LOGIN) {
        this.USR_LOGIN = USR_LOGIN;
    }

    public String getUSR_NOME() {
        return USR_NOME;
    }

    public void setUSR_NOME(String USR_NOME) {
        this.USR_NOME = USR_NOME;
    }

    public String getUSR_SENHA() {
        return USR_SENHA;
    }

    public void setUSR_SENHA(String USR_SENHA) {
        this.USR_SENHA = USR_SENHA;
    }

}
