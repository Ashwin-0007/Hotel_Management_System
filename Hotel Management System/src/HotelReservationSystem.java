import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class HotelReservationSystem {

    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "Ashwin#2626#";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            while (true) {
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner sc = new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservation");
                System.out.println("3. Get Room Number ");
                System.out.println("4. Update Resrvation");
                System.out.println("5. Delete Reservation");
                System.out.println("0. Exit");
                System.out.println("Choose an option");
                int choice = sc.nextInt();
                switch (choice) {

                    case 1:
                        reserveRoom(connection, sc);
                        break;
                    case 2:
                        viewReservation(connection);
                        break;
                    case 3:
                        getRoomNumber(connection, sc);
                        break;
                    case 4:
                        updateReservation(connection, sc);
                        break;
                    case 5:
                        deleteReservation(connection, sc);
                        break;
                    case 0:
                        exit();
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void reserveRoom(Connection connection, Scanner sc) {
        try {
            System.out.println("Enter Guest name: ");
            String guestName = sc.next();
            sc.nextLine();
            System.out.println("Enter room number: ");
            int roomNumber = sc.nextInt();
            System.out.println("Enter contact number: ");
            int contactNumber = sc.nextInt();

            String sql = "INSERT INTO reservation(guest_name, room_number, contact_number)" +
                    "VALUES ('" + guestName + "'," + roomNumber + ", '" + contactNumber + "')";

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation Successful !");
                } else {
                    System.out.println("Reservation Failed");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewReservation(Connection connection) throws SQLException {
        String sql = "SELECT reservation_id, guest_name, room_number, contact_number,reservation_date FROM reservation";

        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {

            System.out.println("Current Reservation: ");
            System.out.println(
                    "+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println(
                    "| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
            System.out.println(
                    "+----------------+-----------------+---------------+----------------------+-------------------------+");
            while (resultSet.next()) {
                int reservationId = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();

                // format to display the reservation date table like format
                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s    |\n ",
                        reservationId, guestName, roomNumber, contactNumber, reservationDate);
            }

            System.out.println(
                    "*-----------------*--------------*----------------*----------------------*-----------------*---------------*");

        }

    }

    private static void getRoomNumber(Connection connection, Scanner sc) {
        System.out.println("Enter reservation ID: ");
        int reservationId = sc.nextInt();
        System.out.println("Enter Guest Name: ");
        String guestName = sc.next();

        String sql = " SELECT room_number FROM reservation " +
                "WHERE reservation_id = " + reservationId +
                " AND guest_name = '" + guestName + "'";

        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {

            if (resultSet.next()) {
                int roomNumber = resultSet.getInt("room_number");
                System.out.println("Room number for Reservation ID " + reservationId +
                        "and Guest " + guestName + " is: " + roomNumber);
            } else {
                System.out.println("Reservation not found for the given ID and guest name. ");
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }

    }

    private static void updateReservation(Connection connection, Scanner sc) {
        try {
            System.out.println("Enter reservation ID to Update: ");
            int reservationId = sc.nextInt();
            sc.nextLine();

            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not Found for the given ID: ");
                return;
            }

            System.out.println("Enter new guest name: ");
            String newGuestName = sc.nextLine();
            System.out.println("Enter room number: ");
            int newRoomNumber = sc.nextInt();
            System.out.println("Enter new contact Number: ");
            String newContactNumber = sc.next();

            String sql = "UPDATE resrevation SET guest_name = '" + newGuestName + "'," +
                    "room_number = " + newRoomNumber + ", " +
                    "contact_number = " + newContactNumber + ", " +
                    "WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation Updated Succesfully!");
                } else {
                    System.out.println("Reservation Failed!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteReservation(Connection connection, Scanner sc) {
        try {
            System.out.println("Enter reservation ID to delete: ");
            int reservationId = sc.nextInt();

            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not Found for the given ID: ");
                return;
            }
            String sql = "DELETE FROM reservation WHERE resrevation_id =" + reservationId;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation Deleted Succesfully!");
                } else {
                    System.out.println("Deletion Failed!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection connection, int reservationId) {
        try {
            String sql = "SELECT reservation_id FROM reservation WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql)) {

                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void exit() throws InterruptedException {
        System.out.println("Existing System");
        int i = 5;
        while (i != 0) {
            System.out.println(".");
            Thread.sleep(450);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!! ");
    }
}
