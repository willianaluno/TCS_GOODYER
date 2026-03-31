package tcs;

import java.io.*;
import java.util.List;

public class FuncionarioHandler {

    static List<Funcionario> funcionarios = List.of(
        new Funcionario("Ana Silva",       "Tecnologia"),
        new Funcionario("João Souza",      "Financeiro"),
        new Funcionario("Maria Oliveira",  "Recursos Humanos"),
        new Funcionario("Carlos Lima",     "Tecnologia"),
        new Funcionario("Fernanda Costa",  "Marketing"),
        new Funcionario("Ricardo Alves",   "Financeiro"),
        new Funcionario("Patrícia Nunes",  "Tecnologia"),
        new Funcionario("Bruno Ferreira",  "Marketing"),
        new Funcionario("Juliana Melo",    "Recursos Humanos"),
        new Funcionario("Lucas Teixeira",  "Tecnologia"),
        new Funcionario("Camila Rocha",    "Financeiro"),
        new Funcionario("Diego Martins",   "Tecnologia"),
        new Funcionario("Larissa Campos",  "Marketing"),
        new Funcionario("Felipe Carvalho", "Recursos Humanos"),
        new Funcionario("Beatriz Santos",  "Tecnologia"),
        new Funcionario("Thiago Pereira",  "Financeiro"),
        new Funcionario("Aline Ribeiro",   "Marketing"),
        new Funcionario("Gustavo Mendes",  "Tecnologia"),
        new Funcionario("Vanessa Lima",    "Recursos Humanos"),
        new Funcionario("Rafael Gomes",    "Financeiro")
    );

    static final int TAMANHO_PAGINA = 5;

    static void servirJson(OutputStream out, int pagina) throws IOException {
        int total      = funcionarios.size();
        int totalPaginas = (int) Math.ceil((double) total / TAMANHO_PAGINA);
        int inicio     = pagina * TAMANHO_PAGINA;
        int fim        = Math.min(inicio + TAMANHO_PAGINA, total);

        if (inicio >= total) {
            String erro = "{\"erro\":\"Página não encontrada\"}";
            byte[] body = erro.getBytes("UTF-8");
            String headers = "HTTP/1.1 404 Not Found\r\n"
                + "Content-Type: application/json; charset=UTF-8\r\n"
                + "Content-Length: " + body.length + "\r\n\r\n";
            out.write(headers.getBytes());
            out.write(body);
            out.flush();
            return;
        }

        List<Funcionario> paginados = funcionarios.subList(inicio, fim);

        StringBuilder json = new StringBuilder();
        json.append("{")
            .append("\"pagina\":").append(pagina).append(",")
            .append("\"totalPaginas\":").append(totalPaginas).append(",")
            .append("\"totalFuncionarios\":").append(total).append(",")
            .append("\"funcionarios\":[");

        for (int i = 0; i < paginados.size(); i++) {
            Funcionario f = paginados.get(i);
            json.append("{")
                .append("\"nome\":\"").append(f.getNome()).append("\",")
                .append("\"departamento\":\"").append(f.getDepartamento()).append("\"")
                .append("}");
            if (i < paginados.size() - 1) json.append(",");
        }
        json.append("]}");

        byte[] body = json.toString().getBytes("UTF-8");
        String headers = "HTTP/1.1 200 OK\r\n"
            + "Content-Type: application/json; charset=UTF-8\r\n"
            + "Access-Control-Allow-Origin: *\r\n"
            + "Content-Length: " + body.length + "\r\n\r\n";

        out.write(headers.getBytes());
        out.write(body);
        out.flush();
    }

    static void servirHtml(OutputStream out) throws IOException {
        String html = """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
                <meta charset="UTF-8">
                <title>Funcionários</title>
                <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    body { font-family: 'Segoe UI', sans-serif; background: #f0f2f5; padding: 40px 20px; }
                    h1 { text-align: center; color: #1a1a2e; margin-bottom: 30px; font-size: 28px; }
                    .container { max-width: 800px; margin: 0 auto; }
                    .info { text-align: center; color: #666; margin-bottom: 20px; font-size: 14px; }
                    table { width: 100%; border-collapse: collapse; background: white; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.08); }
                    thead { background: #1a1a2e; color: white; }
                    thead th { padding: 16px 20px; text-align: left; font-size: 14px; text-transform: uppercase; letter-spacing: 1px; }
                    tbody tr { border-bottom: 1px solid #f0f0f0; transition: background 0.2s; }
                    tbody tr:last-child { border-bottom: none; }
                    tbody tr:hover { background: #f8f9ff; }
                    tbody td { padding: 14px 20px; color: #333; font-size: 15px; }
                    .badge { display: inline-block; padding: 4px 12px; border-radius: 20px; font-size: 12px; font-weight: 600; }
                    .badge-tecnologia { background: #e0f0ff; color: #0066cc; }
                    .badge-financeiro { background: #e6f9ec; color: #1a8c3a; }
                    .badge-marketing  { background: #fff3e0; color: #cc6600; }
                    .badge-rh         { background: #f3e5f5; color: #7b1fa2; }
                    .loading { text-align: center; padding: 40px; color: #888; }
                    .erro   { text-align: center; padding: 40px; color: #cc0000; }
                    .paginacao { display: flex; justify-content: center; align-items: center; gap: 12px; margin-top: 24px; }
                    .paginacao button { padding: 8px 20px; border: none; border-radius: 8px; background: #1a1a2e; color: white; font-size: 14px; cursor: pointer; transition: opacity 0.2s; }
                    .paginacao button:disabled { opacity: 0.3; cursor: not-allowed; }
                    .paginacao button:hover:not(:disabled) { opacity: 0.8; }
                    .paginacao span { color: #555; font-size: 14px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>👥 Lista de Funcionários</h1>
                    <p class="info" id="info"></p>
                    <table id="tabela" style="display:none">
                        <thead>
                            <tr><th>#</th><th>Nome</th><th>Departamento</th></tr>
                        </thead>
                        <tbody id="corpo"></tbody>
                    </table>
                    <div class="paginacao" id="paginacao" style="display:none">
                        <button id="btnAnterior" onclick="mudarPagina(-1)">← Anterior</button>
                        <span id="infoPagina"></span>
                        <button id="btnProximo" onclick="mudarPagina(1)">Próximo →</button>
                    </div>
                    <div class="loading" id="loading">Carregando funcionários...</div>
                    <div class="erro" id="erro" style="display:none"></div>
                </div>
                <script>
                    let paginaAtual  = 0;
                    let totalPaginas = 0;

                    const badges = {
                        "tecnologia":       "badge-tecnologia",
                        "financeiro":       "badge-financeiro",
                        "marketing":        "badge-marketing",
                        "recursos humanos": "badge-rh"
                    };

                    async function carregar(pagina) {
                        document.getElementById("corpo").innerHTML   = "";
                        document.getElementById("loading").style.display = "block";
                        document.getElementById("tabela").style.display  = "none";
                        document.getElementById("paginacao").style.display = "none";

                        try {
                            const res  = await fetch("/api/funcionarios?pagina=" + pagina);
                            const data = await res.json();
                            totalPaginas = data.totalPaginas;

                            const corpo = document.getElementById("corpo");
                            const offset = pagina * 5;

                            data.funcionarios.forEach((f, i) => {
                                const cls = badges[f.departamento.toLowerCase()] || "badge-tecnologia";
                                const tr  = document.createElement("tr");
                                tr.innerHTML = `
                                    <td>${offset + i + 1}</td>
                                    <td>${f.nome}</td>
                                    <td><span class="badge ${cls}">${f.departamento}</span></td>
                                `;
                                corpo.appendChild(tr);
                            });

                            document.getElementById("info").textContent =
                                data.totalFuncionarios + " funcionários no total";
                            document.getElementById("infoPagina").textContent =
                                "Página " + (pagina + 1) + " de " + totalPaginas;

                            document.getElementById("btnAnterior").disabled = pagina === 0;
                            document.getElementById("btnProximo").disabled  = pagina >= totalPaginas - 1;

                            document.getElementById("loading").style.display   = "none";
                            document.getElementById("tabela").style.display    = "table";
                            document.getElementById("paginacao").style.display = "flex";

                        } catch (e) {
                            document.getElementById("loading").style.display = "none";
                            document.getElementById("erro").style.display    = "block";
                            document.getElementById("erro").textContent      = "⚠️ Erro ao carregar dados.";
                        }
                    }

                    function mudarPagina(direcao) {
                        paginaAtual += direcao;
                        carregar(paginaAtual);
                    }

                    carregar(0);
                </script>
            </body>
            </html>
        """;

        byte[] body = html.getBytes("UTF-8");
        String headers = "HTTP/1.1 200 OK\r\n"
            + "Content-Type: text/html; charset=UTF-8\r\n"
            + "Content-Length: " + body.length + "\r\n\r\n";

        out.write(headers.getBytes());
        out.write(body);
        out.flush();
    }
}