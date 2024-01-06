package com.bengkel.booking.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.bengkel.booking.models.BookingOrder;
import com.bengkel.booking.models.Customer;
import com.bengkel.booking.models.ItemService;
import com.bengkel.booking.models.MemberCustomer;
import com.bengkel.booking.models.Vehicle;
import com.bengkel.booking.repositories.ItemServiceRepository;

public class BengkelService {

	// Silahkan tambahkan fitur-fitur utama aplikasi disini
	private static List<ItemService> serviceList = ItemServiceRepository.getAllItemService();
	private static PrintService printService = new PrintService();

	// Login
	public boolean login(List<Customer> listAllCustomers, String custID, String password) {
    // Periksa apakah parameter atau objek yang digunakan tidak null
    if (listAllCustomers == null || custID == null || password == null) {
        System.out.println("Parameter atau objek null tidak diizinkan.");
        return false;
    }

    // Temukan pelanggan berdasarkan ID
    Customer foundCustomer = findCustomerByID(custID, listAllCustomers);

    // Periksa apakah ID pelanggan ditemukan dan cocok dengan kata sandi
    if (foundCustomer != null && Objects.equals(password, foundCustomer.getPassword())) {
        // Login berhasil
        return true;
    } else {
        System.out.println("Login Gagal: ID Pelanggan atau Password Salah!");
        return false;
    }
}

// Metode bantu untuk menemukan pelanggan berdasarkan ID
public static Customer findCustomerByID(String custID, List<Customer> listAllCustomers) {
    return listAllCustomers.stream()
            .filter(customer -> Objects.equals(customer.getCustomerId(), custID))
            .findFirst()
            .orElseGet(() -> null);
}

public void getDetailCustomer(Customer customer) {
    // Menampilkan profil pelanggan
    PrintService.printTitleFeature("Customer Profile");
    System.out.printf("%-15s : %-15s\n", "Customer ID", customer.getCustomerId());
    System.out.printf("%-15s : %-15s\n", "Nama", customer.getName());
    System.out.printf("%-15s : %-15s\n", "Customer Status", (customer instanceof MemberCustomer) ? "Member" : "Non Member");
    System.out.printf("%-15s : %-15s\n", "Alamat", customer.getAddress());

    if (customer instanceof MemberCustomer) {
        // Jika pelanggan adalah member, tampilkan saldo koin
        System.out.printf("%-15s : %-15s\n", "Saldo Koin", ((MemberCustomer) customer).getSaldoCoin());
    }

    System.out.println("List Kendaraan  :");
    PrintService.printVechicle(customer.getVehicles());
}

public void createReservation(Customer currentCustomer, List<BookingOrder> bookingOrders) {
    String paymentMethod = "";
    List<ItemService> selectedItemServices = new ArrayList<>();
    PrintService.printTitleFeature("Booking Bengkel");
    do {
        // Meminta input Vehicle ID untuk membuat reservasi
        String vehicleID = Validation.validasiInput("Masukkan Vehicle ID (Input 0 untuk membatalkan) : ",
                "Input Tidak Valid", "[^\\u0000]*");
        Vehicle isVehicle = currentCustomer.getVehicles().stream()
                .filter(vehicle -> vehicle.getVehiclesId().equals(vehicleID))
                .findFirst()
                .orElse(null);

        if (vehicleID.equals("0"))
            break;

        if (isVehicle == null) {
            // Jika kendaraan tidak ditemukan, tampilkan pesan dan minta input ulang
            System.out.println("Kendaraan Tidak Ditemukan");
            continue;
        }

        // Memilih layanan yang ingin direservasi untuk kendaraan tertentu
        selectedItemServices = selectedServices(isVehicle);

        if (currentCustomer instanceof MemberCustomer)
            // Jika pelanggan adalah member, minta pilihan metode pembayaran
            paymentMethod = Validation.validasiInput("Silahkan Pilih Metode Pembayaran (Saldo Coin atau Cash) : ",
                    "Input Tidak Valid", "(?i)\\b(saldo coin|cash)\\b");
        else
            // Jika bukan member, metode pembayaran otomatis Cash
            System.out.println("Metode Pembayaran menggunakan Cash");

        // Jika tidak ada pilihan, secara otomatis menggunakan Cash
        paymentMethod = paymentMethod.equals("") ? "Cash" : paymentMethod;
        System.out.println(paymentMethod);

        // Pembuatan ID reservasi dan perhitungan total harga reservasi
        String bookingID = "Book-" + UUID.randomUUID().toString().substring(0, 3);
        double bookingPrice = calculateReservationPrice(selectedItemServices);

        // Pembuatan objek BookingOrder untuk menyimpan informasi reservasi
        BookingOrder bookingOrder = new BookingOrder();
        bookingOrder.setBookingId(bookingID);
        bookingOrder.setCustomer(currentCustomer);
        bookingOrder.setServices(selectedItemServices);
        bookingOrder.setPaymentMethod(paymentMethod);
        bookingOrder.setTotalServicePrice(bookingPrice);
        bookingOrder.calculatePayment();

        // Menambahkan reservasi ke daftar reservasi
        bookingOrders.add(bookingOrder);

        // Menampilkan informasi hasil reservasi
        System.out.println("Booking berhasil!!!");
        System.out.println("Total Harga Service : Rp. " + bookingOrder.getTotalServicePrice());
        System.out.println("Total Pembayaran : Rp. " + bookingOrder.getTotalPayment());

        if (currentCustomer instanceof MemberCustomer) {
            // Jika pelanggan adalah member, mengurangi saldo koin sesuai total harga reservasi
            MemberCustomer memberCustomer = (MemberCustomer) currentCustomer;
            memberCustomer.setSaldoCoin(memberCustomer.getSaldoCoin() - bookingPrice);
        }

        break;
    } while (true);
}

private List<ItemService> selectedServices(Vehicle isVehicle) {
    List<ItemService> selectedItemServices = new ArrayList<>();

    // Menampilkan layanan yang tersedia untuk kendaraan tertentu
    System.out.println("List service yang tersedia : ");
    printService.printService(serviceList, isVehicle);
    do {
        // Meminta input Service ID dari pengguna
        String serviceID = Validation.validasiInput("Masukkan Service ID : ", "Input Tidak Valid", "[^\\u0000]*");
        ItemService itemService = findItemServiceByID(serviceID);

        if (itemService == null) {
            // Jika Service ID tidak ditemukan, tampilkan pesan dan minta input ulang
            System.out.println("Service tidak ditemukan.");
            continue;
        }

        if (!selectedItemServices.contains(itemService))
            // Menambahkan layanan ke dalam daftar jika belum ada
            selectedItemServices.add(itemService);
        else
            System.out.println("Service sudah di pilih");

        // Meminta konfirmasi untuk menambahkan service lainnya
        String selectAgain = Validation.validasiInput("Apakah anda ingin menambahkan Service Lainnya? (Y/T) : ",
                "Input Tidak Valid", "[YyTt]");

        if (selectAgain.equalsIgnoreCase("t"))
            break;

    } while (true);

    return selectedItemServices;
}

private ItemService findItemServiceByID(String service) {
    // Mencari layanan berdasarkan Service ID
    return serviceList.stream()
            .filter(itemService -> itemService.getServiceId().equals(service))
            .findFirst()
            .orElse(null);
}

private static double calculateReservationPrice(List<ItemService> selectedItemServices) {
    // Menghitung total harga reservasi berdasarkan layanan yang dipilih
    double finalPrice = selectedItemServices.stream()
            .mapToDouble(ItemService::getPrice)
            .sum();
    return finalPrice;
}


public void editSaldoCoin(Customer currentCustomer) {
    if (!(currentCustomer instanceof MemberCustomer)) {
        // Menampilkan pesan bahwa top up saldo coin hanya untuk member
        PrintService.printNotMember();
    } else {
        // Jika customer adalah member, maka izinkan top up saldo coin
        PrintService.printTitleFeature("Top Up Saldo Coin");

        // Meminta input nominal top up dari pengguna
        int topUpSaldoCoin = Validation.validasiNumberWithRange(
                "Masukkan nominal dana yang ingin di Top Up ke Saldo Coin : Rp. ", "Input Tidak Valid",
                "[0-9]+(,[0-9][0-9]*)*", 9999999, 1);

        // Mengakses objek MemberCustomer untuk menambahkan saldo coin
        MemberCustomer memberCustomer = (MemberCustomer) currentCustomer;
        memberCustomer.setSaldoCoin(memberCustomer.getSaldoCoin() + topUpSaldoCoin);
        System.out.println("+---------------------------------+");
    }
}

public void getBookingOrdersHistory(Customer currentCustomer, List<BookingOrder> bookingOrders) {
    // Memfilter booking orders untuk customer yang sedang login
    List<BookingOrder> currentBookingOrders = bookingOrders.stream()
            .filter(bookingOrder -> bookingOrder.getCustomer().getCustomerId()
                    .equals(currentCustomer.getCustomerId()))
            .toList();

    // Menampilkan riwayat booking orders untuk customer yang sedang login
    PrintService.printTitleFeature("Booking Order Menu");
    PrintService.printBookingOrder(currentBookingOrders);
}


	

}
