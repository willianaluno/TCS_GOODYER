package tcs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Exercicio3 {
	
	public static void main(String[] args) {
        Scanner leitor = new Scanner(System.in);
        List<String> listaDeObjetos = new ArrayList<>();

        System.out.print("Quantos itens você deseja digitar? ");
        int quantidade = leitor.nextInt();
        leitor.nextLine();

        for (int i = 0; i < quantidade; i++) {
            System.out.print("Digite o item " + (i + 1) + ": ");
            String item = leitor.nextLine();
            listaDeObjetos.add(item);
        }
        listaDeObjetos.sort(String.CASE_INSENSITIVE_ORDER);
        System.out.println("\n Lista em Ordem Alfabética ");
        for (String objeto : listaDeObjetos) {
            System.out.println("- " + objeto);
        }

        leitor.close();
	}
}
