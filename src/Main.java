import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.ResultSet;


public class Main {

    public static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        int i = 5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(1000);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!!!");
    }
    private static void reserveRoom(Connection connection,Scanner scanner){
        try {
            System.out.println("Enter a Guest name:");
            String guestName=scanner.next();
            scanner.nextLine();
            System.out.println("Enter a Room Number:");
            int roomNumber=scanner.nextInt();
            System.out.println("Enter a Contact Number:");
            String contactNumber= scanner.next();

            String sql = "INSERT INTO reservation (guest_name, room_number, contact_number) " +
                    "VALUES ('" + guestName + "', " + roomNumber + ", '" + contactNumber + "')";

            try(Statement statement= connection.createStatement()){
                int affectedRows= statement.executeUpdate(sql);
                if(affectedRows>0){
                    System.out.println("Reservation Successful");
                }else {
                    System.out.println("Reservation Failed");
                }
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private static void viewReservations(Connection connection)throws SQLException{
        String sql="Select reservation_id,guest_name,room_number,contact_number,reservation_date from reservation";
        try(Statement statement= connection.createStatement()){
            ResultSet resultSet=statement.executeQuery(sql);

            System.out.println("Current Reservations:");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

            while (resultSet.next()){
                int reservationId=resultSet.getInt("reservation_id");
                String guestName=resultSet.getString("guest_name");
                int roomNumber=resultSet.getInt("room_number");
                String contactNumber=resultSet.getString("contact_number");
                String reservationDate=resultSet.getTimestamp("reservation_date").toString();

                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        reservationId, guestName, roomNumber, contactNumber, reservationDate);
            }
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
        }
    }

    private static void getRoomNumber(Connection connection,Scanner scanner){
        try {
            System.out.println("Enter a reservation id:");
            int reservationId=scanner.nextInt();
            System.out.println("Enter a Guest name :");
            String guestName=scanner.next();

            String sql = "SELECT room_number FROM reservation " +
                    "WHERE reservation_id = " + reservationId +
                    " AND guest_name = '" + guestName + "'";

            try(Statement statement= connection.createStatement();
                ResultSet resultSet= statement.executeQuery(sql)){
                if(resultSet.next()){
                    int roomNumber=resultSet.getInt("room_number");
                    System.out.println("Room number for Reservation ID " + reservationId +
                            " and Guest " + guestName + " is: " + roomNumber);
                }else{
                    System.out.println("Reservation not found!!!");
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void updateReservation(Connection connection,Scanner scanner){
        try {
            System.out.println("Enter a Reservation id to update:");
            int reservationId = scanner.nextInt();
            scanner.nextLine();
            if(!reservationExits(connection,reservationId)){
                System.out.println("Reservation does not exit!!!");
                return;
            }

            System.out.println("Enter new Guest Name:");
            String newGuestName= scanner.nextLine();
            System.out.println("Enter new room Number:");
            int newRoomNumber= scanner.nextInt();
            System.out.println("Enter new contact number:");
            String newContactNumber= scanner.next();

            String sql = "UPDATE reservation SET guest_name = '" + newGuestName + "', " +
                    "room_number = " + newRoomNumber + ", " +
                    "contact_number = '" + newContactNumber + "' " +
                    "WHERE reservation_id = " + reservationId;

            try(Statement statement= connection.createStatement()){
                int rowAffected=statement.executeUpdate(sql);
                if(rowAffected>0){
                    System.out.println("Reservation Updated Succesfully!!!");
                }else{
                    System.out.println("Reservation Updation failed!!!");
                }
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private static boolean reservationExits(Connection connection,int reservationId){

        String sql="Select reservation_id from reservation where reservation_id="+reservationId;
        try(Statement statement= connection.createStatement();
        ResultSet resultSet= statement.executeQuery(sql)){
            return resultSet.next();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private static void deleteReservation(Connection connection,Scanner scanner){
        try{
            System.out.println("Enter a Reservation Id to delete:");
            int reservationId= scanner.nextInt();
            if(!reservationExits(connection,reservationId)){
                System.out.println("Reservation not Exit!!!");
            }

            String sql="DELETE from reservation where reservation_id="+reservationId;
            try(Statement statement= connection.createStatement()){
                int rowsAffected= statement.executeUpdate(sql);

                if(rowsAffected>0){
                    System.out.println("Deletion of record successfully");
                }else{
                    System.out.println("Deletion failed!!!");
                }

            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }


    private static final String url="jdbc:mysql://localHost:3306/hotel_db";

    private static final String username="root";

    private static final String password="Shreyash@96";

    public static void main(String[] args) throws ClassNotFoundException,SQLException {

        try{
           Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try{
            Connection connection = DriverManager.getConnection(url,username,password);
            while(true){
                System.out.println();
                System.out.println("=====Hotel Reservation System=====");
                Scanner scanner=new Scanner(System.in);
                System.out.println("1:Reserve a room");
                System.out.println("2:View Reservations");
                System.out.println("3:Get Room Number");
                System.out.println("4:Update Reservations");
                System.out.println("5:Delete Reservatons");
                System.out.println("0:Exit");
                int choice= scanner.nextInt();
                switch (choice){
                    case 1:
                        reserveRoom(connection,scanner);
                        break;
                    case 2:
                        viewReservations(connection);
                        break;
                    case 3:
                        getRoomNumber(connection,scanner);
                        break;
                    case 4:
                        updateReservation(connection,scanner);
                        break;
                    case 5:
                        deleteReservation(connection,scanner);
                    case 0:
                        exit();
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice");

                }

            }
        }catch(SQLException | InterruptedException e){
            System.out.println(e.getMessage());
        }

    }
}