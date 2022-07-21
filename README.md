# applet-exportacao-scdpa
Applet para assinatura de documentos na exportação ao scdpa.

1. Se for necessário, configure um novo projeto com as classes java que estão presentes dentro do "src" na pasta "applet".
![image](https://user-images.githubusercontent.com/91630164/180212533-8540e9d8-270e-464b-8deb-4fceae8b908c.png)

2. Como dependencias do projeto, adicionei os arquivos .jar presentes dentro da pasta "dependencias-assdigital". Também será necessário adicionar 
o jre1.8.0_171 como dependencia.
![image](https://user-images.githubusercontent.com/91630164/180214366-8215c387-b05c-4b8e-86d0-08b908cd59d6.png)

3. Depois da configuração inicial, pode ser que o eclipse reclame que a importação da classe JSObject esteja ausente, nesse caso, exclua a importação
"import netscape.javascript.JSObject" e tente importar novamente pela sugestão de importação.

![image](https://user-images.githubusercontent.com/91630164/180215008-a2833e34-439b-4ab8-9963-c3934e44aa3c.png)

---------------
Depois de configurado, deixei hardcoded a opção de leitura do token para o tipo A1, se necessário é só alterar.
![image](https://user-images.githubusercontent.com/91630164/180215375-c7c8c954-70c4-436f-bcd1-cabf030cbe23.png)


