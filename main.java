package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    static String url = "jdbc:postgresql://localhost:5432/Health-and-Fitness-Club-Management-Database";
    static String user = "postgres";
    static String pass = "postgres";
    static String className = "org.example.Main";

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        String input = "start";
        int accountType = 0;//put account type
        String username = "";
        String password = "";
        String fName = "";
        String lName = "";

        System.out.println("Welcome to Absolutely Struggling Fitness Management System");
        System.out.println("---------------------------------------------------------------");
        while (!(input.equals("1") || input.equals("2"))) {
            System.out.println("(1) Log in\n(2) Register");
            input = scan.nextLine();
        }

        if (input.equals("1")) {
            //logging in
            while (true) {
                System.out.println("Enter your Username: ");
                username = scan.nextLine();
                System.out.println("Enter your Password: ");
                password = scan.nextLine();
                accountType = checkUserPass(username, password);
                fName = getFirstName(username);
                if (accountType != 0) {//check for username and password
                    break;
                } else
                    System.out.println("Incorrect Username or Password");
            }

        } else if (input.equals("2")) {
            //registration process
            while (true) {
                System.out.println("Enter your Username: ");
                username = scan.nextLine();
                System.out.println("Enter your Password: ");
                password = scan.nextLine();
                System.out.println("Enter your First Name: ");
                fName = scan.nextLine();
                System.out.println("Enter your Last Name: ");
                lName = scan.nextLine();

                accountType = checkUserPass(username);//this overloaded method will only check for usernames (passwords can be the same in registration)
                if (accountType == 0) {//0 means this account username is unique
                    registerAccount(username, password, fName, lName);
                    accountType = 1;
                    break;
                } else
                    System.out.println("Username taken");
            }
        }

        System.out.println("Logged in");
        System.out.println("Welcome " + fName);// replace with first name
        System.out.println("---------------------------------------------------------------");
        int id = getId(username);

        //main loop
        while (true) {
            int iInput = 0;
            float fInput = 0.00f;
            String sInputFirst = "start";
            String sInputLast = "start";
            boolean flag = false;
            switch (accountType) {

                case 1: //Members
                    while (iInput < 1 || iInput > 7) {
                        System.out.println("(1) Add Training Session");
                        System.out.println("(2) Join Group Class");
                        System.out.println("(3) Check Schedule");
                        System.out.println("(4) Change Goal Weight");
                        System.out.println("(5) Change Desired Time");
                        System.out.println("(6) Update Current Weight");
                        System.out.println("(7) Change Name");

                        iInput = scan.nextInt();
                        if (iInput >= 1 && iInput <= 7) {
                            ArrayList<Integer> sessionList = new ArrayList<>();
                            ArrayList<Integer> classList = new ArrayList<>();
                            switch (iInput) {
                                case 1://add trainig sessions
                                    iInput = 0;
                                    flag = false;
                                    sessionList = getTrainingSessionArray();
                                    //pick one training to add
                                    while (!sessionList.contains(iInput)) {
                                        printAllTrainingSessions();
                                        iInput = scan.nextInt();
                                        if (sessionList.contains(iInput)) {
                                            //ask to replace training if youre already in one
                                            if (getSession(id) != 0) {
                                                String tempIntput = "yes/no";
                                                while (!(tempIntput.equalsIgnoreCase("y") || tempIntput.equalsIgnoreCase("n"))) {
                                                    System.out.println("Would you like to replace your current Training Session? (y/n)");
                                                    scan.nextLine();//buffer reads the new line form last print
                                                    tempIntput = scan.nextLine();
                                                    if (tempIntput.equalsIgnoreCase("y")) {
                                                        addSession(id, iInput);
                                                        flag = true;
                                                        break;
                                                    }
                                                }
                                                if (flag) break;
                                            } else {//add to member (session_id)
                                                addSession(id, iInput);
                                                flag = true;
                                                break;
                                            }
                                        } else
                                            System.out.println("Invalid Input");
                                        if (flag) break;
                                    }
                                    break;

                                case 2://joion a group class
                                    iInput = 0;
                                    flag = false;
                                    classList = getGroupClassesArray();
                                    //pick a group class
                                    while (!classList.contains(iInput)) {
                                        printAllGroupClasses();
                                        //pick one training to add
                                        iInput = scan.nextInt();
                                        if (classList.contains(iInput)) {
                                            //ask to replace class if already in one
                                            if (getGroupClass(id) != 0) {
                                                String tempIntput = "yes/no";
                                                while (!(tempIntput.equalsIgnoreCase("y") || tempIntput.equalsIgnoreCase("n"))) {
                                                    System.out.println("Would you like to replace your current Group Class? (y/n)");
                                                    scan.nextLine();
                                                    tempIntput = scan.nextLine();
                                                    if (tempIntput.equalsIgnoreCase("y")) {
                                                        addClass(id, iInput);
                                                        flag = false;
                                                        break;
                                                    }
                                                }
                                                if (flag) break;
                                            } else {//add to member (session_id)
                                                addClass(id, iInput);
                                                flag = false;
                                                break;
                                            }
                                        } else
                                            System.out.println("Invalid Input");
                                        if (flag) break;
                                    }
                                    break;

                                case 3://schedule
                                    System.out.println("Training Sessions");
                                    printSession(id);
                                    System.out.println("\nGroup Classes");
                                    printClass(id);
                                    System.out.println();
                                    flag = true;
                                    break;

                                case 4:
                                    fInput = 0.00f;
                                    while (fInput <= 0.0f) {
                                        System.out.println("Enter Goal Weight:");
                                        fInput = scan.nextFloat();
                                        if (fInput > 0f) {
                                            //change weight
                                            updateWeight(id, fInput);
                                            flag = true;
                                            break;
                                        } else
                                            System.out.println("Invalid Input");
                                    }
                                    break;

                                case 5:
                                    //change time
                                    iInput = 0;
                                    while (iInput <= 0) {
                                        System.out.println("Enter Goal Time in days:");
                                        iInput = scan.nextInt();
                                        if (iInput > 0) {
                                            updateTime(id, iInput);
                                            flag = true;
                                            break;
                                        } else
                                            System.out.println("Invalid Input");
                                    }
                                    break;

                                case 6:
                                    fInput = 0.00f;
                                    while (fInput <= 0.0f) {
                                        System.out.println("Enter Current Weight:");
                                        fInput = scan.nextFloat();
                                        if (fInput > 0f) {
                                            //change currWeight
                                            updateCurrentWeight(id, fInput);
                                            flag = true;
                                            break;
                                        } else
                                            System.out.println("Invalid Input");
                                    }
                                    break;

                                case 7:
                                    sInputFirst = "";
                                    sInputLast = "";
                                    while (sInputFirst.equalsIgnoreCase("")) {
                                        System.out.println("Enter First Name:");
                                        scan.nextLine();
                                        sInputFirst = scan.nextLine();
                                        if (!sInputFirst.equalsIgnoreCase(""))
                                            break;
                                        else
                                            System.out.println("Invalid Input");
                                    }
                                    while (sInputLast.equalsIgnoreCase("")) {
                                        System.out.println("Enter Last Name:");
                                        sInputLast = scan.nextLine();
                                        if (!sInputLast.equalsIgnoreCase("")) {
                                            updateName(id, sInputFirst, sInputLast);
                                            flag = true;
                                            break;
                                        } else
                                            System.out.println("Invalid Input");
                                    }
                                    break;
                            }
                        } else
                            System.out.println("Invalid Input");

                    }
                    if (flag) break;

                case 2: //Trainers
                    while (iInput < 1 || iInput > 3) {
                        System.out.println("(1) Schedule");
                        System.out.println("(2) Search Member");
                        System.out.println("(3) Create Training Session");
                        iInput = scan.nextInt();

                        if (iInput >= 1 && iInput <= 3) {
                            switch (iInput) {
                                case 1:
                                    System.out.println("Training Sessions");
                                    printTrainerSessions(id);
                                    System.out.println("\nGroup Classes");
                                    printTrainerClasses(id);
                                    System.out.println();
                                    break;
                                case 2:
                                    printAllMembers();
                                    System.out.println();
                                    break;
                                case 3:
                                    String sInput = "";
                                    Pattern pattern = Pattern.compile("[0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-9][0-9] [0-9][0-9]:[0-5][0-9]");
                                    Matcher matcher = pattern.matcher(sInput);
                                    String date_time = "";
                                    float duration = 0.0f;
                                    String description = "";
                                    flag = false;

                                    while (!(matcher.find())) {
                                        System.out.println("Enter Date and Time (YYYY-MM-DD HH:MM)");
                                        scan.nextLine();
                                        date_time = scan.nextLine();
                                        matcher = pattern.matcher(date_time);
                                        if (matcher.find()) {
                                            while (duration <= 0.0f) {
                                                System.out.println("Enter Duration of Session in hours");
                                                duration = scan.nextFloat();
                                                if (duration > 0) {
                                                    while (description.equals("")) {
                                                        System.out.println("Enter Description");
                                                        scan.nextLine();
                                                        description = scan.nextLine();
                                                        if (!description.equals("")) {
                                                            addTrainingSession(id, date_time, duration, description);
                                                            flag = true;
                                                            break;
                                                        } else
                                                            System.out.println("Invalid Input");
                                                    }
                                                } else
                                                    System.out.println("Invalid Input");
                                                if (flag) break;
                                            }
                                        } else
                                            System.out.println("Invalid Input");
                                        if (flag) break;
                                    }
                                    break;
                            }
                        } else
                            System.out.println("Invalid Input");
                    }
                    break;

                case 3: //Admins
                    while (iInput < 1 || iInput > 7) {
                        System.out.println("(1) Book Room");
                        System.out.println("(2) Check Equipments");
                        System.out.println("(3) Group Class Schedule");
                        System.out.println("(4) Check all Rooms");
                        System.out.println("(5) Create Group Class");
                        System.out.println("(6) Create Equipment");
                        System.out.println("(7) Billing and Payment");
                        iInput = scan.nextInt();

                        if (iInput >= 1 && iInput <= 7) {
                            ArrayList<Integer> roomList = new ArrayList<>();

                            ArrayList<Integer> fullList = getTrainingSessionArray();
                            int size = fullList.size();
                            fullList.addAll(getGroupClassesArray());

                            switch (iInput) {

                                case 1:
                                    iInput = 0;
                                    flag = false;
                                    int iInput2 = 0;
                                    roomList = getRoomArray();

                                    while (!roomList.contains(iInput)) {
                                        printAllRooms();
                                        iInput = scan.nextInt();
                                        if (roomList.contains(iInput)) {
                                            //iinput is room number
                                            int roomType = getRoom(iInput);

                                            if (roomType != 0) {//if room is already booked
                                                String tempIntput = "yes/no";
                                                while (!(tempIntput.equalsIgnoreCase("y") || tempIntput.equalsIgnoreCase("n"))) {
                                                    System.out.println("Would you like to replace the current Training Session or Group Class? (y/n)");
                                                    tempIntput = scan.nextLine();
                                                    if (tempIntput.equalsIgnoreCase("y")) {
                                                        while (iInput2 <= 0 || iInput2 >= fullList.size()) {
                                                            printAllSessionsAndClasses();
                                                            iInput2 = scan.nextInt();
                                                            if (fullList.contains(iInput2)) {
                                                                bookRoom(iInput, roomType, iInput2);//bookRoom(RoomId, type, session/class ID)
                                                                flag = false;
                                                                break;
                                                            } else
                                                                System.out.println("Invalid Input");
                                                        }
                                                        if (flag) break;
                                                    }
                                                }
                                            } else {//book room to session or class
                                                while (iInput2 <= 0 || iInput2 >= fullList.size()) {
                                                    printAllSessionsAndClasses();
                                                    iInput2 = scan.nextInt();
                                                    if (iInput2 - 1 < size)
                                                        roomType = 3;//training session but both fields are null
                                                    else
                                                        roomType = 4;//group class bu boh fields are null
                                                    if (iInput2 > 0 || iInput2 < fullList.size()) {
                                                        bookRoom(iInput, roomType, iInput2);//bookRoom(RoomId, type, session/class ID)
                                                        flag = false;
                                                        break;
                                                    } else
                                                        System.out.println("Invalid Input");
                                                }
                                            }
                                        } else
                                            System.out.println("Invalid Input");
                                    }
                                    break;

                                case 2://all equipments
                                    printAllEquipment();
                                    System.out.println();
                                    break;

                                case 3://group class schedule
                                    printAllGroupClasses();
                                    System.out.println();
                                    break;

                                case 4://show all rooms
                                    printAllRooms();
                                    System.out.println();
                                    break;

                                case 5://creat group classes
                                    String date_time = "";
                                    Pattern pattern = Pattern.compile("[0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-9][0-9] [0-9][0-9]:[0-5][0-9]");
                                    Matcher matcher = pattern.matcher(date_time);
                                    float duration = 0.0f;
                                    String description = "";
                                    int trainerId = 0;
                                    flag = false;

                                    while (!(matcher.find())) {
                                        System.out.println("Enter Date and Time (YYYY-MM-DD HH:MM)");
                                        scan.nextLine();
                                        date_time = scan.nextLine();
                                        matcher = pattern.matcher(date_time);
                                        if (matcher.find()) {
                                            while (duration <= 0.0f) {
                                                System.out.println("Enter Duration of Session in hours");
                                                duration = scan.nextFloat();
                                                if (duration > 0) {
                                                    while (description.equals("")) {
                                                        System.out.println("Enter Description");
                                                        scan.nextLine();
                                                        description = scan.nextLine();
                                                        if (!description.equals("")) {
                                                            while (trainerId == 0) {
                                                                System.out.println("Assign a Trainer");
                                                                printTrainers();
                                                                trainerId = scan.nextInt();
                                                                if (trainerId != 0) {
                                                                    addGroupClass(trainerId, date_time, duration, description);
                                                                    flag = true;
                                                                    break;
                                                                } else
                                                                    System.out.println("Invalid Input");
                                                            }
                                                        } else
                                                            System.out.println("Invalid Input");
                                                        if (flag) break;
                                                    }
                                                } else
                                                    System.out.println("Invalid Input");
                                                if (flag) break;
                                            }
                                        } else
                                            System.out.println("Invalid Input");
                                        if (flag) break;
                                    }
                                    break;

                                case 6://crete equipment
                                    String desc = "";
                                    flag = false;
                                    fullList = getTrainingSessionArray();
                                    size = fullList.size();
                                    fullList.addAll(getGroupClassesArray());
                                    id = -1;

                                    while (desc.equals("")) {
                                        System.out.println("Enter a Description");
                                        scan.nextLine();
                                        desc = scan.nextLine();
                                        if (!desc.equals("")) {
                                            while (id < 0 || id > fullList.size()) {
                                                System.out.println("Book the Equipment");
                                                printAllSessionsAndClasses();
                                                System.out.println("(0) To not Book anything");
                                                id = scan.nextInt();
                                                if (id >= 0 && id <= fullList.size()) {
                                                    addEquipment(desc, id);
                                                    flag = true;
                                                    break;
                                                } else
                                                    System.out.println("Invalid Input");
                                            }
                                            if (flag) break;
                                        } else
                                            System.out.println("Invalid Input");
                                    }
                                    break;

                                case 7:
                                    System.out.println("All members paid");
                                    System.out.println();
                                    break;

                            }
                        } else
                            System.out.println("Invalid Input");
                    }
            }
        }
    }

    /*
    CheckUserPass
    Checks if username and password are in Database
    String u: username
    String p: password
      Return: 0 if the account does not exist, 1 is user is member, 2 if user is trainer, and 3 if user is admin
     */
    public static int checkUserPass(String u, String p) {
        int acc = 0;
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query

                String SQLmember = "SELECT _username, _password FROM Members";
                String SQLtrainer = "SELECT _username, _password FROM Trainers";
                String SQLadmin = "SELECT _username, _password FROM Admins";

                ResultSet rsMember = stmt.executeQuery(SQLmember); // Process the result
                while (rsMember.next()) {
                    String username = rsMember.getString("_username");
                    String password = rsMember.getString("_password");
                    if (username.equals(u) && password.equals(p)) {
                        acc = 1;
                        break;
                    }
                }

                ResultSet rsTrainer = stmt.executeQuery(SQLtrainer); // Process the result
                while (rsTrainer.next()) {
                    String username = rsTrainer.getString("_username");
                    String password = rsTrainer.getString("_password");
                    if (username.equals(u) && password.equals(p)) {
                        acc = 2;
                        break;
                    }
                }
                ResultSet rsAdmin = stmt.executeQuery(SQLadmin); // Process the result
                while (rsAdmin.next()) {
                    String username = rsAdmin.getString("_username");
                    String password = rsAdmin.getString("_password");
                    if (username.equals(u) && password.equals(p)) {
                        acc = 3;
                        break;
                    }
                }

                // Close resources
                rsMember.close();
                rsTrainer.close();
                rsAdmin.close();
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return acc;
    }

    /*
    checkUserPass
    (Overloaded)
    Checks if username are in Database. The reason for this method to be overloaded is that
    when registering for an account this allows users to have the same password but can not share
    the same username
    String u: username
      Return: 0 if the account does not exist, 1 is user is member, 2 if user is trainer, and 3 if user is admin
     */
    public static int checkUserPass(String u) {
        int acc = 0;
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query

                String SQLmember = "SELECT _username FROM Members";
                String SQLtrainer = "SELECT _username FROM Trainers";
                String SQLadmin = "SELECT _username FROM Admins";

                ResultSet rsMember = stmt.executeQuery(SQLmember); // Process the result
                while (rsMember.next()) {
                    String username = rsMember.getString("_username");
                    if (username.equals(u)) {
                        acc = 1;
                        break;
                    }
                }

                ResultSet rsTrainer = stmt.executeQuery(SQLtrainer); // Process the result
                while (rsTrainer.next()) {
                    String username = rsTrainer.getString("_username");
                    if (username.equals(u)) {
                        acc = 2;
                        break;
                    }
                }

                ResultSet rsAdmin = stmt.executeQuery(SQLadmin); // Process the result
                while (rsAdmin.next()) {
                    String username = rsAdmin.getString("_username");
                    if (username.equals(u)) {
                        acc = 3;
                        break;
                    }
                }

                // Close resources
                rsMember.close();
                rsTrainer.close();
                rsAdmin.close();
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return acc;
    }

    /*
    registerAccount
    Registers account and adds username, password, and name to the Members database
     */
    public static void registerAccount(String username, String password, String fName, String lName) {
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query

                String SQL = String.format("INSERT INTO Members (_username, _password, first_name, last_name) VALUES ('%s', '%s', '%s', '%s')", username, password, fName, lName);
                stmt.executeUpdate(SQL); // Process the result

                // Close resources
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    Prints all Training Sessions
     */
    public static void printAllTrainingSessions() {
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query

                String SQL = "SELECT Training_Sessions.session_id, Training_Sessions.date_time, Training_Sessions.duration, Training_Sessions.description, Trainers.first_name, Trainers.last_name " +
                        "FROM Training_Sessions " +
                        "LEFT JOIN Trainers ON Training_Sessions.trainer_id=Trainers.trainer_id " +
                        "ORDER BY session_id";
                ResultSet rs = stmt.executeQuery(SQL); // Process the result
                //stmt.executeUpdate(SQL); // for SQL string that dont have outputs

                System.out.println("ID  |         Start         |          End          |         Trainer         | Description");

                while (rs.next()) {
                    String id = "(" + rs.getInt("session_id") + ")";
                    Timestamp date_time = rs.getTimestamp("date_time");
                    String start_time = String.valueOf(date_time);
                    float duration = rs.getFloat("duration");
                    String end_time = calcEndTime(date_time, duration);
                    String description = rs.getString("description");
                    String trainer_name = rs.getString("first_name") + " " + rs.getString("last_name");

                    System.out.printf("%-4s|%-23s|%-23s|%-25s| %s\n", id, start_time, end_time, trainer_name, description);
                }
                if (rs.wasNull()) System.out.println("No Records");

                // Close resources
                rs.close();
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    Prints all Croup Classes to the member user
     */
    public static void printAllGroupClasses() {
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query

                String SQL = "SELECT Group_Classes.class_id, Group_Classes.date_time, Group_Classes.duration, Group_Classes.description, Trainers.first_name, Trainers.last_name " +
                        "FROM Group_Classes " +
                        "LEFT JOIN Trainers ON Group_Classes.trainer_id=Trainers.trainer_id " +
                        "ORDER BY class_id";
                ResultSet rs = stmt.executeQuery(SQL); // Process the result
                //stmt.executeUpdate(SQL); // for SQL string that dont have outputs

                System.out.println("ID  |         Start         |          End          |         Trainer         | Description");

                while (rs.next()) {
                    String id = "(" + rs.getInt("class_id") + ")";
                    Timestamp date_time = rs.getTimestamp("date_time");
                    String start_time = String.valueOf(date_time);
                    float duration = rs.getFloat("duration");
                    String end_time = calcEndTime(date_time, duration);
                    String description = rs.getString("description");
                    String trainer_name = rs.getString("first_name") + " " + rs.getString("last_name");

                    System.out.printf("%-4s|%-23s|%-23s|%-25s| %s\n", id, start_time, end_time, trainer_name, description);
                }
                if (rs.wasNull()) System.out.println("No Records");
                // Close resources
                rs.close();
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    calcEndTime
    calculates the time after a certain duration
     */
    public static String calcEndTime(Timestamp startTime, float duration) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startTime.getTime());
        cal.add(Calendar.SECOND, (int) duration * 3600);
        return String.valueOf(new Timestamp(cal.getTime().getTime()));
    }

    /*
    getTrainingSessionArray
    returns an arrayList of training session IDs
     */
    public static ArrayList<Integer> getTrainingSessionArray() {
        ArrayList<Integer> arr = new ArrayList<Integer>();
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query
                String SQL = "SELECT session_id FROM Training_Sessions ORDER BY session_id";
                ResultSet rs = stmt.executeQuery(SQL); // Process the result
                //stmt.executeUpdate(SQL); // for SQL string that dont have outputs
                while (rs.next()) {
                    arr.add(rs.getInt("session_id"));
                }
                // Close resources
                rs.close();
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return arr;
    }

    /*
    getGroupClassesArray
    returns an arrayList of Group Classes
     */
    public static ArrayList<Integer> getGroupClassesArray() {
        ArrayList<Integer> arr = new ArrayList<>();
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query
                String SQL = "SELECT class_id FROM Group_Classes ORDER BY class_id";
                ResultSet rs = stmt.executeQuery(SQL); // Process the result
                //stmt.executeUpdate(SQL); // for SQL string that dont have outputs
                while (rs.next()) {
                    arr.add(rs.getInt("class_id"));
                }
                // Close resources
                rs.close();
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return arr;
    }

    /*
    getId
    returns the ID of the given Username
     */
    public static int getId(String username) {
        int id = 0;
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query

                String memberSQL = String.format("SELECT member_id FROM Members WHERE _username='%s'", username);
                String trainerSQL = String.format("SELECT trainer_id FROM Trainers WHERE _username='%s'", username);
                String adminSQL = String.format("SELECT admin_id FROM Admins WHERE _username='%s'", username);

                //stmt.executeUpdate(SQL); // for SQL string that dont have outputs
                ResultSet rsMember = stmt.executeQuery(memberSQL); // Process the result
                while (rsMember.next()) {
                    id = rsMember.getInt("member_id");
                }
                ResultSet rsTrainer = stmt.executeQuery(trainerSQL); // Process the result
                while (rsTrainer.next()) {
                    id = rsTrainer.getInt("trainer_id");
                }
                ResultSet rsAdmin = stmt.executeQuery(adminSQL); // Process the result
                while (rsAdmin.next()) {
                    id = rsAdmin.getInt("admin_id");
                }

                // Close resources
                rsMember.close();
                rsTrainer.close();
                rsAdmin.close();
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    //returns the session ID of the member that is taking it
    //returns 0 if member is not taking a training session
    public static int getSession(int memberId) {
        int id = 0;
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query

                String SQL = String.format("SELECT session_id FROM Members WHERE member_id=%d", memberId);
                ResultSet rs = stmt.executeQuery(SQL); // Process the result
                //stmt.executeUpdate(SQL); // for SQL string that dont have outputs

                while (rs.next()) {
                    id = rs.getInt("session_id");
                    if (rs.wasNull()) {
                        id = 0;
                    }
                }
                // Close resources
                rs.close();
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    //add session to member
    public static void addSession(int memberId, int sessionId) {
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query

                String SQL = String.format("UPDATE Members SET session_id=%d WHERE member_id=%d", sessionId, memberId);
                stmt.executeUpdate(SQL); // Process the result
                //stmt.executeUpdate(SQL); // for SQL string that dont have outputs

                // Close resources
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    //prints all sessions for the memeber
    public static void printSession(int memberId) {
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query

                String SQL = String.format("SELECT Members.session_id, Training_Sessions.date_time, Training_Sessions.duration, Training_Sessions.description, Trainers.first_name, Trainers.last_name FROM Members JOIN Training_Sessions ON Members.session_id=Training_Sessions.session_id JOIN Trainers ON Training_Sessions.trainer_id=Trainers.trainer_id WHERE member_id=%d ORDER BY session_id", memberId);
                ResultSet rs = stmt.executeQuery(SQL); // Process the result
                //stmt.executeUpdate(SQL); // for SQL string that dont have outputs

                System.out.println("ID  |         Start         |          End          |         Trainer         | Description");

                while (rs.next()) {
                    String id = "(" + rs.getInt("session_id") + ")";
                    Timestamp date_time = rs.getTimestamp("date_time");
                    String start_time = String.valueOf(date_time);
                    float duration = rs.getFloat("duration");
                    String end_time = calcEndTime(date_time, duration);
                    String description = rs.getString("description");
                    String trainer_name = rs.getString("first_name") + " " + rs.getString("last_name");

                    System.out.printf("%-4s|%-23s|%-23s|%-25s| %s\n", id, start_time, end_time, trainer_name, description);
                }
                if (rs.wasNull()) System.out.println("No Records");
                // Close resources
                rs.close();
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    //returns the id of the group class the member is taking
    public static int getGroupClass(int memberId) {
        int id = 0;
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query

                String SQL = String.format("SELECT class_id FROM Members WHERE member_id=%d", memberId);
                ResultSet rs = stmt.executeQuery(SQL); // Process the result
                //stmt.executeUpdate(SQL); // for SQL string that dont have outputs

                while (rs.next()) {
                    id = rs.getInt("class_id");
                    if (rs.wasNull()) {
                        id = 0;
                    }
                }
                // Close resources
                rs.close();
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    //add group class to member
    public static void addClass(int memberId, int classId) {
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query

                String SQL = String.format("UPDATE Members SET class_id=%d WHERE member_id=%d", classId, memberId);
                stmt.executeUpdate(SQL); // Process the result
                //stmt.executeUpdate(SQL); // for SQL string that dont have outputs

                // Close resources
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    //prints the class for member
    public static void printClass(int memberId) {
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query

                String SQL = String.format("SELECT Members.class_id, Group_Classes.date_time, Group_Classes.duration, Group_Classes.description, Trainers.first_name, Trainers.last_name FROM Members JOIN Group_Classes ON Members.class_id=Group_Classes.class_id JOIN Trainers ON Group_Classes.trainer_id=Trainers.trainer_id WHERE member_id=%d ORDER BY class_id", memberId);
                ResultSet rs = stmt.executeQuery(SQL); // Process the result
                //stmt.executeUpdate(SQL); // for SQL string that dont have outputs

                System.out.println("ID  |         Start         |          End          |         Trainer         | Description");

                while (rs.next()) {
                    String id = "(" + rs.getInt("class_id") + ")";
                    Timestamp date_time = rs.getTimestamp("date_time");
                    String start_time = String.valueOf(date_time);
                    float duration = rs.getFloat("duration");
                    String end_time = calcEndTime(date_time, duration);
                    String description = rs.getString("description");
                    String trainer_name = rs.getString("first_name") + " " + rs.getString("last_name");

                    System.out.printf("%-4s|%-23s|%-23s|%-25s| %s\n", id, start_time, end_time, trainer_name, description);

                }
                if (rs.wasNull()) System.out.println("No Records");
                // Close resources
                rs.close();
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    updateWeight
    updates the goal weight of the member
     */
    public static void updateWeight(int memberId, float weight) {
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query

                String SQL = String.format("UPDATE Members SET goalWeight=%f WHERE member_id=%d", weight, memberId);
                stmt.executeUpdate(SQL); // Process the result

                // Close resources
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    updateCurrentWeight
    updates the current weight of the member
     */
    public static void updateCurrentWeight(int memberId, float currWeight) {
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query

                String SQL = String.format("UPDATE Members SET currentweight=%f WHERE member_id=%d", currWeight, memberId);
                stmt.executeUpdate(SQL); // Process the result

                // Close resources
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    updateName
    updates the Members name, first name and last name
     */
    public static void updateName(int memberId, String firstName, String lastName) {
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query

                String SQL = String.format("UPDATE Members SET first_name='%s', last_name='%s' WHERE member_id=%d", firstName, lastName, memberId);
                stmt.executeUpdate(SQL); // Process the result

                // Close resources
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    updateTime
    updates the goal time for the member
     */
    public static void updateTime(int memberId, int timeDays) {
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query

                String SQL = String.format("UPDATE Members SET time_days=%d WHERE member_id=%d", timeDays, memberId);
                stmt.executeUpdate(SQL); // Process the result

                // Close resources
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    printsTrainerSessions
    prints the training sessions with the trainer
     */
    public static void printTrainerSessions(int trainerId) {
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query

                String SQL = String.format("SELECT Training_Sessions.session_id, Training_Sessions.date_time, Training_Sessions.duration, Training_Sessions.description, Trainers.first_name, Trainers.last_name FROM Trainers JOIN Training_Sessions ON Trainers.trainer_id=Training_Sessions.trainer_id WHERE Trainers.trainer_id=%d ORDER BY session_id", trainerId);
                ResultSet rs = stmt.executeQuery(SQL); // Process the result
                //stmt.executeUpdate(SQL); // for SQL string that dont have outputs

                System.out.println("ID  |         Start         |          End          |         Trainer         | Description");
                while (rs.next()) {
                    String id = "(" + rs.getInt("session_id") + ")";
                    Timestamp date_time = rs.getTimestamp("date_time");
                    String start_time = String.valueOf(date_time);
                    float duration = rs.getFloat("duration");
                    String end_time = calcEndTime(date_time, duration);
                    String description = rs.getString("description");
                    String trainer_name = rs.getString("first_name") + " " + rs.getString("last_name");

                    System.out.printf("%-4s|%-23s|%-23s|%-25s| %s\n", id, start_time, end_time, trainer_name, description);

                }
                if (rs.wasNull()) System.out.println("No Records");
                // Close resources
                rs.close();
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    printTrainerClasses
    prints the groupClasses with the trainer
     */
    public static void printTrainerClasses(int trainerId) {
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query

                String SQL = String.format("SELECT Group_Classes.class_id, Group_Classes.date_time, Group_Classes.duration, Group_Classes.description, Trainers.first_name, Trainers.last_name FROM Trainers JOIN Group_Classes ON Trainers.trainer_id=Group_Classes.trainer_id WHERE Trainers.trainer_id=%d ORDER BY class_id", trainerId);
                ResultSet rs = stmt.executeQuery(SQL); // Process the result
                //stmt.executeUpdate(SQL); // for SQL string that dont have outputs

                System.out.println("ID  |         Start         |          End          |         Trainer         | Description");
                while (rs.next()) {
                    String id = "(" + rs.getInt("class_id") + ")";
                    Timestamp date_time = rs.getTimestamp("date_time");
                    String start_time = String.valueOf(date_time);
                    float duration = rs.getFloat("duration");
                    String end_time = calcEndTime(date_time, duration);
                    String description = rs.getString("description");
                    String trainer_name = rs.getString("first_name") + " " + rs.getString("last_name");

                    System.out.printf("%-4s|%-23s|%-23s|%-25s| %s\n", id, start_time, end_time, trainer_name, description);

                }
                if (rs.wasNull()) System.out.println("No Records");
                // Close resources
                rs.close();
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    //prints all Members
    public static void printAllMembers() {
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query

                String SQL = "SELECT * FROM Members ORDER BY member_id";
                ResultSet rs = stmt.executeQuery(SQL); // Process the result
                //stmt.executeUpdate(SQL); // for SQL string that dont have outputs
                System.out.println("ID  |             Name             |   Current Weight   |    Goal Weight    |   Goal Time   |");
                while (rs.next()) {
                    int id = rs.getInt("member_id");
                    String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
                    float currWeight = rs.getFloat("currentweight");
                    float goalWeight = rs.getFloat("goalWeight");
                    int goalTime = rs.getInt("time_days");
                    System.out.printf("%-4d|%-30s|%-20.2f|%-19.2f|%-15d\n", id, fullName, currWeight, goalWeight, goalTime);
                }
                if (rs.wasNull()) System.out.println("No Records");
                // Close resources
                rs.close();
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addTrainingSession(int trainerId, String dateTime, float duration, String description) {
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query

                String SQL = String.format("INSERT INTO Training_Sessions (trainer_id, date_time, duration, description) VALUES (%d, '%s', %.2f, '%s')", trainerId, dateTime, duration, description);
                stmt.executeUpdate(SQL); // Process the result

                // Close resources
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    //returns an array of room_numbers
    public static ArrayList<Integer> getRoomArray() {
        ArrayList<Integer> arr = new ArrayList<Integer>();
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query
                String SQL = "SELECT room_number FROM Rooms";
                ResultSet rs = stmt.executeQuery(SQL); // Process the result
                //stmt.executeUpdate(SQL); // for SQL string that dont have outputs
                while (rs.next()) {
                    arr.add(rs.getInt("room_number"));
                }
                // Close resources
                rs.close();
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return arr;
    }

    //prints all Rooms
    public static void printAllRooms() {
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query

                String SQL = "SELECT Rooms.room_number, Rooms.room_size, Training_Sessions.description AS sessionDesc, Group_Classes.description AS classDesc FROM Rooms FULL JOIN Training_Sessions ON Rooms.session_id=Training_Sessions.session_id FULL JOIN Group_Classes ON Rooms.class_id=Group_Classes.class_id WHERE room_size IS NOT NULL ORDER BY room_number";
                ResultSet rs = stmt.executeQuery(SQL); // Process the result
                //stmt.executeUpdate(SQL); // for SQL string that dont have outputs
                System.out.println("Room Number | Room Size |    Training Session    |    Group Class");
                while (rs.next()) {
                    String roomNum = "(" + rs.getInt("room_number") + ")";
                    String roomSize = rs.getString("room_size");
                    String trainingSesh = rs.getString("sessionDesc");
                    String groupClass = rs.getString("classDesc");
                    System.out.printf("%-12s|%-11s|%-24s|%-2s\n", roomNum, roomSize, trainingSesh, groupClass);
                }
                // Close resources
                rs.close();
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    //0 is both null, 1 is trainingsession, 2 is groupclass
    public static int getRoom(int roomNum) {
        int id = 0;
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query

                String sessionSQL = "SELECT session_id FROM Rooms WHERE room_number=" + roomNum;
                String classSQL = "SELECT class_id FROM Rooms WHERE room_number=" + roomNum;

                //stmt.executeUpdate(SQL); // for SQL string that dont have outputs
                ResultSet sessionRs = stmt.executeQuery(sessionSQL); // Process the result
                while (sessionRs.next()) {
                    id = sessionRs.getInt("session_id");
                }
                if (sessionRs.wasNull())
                    id = 0;
                else
                    id = 1;

                ResultSet classRs = stmt.executeQuery(classSQL); // Process the result
                while (classRs.next()) {
                    id = classRs.getInt("class_id");
                }
                if (classRs.wasNull())
                    id = 0;
                else
                    id = 2;
                // Close resources
                sessionRs.close();
                classRs.close();
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    //for Trainers to print all sessiosn and groupclasses in one table
    public static void printAllSessionsAndClasses() {
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query
                int count = 1;
                String sessionSQL = "SELECT Training_Sessions.*, Trainers.first_name, Trainers.last_name FROM Training_Sessions JOIN Trainers ON Training_Sessions.trainer_id=Trainers.trainer_id ORDER BY session_id";
                String classSQL = "SELECT Group_Classes.*, Trainers.first_name, Trainers.last_name FROM Group_Classes JOIN Trainers ON Group_Classes.trainer_id=Trainers.trainer_id ORDER BY class_id";

                ResultSet sessionRs = stmt.executeQuery(sessionSQL); // Process the result

                System.out.println("()  |  ID  |         Start         |          End          |         Trainer         | Description");

                //stmt.executeUpdate(SQL); // for SQL string that dont have outputs
                while (sessionRs.next()) {
                    int id = sessionRs.getInt("session_id");
                    Timestamp date_time = sessionRs.getTimestamp("date_time");
                    String start_time = String.valueOf(date_time);
                    float duration = sessionRs.getFloat("duration");
                    String end_time = calcEndTime(date_time, duration);
                    String description = sessionRs.getString("description");
                    String trainer_name = sessionRs.getString("first_name") + " " + sessionRs.getString("last_name");

                    System.out.printf("%-4s|%-6d|%-23s|%-23s|%-25s| %s\n", "(" + count + ")", id, start_time, end_time, trainer_name, description);
                    count++;
                }
                if (sessionRs.wasNull()) System.out.println("No Records");

                ResultSet classRs = stmt.executeQuery(classSQL); // Process the result
                while (classRs.next()) {
                    int id = classRs.getInt("class_id");
                    Timestamp date_time = classRs.getTimestamp("date_time");
                    String start_time = String.valueOf(date_time);
                    float duration = classRs.getFloat("duration");
                    String end_time = calcEndTime(date_time, duration);
                    String description = classRs.getString("description");
                    String trainer_name = classRs.getString("first_name") + " " + classRs.getString("last_name");

                    System.out.printf("%-4s|%-6d|%-23s|%-23s|%-25s| %s\n", "(" + count + ")", id, start_time, end_time, trainer_name, description);
                    count++;
                }
                if (classRs.wasNull()) System.out.println("No Records");
                // Close resources
                sessionRs.close();
                classRs.close();
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    //for Admins to book a room to a session or class
    public static void bookRoom(int roomNum, int roomType, int booking_id) {
        ArrayList<Integer> fullList = getTrainingSessionArray();
        int size = fullList.size();
        fullList.addAll(getGroupClassesArray());

        if (roomType == 1 || roomType == 3) {
            try {
                Class.forName(className);
                Connection con = DriverManager.getConnection(url, user, pass);
                if (con != null) {
                    Statement stmt = con.createStatement(); // Execute SQL query

                    String SQL = "UPDATE Rooms SET session_id=" + fullList.get(booking_id - 1) + " WHERE room_number=" + roomNum;
                    stmt.executeUpdate(SQL); // Process the result

                    // Close resources
                    stmt.close();
                } else {
                    System.out.println("Failed to Connect");
                }
                con.close();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }

        } else if (roomType == 2 || roomType == 4) {
            try {
                Class.forName(className);
                Connection con = DriverManager.getConnection(url, user, pass);
                if (con != null) {
                    Statement stmt = con.createStatement(); // Execute SQL query

                    String SQL = "UPDATE Rooms SET class_id=" + (fullList.get(booking_id - 1)) + " WHERE room_number=" + roomNum;
                    stmt.executeUpdate(SQL); // Process the result

                    // Close resources
                    stmt.close();
                } else {
                    System.out.println("Failed to Connect");
                }
                con.close();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void printTrainers() {
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query

                String SQL = "SELECT * FROM Trainers ORDER BY trainer_id";
                ResultSet rs = stmt.executeQuery(SQL); // Process the result
                //stmt.executeUpdate(SQL); // for SQL string that dont have outputs
                System.out.println("ID  |          Name");
                while (rs.next()) {
                    int id = rs.getInt("trainer_id");
                    String sid = "(" + id + ")";
                    String name = rs.getString("first_name") + " " + rs.getString("last_name");
                    System.out.printf("%-4s|%-25s\n", "(" + id + ")", name);
                }

                // Close resources
                rs.close();
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    //prints all the equipment
    public static void printAllEquipment() {
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query

                String SQL = "SELECT Equipment.equipment_id, Equipment.description, Training_Sessions.description AS Training_Session, Group_Classes.description AS Group_Class FROM Equipment FULL JOIN Training_Sessions ON Equipment.session_id=Training_Sessions.session_id FULL JOIN Group_Classes ON Equipment.class_id=Group_Classes.class_id WHERE equipment.description IS NOT NULL ORDER BY equipment_id";
                ResultSet rs = stmt.executeQuery(SQL); // Process the result
                //stmt.executeUpdate(SQL); // for SQL string that dont have outputs
                System.out.println("ID  |       description       |   Training Session   |     Group Class    ");
                while (rs.next()) {
                    int id = rs.getInt("equipment_id");
                    String description = rs.getString("description");
                    String sessionDesc = rs.getString("Training_Session");
                    String classDesc = rs.getString("Group_Class");

                    System.out.printf("%-4s|%-25s|%-22s|%-20s\n", "(" + id + ")", description, sessionDesc, classDesc);
                }
                // Close resources
                rs.close();
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getFirstName(String u) {
        String fname = "";
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query

                String SQLmember = "SELECT first_name FROM Members WHERE _username='" + u + "'";
                String SQLtrainer = "SELECT first_name FROM Trainers WHERE _username='" + u + "'";
                String SQLadmin = "SELECT first_name FROM Admins WHERE _username='" + u + "'";

                ResultSet rsMember = stmt.executeQuery(SQLmember); // Process the result
                while (rsMember.next()) {
                    fname = rsMember.getString("first_name");
                    break;
                }

                ResultSet rsTrainer = stmt.executeQuery(SQLtrainer); // Process the result
                while (rsTrainer.next()) {
                    fname = rsTrainer.getString("first_name");
                    break;
                }

                ResultSet rsAdmin = stmt.executeQuery(SQLadmin); // Process the result
                while (rsAdmin.next()) {
                    fname = rsAdmin.getString("first_name");
                    break;
                }

                // Close resources
                rsMember.close();
                rsTrainer.close();
                rsAdmin.close();
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return fname;
    }

    public static void addGroupClass(int trainerId, String date_time, float duration, String description) {
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query

                String SQL = String.format("INSERT INTO Group_Classes (trainer_id, date_time, duration, description) VALUES (%d, '%s', %.2f, '%s')", trainerId, date_time, duration, description);
                stmt.executeUpdate(SQL); // Process the result

                // Close resources
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addEquipment(String desc, int id) {
        ArrayList<Integer> fullList = getTrainingSessionArray();
        int size = fullList.size();
        fullList.addAll(getGroupClassesArray());

        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query

                String SQL = "INSERT INTO Equipment ";

                if (id == 0) {//they chose not to book
                    SQL += String.format("(description) VALUES ('%s')", desc);
                } else if (id < size) {// add to training session
                    SQL += String.format("(description, session_id) VALUES ('%s', %d)", desc, fullList.get(id - 1));
                } else if (id >= size) {// add to group class
                    SQL += String.format("(description, class_id) VALUES ('%s', %d)", desc, fullList.get(id - 1));
                }

                stmt.executeUpdate(SQL); // Process the result

                // Close resources
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }


    }

    public static void genericMethod() {
        try {
            Class.forName(className);
            Connection con = DriverManager.getConnection(url, user, pass);
            if (con != null) {
                Statement stmt = con.createStatement(); // Execute SQL query

                String SQL = "";
                ResultSet rs = stmt.executeQuery(SQL); // Process the result
                //stmt.executeUpdate(SQL); // for SQL string that dont have outputs
                while (rs.next()) {

                }

                // Close resources
                rs.close();
                stmt.close();
            } else {
                System.out.println("Failed to Connect");
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }
}
