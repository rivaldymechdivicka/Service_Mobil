package com.bengkel.booking.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.bengkel.booking.models.BookingOrder;
import com.bengkel.booking.models.Customer;
import com.bengkel.booking.models.ItemService;
import com.bengkel.booking.repositories.CustomerRepository;
import com.bengkel.booking.repositories.ItemServiceRepository;

public class MenuService {
	private static List<Customer> listAllCustomers = CustomerRepository.getAllCustomer();
	private static List<ItemService> listAllItemService = ItemServiceRepository.getAllItemService();
	private static List<BookingOrder> bookingOrders = new ArrayList<>();
	private static Scanner input = new Scanner(System.in);
	private static BengkelService bengkelService = new BengkelService();
	private static Customer currentCustomer = new Customer();

	public static void run() {
		boolean isLooping = true;
		do {
			login();
			mainMenu();
		} while (isLooping);

	}

	public static void login() {
		int maxLoginAttempts = 3;
		PrintService.printTitleFeature("Booking Bengkel Bandung");
	
		while (true) {
			// Tampilkan menu login
			System.out.println("1. Silahkan Login");
			System.out.println("0. Exit");
	
			// Validasi input untuk memilih menu
			String menuLogin = Validation.validasiInput("Pilih Menu : ", "Input is not valid ", "[01]");
	
			if (menuLogin.equals("0")) {
				// Exit dari aplikasi jika user memilih 0
				System.exit(0);
			} else if (!menuLogin.equals("1")) {
				// Tampilkan pesan kesalahan jika input tidak valid dan lanjutkan loop
				System.err.println("Invalid Input!");
				continue;
			}
	
			// Meminta input Customer ID dan Password
			PrintService.printTitleFeature("Login");
			String custID = Validation.validasiInput("Masukkan Customer ID Anda : ", "Masuka ID Anda Yang Benar", "[^\\u0000]*");
			String password = Validation.validasiInput("Masukkan Passworda Anda : ", "Masuka Password Anda Yang Benar", "[^\\u0000]*");
			System.out.println("+=========================================================+");
	
			// Coba melakukan login
			if (bengkelService.login(listAllCustomers, custID, password)) {
				// Jika login berhasil, set currentCustomer dan keluar dari loop
				currentCustomer = BengkelService.findCustomerByID(custID, listAllCustomers);
				break;
			}
	
			// Kurangi jumlah percobaan
			maxLoginAttempts--;
	
			if (maxLoginAttempts == 0) {
				// Jika sudah melewati batas percobaan, tampilkan pesan dan keluar dari program
				System.out.println("+=========================================+");
				System.out.println("Jika Anda Memasukan ID dan Password Secara Salah Dan Berkala Program Ini Akan DIHENTIKAN!!");
				System.out.println("+=========================================+");
				System.exit(0);
			} else {
				// Tampilkan sisa percobaan
				System.out.println("+====================+");
				System.out.println(maxLoginAttempts + " Kali Percobaan Program Ini Akan DIHENTIKAN!!!");
				System.out.println("+====================+");
			}
		}
	}
	
	public static void mainMenu() {
		String[] listMenu = { "Informasi Customer", "Booking Bengkel", "Top Up Bengkel Coin", "Informasi Booking",
				"Logout" };
		int menuChoice = 0;
		boolean isLooping = true;
	
		do {
			// Tampilkan menu utama
			PrintService.printMenu(listMenu, "Booking Bengkel Menu");
	
			// Validasi input untuk memilih menu
			menuChoice = Validation.validasiNumberWithRange("Masukan Pilihan Menu : ", "Masukan Angka!",
					"^[0-9]+$", listMenu.length - 1, 0);
	
			switch (menuChoice) {
				case 1:
					// Tampilkan informasi pelanggan
					bengkelService.getDetailCustomer(currentCustomer);
					break;
				case 2:
					// Buat reservasi bengkel
					bengkelService.createReservation(currentCustomer, bookingOrders);
					break;
				case 3:
					// Edit saldo Bengkel Coin
					bengkelService.editSaldoCoin(currentCustomer);
					break;
				case 4:
					// Tampilkan riwayat pesanan booking
					bengkelService.getBookingOrdersHistory(currentCustomer, bookingOrders);
					break;
				default:
					// Keluar dari loop jika memilih Logout
					System.out.println("Logout");
					isLooping = false;
					break;
			}
		} while (isLooping);
	}
	
	// Silahkan tambahkan kodingan untuk keperluan Menu Aplikasi
}
