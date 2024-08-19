import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
class  HelpDeskSystem {
    private Map<String, User> users = new HashMap<>();
    private Map<String, Ticket> tickets = new HashMap<>();
    private int nextUserId = 1;
    private int nextTicketId = 1;
    private String currentUserId = null;

    class User {
        String username;
        String password;
        String role; // user, staff, or admin

        User(String username, String password, String role) {
            this.username = username;
            this.password = password;
            this.role = role;
        }
    }

    class Ticket {
        String title;
        String description;
        String status;
        String userId;
        String staffId;

        Ticket(String title, String description, String userId) {
            this.title = title;
            this.description = description;
            this.status = "Open";
            this.userId = userId;
            this.staffId = null;
        }
    }

    void registerUser(String username, String password, String role) {
        String userId = String.valueOf(nextUserId++);
        users.put(userId, new User(username, password, role));
        System.out.println("User registered with ID: " + userId);
    }

    void login(String username, String password) {
        for (Map.Entry<String, User> entry : users.entrySet()) {
            User user = entry.getValue();
            if (user.username.equals(username) && user.password.equals(password)) {
                currentUserId = entry.getKey();
                System.out.println("Logged in as " + username + " (" + user.role + ")");
                return;
            }
        }
        System.out.println("Invalid username or password");
    }

    void logout() {
        currentUserId = null;
        System.out.println("Logged out");
    }

    void createTicket(String title, String description) {
        if (currentUserId == null) {
            System.out.println("Please log in to create a ticket");
            return;
        }

        User user = users.get(currentUserId);
        if (!"user".equals(user.role)) {
            System.out.println("Only users can create tickets");
            return;
        }

        String ticketId = String.valueOf(nextTicketId++);
        tickets.put(ticketId, new Ticket(title, description, currentUserId));
        System.out.println("Ticket created with ID: " + ticketId);
    }

    void viewTickets() {
        if (currentUserId == null) {
            System.out.println("Please log in to view tickets");
            return;
        }

        User user = users.get(currentUserId);
        if ("user".equals(user.role)) {
            for (Map.Entry<String, Ticket> entry : tickets.entrySet()) {
                Ticket ticket = entry.getValue();
                if (ticket.userId.equals(currentUserId)) {
                    System.out.println("Ticket ID: " + entry.getKey());
                    System.out.println("Title: " + ticket.title);
                    System.out.println("Description: " + ticket.description);
                    System.out.println("Status: " + ticket.status);
                    System.out.println();
                }
            }
        } else if ("staff".equals(user.role)) {
            for (Map.Entry<String, Ticket> entry : tickets.entrySet()) {
                Ticket ticket = entry.getValue();
                if (ticket.staffId != null && ticket.staffId.equals(currentUserId)) {
                    System.out.println("Ticket ID: " + entry.getKey());
                    System.out.println("Title: " + ticket.title);
                    System.out.println("Description: " + ticket.description);
                    System.out.println("Status: " + ticket.status);
                    System.out.println();
                }
            }
        }
    }

    void updateTicketStatus(String ticketId, String status) {
        if (currentUserId == null) {
            System.out.println("Please log in to update a ticket");
            return;
        }

        User user = users.get(currentUserId);
        Ticket ticket = tickets.get(ticketId);

        if (ticket == null) {
            System.out.println("Ticket not found");
            return;
        }

        if ("user".equals(user.role) && ticket.userId.equals(currentUserId)) {
            ticket.status = status;
            System.out.println("Ticket ID " + ticketId + " status updated to " + status);
        } else if ("staff".equals(user.role) && ticket.staffId != null && ticket.staffId.equals(currentUserId)) {
            ticket.status = status;
            System.out.println("Ticket ID " + ticketId + " status updated to " + status);
        } else {
            System.out.println("You do not have permission to update this ticket");
        }
    }

    void adminPanel() {
        if (currentUserId == null) {
            System.out.println("Please log in as an admin to access the admin panel");
            return;
        }

        User user = users.get(currentUserId);
        if (!"admin".equals(user.role)) {
            System.out.println("Only admins can access the admin panel");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Admin Panel");
            System.out.println("1. Register User");
            System.out.println("2. Register Support Staff");
            System.out.println("3. Assign Ticket to Support Staff");
            System.out.println("4. View All Tickets");
            System.out.println("5. Back to Main Menu");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter username: ");
                    String username = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();
                    registerUser(username, password, "user");
                    break;
                case 2:
                    System.out.print("Enter username: ");
                    username = scanner.nextLine();
                    System.out.print("Enter password: ");
                    password = scanner.nextLine();
                    registerUser(username, password, "staff");
                    break;
                case 3:
                    System.out.print("Enter ticket ID to assign: ");
                    String ticketId = scanner.nextLine();
                    assignTicketToStaff(ticketId);
                    break;
                case 4:
                    viewTickets();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    void assignTicketToStaff(String ticketId) {
        if (currentUserId == null) {
            System.out.println("Please log in to assign a ticket");
            return;
        }

        User user = users.get(currentUserId);
        if (!"admin".equals(user.role)) {
            System.out.println("Only admins can assign tickets");
            return;
        }

        Ticket ticket = tickets.get(ticketId);
        if (ticket != null) {
            System.out.print("Enter staff ID to assign: ");
            Scanner scanner = new Scanner(System.in);
            String staffId = scanner.nextLine();
            ticket.staffId = staffId;
            System.out.println("Ticket ID " + ticketId + " assigned to staff ID " + staffId);
        } else {
            System.out.println("Ticket not found");
        }
    }

    public static void main(String[] args) {
        HelpDeskSystem system = new HelpDeskSystem();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Help Desk System");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Logout");
            System.out.println("4. Create Ticket");
            System.out.println("5. View Tickets");
            System.out.println("6. Update Ticket Status");
            System.out.println("7. Admin Panel");
            System.out.println("8. Exit");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter username: ");
                    String username = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();
                    System.out.print("Enter role (user/staff/admin): ");
                    String role = scanner.nextLine();
                    system.registerUser(username, password, role);
                    break;
                case 2:
                    System.out.print("Enter username: ");
                    username = scanner.nextLine();
                    System.out.print("Enter password: ");
                    password = scanner.nextLine();
                    system.login(username, password);
                    break;
                case 3:
                    system.logout();
                    break;
                case 4:
                    System.out.print("Enter ticket title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter ticket description: ");
                    String description = scanner.nextLine();
                    system.createTicket(title, description);
                    break;
                case 5:
                    system.viewTickets();
                    break;
                case 6:
                    System.out.print("Enter ticket ID: ");
                    String ticketId = scanner.nextLine();
                    System.out.print("Enter new status: ");
                    String status = scanner.nextLine();
                    system.updateTicketStatus(ticketId, status);
                    break;
                case 7:
                    system.adminPanel();
                    break;
                case 8:
                    System.out.println("Exiting...");
                    return;
            }
        }
    }
}

