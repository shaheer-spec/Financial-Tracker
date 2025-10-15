package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FinancialTracker {

    private static final ArrayList<Transaction> transactions = new ArrayList<>();
    private static final String FILE_NAME = "transactions.csv";

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";

    public static void main(String[] args) {
        loadTransactions(FILE_NAME);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("Welcome to TransactionApp");
            System.out.println("Choose an option:");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "D" -> addDeposit(scanner);
                case "P" -> addPayment(scanner);
                case "L" -> ledgerMenu(scanner);
                case "X" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
        scanner.close();
    }

    public static void loadTransactions(String fileName) {

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_NAME));
            String line;
            while ((line = bufferedReader.readLine()) != null ){
                String[] parts = line.split("\\|");
                LocalDate date = LocalDate.parse(parts[0]);
                LocalTime time = LocalTime.parse(parts[1]);
                String description = parts[2];
                String vendor = parts[3];
                double amount = Double.parseDouble(parts[4]);
                transactions.add(new Transaction(date, time, description, vendor, amount));
            }

            sortTransactions();

            bufferedReader.close();
        } catch (Exception ex) {
            System.err.println("Error");
        }
    }

    private static void addDeposit(Scanner scanner) {
        System.out.print("Date & Time (yyyy-MM-dd HH:mm:ss): ");
        String dateTime = scanner.nextLine();
        System.out.print("Description: ");
        String description = scanner.nextLine();
        System.out.print("Vendor: ");
        String vendor = scanner.nextLine();
        System.out.print("Amount (Positive): ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        try {
            String[] parts = dateTime.split(" ");
            LocalDate date = LocalDate.parse(parts[0]);
            LocalTime time = LocalTime.parse(parts[1]);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(FILE_NAME, true));
            bufferedWriter.write(date + "|" + time + "|" + description + "|" + vendor + "|" + amount);
            bufferedWriter.newLine();
            bufferedWriter.close();
            transactions.add(new Transaction(date, time, description, vendor, amount));

            sortTransactions();

        } catch (Exception ex) {
            System.err.println("Error");
        }
    }

    private static void addPayment(Scanner scanner) {
        System.out.print("Date & Time (yyyy-MM-dd HH:mm:ss): ");
        String dateTime = scanner.nextLine();
        System.out.print("Description: ");
        String description = scanner.nextLine();
        System.out.print("Vendor: ");
        String vendor = scanner.nextLine();
        System.out.print("Amount (Negative): ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        try {
            String[] parts = dateTime.split(" ");
            LocalDate date = LocalDate.parse(parts[0]);
            LocalTime time = LocalTime.parse(parts[1]);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(FILE_NAME, true));
            bufferedWriter.write(date + "|" + time + "|" + description + "|" + vendor + "|" + amount);
            bufferedWriter.newLine();
            bufferedWriter.close();
            transactions.add(new Transaction(date, time, description, vendor, amount));

            sortTransactions();

        } catch (Exception ex) {
            System.err.println("Error");
        }
    }

    public static void sortTransactions (){
        transactions.sort(Comparator.comparing(Transaction::getDate)
                .thenComparing(Transaction::getTime)
                .reversed());
    }

    private static void ledgerMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Ledger");
            System.out.println("Choose an option:");
            System.out.println("A) All");                              // Done
            System.out.println("D) Deposits");                         // Done
            System.out.println("P) Payments");                         // Done
            System.out.println("R) Reports");
            System.out.println("H) Home");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "A" -> displayLedger();   // Done
                case "D" -> displayDeposits(); // Done
                case "P" -> displayPayments(); // Done
                case "R" -> reportsMenu(scanner);
                case "H" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    private static void displayLedger() {
        System.out.println("Date         Time         Description                  Vendor                  Amount");
        System.out.println("----------------------------------------------------------------------------------------------");

        try {
            for (Transaction transaction : transactions) {
                System.out.printf("%-12s %-10s %-30s %-20s %10.2f \n", transaction.getDate(), transaction.getTime(), transaction.getDescription(), transaction.getVendor(), transaction.getAmount());

            }

        } catch (Exception ex) {
            System.err.println("An error occurred");
        }
    }

    private static void displayDeposits() {
        System.out.println("Date         Time         Description                  Vendor                  Amount");
        System.out.println("----------------------------------------------------------------------------------------------");

        for (Transaction transaction : transactions) {
            if (transaction.getAmount() >= 0) {
                System.out.printf("%-12s %-10s %-30s %-20s %10.2f \n", transaction.getDate(), transaction.getTime(), transaction.getDescription(), transaction.getVendor(), transaction.getAmount());
            }
        }
    }

    private static void displayPayments() {
        System.out.println("Date         Time         Description                  Vendor                  Amount");
        System.out.println("----------------------------------------------------------------------------------------------");

        for (Transaction transaction : transactions) {
            if (transaction.getAmount() < 0) {
                System.out.printf("%-12s %-10s %-30s %-20s %10.2f \n", transaction.getDate(), transaction.getTime(), transaction.getDescription(), transaction.getVendor(), transaction.getAmount());
            }
        }
    }

    private static void reportsMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Reports");
            System.out.println("Choose an option:");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("6) Custom Search");
            System.out.println("0) Back");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> {
                    LocalDate start = LocalDate.now().withDayOfMonth(1);
                    LocalDate end = LocalDate.now();
                    filterTransactionsByDate(start, end);
                }
                case "2" -> {
                    LocalDate previousMonth = LocalDate.now().minusMonths(1);
                    LocalDate start = previousMonth.withDayOfMonth(1);
                    LocalDate end = previousMonth.withDayOfMonth(previousMonth.lengthOfMonth());
                    filterTransactionsByDate(start, end);
                }
                case "3" -> {
                    LocalDate start = LocalDate.now().withDayOfYear(1);
                    LocalDate end = LocalDate.now();
                    filterTransactionsByDate(start, end);
                }
                case "4" -> {
                    LocalDate previousYear = LocalDate.now().minusYears(1);
                    LocalDate start = previousYear.withDayOfYear(1);
                    LocalDate end = previousYear.withDayOfYear(previousYear.lengthOfYear());
                    filterTransactionsByDate(start, end);
                }
                case "5" -> {
                    System.out.print("Vendor name: ");
                    String vendor = scanner.nextLine();
                    filterTransactionsByVendor(vendor);
                }
                case "6" -> customSearch(scanner);
                case "0" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    private static void filterTransactionsByDate(LocalDate start, LocalDate end) {

        System.out.println("Date         Time         Description                  Vendor                  Amount");
        System.out.println("----------------------------------------------------------------------------------------------");

        for (Transaction transaction : transactions) {
            LocalDate date = transaction.getDate();
            if (!date.isBefore(start) && !date.isAfter(end)){
                System.out.printf("%-12s %-10s %-30s %-20s %10.2f \n", transaction.getDate(), transaction.getTime(), transaction.getDescription(), transaction.getVendor(), transaction.getAmount());
            }
        }
    }

    private static void filterTransactionsByVendor(String vendor) {

        System.out.println("Date         Time         Description                  Vendor                  Amount");
        System.out.println("----------------------------------------------------------------------------------------------");

        for (Transaction transaction : transactions) {
            String vendor2 = transaction.getVendor();
            if (vendor2.equalsIgnoreCase(vendor)){
                System.out.printf("%-12s %-10s %-30s %-20s %10.2f \n", transaction.getDate(), transaction.getTime(), transaction.getDescription(), transaction.getVendor(), transaction.getAmount());
            }
        }
    }

    private static void customSearch(Scanner scanner) {
        System.out.print("Start date (yyyy-MM-dd, blank = none): ");
        String startDateString = scanner.nextLine().trim();

        System.out.print("end date (yyyy-MM-dd, blank = none): ");
        String endDateString = scanner.nextLine().trim();

        System.out.print("Description (blank = any): ");
        String description = scanner.nextLine();

        System.out.print("Vendor (blank = any): ");
        String vendor = scanner.nextLine();

        System.out.print("Amount (blank = any): ");
        String amount = scanner.nextLine();

        Double finalAmount = null;
        if (!amount.isEmpty()){
            finalAmount = Double.parseDouble(amount);
        }
        LocalDate startDate = null;
        if (!startDateString.isEmpty()) {
            startDate = LocalDate.parse(startDateString);
        }
        LocalDate endDate = null;
        if (!endDateString.isEmpty()){
            endDate = LocalDate.parse(endDateString);
        }

        System.out.println("Date         Time         Description                  Vendor                  Amount");
        System.out.println("----------------------------------------------------------------------------------------------");
        try {
            for (Transaction transaction : transactions) {
                if (startDate != null && transaction.getDate().isBefore(startDate)){ continue; }
                if (endDate != null && transaction.getDate().isAfter(endDate)) { continue; }
                if (!description.isEmpty() && !description.equalsIgnoreCase(transaction.getDescription())) { continue; }
                if (!vendor.isEmpty() && !vendor.equalsIgnoreCase(transaction.getVendor())) { continue; }
                if (finalAmount != null && transaction.getAmount() != finalAmount) { continue; }

                System.out.printf("%-12s %-10s %-30s %-20s %10.2f \n", transaction.getDate(), transaction.getTime(), transaction.getDescription(), transaction.getVendor(), transaction.getAmount());
            }
        } catch (Exception ex){
            System.err.println("Error");
        }

    }
}
