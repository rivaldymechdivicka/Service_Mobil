package com.bengkel.booking.services;

import java.util.Scanner;

public class Validation {
	
	public static String validasiInput(String question, String errorMessage, String regex) {
    Scanner input = new Scanner(System.in);
    String result;
    boolean isLooping = true;
    do {
        System.out.print(question);
        result = input.nextLine();


        if (result.matches(regex)) {
            isLooping = false;
        } else {
            System.out.println(errorMessage);
        }

    } while (isLooping);

    return result;
}

/**
 * Metode untuk validasi input angka dengan rentang tertentu.
 *
 * @param question    Pertanyaan yang ditampilkan untuk pengguna.
 * @param errorMessage Pesan kesalahan yang ditampilkan jika input tidak valid.
 * @param regex       Pola regex untuk validasi input.
 * @param max         Nilai maksimum yang diperbolehkan.
 * @param min         Nilai minimum yang diperbolehkan.
 * @return Angka yang telah divalidasi.
 */
public static int validasiNumberWithRange(String question, String errorMessage, String regex, int max, int min) {
    int result;
    boolean isLooping = true;
    do {
        // Memanggil validasiInput untuk memastikan input adalah angka
        result = Integer.valueOf(validasiInput(question, errorMessage, regex));

        // Validasi rentang angka
        if (result >= min && result <= max) {
            isLooping = false;
        } else {
            System.out.println("Pilihan angka " + min + " s.d " + max);
        }
    } while (isLooping);

    return result;
   }
}