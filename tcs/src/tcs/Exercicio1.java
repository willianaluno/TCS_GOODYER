package tcs;

import java.util.Scanner;

public class Exercicio1 {

	public static void main(String[] args) {
		
		Scanner leitor = new Scanner(System.in);
		
		System.out.print("Digite uma palavra ou frase para inverter: ");
		
		String entrada = leitor.nextLine();
		
		String invertida = new StringBuilder(entrada).reverse().toString();
		
		System.out.println("Texto original:  " + entrada);
        System.out.println("Texto invertido: " + invertida);
	}
	

}
