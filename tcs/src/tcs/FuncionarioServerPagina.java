package tcs;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FuncionarioServerPagina {

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket();
		serverSocket.setReuseAddress(true);
		serverSocket.bind(new java.net.InetSocketAddress(9090));
		System.out.println("Servidor rodando!");
		System.out.println("Página:  http://localhost:8080/");
		System.out.println("API:     http://localhost:8080/api/funcionarios?pagina=0");

		while (true) {
			Socket socket = serverSocket.accept();
			new Thread(() -> handleRequest(socket)).start();
		}
	}

	static void handleRequest(Socket socket) {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				OutputStream out = socket.getOutputStream()) {
			String requestLine = in.readLine();
			if (requestLine == null)
				return;

			if (requestLine.startsWith("GET /api/funcionarios")) {
				int pagina = 0;
				if (requestLine.contains("pagina=")) {
					String query = requestLine.split("pagina=")[1].split(" ")[0];
					pagina = Integer.parseInt(query);
				}
				FuncionarioHandler.servirJson(out, pagina);
			} else if (requestLine.startsWith("GET /")) {
				FuncionarioHandler.servirHtml(out);
			} else {
				out.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
			}

		} catch (IOException e) {
			System.err.println("Erro: " + e.getMessage());
		}
	}
}
