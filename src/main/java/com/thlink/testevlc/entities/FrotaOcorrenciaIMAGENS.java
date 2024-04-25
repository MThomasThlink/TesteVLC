
package com.thlink.testevlc.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;

@Entity
public class FrotaOcorrenciaIMAGENS implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nomeArquivo, Linha, idOperador;
    private Integer carro, tabela, codOcorrencia, idCam, matMot;
    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataHoraInsercao;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date data;
    
    @Temporal(javax.persistence.TemporalType.TIME)
    private Date horaIni, horaFim;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getLinha() {
        return Linha;
    }

    public void setLinha(String Linha) {
        this.Linha = Linha;
    }

    public String getIdOperador() {
        return idOperador;
    }

    public void setIdOperador(String idOperador) {
        this.idOperador = idOperador;
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

    public Integer getCodOcorrencia() {
        return codOcorrencia;
    }

    public void setCodOcorrencia(Integer codOcorrencia) {
        this.codOcorrencia = codOcorrencia;
    }

    public Integer getIdCam() {
        return idCam;
    }

    public void setIdCam(Integer idCam) {
        this.idCam = idCam;
    }

    public Date getDataHoraInsercao() {
        return dataHoraInsercao;
    }

    public void setDataHoraInsercao(Date dataHoraInsercao) {
        this.dataHoraInsercao = dataHoraInsercao;
    }
    
    public Date getHoraIni() {
        return horaIni;
    }

    public void setHoraIni(Date horaIni) {
        this.horaIni = horaIni;
    }

    public Date getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(Date horaFim) {
        this.horaFim = horaFim;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Integer getMatMot() {
        return matMot;
    }

    public void setMatMot(Integer matMot) {
        this.matMot = matMot;
    }
 
    
}
