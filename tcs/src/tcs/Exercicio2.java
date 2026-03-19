package tcs;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Optional;

public class Exercicio2 {

	public static void main(String[] args) {
		
		Scanner leitor = new Scanner(System.in);
        List<String> nomes = new ArrayList<>();

        System.out.println("Cadastro de Nomes");
        System.out.print("Quantos nomes deseja inserir? ");
        int quantidade = leitor.nextInt();
        leitor.nextLine();
        
        for (int i = 0; i < quantidade; i++) {
            System.out.print("Digite o nome " + (i + 1) + ": ");
            nomes.add(leitor.nextLine());
        }
        System.out.println("\n--- Busca ---");
        System.out.print("Qual nome você deseja encontrar? ");
        String termoBusca = leitor.nextLine();
        
        Optional<String> encontrado = nomes.stream()
                .filter(n -> n.equalsIgnoreCase(termoBusca))
                .findFirst();
        
        if (encontrado.isPresent()) {
            System.out.println("Sucesso: '" + encontrado.get() + "' está na lista!");
        } else {
            System.out.println("Erro: O nome '" + termoBusca + "' não foi encontrado.");
        }

        leitor.close();
	}

}
