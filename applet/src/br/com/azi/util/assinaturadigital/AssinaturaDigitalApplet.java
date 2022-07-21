package br.com.azi.util.assinaturadigital;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.AuthProvider;
import java.security.ProviderException;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import br.com.azi.sigacomum.assinaturaDigital.assinatura.TokenAccess;
import br.com.azi.sigacomum.assinaturaDigital.enumeration.EnumOrientacaoRelatorio;
import br.com.azi.sigacomum.assinaturaDigital.utils.AssinaturaUtils;
import br.com.azi.util.validator.SimpleValidate;
import netscape.javascript.JSObject;

/**
 * Applete responsável por validar os dados de entrada para assinatura digital de documentos do sistema.
 * 
 * @author elugo
 *
 */
public class AssinaturaDigitalApplet extends JApplet {

    private static final long serialVersionUID = 6340359802440929488L;

    
    private JPasswordField passwordField;
    private File file;
    private String hashArquivo;
    private Boolean documentoJaAssinado;
    private String formulario;
    private String campo;
    private JSObject campoFormulario;
    private JSObject objWin;
    private EnumOrientacaoRelatorio orientacaoRelatorio;
    private String assinaturasAnteriores;
    private String scriptPosSucessoAssinatura;
    
    private String nomeCampoNomeDataAssinatura;
    private JSObject campoNomeDataAssinatura;
    
    public AssinaturaDigitalApplet() {
    	
    }
    
    public void init () {

    	
    	
    	objWin =  null;//JSObject.getWindow(this);
        
        hashArquivo 	    		= getParameter("hashArquivo");
        formulario  	    		= getParameter("form");
        campo               		= getParameter("hashAssinado");
        documentoJaAssinado 		= getParameter("documentoJaAssinado") != null && getParameter("documentoJaAssinado").equals("S") ? Boolean.TRUE : Boolean.FALSE;
        orientacaoRelatorio 		= getParameter("orientacaoRelatorio") != null && getParameter("orientacaoRelatorio").equals(EnumOrientacaoRelatorio.RETRATO.toString()) ? EnumOrientacaoRelatorio.RETRATO : EnumOrientacaoRelatorio.PAISAGEM;
        assinaturasAnteriores   	= getParameter("assinaturasAnteriores");
        nomeCampoNomeDataAssinatura = getParameter("campoNomeDataAssinatura");
        scriptPosSucessoAssinatura  = getParameter("scriptPosSucessoAssinatura");
        
        try {
            BASE64Decoder decode64 = new BASE64Decoder(); 
            byte[] arquivoHash = decode64.decodeBuffer(hashArquivo);
            file = File.createTempFile("termoReferencia", ".pdf", new File(System.getProperty("java.io.tmpdir")));
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(arquivoHash);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(getContentPane(), "Erro ao assinar arquivo", "Erro", JOptionPane.ERROR_MESSAGE);
        }
        
        final JPanel panel = new JPanel();
        getContentPane().setLayout(null);
        getContentPane().add(panel, BorderLayout.NORTH);
        panel.setLayout(null);
        getContentPane().setEnabled(false);

        JLabel lblSenha = new JLabel("Senha");
        lblSenha.setBounds(10, 11, 46, 14);
        getContentPane().add(lblSenha);

        passwordField = new JPasswordField();
        passwordField.setBounds(66, 8, 182, 20);
        getContentPane().add(passwordField);

        JButton btnAssinar = new JButton("Assinar");

        btnAssinar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if("".equals(String.copyValueOf(passwordField.getPassword()))) {
                    JOptionPane.showMessageDialog(panel, "Insira a senha.", "Atenção", JOptionPane.WARNING_MESSAGE);  
                } else {
                    try {
                    	
                        // assina digitalmente o documento
                    	
                        byte[] arquivoAssinado = TokenAccess.processarDadosAssinatura(String.copyValueOf(passwordField.getPassword()), file, assinaturasAnteriores, documentoJaAssinado, orientacaoRelatorio);
                        JOptionPane.showMessageDialog(panel, "Documento assinado com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        file.deleteOnExit();
                        
                        // cria um encode do arquivo assinado
                        BASE64Encoder encoder = new BASE64Encoder();
                        String hashArquivoAssinado = encoder.encode(arquivoAssinado);
                        
                        // seta o hash no formulário
                        JSObject objDoc = (JSObject)objWin.getMember("document");
                        JSObject objForms = (JSObject)objDoc.getMember("forms");
                        JSObject objForm = (JSObject)objForms.getMember(formulario);
                        JSObject objElements = (JSObject)objForm.getMember("elements");
                        
                        campoFormulario = (JSObject)objElements.getMember(campo);
                        campoFormulario.setMember("value", hashArquivoAssinado);
                        
                        campoNomeDataAssinatura = (JSObject)objElements.getMember(nomeCampoNomeDataAssinatura);
                        campoNomeDataAssinatura.setMember("value", TokenAccess.nameDateSignature());
                        
                        
                        if(!SimpleValidate.isNullOrBlank(scriptPosSucessoAssinatura)){
                        	objWin.eval(scriptPosSucessoAssinatura);
                        }
                        
                    } catch (ProviderException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(panel, "Token não encontrado", "Atenção", JOptionPane.WARNING_MESSAGE);  
                    } catch(IOException e) {
                        e.printStackTrace();
                        Throwable c = e.getCause();
                        if(c != null) {
                            c = c.getCause();
                            if(c!= null && c.getMessage().equals("CKR_PIN_INCORRECT")) {
                                JOptionPane.showMessageDialog(panel, "Senha incorreta.", "Atenção", JOptionPane.WARNING_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(panel, "Arquivo inválido.", "Atenção", JOptionPane.WARNING_MESSAGE);
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(panel, "Erro ao assinar arquivo", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        btnAssinar.setBounds(66, 39, 89, 23);
        getContentPane().add(btnAssinar);

        JButton btnLimpar = new JButton("Limpar");
        btnLimpar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                passwordField.setText("");
            }
        });
        btnLimpar.setBounds(159, 39, 89, 23);
        getContentPane().add(btnLimpar);
    }
    
    @Override
    public void stop() {
        try {
            AuthProvider ap = (AuthProvider) AssinaturaUtils.getProvider();
            ap.logout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
