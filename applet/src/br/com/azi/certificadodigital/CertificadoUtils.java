package br.com.azi.certificadodigital;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;

public final class CertificadoUtils {

    public static String extractCN(X509Certificate certificate) throws CertificateEncodingException {
        if (certificate != null && certificate.getSubjectDN() != null) {
            X500Name x500name = new JcaX509CertificateHolder(certificate).getSubject();
            RDN cn = x500name.getRDNs(BCStyle.CN)[0];
            return IETFUtils.valueToString(cn.getFirst().getValue());
        }
        return "";
    }

    public static String extractOU(X509Certificate certificate) throws CertificateEncodingException {
        if (certificate != null && certificate.getSubjectDN() != null) {
            X500Name x500name = new JcaX509CertificateHolder(certificate).getSubject();
            RDN ou = x500name.getRDNs(BCStyle.OU)[0];
            return IETFUtils.valueToString(ou.getFirst().getValue());
        }
        return "";
    }
    
    public static String extractEmailAddress(X509Certificate certificate) throws CertificateEncodingException {
        if (certificate != null && certificate.getSubjectDN() != null) {
            X500Name x500name = new JcaX509CertificateHolder(certificate).getSubject();
            if (x500name.getRDNs(BCStyle.EmailAddress).length > 0) {
                RDN ou = x500name.getRDNs(BCStyle.EmailAddress)[0];
                return IETFUtils.valueToString(ou.getFirst().getValue());
            }
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    public static CMSSignedDataGenerator generator(final KeyStore keystore, final String alias, final String password) throws Exception {

        Security.addProvider(new BouncyCastleProvider());

        Certificate[] certchain = (Certificate[]) keystore.getCertificateChain(alias);

        final List<Certificate> certlist = new ArrayList<Certificate>();

        for (int i = 0, length = certchain == null ? 0 : certchain.length; i < length; i++) {
            certlist.add(certchain[i]);
        }

        Store certstore = new JcaCertStore(certlist);

        Certificate cert = keystore.getCertificate(alias);

        ContentSigner signer = new JcaContentSignerBuilder("SHA1withRSA").setProvider(keystore.getProvider()).build((PrivateKey) (keystore.getKey(alias, password.toCharArray())));

        CMSSignedDataGenerator generator = new CMSSignedDataGenerator();

        generator.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build()).build(signer, (X509Certificate) cert));

        generator.addCertificates(certstore);

        return generator;
    }

    public static byte[] signPkcs7(final byte[] content, final CMSSignedDataGenerator generator) throws Exception {

        CMSTypedData cmsdata = new CMSProcessableByteArray(content);
        CMSSignedData signeddata = generator.generate(cmsdata, true);
        return signeddata.getEncoded();
    }

    private static String dataAtual() {
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
        return formatter.format(new Date());
    }

    public static byte[] gerarChaveAcesso(KeyStore keyStore, String alias, String login, String password) throws Exception {
        try {
            // cria a chave de acesso baseado no login do usuario
            String conteudo = login + ";" + dataAtual();
            CMSSignedDataGenerator signatureGenerator = generator(keyStore, alias, password);
            return signPkcs7(conteudo.getBytes("UTF-8"), signatureGenerator);
        } catch (Exception e) {
        	e.printStackTrace();
            System.err.println("Erro ao gerar chave de acesso.");
        }
        return null;
    }
}
