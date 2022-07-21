package br.com.azi.certificadodigital;

import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public final class CertificadoDigitalWindows {

    public static List<Certificado> retornaCertificadosDisponiveis(String login) throws Exception {
        List<Certificado> listCertificado = new ArrayList<Certificado>();
        
        KeyStore keyStore = KeyStore.getInstance("Windows-MY", "SunMSCAPI");
        keyStore.load(null, null);

        Enumeration<String> al = keyStore.aliases();
        System.out.println("ALIEASESSS^:" + keyStore.aliases());
        while (al.hasMoreElements()) {
            String alias = al.nextElement();
            if (keyStore.containsAlias(alias)) {
                X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);
                if (cert != null) {
                    Certificado certificado = new Certificado();
                    certificado.setTitular(CertificadoUtils.extractCN(cert));
                   // certificado.setEmissor(CertificadoUtils.extractOU(cert));
                    certificado.setEmail(CertificadoUtils.extractEmailAddress(cert));
                    certificado.setAlias(alias);
                    System.out.println("ALIAS:" + alias);
                    certificado.setValidade(cert.getNotAfter());
                    certificado.setCertificado(cert);
                    byte[] signedBytes = CertificadoUtils.gerarChaveAcesso(keyStore, alias, login, "978522Di@");
                    certificado.setChaveAcesso(signedBytes);
                    if (certificado.getChaveAcesso() != null) {
                        listCertificado.add(certificado);
                    }
                }
            }
        }

        return listCertificado;
    }

}
