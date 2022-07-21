package br.com.azi.certificadodigital;

import java.security.AuthProvider;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import br.com.azi.sigacomum.assinaturaDigital.utils.AssinaturaUtils;

public final class CertificadoDigitalToken {

    public static List<Certificado> retornaCertificadosDisponiveis(String login, String password) throws Exception {

        List<Certificado> listCertificado = new ArrayList<Certificado>();

        Provider provider = AssinaturaUtils.getProvider();
       // System.out.println("AssinaturaUtils.getProvider(): " + provider.getName() + " " + provider.getInfo());

        KeyStore keyStore = KeyStore.getInstance("PKCS11", provider);
       // System.out.println(" KeyStore.getInstance(\"PKCS11\", provider): " + keyStore);
        keyStore.load(null, password.toCharArray());
      // System.out.println("password.toCharArray(): " + password.toCharArray());

        //System.out.println("keyStoree: " + keyStore.getType() + " " + keyStore.getProvider() + " " + keyStore);
       // System.out.println(" keyStore.aliases(): " + keyStore.aliases());
       // System.out.println("keyStore.aliases().hasMoreElements(): " + keyStore.aliases().hasMoreElements());
//        System.out.println(" keyStore.aliases().nextElement(): " + keyStore.aliases().nextElement());

        Enumeration<String> al = keyStore.aliases();
       // System.out.println(" al.hasMoreElements(): " + al.hasMoreElements());
        //System.out.println(" al. al.nextElement(): " + al.nextElement());

        while (al.hasMoreElements()) {
            String alias = al.nextElement();
            alias = "DIEGO OTAVIO BORGES:11139";
            if (keyStore.containsAlias(alias)) {
                X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);
                if (cert != null) {
                    Certificado certificado = new Certificado();
                    certificado.setTitular(CertificadoUtils.extractCN(cert));
                    certificado.setEmissor(CertificadoUtils.extractOU(cert));
                    certificado.setEmail(CertificadoUtils.extractEmailAddress(cert));
                    certificado.setAlias(alias);
                    certificado.setValidade(cert.getNotAfter());
                    certificado.setCertificado(cert);
                    byte[] signedBytes = CertificadoUtils.gerarChaveAcesso(keyStore, alias, login, password);
                    certificado.setChaveAcesso(signedBytes);
                    if (certificado.getChaveAcesso() != null) {
                        listCertificado.add(certificado);
                    }
                }
            }
        }
        AuthProvider ap = (AuthProvider) provider;
        ap.logout();
        return listCertificado;
    }

}
