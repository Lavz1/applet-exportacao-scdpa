package br.com.azi.util.autenticacaodigital;

import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.ProviderException;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import org.bouncycastle.util.encoders.Base64;
import br.com.azi.certificadodigital.Certificado;
import br.com.azi.certificadodigital.CertificadoDigitalToken;
import br.com.azi.certificadodigital.CertificadoDigitalWindows;
import br.com.azi.util.validator.SimpleValidate;
import netscape.javascript.JSObject;

public class AutenticacaoDigitalApplet extends JApplet {

    private static final long serialVersionUID = 1L;

    private List<Certificado> listCertificados;

    private String login = "az.info";

    private String formulario;

    private String hashAcessoApplet;

    private String tipoCertificado;

    private JSObject campoFormulario;

    private JSObject objWin;

    private String scriptPosSucessoAssinatura;

    public AutenticacaoDigitalApplet() throws HeadlessException {
    	init();
    }

    public void init() {
//        objWin = JSObject.getWindow(this);

        hashAcessoApplet = ""; //getParameter("hashAcessoApplet");
        tipoCertificado = "A1"; //getParameter("tipoCertificado");
        formulario = "";//getParameter("form");
        scriptPosSucessoAssinatura ="";// getParameter("scriptPosSucessoAssinatura");

        System.out.println("getParameter(tipoCertificado):: " + tipoCertificado);
        if (tipoCertificado.equalsIgnoreCase("A1")) {
            try {
                if (listCertificados == null) {
                    listCertificados = CertificadoDigitalWindows.retornaCertificadosDisponiveis(login);
                    System.out.println("listCertificados == null ?????????????? se sim retorna CertificadoDigitalWindows.retornaCertificadosDisponiveis(login)::::" + listCertificados );
                } else {
                    System.out.println("listCertificado NAOOOOOOO EH NULO NAO SLCCC::: " + listCertificados);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            criarTelaCertificado();
        } else {
            criarPainelAutenticacao();
        }

    }

    public void criarPainelAutenticacao() {
        final JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.PAGE_AXIS));

        final JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new FlowLayout());
        final JLabel lblInfo = new JLabel("Informe a senha do Token");
        lblInfo.setHorizontalAlignment(JLabel.CENTER);
        panelInfo.add(lblInfo);
        panelPrincipal.add(panelInfo);

        final JPanel panelSenha = new JPanel();
        panelSenha.setLayout(new FlowLayout());
        final JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setHorizontalAlignment(JLabel.RIGHT);
        panelSenha.add(lblSenha);

        final JPasswordField fldSenha = new JPasswordField(10);
        fldSenha.setHorizontalAlignment(JPasswordField.LEFT);
        panelSenha.add(fldSenha);
        panelPrincipal.add(panelSenha);

        final JPanel panelConfirme = new JPanel();
        panelConfirme.setLayout(new FlowLayout());
        final JButton btnOk = new JButton("Ok");
        btnOk.setHorizontalAlignment(JLabel.CENTER);

        btnOk.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (fldSenha.getPassword().length > 0) {
                    if (validarAcessoTelaCertificado(String.valueOf(fldSenha.getPassword()), panelPrincipal)) {
                        panelPrincipal.setVisible(false);
                        criarTelaCertificado();
                        limparTelaAutenticacao();
                    }
                }
            }
        });

        panelConfirme.add(btnOk);
        panelPrincipal.add(panelConfirme);

        add(panelPrincipal);
    }

    public boolean validarAcessoTelaCertificado(String password, JPanel panelPrincipal) {
        boolean valido = false;
        try {
            listCertificados = CertificadoDigitalToken.retornaCertificadosDisponiveis(login, password);
            System.out.println("teste validarAcessoTelaCertificado::::: " + listCertificados);
            valido = true;
        } catch (ProviderException e) {
            JOptionPane.showMessageDialog(panelPrincipal, "Token não encontrado", "Atenção", JOptionPane.WARNING_MESSAGE);
        } catch (IOException e) {
            Throwable c = e.getCause();
            if (c != null) {
                if (c.getCause() != null && c.getCause().toString().contains("CKR_PIN_INCORRECT")) {
                    JOptionPane.showMessageDialog(panelPrincipal, "Senha incorreta.", "Atenção", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal, "Erro ao autenticar.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
        return valido;
    }

    public void criarTelaCertificado() {
       // System.out.println("teste BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
        final JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.PAGE_AXIS));

        final JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new FlowLayout());
        final JLabel lblInfo = new JLabel("Selecione o Certificado");
        lblInfo.setHorizontalAlignment(JLabel.CENTER);
        panelInfo.add(lblInfo);
        panelPrincipal.add(panelInfo);

        final JPanel panelSenha = new JPanel();

        final JComboBox cbxCert = new JComboBox(converterCertificados(listCertificados));

        panelSenha.add(cbxCert);
        panelPrincipal.add(panelSenha);

        final JPanel panelConfirme = new JPanel();
        panelConfirme.setLayout(new FlowLayout());
        final JButton btnOk = new JButton("Ok");
        btnOk.setHorizontalAlignment(JLabel.CENTER);
        panelConfirme.add(btnOk);
        panelPrincipal.add(panelConfirme);

        btnOk.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Certificado certificado = listCertificados.get(cbxCert.getSelectedIndex());

                JSObject objDoc = (JSObject) objWin.getMember("document");
                JSObject objForms = (JSObject) objDoc.getMember("forms");
                JSObject objForm = (JSObject) objForms.getMember(formulario);
                JSObject objElements = (JSObject) objForm.getMember("elements");

                campoFormulario = (JSObject) objElements.getMember(hashAcessoApplet);
                campoFormulario.setMember("value", Base64.toBase64String(certificado.getChaveAcesso()));

                if (!SimpleValidate.isNullOrBlank(scriptPosSucessoAssinatura)) {
                    objWin.eval(scriptPosSucessoAssinatura);
                }
            }
        });

        add(panelPrincipal);
    }

    private void limparTelaAutenticacao() {
        validate();
        repaint();
    }

    private String[] converterCertificados(List<Certificado> certificados) {
        System.out.println("LISTA SIZEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE: " + certificados.size());
        System.out.println("CERTIFICADO: " + certificados);
        String[] certficadosConvertidos = new String[certificados.size()];

        for (int i = 0; i < certificados.size(); i++) {
            certficadosConvertidos[i] = certificados.get(i).toString();
        }
        return certficadosConvertidos;
    }

}
