# GestionReparation - Repair Management System

A comprehensive Java Swing desktop application for managing device repair shops, repair requests, repairers, and financial tracking. Built with Java Swing, Lombok, and a clean MVC architecture.

## 📋 Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [Installation](#installation)
- [Usage](#usage)
- [User Roles](#user-roles)
- [Key Features](#key-features)
- [Screenshots](#screenshots)

## ✨ Features

### Core Functionality
- **User Authentication**: Secure login system with role-based access control
- **Shop Management**: Owners can create and manage their repair shops
- **Repair Request Management**: Create, track, and update repair requests with unique codes
- **Repairer Management**: Add, update, and manage repairers in your shop
- **Status Tracking**: Track repair status (PENDING, IN_PROGRESS, COMPLETED)
- **Financial Management**: 
  - Track completed repairs revenue
  - Manage loans and track loan history
  - Calculate net revenue (completed repairs - active loans)
  - View shop cash as sum of all repairers' net
- **Client Tracking**: Clients can track their repair requests using unique codes
- **Real-time Statistics**: View completed repairs, active loans, and net revenue in the status bar

### User Interface
- **Modern Dark Theme**: HackTheBox-inspired dark theme with neon green accents
- **Single-Page Forms**: All signup, add repairer, and add repair request forms are single-page dialogs
- **Compact List Views**: Loans and repair requests displayed in compact, easy-to-read formats
- **Advanced Filtering**: Filter repair requests by client name or code
- **Status Dropdowns**: Easy status updates with dropdown menus

## 🛠 Technologies Used

- **Java**: Core programming language
- **Java Swing**: GUI framework
- **Lombok**: Reduces boilerplate code with annotations (@Data, @NoArgsConstructor, etc.)
- **Eclipse IDE**: Development environment

## 📁 Project Structure

```
GestionReparation/
├── src/
    ├── dao/                    # Data Access Object layer
    │   ├── LoanHistoryDAO.java
    │   ├── RepairDAO.java
    │   ├── ShopDAO.java
    │   └── UserDAO.java
    │
    ├── exception/              # Custom exceptions
    │   ├── AbstractAppException.java
    │   ├── AuthenticationException.java
    │   ├── DataNotFoundException.java
    │   ├── InvalidInputException.java
    │   ├── OperationNotAllowedException.java
    │   └── ShopAssignmentException.java
    │
    ├── metier/                 # Business logic layer
    │   ├── models/             # Domain models
    │   │   ├── Device.java
    │   │   ├── LoanHistory.java
    │   │   ├── Repair.java
    │   │   ├── Shop.java
    │   │   └── User.java
    │   └── services/           # Business services
    │       ├── BusinessService.java
    │       ├── RepairService.java
    │       ├── ShopService.java
    │       ├── StatisticsService.java
    │       └── UserService.java
    │
    └── presentation/           # UI layer
        ├── AnimatableButton.java
        ├── ClientPanel.java
        ├── ClientTrackRequestPanel.java
        ├── DashboardPanel.java
        ├── LoansPanel.java
        ├── LoginPanel.java
        ├── MainFrame.java
        ├── ManageRepairersPanel.java
        ├── OwnerPanel.java
        ├── RepairerPanel.java
        ├── Theme.java
        └── TrackRequestPanel.java



## 🚀 Installation

### Prerequisites
- Java JDK 8 or higher
- Eclipse IDE (recommended) or any Java IDE
- Lombok plugin for Eclipse (if using Eclipse)

### Setup Steps

1. **Clone the repository**
   ```bash
   git clone <your-repo-url>
   cd GestionReparation
   ```

2. **Install Lombok** (if using Eclipse)
   - Download Lombok from [projectlombok.org](https://projectlombok.org)
   - Run the installer and select your Eclipse installation
   - Restart Eclipse

3. **Import into Eclipse**
   - Open Eclipse
   - File → Import → Existing Projects into Workspace
   - Select the GestionReparation folder
   - Click Finish

4. **Configure Build Path**
   - Right-click project → Properties
   - Java Build Path → Ensure `src` is set as Source folder
   - Libraries → Add Lombok if not already present

5. **Clean and Build**
   - Project → Clean → Select GestionReparation → Clean
   - Project → Build Project

6. **Run the Application**
   - Right-click `MainFrame.java`
   - Run As → Java Application

## 📖 Usage

### Getting Started

1. **Sign Up as Owner**
   - Click "Sign Up Owner" on the login screen
   - Fill in all required fields:
     - Username
     - Email
     - Phone
     - Password
     - Confirm Password
     - Shop Name
     - Check "Also a repairer?" if you want to perform repairs yourself
   - Click "Sign Up"

2. **Login**
   - Enter your username and password
   - Click "Login"

3. **Dashboard**
   - After login, you'll see the dashboard with quick action buttons
   - The status bar at the bottom shows your completed repairs, loans, and net revenue

### Managing Your Shop

#### Add Repairers
- Click "Add Repairer" or "Manage Repairers"
- Fill in the repairer's information:
  - Username
  - Password
  - Email (optional)
  - Phone (optional)
- Click "Add Repairer"

#### View Shop Information
- Click "View Shop"
- See total cash available (sum of all repairers' net)
- View list of all repairers with their individual net amounts

### Managing Repair Requests

#### Add Repair Request
- Click "Add Repair Request"
- Fill in all fields:
  - Client Name
  - Device IMEI
  - Device Type
  - Brand
  - Model
  - Description
  - Repair Cost
- A unique code will be generated for tracking

#### Update Repair Status
- Click "Update Status"
- View all repair requests in a table
- Use the filter to search by client name or code
- Change status using the dropdown menu (PENDING, IN_PROGRESS, COMPLETED)
- Status updates automatically when changed

### Managing Loans

#### Add a Loan
- Go to "Loans & History" tab
- Enter person name and amount
- Click "Add Loan"

#### View Loans
- Loans are displayed in a compact line-by-line format
- Shows: Person name, amount, exact date and time
- Click "Mark Returned" to mark a loan as returned

### Client Features

#### Track Repair Request
- On the login screen, enter your repair code in "Client Repair Code Lookup"
- Click "Track" to see your repair status

## 👥 User Roles

### Owner
- Create and manage shop
- Add and manage repairers
- View shop financial information
- Add repair requests
- Update repair status
- Manage loans

### Owner + Repairer (BOTH)
- All owner privileges
- Can perform repairs
- Net revenue included in shop cash calculation

### Repairer
- Add repair requests
- Update repair status
- View assigned repairs
- Manage loans

## 🎯 Key Features

### Single-Page Forms
All forms (signup, add repairer, add repair request) are displayed as single-page dialogs with all fields visible, eliminating the need for multiple popup dialogs.

### Real-Time Statistics
The status bar at the bottom of the application always displays:
- Completed repairs total
- Active loans total
- Net revenue (completed - loans)

### Advanced Filtering
The Update Status dialog includes:
- Filter by client name
- Filter by repair code
- Real-time filtering as you type

### Financial Tracking
- Track completed repairs revenue
- Manage loans with date/time tracking
- Calculate net revenue per user
- Shop cash shows sum of all repairers' net

### Compact Display
- Loans displayed in compact line format
- Repair requests in organized tables
- Easy-to-read information layout

## 🎨 Theme

The application uses a modern dark theme inspired by HackTheBox:
- Dark navy backgrounds
- Neon green accents
- High contrast for readability
- Professional appearance

## 📝 Notes

- All data is stored in memory (using ArrayLists in DAO classes)
- Data is not persisted between application restarts
- For production use, consider implementing database persistence

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is open source and available for educational purposes.

## 👤 Author

Developed as a Java Swing desktop application for repair shop management.

---

**Note**: This application requires Lombok to compile. Make sure Lombok is properly installed and configured in your IDE.

