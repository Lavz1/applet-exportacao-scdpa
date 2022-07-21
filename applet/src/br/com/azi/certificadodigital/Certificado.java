package br.com.azi.certificadodigital;

import java.security.cert.Certificate;
import java.util.Date;

public class Certificado {

    private String titular;

    private String emissor;

    private String alias;

    private String email;

    private Date validade;

    private Certificate certificado;

    private byte[] chaveAcesso;

    public Certificado() {
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public String getEmissor() {
        return emissor;
    }

    public void setEmissor(String emissor) {
        this.emissor = emissor;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getValidade() {
        return validade;
    }

    public void setValidade(Date validade) {
        this.validade = validade;
    }

    public Certificate getCertificado() {
        return certificado;
    }

    public void setCertificado(Certificate certificado) {
        this.certificado = certificado;
    }

    public byte[] getChaveAcesso() {
        return chaveAcesso;
    }

    public void setChaveAcesso(byte[] chaveAcesso) {
        this.chaveAcesso = chaveAcesso;
    }

    @Override
    public String toString() {
        return titular;
    }

}
