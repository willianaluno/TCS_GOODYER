package tcs;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class FuncionarioServer {

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

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9090);
        System.out.println("Servidor rodando!");
        System.out.println("Página:    http://localhost:9090/");
        System.out.println("API:       http://localhost:8080/api/funcionarios");

        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(() -> handleRequest(socket)).start();
        }
    }

    static void handleRequest(Socket socket) {
        try (
            BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            OutputStream   out = socket.getOutputStream()
        ) {
            String requestLine = in.readLine();
            if (requestLine == null) return;

            if (requestLine.startsWith("GET /api/funcionarios")) {
                servirJson(out);
            } else if (requestLine.startsWith("GET /")) {
                servirHtml(out);
            } else {
                out.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
            }

        } catch (IOException e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    static void servirJson(OutputStream out) throws IOException {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < funcionarios.size(); i++) {
            Funcionario f = funcionarios.get(i);
            json.append("{")
                .append("\"nome\":\"").append(f.getNome()).append("\",")
                .append("\"departamento\":\"").append(f.getDepartamento()).append("\"")
                .append("}");
            if (i < funcionarios.size() - 1) json.append(",");
        }
        json.append("]");

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
                </style>
            </head>
            <body>
                <div class="container">
                    <h1> Lista de Funcionários</h1>
                    <p class="info" id="info"></p>
                    <table id="tabela" style="display:none">
                        <thead>
                            <tr><th>#</th><th>Nome</th><th>Departamento</th></tr>
                        </thead>
                        <tbody id="corpo"></tbody>
                    </table>
                    <div class="loading" id="loading">Carregando funcionários...</div>
                    <div class="erro" id="erro" style="display:none"></div>
                </div>
                <script>
                    const badges = {
                        "tecnologia": "badge-tecnologia",
                        "financeiro": "badge-financeiro",
                        "marketing":  "badge-marketing",
                        "recursos humanos": "badge-rh"
                    };

                    async function carregar() {
                        try {
                            const res  = await fetch("/api/funcionarios");
                            const data = await res.json();
                            const corpo = document.getElementById("corpo");

                            data.forEach((f, i) => {
                                const cls = badges[f.departamento.toLowerCase()] || "badge-tecnologia";
                                const tr  = document.createElement("tr");
                                tr.innerHTML = `
                                    <td>${i + 1}</td>
                                    <td>${f.nome}</td>
                                    <td><span class="badge ${cls}">${f.departamento}</span></td>
                                `;
                                corpo.appendChild(tr);
                            });

                            document.getElementById("info").textContent = data.length + " funcionários encontrados";
                            document.getElementById("loading").style.display = "none";
                            document.getElementById("tabela").style.display  = "table";
                        } catch (e) {
                            document.getElementById("loading").style.display = "none";
                            document.getElementById("erro").style.display    = "block";
                            document.getElementById("erro").textContent      = " Erro ao carregar dados.";
                        }
                    }

                    carregar();
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