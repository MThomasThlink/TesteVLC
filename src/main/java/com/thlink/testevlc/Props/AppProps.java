
package com.thlink.testevlc.Props;

public class AppProps 
{
    private static int id;
    private static String login, senha;
    private static String bancoServidor;
    private static String bancoNome;
    private static String bancoLogin;
    private static String bancoSenha;
    private static String versao;
    
    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        AppProps.id = id;
    }

    public static String getBancoServidor() {
        return bancoServidor;
    }

    public static void setBancoServidor(String bancoServidor) {
        AppProps.bancoServidor = bancoServidor;
    }

    public static String getBancoNome() {
        return bancoNome;
    }

    public static void setBancoNome(String bancoNome) {
        AppProps.bancoNome = bancoNome;
    }

    public static String getBancoLogin() {
        return bancoLogin;
    }

    public static void setBancoLogin(String bancoLogin) {
        AppProps.bancoLogin = bancoLogin;
    }

    public static String getBancoSenha() {
        return bancoSenha;
    }

    public static void setBancoSenha(String bancoSenha) {
        AppProps.bancoSenha = bancoSenha;
    }

    public static String getLogin() {
        return login;
    }

    public static void setLogin(String login) {
        AppProps.login = login;
    }

    public static String getSenha() {
        return senha;
    }

    public static void setSenha(String senha) {
        AppProps.senha = senha;
    }

    public static String getVersao() {
        return versao;
    }

    public static void setVersao(String versao) {
        AppProps.versao = versao;
    }
    
    
    
}
