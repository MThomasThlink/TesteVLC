
package com.thlink.testevlc.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class FrotaTipoOcorrenciaIMAGENS implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int codigo;
    private String descricao;
    private Boolean ativo, defeito, naoEnviarEmail;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Boolean getDefeito() {
        return defeito;
    }

    public void setDefeito(Boolean defeito) {
        this.defeito = defeito;
    }

    public Boolean getNaoEnviarEmail() {
        return naoEnviarEmail;
    }

    public void setNaoEnviarEmail(Boolean naoEnviarEmail) {
        this.naoEnviarEmail = naoEnviarEmail;
    }
    
    @Override
    public String toString ()
    {
        return String.format("%02d-%s", this.getCodigo(), this.getDescricao());
    }
    
}
