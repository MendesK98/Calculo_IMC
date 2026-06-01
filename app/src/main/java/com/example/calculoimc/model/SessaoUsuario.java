package com.example.calculoimc.model;

public class SessaoUsuario {
    // Instância única da classe (Singleton)
    private static SessaoUsuario instance;
    private UserAtributos usuarioLogado;

    // Construtor privado para ninguém criar outra instância
    private SessaoUsuario() {}

    public static synchronized SessaoUsuario getInstance() {
        if (instance == null) {
            instance = new SessaoUsuario();
        }
        return instance;
    }

    public UserAtributos getUsuarioLogado() {
        return usuarioLogado;
    }

    public void setUsuarioLogado(UserAtributos usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public boolean estaLogado() {
        return usuarioLogado != null;
    }

    public void encerrarSessao() {
        usuarioLogado = null;
    }
}