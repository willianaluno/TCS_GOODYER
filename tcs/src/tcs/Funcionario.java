package tcs;

public class Funcionario {
    private String nome;
    private String departamento;

    public Funcionario(String nome, String departamento) {
        this.nome = nome;
        this.departamento = departamento;
    }

    public String getNome() { return nome; }
    public String getDepartamento() { return departamento; }
}