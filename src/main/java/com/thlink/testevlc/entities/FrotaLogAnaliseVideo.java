
package com.thlink.testevlc.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;

@Entity
public class FrotaLogAnaliseVideo implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String idOperador, nomeArquivo;
    private Integer linha, carro, tabela, matMot, rate;

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }
    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataHoraInsercao;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdOperador() {
        return idOperador;
    }

    public void setIdOperador(String idOperador) {
        this.idOperador = idOperador;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public Integer getLinha() {
        return linha;
    }

    public void setLinha(Integer linha) {
        this.linha = linha;
    }

    public Integer getCarro() {
        return carro;
    }

    public void setCarro(Integer carro) {
        this.carro = carro;
    }

    public Integer getTabela() {
        return tabela;
    }

    public void setTabela(Integer tabela) {
        this.tabela = tabela;
    }

    public Integer getMatMot() {
        return matMot;
    }

    public void setMatMot(Integer matMot) {
        this.matMot = matMot;
    }

    public Date getDataHoraInsercao() {
        return dataHoraInsercao;
    }

    public void setDataHoraInsercao(Date dataHoraInsercao) {
        this.dataHoraInsercao = dataHoraInsercao;
    }
    
    
    
}
